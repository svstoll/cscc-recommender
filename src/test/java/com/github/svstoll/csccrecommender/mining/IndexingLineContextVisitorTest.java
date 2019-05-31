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
import com.github.tomtung.jsimhash.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndexingLineContextVisitorTest {

  private List<String> lineContext = new LinkedList<>();
  private List<String> overallContext = new LinkedList<>();
  private IndexingLineContextVisitor indexingLineContextVisitor;
  private String method;
  private String type;
  private String overallContextText;
  private CsccContext csccContext;

  @BeforeEach
  public void setUp() {
    overallContextText = "Code Completion helps developers learn APIs and\n" +
        "frees them from remembering every detail. In this paper, we\n" +
        "describe a novel technique called CSCC";

    method = "testMe";
    type = "ch.uzh.ifi.ase.csccrecommender.Something";
    indexingLineContextVisitor = new IndexingLineContextVisitor();
    lineContext.add("technique");
    lineContext.add("called");
    lineContext.add("CSCC");
    String[] overallSplit = overallContextText.split(" ");
    overallContext.addAll(Arrays.asList(overallSplit));

    csccContext = mock(CsccContext.class);
    when(csccContext.getLineContextTokens()).thenReturn(lineContext);
    when(csccContext.getOverallContextTokens()).thenReturn(overallContext);
    indexingLineContextVisitor.handleMethodInvocation(method, type, csccContext);
  }

  @Test
  public void handleMethodInvocation_MethodName_IsStored() {
    assertEquals(
        method,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("methodName").stringValue());
  }

  @Test
  public void handleMethodInvocation_type_isStored() {
    assertEquals(
        type,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("type").stringValue());
  }

  @Test
  public void handleMethodInvocation_contextSimHash_isCorrectAndStored() {
    String expectedOverallSimhash = "1011101010111010001111011101111010011101111011011101011001011100";
    String actualLineSimHash = Util.simHashToString(indexingLineContextVisitor.getCachedDocuments().get(0)
        .getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD).numericValue().longValue());
    assertEquals(expectedOverallSimhash, actualLineSimHash);
  }

  @Test
  public void handleMethodInvocation_contextSimHash_isNotCorrectAndNotStored() {
    String expectedOverallSimHash = "1011101010111010001111011101111010011101111011011101011001011111";
    String actualLineSimHash = Util.simHashToString(indexingLineContextVisitor.getCachedDocuments().get(0)
        .getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD).numericValue().longValue());
    assertNotEquals(expectedOverallSimHash, actualLineSimHash);
  }

  @Test
  public void handleMethodInvocation_lineSimHash_isCorrectAndStored() {
    String expectedLineSimhash = "0101101001101110000011110100111010101001101000110111101101010111";
    String actualLineSimHash = Util.simHashToString(indexingLineContextVisitor.getCachedDocuments().get(0)
        .getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD).numericValue().longValue());
    assertEquals(expectedLineSimhash, actualLineSimHash);
  }

  @Test
  public void handleMethodInvocation_lineSimHash_isNotCorrectAndNotStored() {
    String expectedLineSimHash = "0101101001101110000011110100111010101001101000110111101101010100";
    String actualLineSimHash = Util.simHashToString(indexingLineContextVisitor.getCachedDocuments().get(0)
        .getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD).numericValue().longValue());
    assertNotEquals(expectedLineSimHash, actualLineSimHash);
  }

  @Test
  public void handleMethodInvocation_overallContext_isStored() {
    assertEquals(
        overallContextText,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD).stringValue());
  }

  @Test
  public void handleMethodInvocation_lineContext_isStored() {
    assertEquals(
        "technique called CSCC",
        indexingLineContextVisitor.getCachedDocuments().get(0).getField(
            MethodInvocationDocumentBuilder.LINE_CONTEXT_FIELD).stringValue());
  }
}
