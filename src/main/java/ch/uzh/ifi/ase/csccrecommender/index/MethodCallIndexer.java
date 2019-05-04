package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.mining.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.IndexingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MethodCallIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodCallIndexer.class);

  private final MethodCallIndex methodCallIndex;
  private final ContextExtractor contextExtractor;
  private final IndexingLineContextVisitor indexingLineContextVisitor;

  @Inject
  protected MethodCallIndexer(MethodCallIndex methodCallIndex,
                              ContextExtractor contextExtractor,
                              IndexingLineContextVisitor indexingLineContextVisitor) {
    this.methodCallIndex = methodCallIndex;
    this.contextExtractor = contextExtractor;
    this.indexingLineContextVisitor = indexingLineContextVisitor;
  }

  public void indexData(boolean resetIndex) {
    LOGGER.info("Start indexing.");
    if (resetIndex) {
      methodCallIndex.clearIndex();
    }
    // TODO: Implement batch processing.
    List<Context> contexts = contextExtractor.readAllContexts();
    for (Context context : contexts) {
      context.getSST().accept(new CsccContextVisitor(), new CsccContext(indexingLineContextVisitor));
      methodCallIndex.indexCachedDocuments();
    }
  }
}
