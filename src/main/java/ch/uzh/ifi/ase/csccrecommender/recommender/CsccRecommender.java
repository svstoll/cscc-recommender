package ch.uzh.ifi.ase.csccrecommender.recommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.RecommendingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsccRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final MethodInvocationIndex methodInvocationIndex;

  @Inject
  protected CsccRecommender(MethodInvocationIndex methodInvocationIndex) {
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
  }
}
