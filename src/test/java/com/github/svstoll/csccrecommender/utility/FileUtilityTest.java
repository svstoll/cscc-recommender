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

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUtilityTest {

  private Path tempIndexDirectoryPath;

  @BeforeEach
  public void setup() throws IOException {
    tempIndexDirectoryPath = Files.createTempDirectory("temp-test-files");
  }

  @After
  public void cleanup() throws IOException {
    Files.delete(tempIndexDirectoryPath);
  }

  @Test
  public void findAllZipFilePaths_givenNonExistingDirectory_shouldReturnEmptyList() {
    // Given:
    Path nonExistingDirectoryPath = Paths.get(tempIndexDirectoryPath.toString(), "null");

    // When:
    List<String> result = FileUtility.findAllZipFilePaths(nonExistingDirectoryPath.toString());

    // When:
    assertTrue(result.isEmpty());
  }

  @Test
  public void findAllZipFilePaths_givenNoFilesInDirectory_shouldReturnEmptyList() {
    // When:
    List<String> result = FileUtility.findAllZipFilePaths(tempIndexDirectoryPath.toString());

    // When:
    assertTrue(result.isEmpty());
  }

  @Test
  public void findAllZipFilePaths_givenSingleZipFileInRootDirectory_shouldReturnEmptyList() throws IOException {
    // Given:
    Path testFilePath = Paths.get(tempIndexDirectoryPath.toString(), "test.zip");
    Files.createFile(testFilePath);

    // When:
    List<String> result = FileUtility.findAllZipFilePaths(tempIndexDirectoryPath.toString());

    // Then:
    assertEquals(1, result.size());
  }

  @Test
  public void findAllZipFilePaths_givenMultipleDifferentFilesInMultipleSubDirectories_shouldReturnAllZipFiles() throws IOException {
    // Given:
    Path zipFilePath1 = Paths.get(tempIndexDirectoryPath.toString(), "sub_1_1", "test.zip");
    Files.createDirectory(zipFilePath1.getParent());
    Files.createFile(zipFilePath1);

    Path zipFilePath2 = Paths.get(tempIndexDirectoryPath.toString(), "sub_1_1", "sub_2_1", "test.zip");
    Files.createDirectory(zipFilePath2.getParent());
    Files.createFile(zipFilePath2);

    Files.createFile(Paths.get(tempIndexDirectoryPath.toString(), "test.txt"));

    // When:
    List<String> result = FileUtility.findAllZipFilePaths(tempIndexDirectoryPath.toString());

    // Then:
    assertEquals(2, result.size());
    assertEquals(1, result.stream().filter(file -> file.equals(zipFilePath1.toString())).count());
    assertEquals(1, result.stream().filter(file -> file.equals(zipFilePath2.toString())).count());
  }
}
