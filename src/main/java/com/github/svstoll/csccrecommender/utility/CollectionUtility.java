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
