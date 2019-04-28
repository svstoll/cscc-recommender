package ch.uzh.ifi.ase.csccrecommender.index;

import cc.kave.commons.model.ssts.visitor.ISSTNode;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class CsccContext {

  Queue<LineContext> lineContexts = new ArrayDeque<>();
  LineContext mostRecentLineContext = null;

  public void addLineContext(ISSTNode node, String... tokens) {
    LineContext lineContext = new LineContext();
    lineContext.setNode(node);
    for (String token : tokens) {
      lineContext.addToken(token);
    }

    if (node != null) {
      lineContext.getNode().accept(new LineContextVisitor(), lineContext);
    }

    if (lineContexts.size() > 4) {
      lineContexts.poll();
    }
    lineContexts.add(lineContext);
    mostRecentLineContext = lineContext;

    if (lineContext.getInvocations().isEmpty()) {
      return;
    }

    // TODO: Here, we can index method invocations.
    printCurrentContext();
  }

  public void printCurrentContext() {
    if (lineContexts.isEmpty() || mostRecentLineContext == null) {
      return;
    }

    Iterator<LineContext> iterator = lineContexts.iterator();
    LineContext currentLineContext = iterator.next();
    StringBuilder invocationContext = new StringBuilder();
    while (iterator.hasNext()) {
      invocationContext.append(currentLineContext.printNode());
      invocationContext.append("\n");
      invocationContext.append("\t=> Tokens: ");
      invocationContext.append(currentLineContext.getTokens());
      invocationContext.append("\n");
      currentLineContext = iterator.next();
    }

    for (Invocation invocation : mostRecentLineContext.getInvocations()) {
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
      System.out.print(invocation.getType());
      System.out.print(".");
      System.out.println(invocation.getMethodName());
      System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
      System.out.println(invocationContext);

      System.out.println(mostRecentLineContext.printNode());
      System.out.println("\t=> Tokens before invocation: " + invocation.getTokensBeforeInvocation());
      System.out.println("=======================================");
      System.out.println();
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
