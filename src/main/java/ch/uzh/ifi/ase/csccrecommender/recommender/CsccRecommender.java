package ch.uzh.ifi.ase.csccrecommender.recommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.RecommendingLineContextVisitor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@Singleton
public class CsccRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final MethodInvocationIndex methodInvocationIndex;

  @Inject
  protected CsccRecommender(MethodInvocationIndex methodInvocationIndex) {
    this.methodInvocationIndex = methodInvocationIndex;
  }

  public List<RecommendationResult> recommendMethods(CompletionEvent completionEvent) {
    LOGGER.debug("Recommendation process started. Originally applied selection: {}",
        ((IMethodName) Objects.requireNonNull(completionEvent.getLastSelectedProposal().getName())).getFullName());
    CsccContextVisitor csccContextVisitor = new CsccContextVisitor();
    RecommendingLineContextVisitor recommendingLineContextVisitor = new RecommendingLineContextVisitor(methodInvocationIndex);
    CsccContext csccContext = new CsccContext(recommendingLineContextVisitor);
    completionEvent.getContext().getSST().accept(csccContextVisitor, csccContext);

    return recommendingLineContextVisitor.getRecommendationResults();
  }
}
