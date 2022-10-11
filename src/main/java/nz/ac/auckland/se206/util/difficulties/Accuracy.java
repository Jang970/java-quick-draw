package nz.ac.auckland.se206.util.difficulties;


public enum Accuracy {
  EASY("easy", 3),
  MEDIUM("medium", 2),
  HARD("hard", 1);

  private final int topNumGuesses;
  private final String label;

  // essentially creates all the different enum types
  private Accuracy(String label, int topNumGuesses) {
    this.label = label;
    this.topNumGuesses = topNumGuesses;
  }

  /**
   * This method can be called when you want to save the requirement at a given accuracy level e.g
   * if parameter is Accuracy.EASY, it will return 3
   *
   * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
   * @return requirement associated to that difficulty level
   */
  public int getTopNumGuesses() {
    return this.topNumGuesses;
  }

  /**
   * This method can be used when you want to get the label of the accuracy level e.g if parameter
   * is Accuracy.MEDIUM, you will get mediumAccuracy
   *
   * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
   * @return label of current difficulty level
   */
  public String getLabel() {
    return this.label;
  }
}
