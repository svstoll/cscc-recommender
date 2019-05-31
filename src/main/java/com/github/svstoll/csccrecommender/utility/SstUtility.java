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
