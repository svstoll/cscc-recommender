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