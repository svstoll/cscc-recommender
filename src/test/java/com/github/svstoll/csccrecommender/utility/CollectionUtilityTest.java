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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilityTest {

  @Test
  void isNullOrEmpty_givenNull_shouldReturnTrue() {
    assertTrue(CollectionUtility.isNullOrEmpty(null));
  }

  @Test
  void isNullOrEmpty_givenEmptyCollection_shouldReturnTrue() {
    assertTrue(CollectionUtility.isNullOrEmpty(Collections.emptyList()));
  }

  @Test
  void isNullOrEmpty_givenNoneEmptyCollection_shouldReturnFalse() {
    assertFalse(CollectionUtility.isNullOrEmpty(Collections.singletonList("test")));
  }

  @Test
  void removeDuplicates_givenNull_shouldReturnEmptyCollection() {
    assertTrue(CollectionUtility.removeDuplicates(null).isEmpty());
  }

  @Test
  void removeDuplicates_givenEmptyCollection_shouldReturnEmptyCollection() {
    assertTrue(CollectionUtility.removeDuplicates(Collections.emptyList()).isEmpty());
  }

  @Test
  void removeDuplicates_givenCollectionWithDuplicates_shouldReturnFilteredCollection() {
    ArrayList<Integer> testCollection = new ArrayList<>();
    testCollection.add(1);
    testCollection.add(5);
    testCollection.add(5);
    testCollection.add(10);
    testCollection.add(10);

    ArrayList<Integer> expectedCollection = new ArrayList<>();
    expectedCollection.add(1);
    expectedCollection.add(5);
    expectedCollection.add(10);

    assertEquals(expectedCollection, CollectionUtility.removeDuplicates(testCollection));
  }

  @Test
  void concatenateStrings_givenNull_shouldReturnEmptyString() {
    assertEquals("", CollectionUtility.concatenateStrings(null, "_"));
  }

  @Test
  void concatenateStrings_givenEmptyList_shouldReturnEmptyString() {
    assertEquals("", CollectionUtility.concatenateStrings(Collections.emptyList(), "_"));
  }

  @Test
  void concatenateStrings_givenListWithOneItem_shouldReturnStringWithoutSeparator() {
    assertEquals("a", CollectionUtility.concatenateStrings(Collections.singletonList("a"), "_"));
  }

  @Test
  void concatenateStrings_givenListWithMultipleItems_shouldReturnStringWithSeparatorBetweenItems() {
    assertEquals("a_b", CollectionUtility.concatenateStrings(Arrays.asList("a", "b"), "_"));
  }

  @Test
  void concatenateStrings_givenNullSeparator_shouldReturnStringWithoutSpace() {
    ArrayList<String> strings = new ArrayList<>();
    strings.add("hello");
    strings.add("world");
    assertEquals("helloworld", CollectionUtility.concatenateStrings(strings, null));
  }
}