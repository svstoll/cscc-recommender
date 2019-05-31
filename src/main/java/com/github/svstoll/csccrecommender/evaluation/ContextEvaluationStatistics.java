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

import com.github.svstoll.csccrecommender.utility.StatisticsUtility;

public class ContextEvaluationStatistics {

  private int recommendationsRequested = 0;
  private int recommendationsMade = 0;
  private int top1Recommendations = 0;
  private int top3Recommendations = 0;
  private int top10Recommendations = 0;

  public void update(boolean isRecommendationMade, boolean isTop1, boolean isTop3, boolean isTop10) {
    recommendationsRequested++;
    if (!isRecommendationMade) {
      return;
    }
    recommendationsMade++;

    if (isTop1) {
      top1Recommendations++;
    }
    if (isTop3) {
      top3Recommendations++;
    }
    if (isTop10) {
      top10Recommendations++;
    }
  }

  public double calculateRecall() {
    return StatisticsUtility.calculateRecall(recommendationsRequested, recommendationsMade);
  }

  public double calculateTop1Precision() {
    return StatisticsUtility.calculatePrecision(recommendationsMade, top1Recommendations);
  }

  public double calculateTop3Precision() {
    return StatisticsUtility.calculatePrecision(recommendationsMade, top3Recommendations);
  }

  public double calculateTop10Precision() {
    return StatisticsUtility.calculatePrecision(recommendationsMade, top10Recommendations);
  }

  public double calculateTop1FMeasure() {
    return StatisticsUtility.calculateFMeasure(calculateTop1Precision(), calculateRecall());
  }

  public double calculateTop3FMeasure() {
    return StatisticsUtility.calculateFMeasure(calculateTop3Precision(), calculateRecall());
  }

  public double calculateTop10FMeasure() {
    return StatisticsUtility.calculateFMeasure(calculateTop10Precision(), calculateRecall());
  }

  public int getRecommendationsRequested() {
    return recommendationsRequested;
  }

  public int getRecommendationsMade() {
    return recommendationsMade;
  }

  public int getTop1Recommendations() {
    return top1Recommendations;
  }

  public int getTop3Recommendations() {
    return top3Recommendations;
  }

  public int getTop10Recommendations() {
    return top10Recommendations;
  }
}
