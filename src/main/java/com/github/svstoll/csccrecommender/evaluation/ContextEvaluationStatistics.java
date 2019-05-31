package com.github.svstoll.csccrecommender.evaluation;

import com.github.svstoll.csccrecommender.utility.StatisticsUtility;

public class ContextEvaluationStatistics {

  private final int totalCases;
  private final double precision;
  private final double recall;

  public ContextEvaluationStatistics(int totalCases, double precision, double recall) {
    this.totalCases = totalCases;
    this.precision = precision;
    this.recall = recall;
  }

  public int getTotalCases() {
    return totalCases;
  }

  public double getPrecision() {
    return precision;
  }

  public double getRecall() {
    return recall;
  }

  public double calculateFMeasure() {
    return StatisticsUtility.calculateFMeasure(precision, recall);
  }
}
