package ch.uzh.ifi.ase.csccrecommender;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.ase.csccrecommender.index.ContextExtractor;
import ch.uzh.ifi.ase.csccrecommender.index.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.index.IndexCreatorVisitor;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

  public static void main(String[] args) {
    // Initialize Guice
    Guice.createInjector(new ProductionModule());

    Logger logger = LoggerFactory.getLogger(Main.class);
    logger.info("Recommender launched!");

    List<Context> contexts = ContextExtractor.extractContextsFromZipFile(args[0]);
    contexts.forEach(context -> context.getSST().accept(new IndexCreatorVisitor(), new CsccContext()));
  }
}
