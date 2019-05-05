package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import ch.uzh.ifi.ase.csccrecommender.properties.ConfigProperties;
import ch.uzh.ifi.ase.csccrecommender.utility.StringUtility;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Make extraction more robust.
public class ContextExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextExtractor.class);

  private final String miningDirectory;

  @Inject
  protected ContextExtractor(@Named(ConfigProperties.MINING_DIRECTORY_PROPERTY) String miningDirectory) {
    this.miningDirectory = miningDirectory;
  }

  public void processAllContexts(ContextExtractionConsumer contextExtractionConsumer) {
    if (StringUtility.isNullOrEmpty(miningDirectory)) {
      return;
    }

    for (String zipFilePath : findAllZipFilePaths(miningDirectory)) {
      // TODO: Could run extraction multiple threads. Index writing might be the bottleneck, but
      //  maybe we can also leave the indexWriter open to improve performance.
      LOGGER.info("Extracting contexts from '{}'.", zipFilePath);
      long start = System.currentTimeMillis();
      List<Context> contexts = extractContextsFromZipFile(zipFilePath);
      long end = System.currentTimeMillis();
      LOGGER.info("Extraction took {} ms.", end - start);

      contextExtractionConsumer.consume(contexts);
    }
  }

  private List<String> findAllZipFilePaths(String directoryPath) {
    if (StringUtility.isNullOrEmpty(directoryPath)) {
      return Collections.emptyList();
    }

    return FileUtils.listFiles(new File(directoryPath), new String[]{"zip"}, true)
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
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
