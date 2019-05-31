package com.github.svstoll.csccrecommender.mining;

import cc.kave.commons.model.ssts.visitor.ISSTNode;

import java.util.ArrayList;
import java.util.List;

import static com.github.svstoll.csccrecommender.utility.SstUtility.isValidToken;

public class LineContext {

  private final CsccContext csccContext;
  private final List<String> tokens = new ArrayList<>();
  private ISSTNode node;

  public LineContext(CsccContext csccContext) {
    this.csccContext = csccContext;
  }

  public CsccContext getCsccContext() {
    return csccContext;
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
