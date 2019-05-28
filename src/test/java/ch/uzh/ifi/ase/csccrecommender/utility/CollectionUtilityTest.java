package ch.uzh.ifi.ase.csccrecommender.utility;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilityTest {

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
  void removeDuplicates_givenNull_shouldReturnEmptyList() {
    assertTrue(CollectionUtility.removeDuplicates(null).isEmpty());
  }

  @Test
  void removeDuplicates_givenListWithDuplicates_shouldListWithoutDuplicates() {
    List<String> result = CollectionUtility.removeDuplicates(Arrays.asList("duplicate", "duplicate"));
    assertEquals(1, result.size());
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
}