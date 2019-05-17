package ch.uzh.ifi.ase.csccrecommender.evaluation;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
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
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.RecommendingLineContextVisitor;

import com.google.inject.Inject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletionEventRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CompletionEventRecommender.class);

  private final MethodInvocationIndex methodInvocationIndex;

  @Inject
  protected CompletionEventRecommender(MethodInvocationIndex methodInvocationIndex) {
    this.methodInvocationIndex = methodInvocationIndex;
  }

  // TODO: Adapt output -> CSV?
  // TODO: Also consider token information (i.e. do not recommend if method does not contain completion token??
  // TODO: Identify identical completion events for evaluation --> same context, same selection, same type
  public void recommendMethods(CompletionEvent completionEvent) {
	  CsccContextVisitor csccContextVisitor = new CsccContextVisitor();
	    RecommendingLineContextVisitor recommendingLineContextVisitor = new RecommendingLineContextVisitor(methodInvocationIndex);
	    CsccContext csccContext = new CsccContext(recommendingLineContextVisitor);
	    completionEvent.getContext().getSST().accept(csccContextVisitor, csccContext);
	    
	    LOGGER.info("Actually selected proposal: {}", ((IMethodName) completionEvent.getLastSelectedProposal().getName()).getFullName());
	    LOGGER.info("CSCC Recommendations: {}", recommendingLineContextVisitor.getRecommendations());
	    
	    IMethodName methodn = (IMethodName) completionEvent.getLastSelectedProposal().getName();
	    String correctRecommendation = methodn.getFullName();
	    
	    List<String> recommendations = recommendingLineContextVisitor.getRecommendations();
	    
	    // get parent type
	    String resultsType = "";
	    if(csccContext.mostRecentLineContext == null)
	    	resultsType = "Others";
	    else {
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
			  }
	    }
		  
	  // record in txt
//		    System.out.println("correct: " + correctRecommendation);
//		    System.out.println("recommendations: " + recommendations.toString());
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
	    			break;
	    		}
	    		else if(i < 3) {
	    			isRecommendationCorrectFor3 = "1";
	        		break;
	    		}
	    		
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
  }
}
