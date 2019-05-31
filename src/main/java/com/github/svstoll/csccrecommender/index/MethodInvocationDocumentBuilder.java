/*
 *  Copyright 2019 Sven Stoll, Dingguang Jin, Tran Phan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.svstoll.csccrecommender.index;

import org.apache.lucene.document.*;

public class MethodInvocationDocumentBuilder {

  public static final String TYPE_FIELD = "type";
  public static final String METHOD_NAME_FIELD = "methodName";
  public static final String OVERALL_CONTEXT_FIELD = "overallContext";
  public static final String LINE_CONTEXT_FIELD = "lineContext";
  public static final String OVERALL_CONTEXT_SIM_HASH_FIELD = "overallContextSimHash";
  public static final String LINE_CONTEXT_SIM_HASH_FIELD = "lineContextSimHash";

  private String methodName;
  private String type;
  private String overallContext;
  private String lineContext;
  private long overallContextSimHash;
  private long lineContextSimHash;

  public MethodInvocationDocumentBuilder withMethodName(String methodName) {
    this.methodName = methodName;
    return this;
  }

  public MethodInvocationDocumentBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public MethodInvocationDocumentBuilder withOverallContext(String overallContext) {
    this.overallContext = overallContext;
    return this;
  }

  public MethodInvocationDocumentBuilder withLineContext(String lineContext) {
    this.lineContext = lineContext;
    return this;
  }

  public MethodInvocationDocumentBuilder withOverallContextSimHash(long overallContextSimHash) {
    this.overallContextSimHash = overallContextSimHash;
    return this;
  }

  public MethodInvocationDocumentBuilder withLineContextSimHash(long lineContextSimHash) {
    this.lineContextSimHash = lineContextSimHash;
    return this;
  }

  public Document createDocument() {
    Document document = new Document();
    document.add(new StringField(METHOD_NAME_FIELD, methodName, Field.Store.YES));
    document.add(new StringField(TYPE_FIELD, type, Field.Store.YES));
    document.add(new StoredField(OVERALL_CONTEXT_SIM_HASH_FIELD, overallContextSimHash));
    document.add(new StoredField(LINE_CONTEXT_SIM_HASH_FIELD, lineContextSimHash));
    document.add(new TextField(OVERALL_CONTEXT_FIELD, overallContext, Field.Store.YES));
    document.add(new TextField(LINE_CONTEXT_FIELD, lineContext, Field.Store.YES));
    return document;
  }
}
