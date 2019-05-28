package ch.uzh.ifi.ase.csccrecommender.utility;

import cc.kave.commons.model.naming.impl.v0.types.TypeName;
import cc.kave.commons.model.naming.types.ITypeName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SstUtilityTest {

  @Test
  void isValidToken_givenNullToken_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken(null));
  }

  @Test
  void isValidToken_givenValidToken_shouldReturnTrue() {
    assertTrue(SstUtility.isValidToken("test"));
  }

  @Test
  void isValidToken_givenShortUnknownMarker_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken(SstUtility.UNKNOWN_MARKER_SHORT));
  }

  @Test
  void isValidToken_givenLongUnknownMarker_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken(SstUtility.UNKNOWN_MARKER_LONG));
  }

  @Test
  void resolveTypeName_givenTypeNameWithoutParameter_shouldReturnTypeNameToken() {
    ITypeName typeName = new TypeName("T`1,P");
    assertEquals("T", SstUtility.resolveTypeNameToken(typeName));
  }

  @Test
  void resolveTypeName_givenTypeNameWithParameter_shouldReturnTypeNameTokenWithParameters() {
    ITypeName typeName = new TypeName("T`1[[G]],P");
    assertEquals("T<G>", SstUtility.resolveTypeNameToken(typeName));
  }

  @Test
  void resolveTypeName_givenNull_shouldThrowException() {
    Assertions.assertThrows(NullPointerException.class, () -> SstUtility.resolveTypeNameToken(null));
  }

  @Test
  void isSelfReferenceToken_givenSelfReferenceToken_shouldReturnTrue() {
    assertTrue(SstUtility.isSelfReferenceToken("this"));
  }

  @Test
  void isSelfReferenceToken_givenNoneSelfReferenceToken_shouldReturnFalse() {
    assertFalse(SstUtility.isSelfReferenceToken("test"));
  }

  @Test
  void isSelfReferenceToken_givenNull_shouldReturnFalse() {
    assertFalse(SstUtility.isSelfReferenceToken(null));
  }
}