package ch.uzh.ifi.ase.csccrecommender.properties;

import java.util.Properties;

public class ConfigProperties extends Properties {

  public static final String FILE_NAME = "config.properties";

  public static final String CONTEXTS_DIRECTORY_PROPERTY = "contextsDirectory";
  private static final String CONTEXTS_DIRECTORY_DEFAULT = "C:/dev/cscc-recommender/contexts/";

  public static final String INDEX_DIRECTORY_PROPERTY = "indexDirectory";
  private static final String INDEX_DIRECTORY_DEFAULT = "C:/dev/cscc-recommender/lucene/";

  public static final String EVENTS_DIRECTORY_PROPERTY = "eventsDirectory";
  private static final String EVENTS_DIRECTORY_DEFAULT = "C:/dev/cscc-recommender/events/";

  public ConfigProperties() {
    super();
    this.setProperty(CONTEXTS_DIRECTORY_PROPERTY, CONTEXTS_DIRECTORY_DEFAULT);
    this.setProperty(INDEX_DIRECTORY_PROPERTY, INDEX_DIRECTORY_DEFAULT);
    this.setProperty(EVENTS_DIRECTORY_PROPERTY, EVENTS_DIRECTORY_DEFAULT);
  }
}