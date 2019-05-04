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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompletionEventExtractor {

  private final String eventsDirectory;

  @Inject
  protected CompletionEventExtractor(@Named(ConfigProperties.EVENTS_DIRECTORY_PROPERTY) String eventsDirectory) {
    this.eventsDirectory = eventsDirectory;
  }

  public List<CompletionEvent> readAllEvents() {
    List<String> userZips = findAllUserZipFiles();
    List<CompletionEvent> completionEvents = new ArrayList<>();
    for (String user : userZips) {
      try (ReadingArchive ra = new ReadingArchive(new File(user))) {
        while (ra.hasNext()) {
          IIDEEvent event = ra.getNext(IIDEEvent.class);
          process(event, completionEvents);
        }
      }
    }
    return completionEvents;
  }

  private List<String> findAllUserZipFiles() {
    List<String> zips = Lists.newLinkedList();
    for (File f : FileUtils.listFiles(new File(eventsDirectory), new String[] { "zip" }, true)) {
      zips.add(f.getAbsolutePath());
    }
    return zips;
  }

  private void process(IIDEEvent event, List<CompletionEvent> completionEvents) {
    if (event instanceof CompletionEvent) {
      CompletionEvent completionEvent = (CompletionEvent) event;
      if (completionEvent.getLastSelectedProposal() != null
          && completionEvent.getLastSelectedProposal().getName() instanceof IMethodName) {
        completionEvents.add(completionEvent);
      }
    }
  }
}
