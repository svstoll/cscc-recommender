package ch.uzh.ifi.ase.csccrecommender.evaluation;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.LineContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.recommender.DocumentComparison;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;

import cc.kave.commons.model.ssts.blocks.IForLoop;
import cc.kave.commons.model.ssts.expressions.loopheader.ILoopHeaderBlockExpression;
import cc.kave.commons.model.ssts.statements.IAssignment;
import cc.kave.commons.model.ssts.statements.IBreakStatement;
import cc.kave.commons.model.ssts.statements.IContinueStatement;
import cc.kave.commons.model.ssts.statements.IEventSubscriptionStatement;
import cc.kave.commons.model.ssts.statements.IExpressionStatement;
import cc.kave.commons.model.ssts.statements.IGotoStatement;
import cc.kave.commons.model.ssts.statements.ILabelledStatement;
import cc.kave.commons.model.ssts.statements.IReturnStatement;
import cc.kave.commons.model.ssts.statements.IThrowStatement;
import cc.kave.commons.model.ssts.statements.IVariableDeclaration;
import cc.kave.commons.model.ssts.visitor.ISSTNode;

import org.apache.lucene.document.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class R4InvocationLineContextVisitor extends LineContextVisitor {

	  public static final int MAX_RECOMMENDATIONS = 10;
	  private static final int MAX_REFINED_CANDIDATES = 200;

	  private final MethodInvocationIndex methodInvocationIndex;
	  private final List<String> recommendations = new ArrayList<>();
	  
  public R4InvocationLineContextVisitor(MethodInvocationIndex methodInvocationIndex) {
	    this.methodInvocationIndex = methodInvocationIndex;
	  }
  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
      String resultsType = "";
	  ISSTNode node = csccContext.mostRecentLineContext.getNode();
	  if(node instanceof IAssignment)
		  resultsType = "IAssignment";
	  else if(node instanceof IBreakStatement)
		  resultsType = "IBreakStatement";
	  else if(node instanceof IContinueStatement)
		  resultsType = "IContinueStatement";
	  else if(node instanceof IEventSubscriptionStatement)
		  resultsType = "IEventSubscriptionStatement";
	  else if(node instanceof IExpressionStatement)
		  resultsType = "IExpressionStatement";
	  else if(node instanceof IGotoStatement)
		  resultsType = "IGotoStatement";
	  else if(node instanceof ILabelledStatement)
		  resultsType = "ILabelledStatement";
	  else if(node instanceof IReturnStatement)
		  resultsType = "IReturnStatement";
	  else if(node instanceof IThrowStatement)
		  resultsType = "IThrowStatement";
	  else if(node instanceof IVariableDeclaration)
		  resultsType = "IVariableDeclaration";
	  else if(node instanceof ILoopHeaderBlockExpression)
		  resultsType = "ILoopHeaderBlockExpression";
	  else if(node instanceof IForLoop)
		  resultsType = "IForLoop";
	  else {
		  resultsType = "Others";
		  System.out.println(node.toString()  );
	  }

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
    // record in txt
    String correctRecommendation = csccContext.getCurrentMethodName().getName();
//    System.out.println("correct: " + correctRecommendation);
//    System.out.println("recommendations: " + recommendations.toString());
    String isRecommendationCorrectFor1 = "0";
    String isRecommendationCorrectFor3 = "0";
    String isRecommendationCorrectFor10 = "0";
    Double recallFor1 = recommendations.size() / 1.0;
    Double recallFor3 = recommendations.size() / 3.0;
    Double recallFor10 = recommendations.size() / 10.0;
    int i = 0;
    for(String recommendation : recommendations) {
    	if(recommendation.equalsIgnoreCase(correctRecommendation)) {
    		isRecommendationCorrectFor10 = "1";
    		if(i < 1) {
    			isRecommendationCorrectFor1 = "1";
    			isRecommendationCorrectFor3 = "1";
    		}
    		else if(i < 3)
    			isRecommendationCorrectFor3 = "1";
    		
    	}
    	i++;
    }
    PrintWriter pw;
	try {
		// 1
		File directory = new File("./tmp/1/"+resultsType);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		pw = new PrintWriter(new FileWriter("./tmp/1/"+resultsType+"/out.txt",true));
		pw.println(isRecommendationCorrectFor1+","+recallFor1.toString());
	    pw.close();
	    // 3
	    directory = new File("./tmp/3/"+resultsType);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		pw = new PrintWriter(new FileWriter("./tmp/3/"+resultsType+"/out.txt",true));
		pw.println(isRecommendationCorrectFor3+","+recallFor3.toString());
	    pw.close();
	    // 10
	    directory = new File("./tmp/10/"+resultsType);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		pw = new PrintWriter(new FileWriter("./tmp/10/"+resultsType+"/out.txt",true));
		pw.println(isRecommendationCorrectFor10+","+recallFor10.toString());
	    pw.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
    
    return;
    
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
