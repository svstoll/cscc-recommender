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
public class MethodCallIndex {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodCallIndex.class);

  private final String indexDirectoryPath;
  private final List<Document> unindexedDocuments = new ArrayList<>();

  @Inject
  public MethodCallIndex(@Named(ConfigProperties.INDEX_DIRECTORY_PROPERTY) String indexDirectoryPath) {
    this.indexDirectoryPath = indexDirectoryPath;
  }

  public void clearIndex() throws IOException {
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));

    try (IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
      indexWriter.deleteAll();
      indexWriter.commit();
    }
  }

  public void addDocument(Document document) {
    unindexedDocuments.add(document);
  }

  public void indexDocuments() {
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

    try (Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));
         IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {

      for (Document document : unindexedDocuments) {
        indexWriter.addDocument(document);
      }
      unindexedDocuments.clear();
      indexWriter.commit();
    }
    catch (IOException exception) {
      LOGGER.error("Could not index document.", exception);
    }
  }

  public List<Document> retrieveMethodCalls(String type, List<String> tokens) throws IOException {
    Query typeQuery = new TermQuery(new Term(MethodCallDocumentBuilder.TYPE_FIELD, type));
    BooleanQuery.Builder contextQueryBuilder = new BooleanQuery.Builder();
    for (String token : tokens) {
      Query tokenQuery = new TermQuery(new Term(MethodCallDocumentBuilder.OVERALL_CONTEXT_FIELD, token));
      contextQueryBuilder.add(tokenQuery, BooleanClause.Occur.SHOULD);
    }
    Query contextQuery = contextQueryBuilder.build();

    Query query = new BooleanQuery.Builder()
        .add(typeQuery, BooleanClause.Occur.MUST)
        .add(contextQuery, BooleanClause.Occur.MUST)
        .build();

    Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectoryPath));
    IndexReader indexReader = DirectoryReader.open(indexDirectory);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(query, 200);

    List<Document> documents = new ArrayList<>();
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      documents.add(searcher.doc(scoreDoc.doc));
    }
    return documents;
  }
}
