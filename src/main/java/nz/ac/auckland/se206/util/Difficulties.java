package nz.ac.auckland.se206.util;

/** This class will house the various enums to be used in DifficultySettings */
public class Difficulties {

  /**
   * Each enum will have the following instance variables label: the name and level of the
   * difficulty in a string format requirement: this will store the requirement of that difficulty
   * at specified level so for example: Accuracy.MEDIUM requirement will have 2 as it will make it
   * so the drawing as to be in the top 2 guesses to be considered a win.
   *
   * <p>then we will have two methods, one to get the requirement and one to get the label.
   */
  public enum Accuracy {
    EASY("easyAccuracy", 3),
    MEDIUM("mediumAccuracy", 2),
    HARD("hardAccuracy", 1);

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

  public enum Time {
    EASY("easyTime", 60),
    MEDIUM("mediumTime", 45),
    HARD("hardTime", 30),
    MASTER("masterTime", 15);

    private final int timeToDraw;
    private final String label;

    private Time(String label, int time) {
      this.label = label;
      this.timeToDraw = time;
    }

    /**
     * Call this method when you want to set the timer value to the value associated at a wanted
     * Time difficulty level e.g say the Time difficulty wanted is level easy, then this will return
     * 60 as that is how much time is allowed at that given level.
     *
     * @param difficulty enum of type time specifying the level wanted e.g Time.EASY
     * @return int of the amount of time given at stated difficulty level
     */
    public int getTimeToDraw() {
      return this.timeToDraw;
    }

    /**
     * Call this method when you want to get the label of a specified time difficulty level
     *
     * @param difficulty enum of type Time e.g Time.HARD
     * @return string that specifies the level and difficulty e.g hardTime
     */
    public String getLabel() {
      return this.label;
    }
  }

  public enum Confidence {
    EASY("easyConfidence", 1),
    MEDIUM("mediumConfidence", 10),
    HARD("hardConfidence", 25),
    MASTER("masterConfidence", 50);

    private final double confidenceLevel;
    private final String label;

    private Confidence(String label, int confidenceLevel) {
      this.label = label;
      this.confidenceLevel = confidenceLevel;
    }

    /**
     * Call this method when you want to get the confidence requirement at a specific confidence
     * level e.g input: Confidence.HARD output: percentage of how 'confident' the ML model must be
     *
     * @param difficulty enum of type Confidence that you want to get the required confidence for
     *     e.g Confidence.HARD
     * @return requirement at wanted confidence level
     */
    public double getProbabilityLevel() {
      return this.confidenceLevel;
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
}
