package ch.uzh.ifi.ase.csccrecommender;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndexer;
import ch.uzh.ifi.ase.csccrecommender.evaluation.CompletionEventEvaluator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ProductionModule());
    MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
    CompletionEventEvaluator completionEventEvaluator = injector.getInstance(
        CompletionEventEvaluator.class);
    LOGGER.info("CSCC Recommender launched.");

    methodInvocationIndexer.indexAllAvailableContexts(true);
    completionEventEvaluator.executeEvaluation();
  }
}
