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

import cc.kave.commons.model.naming.impl.v0.codeelements.MethodName;
import cc.kave.commons.model.ssts.impl.statements.Assignment;
import cc.kave.commons.model.ssts.impl.statements.BreakStatement;
import cc.kave.commons.model.ssts.impl.statements.ContinueStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CsccContextTest {

  private CsccContext csccContext;
  private LineContext lineContext;

  @BeforeEach
  void setUp() {
    IndexingLineContextVisitor lineContextVisitorMock = mock(IndexingLineContextVisitor.class);
    csccContext = new CsccContext(lineContextVisitorMock);
    lineContext = new LineContext(csccContext);

    BreakStatement testNode1 = new BreakStatement();
    csccContext.addLineContext(testNode1, "break");

    ContinueStatement testNode2 = new ContinueStatement();
    csccContext.addLineContext(testNode2, "continue");

    Assignment testNode3 = new Assignment();
    csccContext.addLineContext(testNode3, "equal");

    ContinueStatement testNode4 = new ContinueStatement();
    csccContext.addLineContext(testNode4, "else");

    BreakStatement testNode5 = new BreakStatement();
    csccContext.addLineContext(testNode5, "switch");
  }

  @Test
  public void addLineContext_lineAdded_successfully(){
    lineContext.addToken("switch");
    assertEquals(lineContext.getTokens(), csccContext.getLineContextTokens());
  }

  @Test
  public void getOverallContextTokens_overallContextIsAdded_correctly(){
    List<String> overallContext = new ArrayList<>();
    overallContext.add("break");
    overallContext.add("continue");
    overallContext.add("equal");
    overallContext.add("else");
    overallContext.add("switch");
    assertEquals(overallContext, csccContext.getOverallContextTokens());
  }

  @Test
  void setCurrentMethodName() {
    MethodName testName = new MethodName("testMethod");
    csccContext.setCurrentMethodName(testName);
    assertEquals(testName, csccContext.getCurrentMethodName());
  }

  @Test
  void isCurrentlyWithinExtensionMethod_notWithin() {
    assertFalse(csccContext.isCurrentlyWithinExtensionMethod());
  }

  @Test
  void isCurrentlyWithinExtensionMethod_Within() {
    csccContext.setCurrentlyWithinExtensionMethod(true);
    assertTrue(csccContext.isCurrentlyWithinExtensionMethod());
  }
}
