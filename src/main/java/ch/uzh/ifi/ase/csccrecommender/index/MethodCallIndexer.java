package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.mining.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.IndexingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCallIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodCallIndexer.class);

  private final MethodCallIndex methodCallIndex;
  private final ContextExtractor contextExtractor;

  @Inject
  protected MethodCallIndexer(MethodCallIndex methodCallIndex,
                              ContextExtractor contextExtractor) {
    this.methodCallIndex = methodCallIndex;
    this.contextExtractor = contextExtractor;
  }

  public void indexData(boolean resetIndex) {
    LOGGER.info("Start indexing.");
    if (resetIndex) {
      methodCallIndex.clearIndex();
    }

    contextExtractor.processAllContexts(contexts -> {
      long start = System.currentTimeMillis();
      IndexingLineContextVisitor indexingLineContextVisitor = new IndexingLineContextVisitor();
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(indexingLineContextVisitor));
      }
      long end = System.currentTimeMillis();
      LOGGER.info("SST traversals with document creation took {} ms.", end - start);

      methodCallIndex.indexDocuments(indexingLineContextVisitor.getCachedDocuments());
    });

    LOGGER.info("Indexing finished.");
  }
}
