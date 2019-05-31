/*
 *  Copyright 2019 Sven Stoll, Dingguang Jin, Tran Phan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
