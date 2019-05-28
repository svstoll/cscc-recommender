package ch.uzh.ifi.ase.csccrecommender.utility;

import cc.kave.commons.model.naming.impl.v0.types.TypeParameterName;
import cc.kave.commons.model.naming.types.ITypeParameterName;
import org.junit.jupiter.api.Test;
import cc.kave.commons.model.naming.impl.v0.types.BaseTypeName;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SstUtilityTest {

  @Test
  void isValidToken_givenNullToken_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken(null));
  }


  @Test
  void isValidToken_givenUNKOWN_MARKER_SHORT_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken("?"));
  }

  @Test
  void isValidToken_givenUNKOWN_MARKER_LONG_shouldReturnFalse() {
    assertFalse(SstUtility.isValidToken("???"));
  }

  @Test
  void resolveTypeName_givenNullTypeParameters_shouldReturnTypeName () {

    BaseTypeName baseTypeName = mock(BaseTypeName.class);
    when(baseTypeName.getTypeParameters()).thenReturn(null);
    when(baseTypeName.getName()).thenReturn("helloworld");
    assertEquals("helloworld", SstUtility.resolveTypeName(baseTypeName));
  }


  @Test
  void resolveTypeName_givenValidTypeParameters_shouldReturnResolvedTypeName () {
    ArrayList<ITypeParameterName> typeParameters = new ArrayList<>();
    TypeParameterName temp1 = new TypeParameterName("public");
    TypeParameterName temp2 = new TypeParameterName("int");
    TypeParameterName temp3 = new TypeParameterName("ArrayList");
    TypeParameterName temp4 = new TypeParameterName("???");

    typeParameters.add(temp1);
    typeParameters.add(temp2);
    typeParameters.add(temp3);
    typeParameters.add(temp4);

    BaseTypeName baseTypeName = mock(BaseTypeName.class);
    when(baseTypeName.getTypeParameters()).thenReturn(typeParameters);
    when(baseTypeName.getName()).thenReturn("helloworld");
    assertEquals("helloworld<public, int, ArrayList, ???>", SstUtility.resolveTypeName(baseTypeName));
  }


  @Test
  void isSelfReferenceToken() {
    String temp = "this";
    assertTrue(SstUtility.isSelfReferenceToken(temp));
  }
}