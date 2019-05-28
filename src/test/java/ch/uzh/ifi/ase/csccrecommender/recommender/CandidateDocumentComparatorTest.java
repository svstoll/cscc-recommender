package ch.uzh.ifi.ase.csccrecommender.recommender;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationDocumentBuilder;
import org.apache.lucene.document.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateDocumentComparatorTest {

  CandidateDocumentComparator docCompare1;
  Double calculatedOverallContextLcsDistance = null;
  Double calculatedLineContextLevensteihnDistance = null;

  @BeforeEach
  void setUp() {
    Document document1 = mock(Document.class);
    LongPoint a = new LongPoint("a", 712391253);
    LongPoint b = new LongPoint("b", 522593414);
    when(document1.getField(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_SIM_HASH_FIELD)).thenReturn(a);
    when(document1.getField(MethodInvocationDocumentBuilder.LINE_CONTEXT_SIM_HASH_FIELD)).thenReturn(b);
    when(document1.get(MethodInvocationDocumentBuilder.OVERALL_CONTEXT_FIELD)).
        thenReturn("Lorem ipsum dolor sit adipiscing elit");
    when(document1.get(MethodInvocationDocumentBuilder.LINE_CONTEXT_FIELD)).
        thenReturn("ipsum dolor sit");
    docCompare1 = new CandidateDocumentComparator(document1, 712391353, 522593404);
  }

  @Test
  void compareOverallContexts() {
    //double testOverallContextLcsDistance = docCompare1.compareOverallContexts("Loren ipsum");
    calculatedOverallContextLcsDistance = docCompare1.compareOverallContexts("Loren ipsum");
    assertEquals(calculatedOverallContextLcsDistance, docCompare1.compareOverallContexts("Loren ipsum"));
  }

  @Test
  void compareLineContexts() {
    calculatedLineContextLevensteihnDistance = docCompare1.compareLineContexts("Loren ipsum");
    assertEquals(calculatedLineContextLevensteihnDistance, docCompare1.compareLineContexts("Loren ipsum"));
  }

  @Test
  void getCandidateDocument() {
    Document temp = docCompare1.getCandidateDocument();
  }

  @Test
  void getHammingDistanceForComparison() {
    double temp = docCompare1.getHammingDistanceForComparison();
  }

}