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

import java.util.*;

public class CollectionUtility {

  private CollectionUtility() {
  }

  public static <T> boolean isNullOrEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> List<T> removeDuplicates(List<T> list) {
    List<T> noneDuplicatesList = new ArrayList<>();
    if (isNullOrEmpty(list)) {
      return noneDuplicatesList;
    }

    Set<T> seenItems = new HashSet<>();
    for (T object : list) {
      if (!seenItems.contains(object)) {
        seenItems.add(object);
        noneDuplicatesList.add(object);
      }
    }
    return noneDuplicatesList;
  }

  public static String concatenateStrings(List<String> strings, String separator) {
    if (isNullOrEmpty(strings)) {
      return "";
    }
    if (separator == null) {
      separator = "";
    }

    StringBuilder stringBuilder = new StringBuilder();
    Iterator<String> iterator = strings.iterator();
    while (iterator.hasNext()) {
      String string = iterator.next();
      stringBuilder.append(string);
      if (iterator.hasNext()) {
        stringBuilder.append(separator);
      }
    }
    return stringBuilder.toString();
  }
}
