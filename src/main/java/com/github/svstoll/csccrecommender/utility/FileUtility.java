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

package com.github.svstoll.csccrecommender.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtility {

  private FileUtility() {
  }

  public static List<String> findAllZipFilePaths(String directoryPath) {
    if (StringUtility.isNullOrEmpty(directoryPath) || !Paths.get(directoryPath).toFile().exists()) {
      return Collections.emptyList();
    }

    return FileUtils.listFiles(new File(directoryPath), new String[]{"zip"}, true)
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
  }
}
