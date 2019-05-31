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

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import com.github.svstoll.csccrecommender.ProductionModule;
import com.github.svstoll.csccrecommender.index.MethodInvocationIndexer;
import com.github.svstoll.csccrecommender.mining.CompletionEventExtractor;
import com.github.svstoll.csccrecommender.properties.ConfigProperties;
import com.github.svstoll.csccrecommender.recommender.CsccRecommender;
import com.github.svstoll.csccrecommender.recommender.RecommendationResult;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.github.svstoll.csccrecommender.utility.StatisticsUtility.calculatePrecision;
import static com.github.svstoll.csccrecommender.utility.StatisticsUtility.calculateRecall;

@Singleton
public class CompletionEventEvaluator {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final String resultsDirectoryPath;
  private final CompletionEventExtractor completionEventExtractor;
  private final CsccRecommender csccRecommender;

  private int recommendationsRequested = 0;
  private int recommendationsMade = 0;

  private long totalRecommendationTimeInMs = 0;

  private int top1Recommendations = 0;
  private int tp3Recommendations = 0;
  private int top10Recommendations = 0;

  private int extensionContextRecommendationsRequested = 0;
  private int extensionContextRecommendationsMade = 0;
  private int top3ExtensionContextRecommendations = 0;

  private int noneExtensionContextRecommendationsRequested = 0;
  private int noneExtensionContextRecommendationsMade = 0;
  private int top3NoneExtensionContextRecommendations = 0;

  @Inject
  protected CompletionEventEvaluator(
      @Named(ConfigProperties.RESULTS_DIRECTORY_PROPERTY) String resultsDirectoryPath,
      CompletionEventExtractor completionEventExtractor,
      CsccRecommender csccRecommender) {
    this.resultsDirectoryPath = resultsDirectoryPath;
    this.completionEventExtractor = completionEventExtractor;
    this.csccRecommender = csccRecommender;
  }

  public static void main(String[] args) {
    LOGGER.info("Starting evaluation of completion events.");
    Injector injector = Guice.createInjector(new ProductionModule());
    MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
    CompletionEventEvaluator completionEventEvaluator = injector.getInstance(CompletionEventEvaluator.class);

    if (args.length > 0 && "-index".equals(args[0])) {
      methodInvocationIndexer.indexAllAvailableContexts(true);
    }
    completionEventEvaluator.executeEvaluation();
  }

  public void executeEvaluation() {
    completionEventExtractor.processAllCompletionEvents(completionEvents -> {
      LOGGER.info("Performing recommendations for {} completion events.", completionEvents.size());
      for (CompletionEvent completionEvent : completionEvents) {
        List<RecommendationResult> recommendationResults = csccRecommender.recommendMethods(completionEvent);
        for (RecommendationResult recommendationResult : recommendationResults) {
          updateStatistics(completionEvent, recommendationResult);
        }
      }
    });

    writeEvaluationResults();
  }

  private void updateStatistics(CompletionEvent completionEvent, RecommendationResult recommendationResult) {
    updateTotalRecommendationsRequested(recommendationResult);
    totalRecommendationTimeInMs += recommendationResult.getRecommendationTimeInMs();

    List<String> recommendedMethods = recommendationResult.getRecommendedMethods();
    String selectedMethodName = ((IMethodName) Objects.requireNonNull(
        completionEvent.getLastSelectedProposal().getName())).getFullName();
    if (recommendedMethods.isEmpty()) {
      return;
    }

    boolean isTop1 = false;
    boolean isTop3 = false;
    boolean isTop10 = false;
    for (int i = 0; i < recommendedMethods.size() && recommendedMethods.size() < 10; i++) {
      if (recommendedMethods.get(i).equals(selectedMethodName)) {
        isTop10 = true;
        if (i == 0) {
          isTop1 = true;
          isTop3 = true;
        }
        else if (i < 3) {
          isTop3 = true;
        }
        break;
      }
    }

    updateRecommendationsMade(recommendationResult);
    updateSuccessfulRecommendations(recommendationResult, isTop1, isTop3, isTop10);
  }

  private void updateTotalRecommendationsRequested(RecommendationResult recommendationResult) {
    recommendationsRequested++;
    if (recommendationResult.isOccurredWithinExtensionMethod()) {
      extensionContextRecommendationsRequested++;
    }
    else {
      noneExtensionContextRecommendationsRequested++;
    }
  }

  private void updateRecommendationsMade(RecommendationResult recommendationResult) {
    recommendationsMade++;
    if (recommendationResult.isOccurredWithinExtensionMethod()) {
      extensionContextRecommendationsMade++;
    }
    else {
      noneExtensionContextRecommendationsMade++;
    }
  }

  private void updateSuccessfulRecommendations(RecommendationResult recommendationResult, boolean isTop1, boolean isTop3, boolean isTop10) {
    if (isTop1) {
      top1Recommendations++;
    }
    if (isTop3) {
      tp3Recommendations++;
      if (recommendationResult.isOccurredWithinExtensionMethod()) {
        top3ExtensionContextRecommendations++;
      }
      else {
        top3NoneExtensionContextRecommendations++;
      }
    }
    if (isTop10) {
      top10Recommendations++;
    }
  }

  private void writeEvaluationResults() {
    File resultsDirectory = new File(resultsDirectoryPath);
    if (!resultsDirectory.exists()) {
      resultsDirectory.mkdirs();
    }
    Path resultsFilePath = Paths.get(resultsDirectoryPath, "completion-event-evaluation-" + System.currentTimeMillis() + ".txt");
    try (PrintWriter pw = new PrintWriter(new FileWriter(resultsFilePath.toFile()))) {
      pw.println("Recommendations requested: " + recommendationsRequested);
      pw.println("Recommendations made: " + recommendationsMade);
      double averageRecommendationTime = 0;
      if (recommendationsRequested > 0) {
        averageRecommendationTime = (double) totalRecommendationTimeInMs / (double) recommendationsRequested;
      }
      pw.println("Average recommendation time (ms): " + averageRecommendationTime);
      pw.println();

      pw.println("Overall recall: " + calculateRecall(recommendationsRequested, recommendationsMade));
      pw.println("Top-1 precision: " + calculatePrecision(recommendationsMade, top1Recommendations));
      pw.println("Top-3 precision: " + calculatePrecision(recommendationsMade, tp3Recommendations));
      pw.println("Top-10 precision: " + calculatePrecision(recommendationsMade, top10Recommendations));
      pw.println();

      pw.println("Recommendations requested within extension methods: " + extensionContextRecommendationsRequested);
      pw.println("Recommendations made within extension methods: " + extensionContextRecommendationsMade);
      pw.println("Overall recall within extension methods: " + calculateRecall(extensionContextRecommendationsRequested, extensionContextRecommendationsMade));
      pw.println("Top-3 precision within extension methods: " + calculatePrecision(extensionContextRecommendationsMade, top3ExtensionContextRecommendations));
      pw.println();

      pw.println("Recommendations requested NONE within extension methods: " + noneExtensionContextRecommendationsRequested);
      pw.println("Recommendations made within NONE extension methods: " + noneExtensionContextRecommendationsMade);
      pw.println("Overall recall within NONE extension methods: " + calculateRecall(noneExtensionContextRecommendationsRequested, noneExtensionContextRecommendationsMade));
      pw.println("Top-3 precision within NONE extension methods: " + calculatePrecision(noneExtensionContextRecommendationsMade, top3NoneExtensionContextRecommendations));

      LOGGER.info("Evaluation finished. The results have been saved to '{}'.", resultsFilePath);
    }
    catch (IOException e) {
      LOGGER.info("Error writing context evaluation results.", e);
    }
  }
}
