package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.ssts.visitor.ISSTNode;
import com.google.inject.Inject;

import java.util.*;

public class CsccContext {

  private final LineContextVisitor lineContextVisitor;
  private final Queue<LineContext> lineContexts = new ArrayDeque<>();

  private LineContext mostRecentLineContext = null;

  @Inject
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

  public String getOverallContextTokens() {
    Set<String> seenTokens = new HashSet<>();
    StringBuilder tokens = new StringBuilder();
    for (LineContext lineContext : lineContexts) {
      appendNoneDuplicateTokens(seenTokens, tokens, lineContext);
    }
    return tokens.toString();
  }

  public String getLineContextTokens() {
    Set<String> seenTokens = new HashSet<>();
    StringBuilder tokens = new StringBuilder();
    appendNoneDuplicateTokens(seenTokens, tokens, mostRecentLineContext);
    return tokens.toString();
  }

  private void appendNoneDuplicateTokens(Set<String> seenTokens, StringBuilder tokens, LineContext lineContext) {
    for (String token : lineContext.getTokens()) {
      if (!seenTokens.contains(token)) {
        seenTokens.add(token);
        tokens.append(token);
        tokens.append(" ");
      }
    }
  }

  public Collection<LineContext> getLineContexts() {
    return lineContexts;
  }

  public void clear() {
    lineContexts.clear();
    mostRecentLineContext = null;
  }
}
