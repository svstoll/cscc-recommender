package com.github.svstoll.csccrecommender.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodInvocationIndexTest {

  private Path tempIndexDirectory;
  private MethodInvocationIndex sut;
  private Document testDocument;

  @BeforeEach
  public void setup() throws IOException {
    tempIndexDirectory = Files.createTempDirectory("temp-index");
    sut = new MethodInvocationIndex(tempIndexDirectory.toString());
    testDocument = new MethodInvocationDocumentBuilder()
        .withType("Test")
        .withMethodName("test()")
        .withOverallContext("overall context including line context")
        .withLineContext("line context")
        .withLineContextSimHash(0)
        .withOverallContextSimHash(0)
        .createDocument();
  }

  @AfterEach
  public void tearDown() throws IOException {
    IOUtils.rm(tempIndexDirectory);
  }

  @Test
  void clearIndex_givenNonEmptyIndex_shouldDeleteAllDocuments() throws IOException {
    // Given:
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
    try (Directory indexDirectory = FSDirectory.open(tempIndexDirectory);
         IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
      indexWriter.addDocument(testDocument);
      indexWriter.commit();
    }

    // When:
    sut.clearIndex();

    // Then:
    try (Directory indexDirectory = FSDirectory.open(tempIndexDirectory);
         IndexReader indexReader = DirectoryReader.open(indexDirectory);
         CheckIndex checkIndex = new CheckIndex(indexDirectory)) {
      CheckIndex.Status status = checkIndex.checkIndex();
      assertTrue(status.clean);
      assertEquals(0, indexReader.getDocCount(MethodInvocationDocumentBuilder.METHOD_NAME_FIELD));
    }
  }

  @Test
  void clearIndex_givenEmptyDirectory_shouldCreateCleanEmptyIndex() throws IOException {
    // When:
    sut.clearIndex();

    // Then:
    try (Directory indexDirectory = FSDirectory.open(tempIndexDirectory);
         CheckIndex checkIndex = new CheckIndex(indexDirectory)) {
      CheckIndex.Status status = checkIndex.checkIndex();
      assertTrue(status.clean);
    }
  }

  @Test
  void indexDocuments_givenOneDocument_shouldAddDocumentToIndex() throws IOException {
    // When:
    sut.indexDocuments(Collections.singletonList(testDocument));

    // Then:
    try (Directory indexDirectory = FSDirectory.open(tempIndexDirectory);
         IndexReader indexReader = DirectoryReader.open(indexDirectory)) {
      MatchAllDocsQuery matchAllDocsQuery = new MatchAllDocsQuery();
      IndexSearcher searcher = new IndexSearcher(indexReader);
      TopDocs topDocs = searcher.search(matchAllDocsQuery, 2);

      assertEquals(1, topDocs.totalHits.value);
      assertEquals(
          testDocument.get(MethodInvocationDocumentBuilder.METHOD_NAME_FIELD),
          searcher.doc(topDocs.scoreDocs[0].doc).get(MethodInvocationDocumentBuilder.METHOD_NAME_FIELD));
    }
  }

  @Test
  void searchMethodInvocationDocuments_givenNoneMatchingTypeAndNoMatchingToken_shouldReturnEmptyList() {
    // Given:
    sut.indexDocuments(Collections.singletonList(testDocument));

    // When:
    List<Document> documents = sut.searchMethodInvocationDocuments("NotMatching", Arrays.asList("none", "included"));

    // Then:
    assertTrue(documents.isEmpty());
  }

  @Test
  void searchMethodInvocationDocuments_givenNoneMatchingTypeAndOneMatchingToken_shouldReturnEmptyList() {
    // Given:
    sut.indexDocuments(Collections.singletonList(testDocument));

    // When:
    List<Document> documents = sut.searchMethodInvocationDocuments("NotMatching", Arrays.asList("overall", "notMatching"));

    // Then:
    assertTrue(documents.isEmpty());
  }

  @Test
  void searchMethodInvocationDocuments_givenMatchingTypeAndNoMatchingToken_shouldReturnEmptyList() {
    // Given:
    sut.indexDocuments(Collections.singletonList(testDocument));

    // When:
    List<Document> documents = sut.searchMethodInvocationDocuments("Test", Arrays.asList("notMatching", "nope"));

    // Then:
    assertTrue(documents.isEmpty());
  }

  @Test
  void searchMethodInvocationDocuments_givenMatchingTypeAndOneMatchingToken_shouldReturnDocument() {
    // Given:
    sut.indexDocuments(Collections.singletonList(testDocument));

    // When:
    List<Document> documents = sut.searchMethodInvocationDocuments("Test", Arrays.asList("overall", "notMatching"));

    // Then:
    assertEquals(1, documents.size());
  }
}