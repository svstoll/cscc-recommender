package ch.uzh.ifi.ase.csccrecommender.evaluation;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import ch.uzh.ifi.ase.csccrecommender.mining.CompletionEventExtractor;
import ch.uzh.ifi.ase.csccrecommender.recommender.CsccRecommender;
import ch.uzh.ifi.ase.csccrecommender.recommender.RecommendationResult;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class CompletionEventEvaluator {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final CompletionEventExtractor completionEventExtractor;
  private final CsccRecommender csccRecommender;

  private int totalRecommendationsRequested = 0;
  private int totalRecommendationsMade = 0;

  private long totalRecommendationTimeInMs = 0;

  private int totalTop1Recommendations = 0;
  private int totalTop3Recommendations = 0;
  private int totalTop10Recommendations = 0;

  private int totalExtensionContextRecommendationsRequested = 0;
  private int totalExtensionContextRecommendationsMade = 0;
  private int totalTop3ExtensionContextRecommendations = 0;

  private int totalNoneExtensionContextRecommendationsRequested = 0;
  private int totalNoneExtensionContextRecommendationsMade = 0;
  private int totalTop3NoneExtensionContextRecommendations = 0;

  @Inject
  protected CompletionEventEvaluator(CompletionEventExtractor completionEventExtractor, CsccRecommender csccRecommender) {
    this.completionEventExtractor = completionEventExtractor;
    this.csccRecommender = csccRecommender;
  }

  public void executeEvaluation() {
    completionEventExtractor.processAllCompletionEvents(completionEvents -> {
      for (CompletionEvent completionEvent : completionEvents) {
        List<RecommendationResult> recommendationResults = csccRecommender.recommendMethods(completionEvent);
        for (RecommendationResult recommendationResult : recommendationResults) {
          updateStatistics(completionEvent, recommendationResult);
        }
      }
    });

    LOGGER.info("Recommendations requested: {}", totalRecommendationsRequested);
    LOGGER.info("Recommendations made: {}", totalRecommendationsMade);
    LOGGER.info("Recommendation time (ms): {}", totalRecommendationTimeInMs / totalRecommendationsRequested);

    LOGGER.info("Total recall: {}", calculateRecall(totalRecommendationsRequested, totalRecommendationsMade));
    LOGGER.info("Top1 precision: {}", calculatePrecision(totalRecommendationsMade, totalTop1Recommendations));
    LOGGER.info("Top3 precision: {}", calculatePrecision(totalRecommendationsMade, totalTop3Recommendations));
    LOGGER.info("Top10 precision: {}", calculatePrecision(totalRecommendationsMade, totalTop10Recommendations));

    LOGGER.info("Recommendations requested within extension methods: {}", totalExtensionContextRecommendationsRequested);
    LOGGER.info("Recommendations made within extension methods: {}", totalExtensionContextRecommendationsMade);
    LOGGER.info("Recall within extension methods: {}", calculateRecall(totalExtensionContextRecommendationsRequested, totalExtensionContextRecommendationsMade));
    LOGGER.info("Precision within extension methods: {}", calculatePrecision(totalExtensionContextRecommendationsMade, totalTop3ExtensionContextRecommendations));

    LOGGER.info("Recommendations requested NONE within extension methods: {}", totalNoneExtensionContextRecommendationsRequested);
    LOGGER.info("Recommendations made within NONE extension methods: {}", totalNoneExtensionContextRecommendationsMade);
    LOGGER.info("Total recall within NONE extension methods: {}", calculateRecall(totalNoneExtensionContextRecommendationsRequested, totalNoneExtensionContextRecommendationsMade));
    LOGGER.info("Top3 precision within NONE extension methods: {}", calculatePrecision(totalNoneExtensionContextRecommendationsMade, totalTop3NoneExtensionContextRecommendations));
  }

  private void updateStatistics(CompletionEvent completionEvent, RecommendationResult recommendationResult) {
    updateTotalRecommendationsRequested(recommendationResult);
    totalRecommendationTimeInMs += recommendationResult.getRecommendationTimeInMs();

    List<String> recommendedMethods = recommendationResult.getRecommendedMethods();
    String selectedMethodName = ((IMethodName) completionEvent.getLastSelectedProposal().getName()).getFullName();
    if (recommendedMethods.isEmpty()) {
      return;
    }

    boolean isTop1 = false;
    boolean isTop3 = false;
    boolean isTop10 = false;
    for (int i = 0; i < recommendedMethods.size(); i++) {
      if (recommendedMethods.get(i).equals(selectedMethodName)) {
        if (i == 0) {
          isTop1 = true;
          isTop3 = true;
          isTop10 = true;
        }
        else if (i < 3) {
          isTop3 = true;
          isTop10 = true;
        }
        else if (i < 10) {
          isTop10 = true;
        }
        break;
      }
    }

    updateRecommendationsMade(recommendationResult);
    updateSuccessfulRecommendations(recommendationResult, isTop1, isTop3, isTop10);
  }

  private void updateTotalRecommendationsRequested(RecommendationResult recommendationResult) {
    totalRecommendationsRequested++;
    if (recommendationResult.isOccurredWithinExtensionMethod()) {
      totalExtensionContextRecommendationsRequested++;
    }
    else {
      totalNoneExtensionContextRecommendationsRequested++;
    }
  }

  private void updateRecommendationsMade(RecommendationResult recommendationResult) {
    totalRecommendationsMade++;
    if (recommendationResult.isOccurredWithinExtensionMethod()) {
      totalExtensionContextRecommendationsMade++;
    }
    else {
      totalNoneExtensionContextRecommendationsMade++;
    }
  }

  private void updateSuccessfulRecommendations(RecommendationResult recommendationResult, boolean isTop1, boolean isTop3, boolean isTop10) {
    if (isTop1) {
      totalTop1Recommendations++;
    }
    if (isTop3) {
      totalTop3Recommendations++;
      if (recommendationResult.isOccurredWithinExtensionMethod()) {
        totalTop3ExtensionContextRecommendations++;
      }
      else {
        totalTop3NoneExtensionContextRecommendations++;
      }
    }
    if (isTop10) {
      totalTop10Recommendations++;
    }
  }

  private double calculateRecall(int requestedRecommendations, int recommendationsMade) {
    if (requestedRecommendations <= 0) {
      return 1;
    }
    if (recommendationsMade > requestedRecommendations) {
      return 1;
    }

    return (double) recommendationsMade / (double) requestedRecommendations;
  }

  private double calculatePrecision(int recommendationsMade, int relevantRecommendations) {
    if (recommendationsMade <= 0) {
      return 1;
    }
    if (relevantRecommendations > recommendationsMade) {
      return 1;
    }

    return (double) relevantRecommendations / (double) recommendationsMade;
  }
}
