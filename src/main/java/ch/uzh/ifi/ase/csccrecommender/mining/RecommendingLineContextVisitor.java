package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.recommender.CandidateDocumentComparator;
import ch.uzh.ifi.ase.csccrecommender.recommender.RecommendationResult;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class RecommendingLineContextVisitor extends LineContextVisitor {

  private static final int MAX_RECOMMENDATIONS = 10;
  private static final int MAX_REFINED_CANDIDATES = 200;

  private final MethodInvocationIndex methodInvocationIndex;
  private final List<RecommendationResult> recommendationResults = new ArrayList<>();

  public RecommendingLineContextVisitor(MethodInvocationIndex methodInvocationIndex) {
    this.methodInvocationIndex = methodInvocationIndex;
  }

  @Override
  protected void handleCompletionExpression(String invocationType, CsccContext csccContext) {
    long startTime = System.currentTimeMillis();

    List<String> overallContextTokens = csccContext.getOverallContextTokens();
    List<String> lineContextTokens = csccContext.getLineContextTokens();
    String overallContextConcatenated = CollectionUtility.concatenateStrings(overallContextTokens, " ");
    String lineContextConcatenated = CollectionUtility.concatenateStrings(lineContextTokens, " ");

    List<Document> documents = methodInvocationIndex.searchMethodInvocationDocuments(invocationType, overallContextTokens);

    SimHashBuilder simHashBuilder = new SimHashBuilder();
    simHashBuilder.addStringFeature(overallContextConcatenated);
    long overallContextSimHash = simHashBuilder.computeResult();

    simHashBuilder.reset();

    simHashBuilder.addStringFeature(lineContextConcatenated);
    long lineContextSimHash = simHashBuilder.computeResult();

    List<CandidateDocumentComparator> comparisons = new ArrayList<>();
    for (Document document : documents) {
      CandidateDocumentComparator candidateDocumentComparator = new CandidateDocumentComparator(document,
          overallContextSimHash, lineContextSimHash);
      comparisons.add(candidateDocumentComparator);
    }

    List<String> recommendedMethods = rankRecommendations(comparisons, overallContextConcatenated, lineContextConcatenated);

    long endTime = System.currentTimeMillis();

    RecommendationResult recommendationResult = new RecommendationResult.RecommendationResultBuilder()
        .withRecommendedMethods(recommendedMethods)
        .withOccurredWithinExtensionMethod(csccContext.isCurrentlyWithinExtensionMethod())
        .withRecommendationTimeInMs(endTime - startTime)
        .createRecommendationResult();
    recommendationResults.add(recommendationResult);
  }

  private List<String> rankRecommendations(List<CandidateDocumentComparator> comparisons, String overallContext, String lineContext) {
    List<String> recommendedMethods = new ArrayList<>();
    comparisons.sort(Comparator.comparingLong(CandidateDocumentComparator::getHammingDistanceForComparison));
    int refinedToIndex = comparisons.size() > MAX_REFINED_CANDIDATES ?
        MAX_REFINED_CANDIDATES :
        comparisons.size();
    List<CandidateDocumentComparator> refinedCandidates = comparisons.subList(0, refinedToIndex);

    refinedCandidates.sort(Comparator.comparingDouble((CandidateDocumentComparator comparison) ->
        comparison.compareOverallContexts(overallContext))
        .thenComparingDouble(comparison -> comparison.compareLineContexts(lineContext)));

    int k = 0;
    HashSet<String> includedMethods = new HashSet<>();
    for (CandidateDocumentComparator comparison : refinedCandidates) {
      if (k >= MAX_RECOMMENDATIONS) {
        break;
      }

      String recommendation = comparison.getCandidateDocument().get(MethodInvocationDocumentBuilder.METHOD_NAME_FIELD);
      if (!includedMethods.contains(recommendation)) {
        recommendedMethods.add(recommendation);
        includedMethods.add(recommendation);
        k++;
      }
    }
    return recommendedMethods;
  }

  public List<RecommendationResult> getRecommendationResults() {
    return recommendationResults;
  }
}
