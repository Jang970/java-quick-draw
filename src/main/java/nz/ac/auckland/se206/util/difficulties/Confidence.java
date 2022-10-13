package nz.ac.auckland.se206.util.difficulties;

public enum Confidence {
  EASY("easy", 0.01),
  MEDIUM("medium", 0.10),
  HARD("hard", 0.25),
  MASTER("master", 0.5);

  private final double probabilityPercentage;
  private final String label;

  /**
   * This is the constructor for the Confidence enum. It stores within it the level of difficulty as
   * a string as well as the relevant confidence requirement for that level.
   *
   * @param label string that contains the relevant Confidence level
   * @param probabilityPercentage double which contains the confidence requirement with regards to
   *     the level of difficulty.
   */
  private Confidence(String label, double probabilityPercentage) {
    this.label = label;
    this.probabilityPercentage = probabilityPercentage;
  }

  /**
   * Call this method when you want to get the confidence requirement at a specific confidence level
   * e.g input: Confidence.HARD output: double of how 'confident' the ML model must be.
   *
   * @return requirement at wanted confidence level
   */
  public double getProbabilityPercentage() {
    return this.probabilityPercentage;
  }

  /**
   * Call this method when you want the label of the Confidence difficulty level.
   *
   * @return label of specified level containing the level and difficulty e.g
   *     Confidence.EASY.getLabel() = easy
   */
  public String getLabel() {
    return this.label;
  }
}
