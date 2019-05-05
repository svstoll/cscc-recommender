package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.ase.csccrecommender.utility.SstUtility.isValidToken;

public class IndexingLineContextVisitor extends LineContextVisitor {

  private final List<Document> cachedDocuments = new ArrayList<>();

  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
    if (!isValidToken(methodName) || ! isValidToken(invocationType)) {
      return;
    }

    SimHashBuilder simHashBuilder = new SimHashBuilder();

    String overallContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getOverallContextTokens(), " ");
    simHashBuilder.addStringFeature(overallContextTokens);
    long overallContextSimHash = simHashBuilder.computeResult();

    simHashBuilder.reset();

    String lineContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getLineContextTokens(), "");
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
