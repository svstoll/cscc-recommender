/*
 *  Copyright 2019 Sven Stoll, Dingguang Jin, Tran Phan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.github.svstoll.csccrecommender.utility.FileUtility.findAllZipFilePaths;

public class CrossProjectEvaluator {

  private static final Logger LOGGER = LoggerFactory.getLogger(CrossProjectEvaluator.class);
  private static final String MASKED_FILE_ENDING = ".masked";

  private final String contextsDirectoryPath;
  private final String resultsDirectoryPath;
  private final MethodInvocationIndex methodInvocationIndex;
  private final MethodInvocationIndexer methodInvocationIndexer;
  private final CsccRecommender csccRecommender;

  @Inject
  protected CrossProjectEvaluator(
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

  public static void main(String[] args) {
    LOGGER.info("Starting context based evaluation.");
    Injector injector = Guice.createInjector(new ProductionModule());
    CrossProjectEvaluator evaluator = injector.getInstance(CrossProjectEvaluator.class);
    evaluator.trainAndEvaluateOnContextDataset(2);
  }

  public void trainAndEvaluateOnContextDataset(int splits) {
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
    ContextEvaluationStatistics statistics = new ContextEvaluationStatistics();
    csccRecommender.recommendForAllInvocationsInAvailableContexts(statistics);

    unmaskZipFiles(maskedFiles);
    writeEvaluationResults(statistics);
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
    for (String zipFile : toBeMasked) {
      List<String> zips = findAllZipFilePaths(Paths.get(contextsDirectoryPath, zipFile).toString());
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

  private void writeEvaluationResults(ContextEvaluationStatistics statistics) {
    File resultsDirectory = new File(resultsDirectoryPath);
    if (!resultsDirectory.exists()) {
      resultsDirectory.mkdirs();
    }
    Path resultsFilePath = Paths.get(resultsDirectoryPath, "cross-project-evaluation-" + System.currentTimeMillis() + ".txt");
    try (PrintWriter pw = new PrintWriter(new FileWriter(resultsFilePath.toFile()))) {
      pw.println("Requested recommendations: " + statistics.getRecommendationsRequested());
      pw.println("Recommendations made: " + statistics.getRecommendationsMade());
      pw.println("Overall recall: " + statistics.calculateRecall());
      pw.println();
      pw.println("Top-1 precision: " + statistics.calculateTop1Precision());
      pw.println("Top-1 F-measure: " + statistics.calculateTop1FMeasure());
      pw.println();
      pw.println("Top-3 precision: " + statistics.calculateTop3Precision());
      pw.println("Top-3 F-measure: " + statistics.calculateTop3FMeasure());
      pw.println();
      pw.println("Top-10 precision: " + statistics.calculateTop10Precision());
      pw.println("Top-10 F-measure: " + statistics.calculateTop10FMeasure());
      pw.println();

      LOGGER.info("Evaluation finished. The results have been saved to '{}'.", resultsFilePath);
    }
    catch (IOException e) {
      LOGGER.info("Error writing context evaluation results.", e);
    }
  }
}
