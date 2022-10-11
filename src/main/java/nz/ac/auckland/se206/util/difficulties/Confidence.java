package nz.ac.auckland.se206.util.difficulties;

public enum Confidence {
  EASY("easy", 0.01),
  MEDIUM("medium", 0.10),
  HARD("hard", 0.25),
  MASTER("master", 0.5);

  private final double probabilityPercentage;
  private final String label;

  private Confidence(String label, double probabilityPercentage) {
    this.label = label;
    this.probabilityPercentage = probabilityPercentage;
  }

  /**
   * Call this method when you want to get the confidence requirement at a specific confidence level
   * e.g input: Confidence.HARD output: percentage of how 'confident' the ML model must be Will
   * return a whole number for now that must be divided 100, can change this as you see fit though
   *
   * @param difficulty enum of type Confidence that you want to get the required confidence for e.g
   *     Confidence.HARD
   * @return requirement at wanted confidence level
   */
  public double getProbabilityPercentage() {
    return this.probabilityPercentage;
  }

  /**
   * Call this method when you want the label of the Confidence difficulty
   *
   * @param difficulty enum of type Confidence with specified level e.g Confidence.EASY
   * @return label of specified level containing the level and difficulty e.g easyConfidence
   */
  public String getLabel() {
    return this.label;
  }
}
