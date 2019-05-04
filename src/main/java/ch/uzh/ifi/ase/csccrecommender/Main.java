package ch.uzh.ifi.ase.csccrecommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import ch.uzh.ifi.ase.csccrecommender.index.MethodCallIndexer;
import ch.uzh.ifi.ase.csccrecommender.mining.CompletionEventExtractor;
import ch.uzh.ifi.ase.csccrecommender.recommender.CsccRecommender;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

  public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ProductionModule());
    MethodCallIndexer methodCallIndexer = injector.getInstance(MethodCallIndexer.class);
    CompletionEventExtractor completionEventExtractor = injector.getInstance(
        CompletionEventExtractor.class);
    CsccRecommender csccRecommender = injector.getInstance(CsccRecommender.class);
    LOGGER.info("CSCC Recommender launched.");

    methodCallIndexer.indexData(true);

    List<CompletionEvent> completionEvents = completionEventExtractor.readAllEvents();
    for (CompletionEvent completionEvent : completionEvents) {
      csccRecommender.recommendMethods(completionEvent);
    }
  }
}
