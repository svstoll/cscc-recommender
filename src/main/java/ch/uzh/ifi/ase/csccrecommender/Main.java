package ch.uzh.ifi.ase.csccrecommender;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.mining.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.IndexCreatorVisitor;
import ch.uzh.ifi.ase.csccrecommender.index.MethodCallDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodCallIndex;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    LOGGER.info("Recommender launched!");
    Injector injector = Guice.createInjector(new ProductionModule());

    // TODO: Refactor index creation.
    MethodCallIndex index = injector.getInstance(MethodCallIndex.class);
    index.clearIndex();

    CsccContext csccContext = injector.getInstance(CsccContext.class);
    ContextExtractor contextExtractor = injector.getInstance(ContextExtractor.class);
    List<Context> contexts = contextExtractor.readAllContexts();
    contexts.forEach(context -> context.getSST().accept(new IndexCreatorVisitor(), csccContext));

    index.indexDocuments();

    // TODO: Example index search. Remove again.
    List<String> tokens = new ArrayList<>();
    tokens.add("string");
    index.retrieveMethodCalls("string", tokens)
        .forEach(document -> LOGGER.info(document.get(MethodCallDocumentBuilder.METHOD_CALL_FIELD)));
  }
}
