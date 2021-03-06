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

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.completionevents.TerminationState;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.utils.io.ReadingArchive;
import com.github.svstoll.csccrecommender.properties.ConfigProperties;
import com.github.svstoll.csccrecommender.utility.FileUtility;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompletionEventExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CompletionEventExtractor.class);

  private final String eventsDirectory;

  @Inject
  protected CompletionEventExtractor(@Named(ConfigProperties.EVENTS_DIRECTORY_PROPERTY) String eventsDirectory) {
    this.eventsDirectory = eventsDirectory;
  }

  public void processAllCompletionEvents(CompletionEventExtractionConsumer consumer) {
    List<String> userZips = FileUtility.findAllZipFilePaths(eventsDirectory);

    for (String zipFilePath : userZips) {
      LOGGER.info("Extracting events from '{}'.", zipFilePath);
      long start = System.currentTimeMillis();
      List<CompletionEvent> completionEvents = new ArrayList<>();
      try (ReadingArchive readingArchive = new ReadingArchive(new File(zipFilePath))) {
        while (readingArchive.hasNext()) {
          IIDEEvent event = readingArchive.getNext(IIDEEvent.class);
          identifyCompletionEvent(event, completionEvents);
        }
      }
      long end = System.currentTimeMillis();
      LOGGER.info("Extraction took {} ms.", end - start);

      consumer.consume(completionEvents);
    }
  }

  private void identifyCompletionEvent(IIDEEvent event, List<CompletionEvent> completionEvents) {
    if (event instanceof CompletionEvent) {
      CompletionEvent completionEvent = (CompletionEvent) event;
      if (completionEvent.getLastSelectedProposal() != null
          && completionEvent.terminatedState == TerminationState.Applied
          && completionEvent.getLastSelectedProposal().getName() instanceof IMethodName) {
        completionEvents.add(completionEvent);
      }
    }
  }
}
