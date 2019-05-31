package com.github.svstoll.csccrecommender.utility;

public class StatisticsUtility {

  private StatisticsUtility() {
  }

  public static double calculateRecall(int requestedRecommendations, int recommendationsMade) {
    if (requestedRecommendations <= 0) {
      return 1;
    }
    if (recommendationsMade > requestedRecommendations) {
      return 1;
    }

    return (double) recommendationsMade / (double) requestedRecommendations;
  }

  public static double calculatePrecision(int recommendationsMade, int relevantRecommendations) {
    if (recommendationsMade <= 0) {
      return 1;
    }
    if (relevantRecommendations > recommendationsMade) {
      return 1;
    }

    return (double) relevantRecommendations / (double) recommendationsMade;
  }

  public static double calculateFMeasure(double precision, double recall) {
    if (precision == 0 && recall == 0) {
      return 0;
    }
    return 2 * precision * recall / (precision + recall);
  }
}
