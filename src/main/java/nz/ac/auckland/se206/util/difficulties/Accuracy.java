package nz.ac.auckland.se206.util.difficulties;

public enum Accuracy {
  EASY("easy", 3),
  MEDIUM("medium", 2),
  HARD("hard", 1);

  private final int topNumGuesses;
  private final String label;

  /**
   * This is the constructor for the Accuracy enum. It stores within it the level of difficulty as a
   * string as well as the relevant accuracy needed for that level.
   *
   * @param label string that contains the relevant accuracy level
   * @param time int that stores the top number of guesses the draw must be in at each relevant
   *     Accuracy difficulty level
   */
  private Accuracy(String label, int topNumGuesses) {
    this.label = label;
    this.topNumGuesses = topNumGuesses;
  }

  /**
   * This method can be called when you want to save the requirement at a given accuracy level e.g
   * if the requirement you want is Accuracy.EASY, it will return 3
   *
   * @return requirement associated to that difficulty level
   */
  public int getTopNumGuesses() {
    return this.topNumGuesses;
  }

  /**
   * This method can be used when you want to get the label of the accuracy level e.g if the label
   * you want is Accuracy.MEDIUM, you will get medium
   *
   * @return label of current difficulty level
   */
  public String getLabel() {
    return this.label;
  }
}
