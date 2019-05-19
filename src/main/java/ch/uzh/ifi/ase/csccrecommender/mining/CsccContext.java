package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.ssts.visitor.ISSTNode;
import ch.uzh.ifi.ase.csccrecommender.utility.CollectionUtility;

import java.util.*;

public class CsccContext {

  public static final String EMPTY_CONTEXT_IDENTIFIER = "!EMPTY!";

  private final Queue<LineContext> lineContexts = new ArrayDeque<>();
  private final LineContextVisitor lineContextVisitor;

  private IMethodName currentMethodName = null;
  private boolean currentlyWithinExtensionMethod = false;
  private LineContext mostRecentLineContext = null;

  public CsccContext(LineContextVisitor lineContextVisitor) {
    this.lineContextVisitor = lineContextVisitor;
  }

  public void addLineContext(ISSTNode node, String... tokens) {
    LineContext lineContext = new LineContext(this);
    lineContext.setNode(node);
    for (String token : tokens) {
      lineContext.addToken(token);
    }

    if (lineContexts.size() > 4) {
      lineContexts.poll();
    }
    lineContexts.add(lineContext);
    mostRecentLineContext = lineContext;

    if (node != null) {
      lineContext.getNode().accept(lineContextVisitor, lineContext);
    }
  }

  public List<String> getOverallContextTokens() {
    List<String> overallContext = new ArrayList<>();
    for (LineContext lineContext : lineContexts) {
      overallContext.addAll(lineContext.getTokens());
    }

    if (overallContext.isEmpty()) {
      // We need to be able to retrieve empty contexts from the index again.
      overallContext.add(EMPTY_CONTEXT_IDENTIFIER);
    }

    return CollectionUtility.removeDuplicates(overallContext);
  }

  public List<String> getLineContextTokens() {
    return CollectionUtility.removeDuplicates(mostRecentLineContext.getTokens());
  }

  public void clear() {
    lineContexts.clear();
    mostRecentLineContext = null;
  }

  public IMethodName getCurrentMethodName() {
    return currentMethodName;
  }

  public void setCurrentMethodName(IMethodName currentMethodName) {
    this.currentMethodName = currentMethodName;
  }

  public boolean isCurrentlyWithinExtensionMethod() {
    return currentlyWithinExtensionMethod;
  }

  public void setCurrentlyWithinExtensionMethod(boolean currentlyWithinExtensionMethod) {
    this.currentlyWithinExtensionMethod = currentlyWithinExtensionMethod;
  }
}
