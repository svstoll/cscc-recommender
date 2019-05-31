package com.github.svstoll.csccrecommender.evaluation;

import com.github.svstoll.csccrecommender.ProductionModule;
import com.github.svstoll.csccrecommender.index.MethodInvocationIndex;
import com.github.svstoll.csccrecommender.index.MethodInvocationIndexer;
import com.github.svstoll.csccrecommender.properties.ConfigProperties;
import com.github.svstoll.csccrecommender.recommender.CsccRecommender;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.github.svstoll.csccrecommender.utility.FileUtility.findAllZipFilePaths;

public class ContextEvaluator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextEvaluator.class);
  private static final String MASKED_FILE_ENDING = ".masked";

  private final String contextsDirectoryPath;
  private final String resultsDirectoryPath;
  private final MethodInvocationIndex methodInvocationIndex;
  private final MethodInvocationIndexer methodInvocationIndexer;
  private final CsccRecommender csccRecommender;

  @Inject
  protected ContextEvaluator(
      @Named(ConfigProperties.CONTEXTS_DIRECTORY_PROPERTY) String contextsDirectoryPath,
      @Named(ConfigProperties.RESULTS_DIRECTORY_PROPERTY) String resultsDirectoryPath,
      MethodInvocationIndex methodInvocationIndex,
      MethodInvocationIndexer methodInvocationIndexer,
      CsccRecommender csccRecommender) {
    this.contextsDirectoryPath = contextsDirectoryPath;
    this.resultsDirectoryPath = resultsDirectoryPath;
    this.methodInvocationIndex = methodInvocationIndex;
    this.methodInvocationIndexer = methodInvocationIndexer;
    this.csccRecommender = csccRecommender;
  }

  public static void main(String[] args) throws IOException {
    LOGGER.info("Starting evaluation of completion events.");
    Injector injector = Guice.createInjector(new ProductionModule());
    ContextEvaluator evaluator = injector.getInstance(ContextEvaluator.class);
    Path tempDirectory = Files.createTempDirectory("context-evaluation");

    evaluator.trainAndEvaluateOnContextDataset(2, tempDirectory.toString());
  }

  public void trainAndEvaluateOnContextDataset(int splits, String tempEvaluationResultsDirectoryPath) {
    methodInvocationIndex.clearIndex();
    List<String[]> groups;
    try {
      groups = splitContextDataset(splits);
    }
    catch (IllegalStateException e) {
      LOGGER.error("Context evaluation could not be executed.", e);
      return;
    }

    // 1. Train the model
    int evaluationGroup = 0;
    List<String> maskedFiles = maskZipFiles(groups.get(evaluationGroup));
    methodInvocationIndexer.indexAllAvailableContexts(true);
    unmaskZipFiles(maskedFiles);
    maskedFiles.clear();

    // 2. Evaluate on the remaining one
    for (int j = 0; j < splits; j++) {
      if (j == evaluationGroup) {
        continue;
      }
      maskedFiles.addAll(maskZipFiles(groups.get(j)));
    }
    csccRecommender.recommendForAllInvocationsInAvailableContexts(tempEvaluationResultsDirectoryPath);

    unmaskZipFiles(maskedFiles);
    writeEvaluationResults(tempEvaluationResultsDirectoryPath);
  }

  private List<String[]> splitContextDataset(int splits) {
    File file = new File(contextsDirectoryPath);
    String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
    if (directories == null || directories.length == 0) {
      throw new IllegalStateException("Contexts directory does not exist or is empty.");
    }
    else if (directories.length < splits) {
      throw new IllegalStateException("Number of splits is greater than number of available projects.");
    }

    LOGGER.info("Splitting context dataset.");
    Collections.shuffle(Arrays.asList(directories), new Random(949666788));
    int groupSize = directories.length / splits;
    ArrayList<String[]> list = new ArrayList<>();
    for (int i = 0; i < splits; i++) {
      String[] groupNames = new String[groupSize];
      System.arraycopy(directories, i * groupSize, groupNames, 0, groupSize);
      LOGGER.info("Projects in group {}: {}", i, groupNames);
      list.add(groupNames);
    }
    return list;
  }

  private List<String> maskZipFiles(String[] toBeMasked) {
    ArrayList<String> maskedZips = new ArrayList<>();
    for (String s : toBeMasked) {
      List<String> zips = findAllZipFilePaths(contextsDirectoryPath + "/" + s);
      for (String zip : zips) {
        File file = new File(zip);

        boolean success = file.renameTo(new File(file + MASKED_FILE_ENDING));
        if (!success) {
          LOGGER.warn("Failed to mask zip '{}'.", zip);
        }
        maskedZips.add(file + MASKED_FILE_ENDING);
      }
    }
    return maskedZips;
  }

  private void unmaskZipFiles(List<String> maskedZips) {
    for (String maskedZip : maskedZips) {
      File file = new File(maskedZip);
      String fileName = file.getAbsolutePath();
      boolean success = file.renameTo(new File(fileName.substring(0, fileName.length() - MASKED_FILE_ENDING.length())));
      if (!success) {
        LOGGER.warn("Failed to unmask zip '{}'.", maskedZip);
      }
    }
  }

  private void writeEvaluationResults(String tempEvaluationResultsDirectoryPath) {
    int[] requestedMaxRecommendations = {1, 3, 10};
    File resultsDirectory = new File(resultsDirectoryPath);
    if (!resultsDirectory.exists()) {
      resultsDirectory.mkdirs();
    }

    String resultsFileName = resultsDirectoryPath + "/" + "cross-validation-result-" + System.currentTimeMillis() + ".txt";
    try (PrintWriter pw = new PrintWriter(new FileWriter(resultsFileName))) {
      for (int maxRecommendations : requestedMaxRecommendations) {
        ContextEvaluationStatistics statistics = calculateStatistics(maxRecommendations, tempEvaluationResultsDirectoryPath);
        pw.println("Maximum Recommendations = " + maxRecommendations);
        pw.println("\tTotal cases: " + statistics.getTotalCases());
        pw.println("\tPrecision: " + statistics.getPrecision());
        pw.println("\tRecall: " + statistics.getRecall());
        pw.println("\tF-measure: " + statistics.calculateFMeasure());
        pw.println();
      }
    }
    catch (IOException e) {
      LOGGER.info("Error writing context evaluation results.", e);
    }
  }

  public ContextEvaluationStatistics calculateStatistics(int recommendations, String tempEvaluationResultsDirectoryPath) throws IOException {
    try (BufferedReader in = new BufferedReader(
        new FileReader(tempEvaluationResultsDirectoryPath + "/" + recommendations + "/results.txt"))) {
      String line;
      double madeAndRelevant = 0;
      double made = 0;
      int requested = 0;
      while ((line = in.readLine()) != null) {
        String[] result = line.split(",");
        requested += 1;
        if (Double.parseDouble(result[0]) > 0) {
          madeAndRelevant += 1.0;
        }
        if (Double.parseDouble(result[1]) > 0) {
          made += 1;
        }
      }
      double precision = 0;
      if (made != 0) {
        precision = madeAndRelevant / made;
      }
      double recall = 0;
      if (requested != 0) {
        recall = made / requested;
      }

      return new ContextEvaluationStatistics(requested, precision, recall);
    }
  }
}
