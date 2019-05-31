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

package com.github.svstoll.csccrecommender.mining;

import com.github.svstoll.csccrecommender.index.MethodInvocationDocumentBuilder;
import com.github.svstoll.csccrecommender.utility.CollectionUtility;
import com.github.svstoll.csccrecommender.utility.SstUtility;
import com.github.tomtung.jsimhash.SimHashBuilder;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IndexingLineContextVisitor extends LineContextVisitor {

  public static final Logger LOGGER = LoggerFactory.getLogger(IndexingLineContextVisitor.class);
  private final List<Document> cachedDocuments = new ArrayList<>();

  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
    if (!SstUtility.isValidToken(methodName) || ! SstUtility.isValidToken(invocationType)) {
      return;
    }

    LOGGER.debug("Indexing method invocation.\n" +
        "Method name: {}\n" +
        "Invocation type: {}\n" +
        "Overall context tokens: {}\n" +
        "Line context tokens: {}",
        methodName, invocationType, csccContext.getOverallContextTokens(),
        csccContext.getLineContextTokens());

    SimHashBuilder simHashBuilder = new SimHashBuilder();

    String overallContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getOverallContextTokens(), " ");
    simHashBuilder.addStringFeature(overallContextTokens);
    long overallContextSimHash = simHashBuilder.computeResult();

    simHashBuilder.reset();

    String lineContextTokens = CollectionUtility.concatenateStrings(
        csccContext.getLineContextTokens(), " ");
    simHashBuilder.addStringFeature(lineContextTokens);
    long lineContextSimHash = simHashBuilder.computeResult();

    Document methodCallDocument = new MethodInvocationDocumentBuilder()
        .withMethodName(methodName)
        .withType(invocationType)
        .withOverallContext(overallContextTokens)
        .withLineContext(lineContextTokens)
        .withOverallContextSimHash(overallContextSimHash)
        .withLineContextSimHash(lineContextSimHash)
        .createDocument();
    cachedDocuments.add(methodCallDocument);
  }

  public List<Document> getCachedDocuments() {
    return cachedDocuments;
  }
}
