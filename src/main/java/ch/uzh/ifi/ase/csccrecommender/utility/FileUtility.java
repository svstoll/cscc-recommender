package ch.uzh.ifi.ase.csccrecommender.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtility {

  private FileUtility() {
  }

  public static List<String> findAllZipFilePaths(String directoryPath) {
    if (StringUtility.isNullOrEmpty(directoryPath)) {
      return Collections.emptyList();
    }

    return FileUtils.listFiles(new File(directoryPath), new String[]{"zip"}, true)
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
  }
}
