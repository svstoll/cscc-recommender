package ch.uzh.ifi.ase.csccrecommender.utility;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilityTest {

  @Test
  public void isNullOrEmpty_givenEmptyCollection_shouldReturnTrue() {
    assertTrue(CollectionUtility.isNullOrEmpty(new ArrayList<Integer>()));
  }

  @Test
  public void isNullOrEmpty_givenNonEmptyCollection_shouldReturnFalse() {
    ArrayList<Integer> test_collection = new ArrayList<>();
    test_collection.add(5);
    assertFalse(CollectionUtility.isNullOrEmpty(test_collection));
  }

  @Test
  public void removeDuplicates_givenNonEmptyCollectionWithDuplicates_shouldReturnFilteredCollection() {
    ArrayList<Integer> test_collection = new ArrayList<>();
    test_collection.add(1);
    test_collection.add(5);
    test_collection.add(5);
    test_collection.add(10);
    test_collection.add(10);
    ArrayList<Integer> expected_collection = new ArrayList<>();
    expected_collection.add(1);
    expected_collection.add(5);
    expected_collection.add(10);

    assertEquals(expected_collection, CollectionUtility.removeDuplicates(test_collection));
  }

  @Test
  public void removeDuplicates_givenEmptyCollection_shouldReturnEmptyCollection() {
    ArrayList<Integer> given_collection = new ArrayList<>();
    assertEquals(0, CollectionUtility.removeDuplicates(given_collection).size());
  }

  @Test
  public void removeDuplicates_givenNull_shouldReturnEmptyCollection() {
    assertEquals(0, CollectionUtility.removeDuplicates(null).size());
  }


  @Test
  void concatenateStrings_givenEmptyListOfString_shouldReturnEmptyString() {
    ArrayList<String> test_list = new ArrayList<>();
    assertEquals("", CollectionUtility.concatenateStrings(test_list, " "));
  }


  @Test
  void concatenateStrings_givenNonEmptyListAndValidSeparator_shouldReturnConcatenatedString() {
    ArrayList<String> test_list = new ArrayList<>();
    test_list.add("hello");
    test_list.add("world");
    assertEquals("hello world", CollectionUtility.concatenateStrings(test_list, " "));
  }


  @Test
  void concatenateStrings_givenNullSeparator_shouldReturnStringWithoutSpace() {
    ArrayList<String> test_list = new ArrayList<>();
    test_list.add("hello");
    test_list.add("world");
    assertEquals("helloworld", CollectionUtility.concatenateStrings(test_list, null));
  }
}