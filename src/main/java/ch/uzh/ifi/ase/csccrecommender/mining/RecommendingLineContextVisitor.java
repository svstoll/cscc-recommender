package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodCallDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodCallIndex;
import ch.uzh.ifi.ase.csccrecommender.recommender.DocumentComparison;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import com.google.inject.Inject;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class RecommendingLineContextVisitor extends LineContextVisitor {

  private static final int MAX_RECOMMENDATIONS = 3;
  private static final int MAX_REFINED_CANDIDATES = 200;

  private final MethodCallIndex methodCallIndex;
  private final List<String> recommendations = new ArrayList<>();

  @Inject
  protected RecommendingLineContextVisitor(MethodCallIndex methodCallIndex) {
    this.methodCallIndex = methodCallIndex;
  }

  @Override
  protected void handleCompletionExpression(String invocationType, CsccContext csccContext) {
    List<String> overallContextTokens = csccContext.getOverallContextTokens();
    List<String> lineContextTokens = csccContext.getLineContextTokens();
    String overallContextConcatenated = CollectionUtility.concatenateStrings(overallContextTokens, " ");
    String lineContextConcatenated = CollectionUtility.concatenateStrings(lineContextTokens, " ");

    List<Document> documents = methodCallIndex.searchMethodCallDocuments(invocationType, overallContextTokens);

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

  private void rankRecommendations(List<DocumentComparison> comparisons, String overallContextConcatenated, String lineContextConcatenated) {
    recommendations.clear();
    // TODO: Include Hamming distance from line context in sorting logic.
    comparisons.sort(Comparator.comparingLong(DocumentComparison::getOverallContextHammingDistance));

    int refinedToIndex = comparisons.size() > MAX_REFINED_CANDIDATES ?
        MAX_REFINED_CANDIDATES :
        comparisons.size();
    List<DocumentComparison> refinedCandidates = comparisons.subList(0, refinedToIndex);

    refinedCandidates.sort(Comparator.comparingInt((DocumentComparison comparison) ->
        comparison.compareOverallContexts(overallContextConcatenated))
        .thenComparingInt(comparison -> comparison.compareLineContexts(lineContextConcatenated)));

    int k = 0;
    HashSet<String> includedMethods = new HashSet<>();
    for (DocumentComparison comparison : refinedCandidates) {
      if (k >= MAX_RECOMMENDATIONS) {
        break;
      }
      String recommendation = comparison.getDocument().get(MethodCallDocumentBuilder.METHOD_CALL_FIELD);
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
