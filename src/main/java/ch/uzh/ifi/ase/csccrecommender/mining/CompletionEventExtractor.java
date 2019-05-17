package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.utils.io.ReadingArchive;
import ch.uzh.ifi.ase.csccrecommender.properties.ConfigProperties;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// TODO: Make extraction more robust.
public class CompletionEventExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CompletionEventExtractor.class);

  private final String eventsDirectory;

  @Inject
  protected CompletionEventExtractor(@Named(ConfigProperties.EVENTS_DIRECTORY_PROPERTY) String eventsDirectory) {
    this.eventsDirectory = eventsDirectory;
  }

  public void processAllCompletionEvents(CompletionEventExtractionConsumer consumer) {
    List<String> userZips = findAllUserZipFiles();

    for (String zipFilePath : userZips) {
      // TODO: Could be run in multiple threads.
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

  private List<String> findAllUserZipFiles() {
    List<String> zips = Lists.newLinkedList();
    for (File f : FileUtils.listFiles(new File(eventsDirectory), new String[] {"zip"}, true)) {
      zips.add(f.getAbsolutePath());
    }
    return zips;
  }

  private void identifyCompletionEvent(IIDEEvent event, List<CompletionEvent> completionEvents) {
    if (event instanceof CompletionEvent) {
      CompletionEvent completionEvent = (CompletionEvent) event;
      if (completionEvent.getLastSelectedProposal() != null
          && completionEvent.getLastSelectedProposal().getName() instanceof IMethodName) {
        completionEvents.add(completionEvent);
      }
    }
  }
}
