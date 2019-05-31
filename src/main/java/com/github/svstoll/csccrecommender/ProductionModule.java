package com.github.svstoll.csccrecommender;

import com.github.svstoll.csccrecommender.properties.ConfigProperties;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProductionModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductionModule.class);

  @Override
  protected void configure() {
    bindConfigProperties();
  }

  protected void bindConfigProperties() {
    try {
      ConfigProperties properties = new ConfigProperties();
      properties.load(getClass().getClassLoader().getResourceAsStream(ConfigProperties.FILE_NAME));
      Names.bindProperties(binder(), properties);
    } catch (IOException e) {
      LOGGER.error("Error loading config properties.", e);
    }
  }
}
