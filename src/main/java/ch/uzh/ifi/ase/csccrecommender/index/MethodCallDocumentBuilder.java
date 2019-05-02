package ch.uzh.ifi.ase.csccrecommender.index;

import org.apache.lucene.document.*;

public class MethodCallDocumentBuilder {

  public static final String TYPE_FIELD = "type";
  public static final String METHOD_CALL_FIELD = "methodCall";
  public static final String OVERALL_CONTEXT_FIELD = "overallContext";
  public static final String LINE_CONTEXT_FIELD = "lineContext";
  public static final String OVERALL_CONTEXT_SIM_HASH_FIELD = "overallContextSimHash";
  public static final String LINE_CONTEXT_SIM_HASH_FIELD = "lineContextSimHash";

  private String methodCall;
  private String type;
  private String overallContext;
  private String lineContext;
  private long overallContextSimHash;
  private long lineContextSimHash;

  public MethodCallDocumentBuilder withMethodCall(String methodCall) {
    this.methodCall = methodCall;
    return this;
  }

  public MethodCallDocumentBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public MethodCallDocumentBuilder withOverallContext(String overallContext) {
    this.overallContext = overallContext;
    return this;
  }

  public MethodCallDocumentBuilder withLineContext(String lineContext) {
    this.lineContext = lineContext;
    return this;
  }

  public MethodCallDocumentBuilder withOverallContextSimHash(long overallContextSimHash) {
    this.overallContextSimHash = overallContextSimHash;
    return this;
  }

  public MethodCallDocumentBuilder withLineContextSimHash(long lineContextSimHash) {
    this.lineContextSimHash = lineContextSimHash;
    return this;
  }

  public Document createDocument() {
    Document document = new Document();
    document.add(new StringField(METHOD_CALL_FIELD, methodCall, Field.Store.YES));
    document.add(new StringField(TYPE_FIELD, type, Field.Store.YES));
    document.add(new StoredField(OVERALL_CONTEXT_SIM_HASH_FIELD, overallContextSimHash));
    document.add(new StoredField(LINE_CONTEXT_SIM_HASH_FIELD, lineContextSimHash));
    document.add(new TextField(OVERALL_CONTEXT_FIELD, overallContext, Field.Store.YES));
    document.add(new TextField(LINE_CONTEXT_FIELD, lineContext, Field.Store.YES));
    return document;
  }
}
