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

    // have a hashmaps, one to store the requirement at each level and another to store the label at
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
     * This method can be called when you want the requirement at a given difficulty level e.g if
     * parameter is Accuracy.EASY, it will return 3
     *
     * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
     * @return requirement associated to that difficulty level
     */
    public int getAccuracyRequired(Accuracy difficulty) {
      return difficultyLevels.get(difficulty);
    }

    /**
     * This method can be used when you want to get the label of the difficulty e.g if parameter is
     * Accuracy.MEDIUM, you will get mediumAccuracy
     *
     * @param difficulty enum of type accuracy that indicates the level e.g Accuracy.EASY
     * @return label of current difficulty level
     */
    public String getDifficultyLabel(Accuracy difficulty) {
      return difficultyLabels.get(difficulty);
    }
  }

  public enum Time {
    EASY(60),
    MEDIUM(45),
    HARD(30),
    MASTER(15);

    // have a hashmap to store the values associate to each difficulty level
    private static final HashMap<Time, Integer> difficultyLevels = new HashMap<>();

    private final int timeToDraw;

    // store values into hashmap
    static {
      for (Time difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getTimeToDraw());
      }
    }

    private Time(int time) {
      this.timeToDraw = time;
    }

    private int getTimeToDraw() {
      return this.timeToDraw;
    }

    // basically only have to call this method with which level we want to set our time instance
    // field in DifficultySettings to
    public int getTimeGiven(Time difficulty) {
      return difficultyLevels.get(difficulty);
    }
  }

  public enum Confidence {
    EASY(1),
    MEDIUM(10),
    HARD(25),
    MASTER(50);

    // have a hashmap to store the values associate to each difficulty level
    private static final HashMap<Confidence, Double> difficultyLevels = new HashMap<>();

    private final double probabilityLevel;

    // store values into hashmap
    static {
      for (Confidence difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getProbabilityLevel());
      }
    }

    private Confidence(int time) {
      this.probabilityLevel = time;
    }

    private double getProbabilityLevel() {
      return this.probabilityLevel;
    }

    // basically only have to call this method with which level we want to set our confidence
    // instance
    // field in DifficultySettings to
    public double getConfidenceRequired(Confidence difficulty) {
      return difficultyLevels.get(difficulty);
    }
  }
}
