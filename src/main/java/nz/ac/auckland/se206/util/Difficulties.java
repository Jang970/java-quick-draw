package nz.ac.auckland.se206.util;

import java.util.HashMap;

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

    // have hashmaps, one to store the requirement at each level and another to store the label at
    // each level
    private static final HashMap<Accuracy, Integer> difficultyLevels = new HashMap<>();
    private static final HashMap<Accuracy, String> difficultyLabels = new HashMap<>();

    private final int topNumGuesses;
    private final String label;

    // store  relevant values into hashmap
    static {
      for (Accuracy difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getTopNumGuesses());
        difficultyLabels.put(difficulty, difficulty.getLabel());
      }
    }

    // essentially creates all the different enum types
    private Accuracy(String label, int topNumGuesses) {
      this.label = label;
      this.topNumGuesses = topNumGuesses;
    }

    private int getTopNumGuesses() {
      return this.topNumGuesses;
    }

    private String getLabel() {
      return this.label;
    }

    /**
     * This method can be called when you want the requirement at a given accuracy level e.g if
     * parameter is Accuracy.EASY, it will return 3
     *
     * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
     * @return requirement associated to that difficulty level
     */
    public int getAccuracyRequired(Accuracy difficulty) {
      return difficultyLevels.get(difficulty);
    }

    /**
     * This method can be used when you want to get the label of the accuracy level e.g if parameter
     * is Accuracy.MEDIUM, you will get mediumAccuracy
     *
     * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
     * @return label of current difficulty level
     */
    public String getAccuracyDifficultyLabel(Accuracy difficulty) {
      return difficultyLabels.get(difficulty);
    }
  }

  public enum Time {
    EASY("easyTime", 60),
    MEDIUM("mediumTime", 45),
    HARD("hardTime", 30),
    MASTER("masterTime", 15);

    // have hashmaps, one to store the requirement at each level and another to store the label at
    // each level
    private static final HashMap<Time, Integer> difficultyLevels = new HashMap<>();
    private static final HashMap<Time, String> difficultyLabels = new HashMap<>();

    private final int timeToDraw;
    private final String label;

    // store values into hashmap
    static {
      for (Time difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getTimeToDraw());
        difficultyLabels.put(difficulty, difficulty.getLabel());
      }
    }

    private Time(String label, int time) {
      this.label = label;
      this.timeToDraw = time;
    }

    private int getTimeToDraw() {
      return this.timeToDraw;
    }

    private String getLabel() {
      return this.label;
    }

    /**
     * Call this method when you want to set the timer value to the value associated at a wanted
     * Time difficulty level e.g say the Time difficulty wanted is level easy, then this will return
     * 60 as that is how much time is allowed at that given level.
     *
     * @param difficulty enum of type time specifying the level wanted e.g Time.EASY
     * @return int of the amount of time given at stated difficulty level
     */
    public int getTimeAllowed(Time difficulty) {
      return difficultyLevels.get(difficulty);
    }

    /**
     * Call this method when you want to get the label of a specified time difficulty level
     *
     * @param difficulty enum of type Time e.g Time.HARD
     * @return string that specifies the level and difficulty e.g hardTime
     */
    public String getTimeDifficultyLabel(Time difficulty) {
      return difficultyLabels.get(difficulty);
    }
  }

  public enum Confidence {
    EASY("easyConfidence", 1),
    MEDIUM("mediumConfidence", 10),
    HARD("hardConfidence", 25),
    MASTER("masterConfidence", 50);

    // have hashmaps, one to store the requirement at each level and another to store the label at
    // each level
    private static final HashMap<Confidence, Double> difficultyLevels = new HashMap<>();
    private static final HashMap<Confidence, String> difficultyLabels = new HashMap<>();

    private final double confidenceLevel;
    private final String label;

    // store values into hashmap
    static {
      for (Confidence difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getProbabilityLevel());
        difficultyLabels.put(difficulty, difficulty.getLabel());
      }
    }

    private Confidence(String label, int confidenceLevel) {
      this.label = label;
      this.confidenceLevel = confidenceLevel;
    }

    private double getProbabilityLevel() {
      return this.confidenceLevel;
    }

    private String getLabel() {
      return this.label;
    }

    /**
     * Call this method when you want to get the confidence requirement at a specific confidence
     * level e.g input: Confidence.HARD output: percentage of how 'confident' the ML model must be
     *
     * @param difficulty enum of type Confidence that you want to get the required confidence for
     *     e.g Confidence.HARD
     * @return requirement at wanted confidence level
     */
    public double getConfidenceRequired(Confidence difficulty) {
      return difficultyLevels.get(difficulty);
    }

    /**
     * Call this method when you want the label of the Confidence difficulty
     *
     * @param difficulty enum of type Confidence with specified level e.g Confidence.EASY
     * @return label of specified level containing the level and difficulty e.g easyConfidence
     */
    public String getConfidenceDifficultyLabel(Confidence difficulty) {
      return difficultyLabels.get(difficulty);
    }
  }
}
