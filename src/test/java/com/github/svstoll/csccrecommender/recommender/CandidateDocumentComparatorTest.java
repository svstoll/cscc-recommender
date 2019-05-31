package com.github.svstoll.csccrecommender.recommender;

import com.github.svstoll.csccrecommender.index.MethodInvocationDocumentBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
  void compareOverallContext_LcsDistanceWasAlreadyCalculated_ReturnTheSameLcsDistanceInSecondCall() {
    //Calculate distance first time, before it was null
    calculatedOverallContextLcsDistance = docCompare1.compareOverallContexts("Loren ipsum");
    //Check distance unchanged in second call
    assertEquals(calculatedOverallContextLcsDistance, docCompare1.compareOverallContexts("Loren ipsum"));
  }

  @Test
  void compareLineContexts_LevensteihnDistanceWasAlreadyCalculated_ReturnTheSameLevensteihnDistanceInSecondCall() {
    //Calculate distance first time, before it was null
    calculatedLineContextLevensteihnDistance = docCompare1.compareLineContexts("Loren ipsum");
    //Check distance unchanged in second call
    assertEquals(calculatedLineContextLevensteihnDistance, docCompare1.compareLineContexts("Loren ipsum"));
  }

}