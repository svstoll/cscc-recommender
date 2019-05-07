package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.recommender.DocumentComparison;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class RecommendingLineContextVisitor extends LineContextVisitor {

  private static final int MAX_RECOMMENDATIONS = 3;
  private static final int MAX_REFINED_CANDIDATES = 200;

  private final MethodInvocationIndex methodInvocationIndex;
  private final List<String> recommendations = new ArrayList<>();

  public RecommendingLineContextVisitor(MethodInvocationIndex methodInvocationIndex) {
    this.methodInvocationIndex = methodInvocationIndex;
  }

  @Override
  protected void handleCompletionExpression(String invocationType, CsccContext csccContext) {
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

    List<DocumentComparison> comparisons = new ArrayList<>();
    for (Document document : documents) {
      DocumentComparison documentComparison = new DocumentComparison(document, overallContextSimHash, lineContextSimHash);
      comparisons.add(documentComparison);
    }

    rankRecommendations(comparisons, overallContextConcatenated, lineContextConcatenated);
  }

  private void rankRecommendations(List<DocumentComparison> comparisons, String overallContext, String lineContext) {
    recommendations.clear();
    // TODO: Include Hamming distance from line context in sorting logic (unclear from paper description).
    comparisons.sort(Comparator.comparingLong(DocumentComparison::getOverallContextHammingDistance));

    int refinedToIndex = comparisons.size() > MAX_REFINED_CANDIDATES ?
        MAX_REFINED_CANDIDATES :
        comparisons.size();
    List<DocumentComparison> refinedCandidates = comparisons.subList(0, refinedToIndex);

    refinedCandidates.sort(Comparator.comparingInt((DocumentComparison comparison) ->
        comparison.compareOverallContexts(overallContext))
        .thenComparingInt(comparison -> comparison.compareLineContexts(lineContext)));

    int k = 0;
    HashSet<String> includedMethods = new HashSet<>();
    for (DocumentComparison comparison : refinedCandidates) {
      if (k >= MAX_RECOMMENDATIONS) {
        break;
      }
      String recommendation = comparison.getDocument().get(MethodInvocationDocumentBuilder.METHOD_NAME_FIELD);
      if (!includedMethods.contains(recommendation)) {
        recommendations.add(recommendation);
        includedMethods.add(recommendation);
        k++;
      }
    }
  }

  public List<String> getRecommendations() {
    return recommendations;
  }
}
