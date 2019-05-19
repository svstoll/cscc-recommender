package ch.uzh.ifi.ase.csccrecommender.recommender;

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
