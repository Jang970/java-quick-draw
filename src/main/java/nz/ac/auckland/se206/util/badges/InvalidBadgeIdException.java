package nz.ac.auckland.se206.util.badges;

public class InvalidBadgeIdException extends Exception {

  /**
   * Exception thrown when a badge ID is invalid
   *
   * @param string message to show when exception is thrown
   */
  public InvalidBadgeIdException(String string) {
    super(string);
  }
}
