package ch.uzh.ifi.ase.csccrecommender.utility;

public class SstUtility {

  // TODO: Add other unknown token identifiers.
  private static final String UNKNOWN_TOKEN_STRING = "???";

  private SstUtility() {
  }

  public static boolean isValidToken(String token) {
    return token != null && !UNKNOWN_TOKEN_STRING.equals(token);
  }
}
