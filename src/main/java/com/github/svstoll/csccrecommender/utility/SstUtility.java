package com.github.svstoll.csccrecommender.utility;

import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.commons.model.naming.types.ITypeParameterName;

public class SstUtility {

  public static final String UNKNOWN_MARKER_SHORT = "?";
  public static final String UNKNOWN_MARKER_LONG = "???";

  private SstUtility() {
  }

  public static boolean isValidToken(String token) {
    return token != null && !UNKNOWN_MARKER_SHORT.equals(token) && !UNKNOWN_MARKER_LONG.equals(token);
  }

  public static String resolveTypeNameToken(ITypeName typeName) {
    StringBuilder result = new StringBuilder(typeName.getName());
    if (CollectionUtility.isNullOrEmpty(typeName.getTypeParameters())) {
      return result.toString();
    }

    result.append("<");
    boolean isFirst = true;
    for (ITypeParameterName parameterName : typeName.getTypeParameters()) {
      if (!isFirst) {
        result.append(", ");
      }
      isFirst = false;

      if (parameterName.isUnknown()) {
        result.append(UNKNOWN_MARKER_SHORT);
      } else if (parameterName.isBound()) {
        result.append(parameterName.getTypeParameterType());
      } else {
        result.append(parameterName.getTypeParameterShortName());
      }
    }

    result.append(">");
    return result.toString();
  }

  public static boolean isSelfReferenceToken(String token) {
    return "this".equals(token);
  }
}