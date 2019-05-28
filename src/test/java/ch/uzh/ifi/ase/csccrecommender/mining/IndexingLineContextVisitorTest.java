package ch.uzh.ifi.ase.csccrecommender.mining;

import org.junit.jupiter.api.BeforeEach;
import com.github.tomtung.jsimhash.Util;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public class IndexingLineContextVisitorTest {

  private List<String> lineContext = new LinkedList<>();
  private List<String> overallContext = new LinkedList<>();
  private IndexingLineContextVisitor indexingLineContextVisitor;
  private String method;
  private String type;
  private String overallContextText;
  CsccContext csccContext;

  @BeforeEach
  public void setUp() {

    overallContextText = "Code Completion helps developers learn APIs and\n" +
        "frees them from remembering every detail. In this paper, we\n" +
        "describe a novel technique called CSCC";

    method = "method1";
    type = "ch.uzh.ifi.ase.csccrecommender.something";
    indexingLineContextVisitor = new IndexingLineContextVisitor();
    lineContext.add("technique");
    lineContext.add("called");
    lineContext.add("CSCC");
    String[] overallSplited = overallContextText.split(" ");
    overallContext.addAll(Arrays.asList(overallSplited));

    csccContext = mock(CsccContext.class);
    when(csccContext.getLineContextTokens()).thenReturn(lineContext);
    when(csccContext.getOverallContextTokens()).thenReturn(overallContext);
    indexingLineContextVisitor.handleMethodInvocation(method, type, csccContext);
  }


  @Test
  public void handleMethodInvocation_MethodName_IsStored() {
    assertEquals(method,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("methodName")
            .stringValue());
  }


  @Test
  public void handleMethodInvocation_Type_IsStored() {
    assertEquals(type,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("type").stringValue());
  }


  @Test
  public void handleMethodInvocation_ContextSimHash_IsCorrectAndStored() {
    //Context Simhash
    String expectedOverallSimhash1 = "1011101010111010001111011101111010011101111011011101011001011100";
    String overallContextSimHashString1 = Util.simHashToString(indexingLineContextVisitor.
        getCachedDocuments().get(0).getField("overallContextSimHash").numericValue().longValue());
    assertEquals(expectedOverallSimhash1, overallContextSimHashString1);
  }

  @Test
  public void handleMethodInvocation_ContextSimHash_IsNotCorrectAndNotStored() {
    //Context Simhash
    String expectedOverallSimhash1 = "1011101010111010001111011101111010011101111011011101011001011111";
    String overallContextSimHashString1 = Util.simHashToString(indexingLineContextVisitor.
        getCachedDocuments().get(0).getField("overallContextSimHash").numericValue().longValue());
    assertNotEquals(expectedOverallSimhash1, overallContextSimHashString1);
  }


  @Test
  public void handleMethodInvocation_LineSimHash_IsCorrectAndStored() {
    //Line simhash
    String expectedLineSimhash1 = "0101101001101110000011110100111010101001101000110111101101010111";
    String lineContextSimHashString1 = Util.simHashToString(indexingLineContextVisitor.
        getCachedDocuments().get(0).getField("lineContextSimHash").numericValue().longValue());
    assertEquals(expectedLineSimhash1, lineContextSimHashString1);
  }


  @Test
  public void handleMethodInvocation_LineSimHash_IsNotCorrectAndNotStored() {
    //Line simhash
    String expectedLineSimhash1 = "0101101001101110000011110100111010101001101000110111101101010100";
    String lineContextSimHashString1 = Util.simHashToString(indexingLineContextVisitor.
        getCachedDocuments().get(0).getField("lineContextSimHash").numericValue().longValue());
    assertNotEquals(expectedLineSimhash1, lineContextSimHashString1);
  }


  @Test
  public void handleMethodInvocation_OverallContext_IsStored() {
    assertEquals(overallContextText,
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("overallContext")
            .stringValue());
  }

  @Test
  public void handleMethodInvocation_LineContext_IsStored() {
    assertEquals("technique called CSCC",
        indexingLineContextVisitor.getCachedDocuments().get(0).getField("lineContext")
            .stringValue());
  }
}
