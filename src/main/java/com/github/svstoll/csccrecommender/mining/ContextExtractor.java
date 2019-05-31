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

package com.github.svstoll.csccrecommender.mining;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import com.github.svstoll.csccrecommender.properties.ConfigProperties;
import com.github.svstoll.csccrecommender.utility.FileUtility;
import com.github.svstoll.csccrecommender.utility.StringUtility;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ContextExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextExtractor.class);

  private final String miningDirectory;

  @Inject
  protected ContextExtractor(@Named(ConfigProperties.CONTEXTS_DIRECTORY_PROPERTY) String miningDirectory) {
    this.miningDirectory = miningDirectory;
  }

  public void processAllContexts(ContextExtractionConsumer contextExtractionConsumer) {
    if (StringUtility.isNullOrEmpty(miningDirectory)) {
      return;
    }

    for (String zipFilePath : FileUtility.findAllZipFilePaths(miningDirectory)) {
      LOGGER.info("Extracting contexts from '{}'.", zipFilePath);
      long start = System.currentTimeMillis();
      List<Context> contexts = extractContextsFromZipFile(zipFilePath);
      long end = System.currentTimeMillis();
      LOGGER.info("Extraction took {} ms.", end - start);

      contextExtractionConsumer.consume(contexts);
    }
  }

  private List<Context> extractContextsFromZipFile(String zipFilePath) {
    LinkedList<Context> contexts = Lists.newLinkedList();
    try (IReadingArchive readingArchive = new ReadingArchive(new File(zipFilePath))) {
      while (readingArchive.hasNext()) {
        contexts.add(readingArchive.getNext(Context.class));
      }
    } catch (Exception e) {
      LOGGER.error("Failed to extract context from ZIP File '{}'.", zipFilePath, e);
    }
    return contexts;
  }
}
