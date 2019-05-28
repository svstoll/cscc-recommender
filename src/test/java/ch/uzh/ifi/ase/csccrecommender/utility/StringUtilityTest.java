package ch.uzh.ifi.ase.csccrecommender.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilityTest {

  @Test
  public void isNullOrEmpty_givenNullString_shouldReturnTrue() {
    assertTrue(StringUtility.isNullOrEmpty(null));
  }

  @Test
  public void isNullOrEmpty_givenEmptyString_shouldReturnTrue() {
    assertTrue(StringUtility.isNullOrEmpty(""));
  }

  @Test
  public void isNullOrEmpty_givenNonEmptyString_shouldReturnFalse() {
    assertFalse(StringUtility.isNullOrEmpty("foo"));
  }
}
