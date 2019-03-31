package ch.uzh.ifi.ase.csccrecommender.utility;

public class StringUtility {

  private StringUtility() {
  }

  public static boolean isNullOrEmpty(String s) {
    return s == null || s.isEmpty();
  }
}
