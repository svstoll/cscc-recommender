package ch.uzh.ifi.ase.csccrecommender.index;

import ch.uzh.ifi.ase.csccrecommender.properties.ConfigProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MethodInvocationIndex {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationIndex.class);

  private final String indexDirectoryPath;

  @Inject
  protected MethodInvocationIndex(@Named(ConfigProperties.INDEX_DIRECTORY_PROPERTY) String indexDirectoryPath) {
    this.indexDirectoryPath = indexDirectoryPath;
  }

  public void clearIndex() {
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

    try (Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));
        IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
      indexWriter.deleteAll();
      indexWriter.commit();
      LOGGER.info("Index cleared.");
    }
    catch (IOException exception) {
      LOGGER.error("Error clearing method call index.", exception);
    }
  }

  public void indexDocuments(List<Document> documents) {
    long start = System.currentTimeMillis();

    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

    try (Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));
         IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
      for (Document document : documents) {
        indexWriter.addDocument(document);
      }
      indexWriter.commit();

      long end = System.currentTimeMillis();
      LOGGER.info("Indexing {} documents took {} ms.", documents.size(), end - start);
    }
    catch (IOException exception) {
      LOGGER.error("Error indexing cached documents.", exception);
    }
  }

  public List<Document> searchMethodInvocationDocuments(String type, List<String> tokens) {
    Query typeQuery = new TermQuery(new Term(MethodInvocationDocumentBuilder.TYPE_FIELD, type));
    BooleanQuery.Builder contextQueryBuilder = new BooleanQuery.Builder();
    for (String token : tokens) {
      Query tokenQuery = new TermQuery(new Term(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD, token));
      contextQueryBuilder.add(tokenQuery, BooleanClause.Occur.SHOULD);
    }
    Query contextQuery = contextQueryBuilder.build();

    Query query = new BooleanQuery.Builder()
        .add(typeQuery, BooleanClause.Occur.MUST)
        .add(contextQuery, BooleanClause.Occur.MUST)
        .build();

    List<Document> documents = new ArrayList<>();
    try (Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));
         IndexReader indexReader = DirectoryReader.open(indexDirectory)) {
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 200);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
          documents.add(searcher.doc(scoreDoc.doc));
        }
    }
    catch (IOException exception) {
      LOGGER.error("Error searching index for method call documents.", exception);
    }

    return documents;
  }
}
