package ch.uzh.ifi.ase.csccrecommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndexer;
import ch.uzh.ifi.ase.csccrecommender.mining.CompletionEventExtractor;
import ch.uzh.ifi.ase.csccrecommender.recommender.CsccRecommender;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ProductionModule());
    MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
    CompletionEventExtractor completionEventExtractor = injector.getInstance(CompletionEventExtractor.class);
    CsccRecommender csccRecommender = injector.getInstance(CsccRecommender.class);
    LOGGER.info("CSCC Recommender launched.");

    methodInvocationIndexer.indexAllAvailableContexts(true);

    completionEventExtractor.processAllCompletionEvents(completionEvents -> {
      for (CompletionEvent completionEvent : completionEvents) {
        csccRecommender.recommendMethods(completionEvent);
      }
    });
  }
}
