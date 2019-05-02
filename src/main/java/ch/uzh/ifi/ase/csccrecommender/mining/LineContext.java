package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.ssts.visitor.ISSTNode;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.ase.csccrecommender.utility.SstUtility.isValidToken;

public class LineContext {

  private final CsccContext overallContext;
  private final List<String> tokens = new ArrayList<>();
  private ISSTNode node;

  public LineContext(CsccContext overallContext) {
    this.overallContext = overallContext;
  }

  public CsccContext getOverallContext() {
    return overallContext;
  }

  public ISSTNode getNode() {
    return node;
  }

  public void setNode(ISSTNode node) {
    this.node = node;
  }

  public List<String> getTokens() {
    return tokens;
  }

  public void addToken(String token) {
    if (isValidToken(token)) {
      tokens.add(token);
    }
  }
}
