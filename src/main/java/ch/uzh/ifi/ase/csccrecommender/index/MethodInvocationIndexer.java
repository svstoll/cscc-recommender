package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.mining.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContextVisitor;
import ch.uzh.ifi.ase.csccrecommender.mining.IndexingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInvocationIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationIndexer.class);

  private final MethodInvocationIndex methodInvocationIndex;
  private final ContextExtractor contextExtractor;

  @Inject
  protected MethodInvocationIndexer(MethodInvocationIndex methodInvocationIndex,
                                    ContextExtractor contextExtractor) {
    this.methodInvocationIndex = methodInvocationIndex;
    this.contextExtractor = contextExtractor;
  }

  public void indexAllAvailableContexts(boolean resetIndex) {
    LOGGER.info("Start indexing.");
    if (resetIndex) {
      methodInvocationIndex.clearIndex();
    }

    contextExtractor.processAllContexts(contexts -> {
      long start = System.currentTimeMillis();
      IndexingLineContextVisitor indexingLineContextVisitor = new IndexingLineContextVisitor();
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(indexingLineContextVisitor));
      }
      long end = System.currentTimeMillis();
      LOGGER.info("SST traversals with document creation took {} ms.", end - start);

      methodInvocationIndex.indexDocuments(indexingLineContextVisitor.getCachedDocuments());
    });

    LOGGER.info("Indexing finished.");
  }
}
