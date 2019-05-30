package ch.uzh.ifi.ase.csccrecommender.recommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Singleton
public class CsccRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final MethodInvocationIndex methodInvocationIndex;
  private final ContextExtractor contextExtractor;

  @Inject
  protected CsccRecommender(
      MethodInvocationIndex methodInvocationIndex,
      ContextExtractor contextExtractor) {
    this.methodInvocationIndex = methodInvocationIndex;
    this.contextExtractor = contextExtractor;
  }

  public List<RecommendationResult> recommendMethods(CompletionEvent completionEvent) {
    LOGGER.debug("Recommendation process started. Originally applied selection: {}",
        ((IMethodName) Objects.requireNonNull(completionEvent.getLastSelectedProposal().getName())).getFullName());
    CsccContextVisitor csccContextVisitor = new CsccContextVisitor();
    RecommendingLineContextVisitor recommendingLineContextVisitor
        = new RecommendingLineContextVisitor(methodInvocationIndex);
    CsccContext csccContext = new CsccContext(recommendingLineContextVisitor);
    completionEvent.getContext().getSST().accept(csccContextVisitor, csccContext);

    return recommendingLineContextVisitor.getRecommendationResults();
  }

  public void recommendForAllInvocationsInAvailableContexts(String tempEvaluationResultsDirectoryPath) {
    try {
      FileUtils.deleteDirectory(new File(tempEvaluationResultsDirectoryPath));
    }
    catch (IOException e) {
      LOGGER.info("Error while clearing the tmp evaluation results folder.", e);
    }

    contextExtractor.processAllContexts(contexts -> {
      LOGGER.info("Perform recommendations for all method invocations in current contexts.");
      InvocationRecommendationLineContextVisitor invocationRecommendationLineContextVisitor
          = new InvocationRecommendationLineContextVisitor(methodInvocationIndex, tempEvaluationResultsDirectoryPath);
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(invocationRecommendationLineContextVisitor));
      }
    });
  }
}
