package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import ch.uzh.ifi.ase.csccrecommender.utility.StringUtility;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ContextExtractor {

  private static Logger logger = LoggerFactory.getLogger(ContextExtractor.class);

  private ContextExtractor() {
  }

  public static List<Context> extractContextsFromZipFile(String zipFile) {
    LinkedList<Context> contexts = Lists.newLinkedList();
    try (IReadingArchive readingArchive = new ReadingArchive(new File(zipFile))) {
      while (readingArchive.hasNext()) {
        contexts.add(readingArchive.getNext(Context.class));
      }
    } catch (Exception e) {
      logger.error("Failed to extract context from ZIP File '{}'.", zipFile, e);
    }
    return contexts;
  }

  public static List<Context> readAllContexts(String directoryPath) {
    if (StringUtility.isNullOrEmpty(directoryPath)) {
      return Collections.emptyList();
    }

    LinkedList<Context> contexts = Lists.newLinkedList();
    for (String zip : findAllZipFilePaths(directoryPath)) {
      contexts.addAll(extractContextsFromZipFile(zip));
    }
    return contexts;
  }

  private static List<String> findAllZipFilePaths(String directoryPath) {
    if (StringUtility.isNullOrEmpty(directoryPath)) {
      return Collections.emptyList();
    }

    return FileUtils.listFiles(new File(directoryPath), new String[]{"zip"}, true)
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
  }
}
