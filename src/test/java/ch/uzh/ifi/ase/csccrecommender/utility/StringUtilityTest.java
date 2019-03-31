package ch.uzh.ifi.ase.csccrecommender.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilityTest {

  @Test
  public void isNullOrEmpty_givenNullString_shouldReturnTrue() {
    boolean actual = StringUtility.isNullOrEmpty(null);
    assertTrue(actual);
  }

  @Test
  public void isNullOrEmpty_givenEmptyString_shouldReturnTrue() {
    boolean actual = StringUtility.isNullOrEmpty("");
    assertTrue(actual);
  }

  @Test
  public void isNullOrEmpty_givenNonEmptyString_shouldReturnFalse() {
    boolean actual = StringUtility.isNullOrEmpty("foo");
    assertFalse(actual);
  }
}