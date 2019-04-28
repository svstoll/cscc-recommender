package ch.uzh.ifi.ase.csccrecommender.index;

import java.util.List;

public class Invocation {

  private List<String> tokensBeforeInvocation;
  private String type;
  private String methodName;

  public Invocation(List<String> tokensBeforeInvocation, String type, String methodName) {
    this.tokensBeforeInvocation = tokensBeforeInvocation;
    this.type = type;
    this.methodName = methodName;
  }

  public List<String> getTokensBeforeInvocation() {
    return tokensBeforeInvocation;
  }

  public String getType() {
    return type;
  }

  public String getMethodName() {
    return methodName;
  }
}
