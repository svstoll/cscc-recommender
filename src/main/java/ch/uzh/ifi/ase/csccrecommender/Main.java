package ch.uzh.ifi.ase.csccrecommender;

import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  public static void main(String[] args) {
    // Initialize Guice
    Guice.createInjector(new ProductionModule());

    Logger logger = LoggerFactory.getLogger(Main.class);
    logger.info("Recommender launched!");
  }
}
