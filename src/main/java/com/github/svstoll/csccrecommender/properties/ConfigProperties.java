package com.github.svstoll.csccrecommender.properties;

import java.util.Properties;

public class ConfigProperties extends Properties {

  public static final String FILE_NAME = "config.properties";

  public static final String CONTEXTS_DIRECTORY_PROPERTY = "contextsDirectory";
  private static final String CONTEXTS_DIRECTORY_DEFAULT = "./data/contexts/";

  public static final String INDEX_DIRECTORY_PROPERTY = "indexDirectory";
  private static final String INDEX_DIRECTORY_DEFAULT = "./data/index/";

  public static final String EVENTS_DIRECTORY_PROPERTY = "eventsDirectory";
  private static final String EVENTS_DIRECTORY_DEFAULT = "./data/events/";

  public static final String RESULTS_DIRECTORY_PROPERTY = "resultsDirectory";
  private static final String RESULTS_DIRECTORY_DEFAULT = "./data/results/";

  public ConfigProperties() {
    super();
    this.setProperty(CONTEXTS_DIRECTORY_PROPERTY, CONTEXTS_DIRECTORY_DEFAULT);
    this.setProperty(INDEX_DIRECTORY_PROPERTY, INDEX_DIRECTORY_DEFAULT);
    this.setProperty(EVENTS_DIRECTORY_PROPERTY, EVENTS_DIRECTORY_DEFAULT);
    this.setProperty(RESULTS_DIRECTORY_PROPERTY, RESULTS_DIRECTORY_DEFAULT);
  }
}