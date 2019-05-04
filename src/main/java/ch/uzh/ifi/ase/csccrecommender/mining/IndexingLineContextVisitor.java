package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodCallDocumentBuilder;
import ch.uzh.ifi.ase.csccrecommender.index.MethodCallIndex;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import com.google.inject.Inject;
import org.apache.lucene.document.Document;

import static ch.uzh.ifi.ase.csccrecommender.utility.SstUtility.isValidToken;

public class IndexingLineContextVisitor extends LineContextVisitor {

  private final MethodCallIndex methodCallIndex;

  @Inject
  protected IndexingLineContextVisitor(MethodCallIndex methodCallIndex) {
    this.methodCallIndex = methodCallIndex;
  }

  @Override
  protected void performMethodInvocationAction(String methodCall, String invocationType, CsccContext csccContext) {
    if (!isValidToken(methodCall) || ! isValidToken(invocationType)) {
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

    Document methodCallDocument = new MethodCallDocumentBuilder()
        .withMethodCall(methodCall)
        .withType(invocationType)
        .withOverallContext(overallContextTokens)
        .withLineContext(lineContextTokens)
        .withOverallContextSimHash(overallContextSimHash)
        .withLineContextSimHash(lineContextSimHash)
        .createDocument();
    methodCallIndex.addDocumentToCache(methodCallDocument);
  }
}
