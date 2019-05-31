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

package com.github.svstoll.csccrecommender.recommender;

import java.util.List;

public class RecommendationResult {

  private final List<String> recommendedMethods;
  private final boolean occurredWithinExtensionMethod;
  private final long recommendationTimeInMs;

  private RecommendationResult(List<String> recommendedMethods,
                               boolean occurredWithinExtensionMethod,
                               long recommendationTimeInMs) {
    this.recommendedMethods = recommendedMethods;
    this.occurredWithinExtensionMethod = occurredWithinExtensionMethod;
    this.recommendationTimeInMs = recommendationTimeInMs;
  }

  public List<String> getRecommendedMethods() {
    return recommendedMethods;
  }

  public boolean isOccurredWithinExtensionMethod() {
    return occurredWithinExtensionMethod;
  }

  public long getRecommendationTimeInMs() {
    return recommendationTimeInMs;
  }

  public static class RecommendationResultBuilder {

    private List<String> recommendedMethods;
    private boolean occurredWithinExtensionMethod;
    private long recommendationTimeInMs;

    public RecommendationResultBuilder withRecommendedMethods(List<String> recommendedMethods) {
      this.recommendedMethods = recommendedMethods;
      return this;
    }

    public RecommendationResultBuilder withOccurredWithinExtensionMethod(boolean occurredWithinExtensionMethod) {
      this.occurredWithinExtensionMethod = occurredWithinExtensionMethod;
      return this;
    }

    public RecommendationResultBuilder withRecommendationTimeInMs(long recommendationTimeInMs) {
      this.recommendationTimeInMs = recommendationTimeInMs;
      return this;
    }

    public RecommendationResult createRecommendationResult() {
      return new RecommendationResult(recommendedMethods, occurredWithinExtensionMethod, recommendationTimeInMs);
    }
  }
}
