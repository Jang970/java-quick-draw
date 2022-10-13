package nz.ac.auckland.se206.util;

public class FilterTooStrictException extends Exception {
  /**
   * When this exception is thrown, the input message is printed to developer
   *
   * @param message string to print on console
   */
  public FilterTooStrictException(String message) {
    super(message);
  }
}
