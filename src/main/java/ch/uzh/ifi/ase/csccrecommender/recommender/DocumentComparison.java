package ch.uzh.ifi.ase.csccrecommender.recommender;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import com.github.tomtung.jsimhash.Util;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.lucene.document.Document;

public class DocumentComparison {

    private static final int LINE_CONTEXT_SWITCH_THRESHOLD = 25;  // 25 is picked randomly
    private final Document document;
    private final long overallContextHammingDistance;
    private final long lineContextHammingDistance;
    private Double overallContextLcsDistance = null;
    private Double lineContextLevenshteinDistance = null;
    private final long chosenHammingDistance;

    public DocumentComparison(Document document, long overallContextSimHashForProposal, long lineContextSimHashForProposal) {
        this.document = document;
        long overallContextSimHash = (long) document.getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD).numericValue();
        long lineContextSimHash = (long) document.getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD).numericValue();
        this.overallContextHammingDistance = Util.hammingDistance(overallContextSimHash, overallContextSimHashForProposal);
        this.lineContextHammingDistance = Util.hammingDistance(lineContextSimHash, lineContextSimHashForProposal);
        //Use overall hamming distance unless hamming distance of line context exceeds predefined threshold.
        if (this.lineContextHammingDistance > LINE_CONTEXT_SWITCH_THRESHOLD) {
            this.chosenHammingDistance = this.lineContextHammingDistance;
        } else {
            this.chosenHammingDistance = this.overallContextHammingDistance;
        }
    }

    public double compareOverallContexts(String overallContextForProposal) {
        if (overallContextLcsDistance != null) {
            return overallContextLcsDistance;
        }
        overallContextLcsDistance = getNormalizedLcs(overallContextForProposal);
        return overallContextLcsDistance;
    }

    public double compareLineContexts(String lineContextForProposal) {
        if (lineContextLevenshteinDistance != null) {
            return lineContextLevenshteinDistance;
        }
        lineContextLevenshteinDistance = getNormalizedLevenshteinDistance(lineContextForProposal);
        return lineContextLevenshteinDistance;
    }

    public Document getDocument() {
        return document;
    }

    public long getChosenHammingDistance() {
        return chosenHammingDistance;
    }

    public double getLineContextLevenshteinDistance() {
        if (lineContextLevenshteinDistance != null) {
            return lineContextLevenshteinDistance;
        } else {
            return 0;
        }
    }

    public double getOverallContextLcsDistance() {
        if (lineContextLevenshteinDistance != null) {
            return lineContextLevenshteinDistance;
        } else {
            return 0;
        }
    }

    /**
     * Compares the overall context of query and candidate using LCS (Longest Common Subsequence)
     * 0 means documents' overall contexts are completely different,
     * 1 means documents' overall contexts are identical
     */
    private double getNormalizedLcs(String queryOverallContext) {
        LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();
        String candidateOverallContext = document.get(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD);
        int maxLength = Math.max(candidateOverallContext.length(), queryOverallContext.length());
        double lcs = longestCommonSubsequence.apply(candidateOverallContext, queryOverallContext);
        return lcs / maxLength;
    }

    /**
     * Compares the line context of query and candidate using Levenshtein Distance
     * 0 means documents' line contexts are completely different,
     * 1 means documents' line contexts are identical
     */
    private double getNormalizedLevenshteinDistance(String queryLineContext) {
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
        String candidateLineContext = document.get(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD);
        int maxLength = Math.max(candidateLineContext.length(), queryLineContext.length());
        double lev = levenshteinDistance.apply(candidateLineContext, queryLineContext);
        return (1 - (lev / maxLength));
    }
}
