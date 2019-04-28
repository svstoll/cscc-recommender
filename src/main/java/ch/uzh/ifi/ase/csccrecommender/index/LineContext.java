package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.ssts.visitor.ISSTNode;
import cc.kave.commons.utils.ssts.SSTPrintingContext;
import cc.kave.commons.utils.ssts.SSTPrintingVisitor;

import java.util.ArrayList;
import java.util.List;

public class LineContext {

  private ISSTNode node;
  private List<String> tokens = new ArrayList<>();
  private List<Invocation> invocations = new ArrayList<>();

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
    tokens.add(token);
  }

  public void addInvocation(String type, String methodName) {
    List<String> tokensCopy = new ArrayList<>(tokens);
    Invocation invocation = new Invocation(tokensCopy, type, methodName);
    invocations.add(invocation);
  }

  public List<Invocation> getInvocations() {
    return invocations;
  }

  public String printNode() {
    if (node == null) {
      return "";
    }
    SSTPrintingContext printingContext = new SSTPrintingContext();
    node.accept(new SSTPrintingVisitor(), printingContext);
    return printingContext.toString();
  }
}
