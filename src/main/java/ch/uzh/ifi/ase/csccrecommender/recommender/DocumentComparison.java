package ch.uzh.ifi.ase.csccrecommender.recommender;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import com.github.tomtung.jsimhash.Util;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.lucene.document.Document;

public class DocumentComparison {

  private final Document document;
  private final long overallContextHammingDistance;
  private final long lineContextHammingDistance;
  private Integer overallContextLcsDistance = null;
  private Integer lineContextLevenshteinDistance = null;

  public DocumentComparison(Document document, long overallContextSimHashForProposal, long lineContextSimHashForProposal) {
    this.document = document;
    long overallContextSimHash = (long) document.getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD).numericValue();
    long lineContextSimHash = (long) document.getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD).numericValue();
    this.overallContextHammingDistance = Util.hammingDistance(overallContextSimHash, overallContextSimHashForProposal);
    this.lineContextHammingDistance = Util.hammingDistance(lineContextSimHash, lineContextSimHashForProposal);
  }

  public int compareOverallContexts(String overallContextForProposal) {
    if (overallContextLcsDistance != null) {
      return overallContextLcsDistance;
    }

    LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();
    overallContextLcsDistance = longestCommonSubsequence.apply(document.get(
        MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD),
        overallContextForProposal);
    return overallContextLcsDistance;
  }

  public int compareLineContexts(String overallContextForProposal) {
    if (lineContextLevenshteinDistance != null) {
      return lineContextLevenshteinDistance;
    }

    LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
    lineContextLevenshteinDistance = levenshteinDistance.apply(
        document.get(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD),
        overallContextForProposal);
    return lineContextLevenshteinDistance;
  }

  public Document getDocument() {
    return document;
  }

  public long getOverallContextHammingDistance() {
    return overallContextHammingDistance;
  }

  public long getLineContextHammingDistance() {
    return lineContextHammingDistance;
  }
}
