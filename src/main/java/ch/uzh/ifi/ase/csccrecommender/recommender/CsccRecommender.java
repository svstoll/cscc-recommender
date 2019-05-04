package ch.uzh.ifi.ase.csccrecommender.recommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.RecommendingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsccRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final RecommendingLineContextVisitor recommendingLineContextVisitor;

  @Inject
  protected CsccRecommender(RecommendingLineContextVisitor recommendingLineContextVisitor) {
    this.recommendingLineContextVisitor = recommendingLineContextVisitor;
  }

  // TODO: Adapt output.
  public void recommendMethods(CompletionEvent completionEvent) {
    completionEvent.getContext().getSST().accept(new CsccContextVisitor(), new CsccContext(recommendingLineContextVisitor));
    LOGGER.info("Actually selected proposal: {}", (completionEvent.getLastSelectedProposal().getName()).getIdentifier());
    LOGGER.info("CSCC Recommendations: {}", recommendingLineContextVisitor.getRecommendations());
  }
}
