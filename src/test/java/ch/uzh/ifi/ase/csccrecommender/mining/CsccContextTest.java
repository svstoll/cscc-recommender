package ch.uzh.ifi.ase.csccrecommender.mining;

import cc.kave.commons.model.naming.impl.v0.codeelements.MethodName;
import cc.kave.commons.model.ssts.impl.statements.BreakStatement;
import cc.kave.commons.model.ssts.impl.statements.ContinueStatement;
import cc.kave.commons.model.ssts.impl.statements.Assignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsccContextTest {

  private CsccContext csccContext;
  private LineContext expectedLineContext = new LineContext(csccContext);

  @BeforeEach
  void setUp() {
    IndexingLineContextVisitor indexingLineContextVisitor = mock(IndexingLineContextVisitor.class);
    csccContext = new CsccContext(indexingLineContextVisitor);


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
  public void addLineContext_LineAdded_Successfully(){
    expectedLineContext.addToken("switch");
    assertEquals(expectedLineContext.getTokens(), csccContext.getLineContextTokens());
  }

  @Test
  public void getOverallContextTokens_OverallContextIsAdded_Correctly(){
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