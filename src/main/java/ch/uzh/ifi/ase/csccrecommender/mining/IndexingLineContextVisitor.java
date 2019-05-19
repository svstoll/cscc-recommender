package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.ase.csccrecommender.utility.SstUtility.isValidToken;

public class IndexingLineContextVisitor extends LineContextVisitor {

  public static final Logger LOGGER = LoggerFactory.getLogger(IndexingLineContextVisitor.class);
  private final List<Document> cachedDocuments = new ArrayList<>();

  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
    if (!isValidToken(methodName) || ! isValidToken(invocationType)) {
      return;
    }

    LOGGER.debug("Indexing method invocation.\n" +
        "Method name: {}\n" +
        "Invocation type: {}\n" +
        "Overall context tokens: {}\n" +
        "Line context tokens: {}",
        methodName, invocationType, csccContext.getOverallContextTokens(),
        csccContext.getLineContextTokens());

    SimHashBuilder simHashBuilder = new SimHashBuilder();

    String overallContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getOverallContextTokens(), " ");
    simHashBuilder.addStringFeature(overallContextTokens);
    long overallContextSimHash = simHashBuilder.computeResult();

    simHashBuilder.reset();

    String lineContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getLineContextTokens(), " ");
    simHashBuilder.addStringFeature(lineContextTokens);
    long lineContextSimHash = simHashBuilder.computeResult();

    Document methodCallDocument = new MethodInvocationDocumentBuilder()
        .withMethodName(methodName)
        .withType(invocationType)
        .withOverallContext(overallContextTokens)
        .withLineContext(lineContextTokens)
        .withOverallContextSimHash(overallContextSimHash)
        .withLineContextSimHash(lineContextSimHash)
        .createDocument();
    cachedDocuments.add(methodCallDocument);
  }

  public List<Document> getCachedDocuments() {
    return cachedDocuments;
  }
}
