package com.github.svstoll.csccrecommender.recommender;

import com.github.svstoll.csccrecommender.index.MethodInvocationDocumentBuilder;
import com.github.tomtung.jsimhash.Util;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.lucene.document.Document;

public class CandidateDocumentComparator {

    private static final int LINE_CONTEXT_SWITCH_THRESHOLD = 25;

    private final Document candidateDocument;
    private Double overallContextLcsDistance = null;
    private Double lineContextLevenshteinDistance = null;
    private final long hammingDistanceForComparison;

    public CandidateDocumentComparator(Document candidateDocument, long overallContextSimHashForProposal, long lineContextSimHashForProposal) {
        this.candidateDocument = candidateDocument;
        long overallContextSimHash = (long) candidateDocument.getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD).numericValue();
        long lineContextSimHash = (long) candidateDocument.getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD).numericValue();
        long overallContextHammingDistance = Util.hammingDistance(overallContextSimHash, overallContextSimHashForProposal);
        long lineContextHammingDistance = Util.hammingDistance(lineContextSimHash, lineContextSimHashForProposal);
        // Use overall hamming distance unless hamming distance of line context exceeds predefined threshold.
        if (lineContextHammingDistance > LINE_CONTEXT_SWITCH_THRESHOLD) {
            this.hammingDistanceForComparison = lineContextHammingDistance;
        } else {
            this.hammingDistanceForComparison = overallContextHammingDistance;
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

    public Document getCandidateDocument() {
        return candidateDocument;
    }

    public long getHammingDistanceForComparison() {
        return hammingDistanceForComparison;
    }

    /**
     * Compares the overall context of query and candidate using LCS (Longest Common Subsequence)
     * 0 means documents' overall contexts are completely different,
     * 1 means documents' overall contexts are identical
     */
    private double getNormalizedLcs(String queryOverallContext) {
        LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();
        String candidateOverallContext = candidateDocument.get(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD);
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
        String candidateLineContext = candidateDocument.get(MethodInvocationDocumentBuilder.LINE_CONTEXT_FIELD);
        int maxLength = Math.max(candidateLineContext.length(), queryLineContext.length());
        double lev = levenshteinDistance.apply(candidateLineContext, queryLineContext);
        return (1 - (lev / maxLength));
    }
}
