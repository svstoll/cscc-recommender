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
