package ch.uzh.ifi.ase.csccrecommender.properties;

import java.util.Properties;

public class ConfigProperties extends Properties {

  public static final String MINING_DIRECTORY_PROPERTY = "miningDirectory";
  public static final String MINING_DIRECTORY_DEFAULT = "C:/dev/cscc-recommender/mining/";

  public static final String INDEX_DIRECTORY_PROPERTY = "indexDirectory";
  public static final String INDEX_DIRECTORY_DEFAULT = "C:/dev/cscc-recommender/lucene/";

  public ConfigProperties() {
    super();
    this.setProperty(MINING_DIRECTORY_PROPERTY, MINING_DIRECTORY_DEFAULT);
    this.setProperty(INDEX_DIRECTORY_PROPERTY, INDEX_DIRECTORY_DEFAULT);
  }
}
