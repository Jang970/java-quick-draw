package nz.ac.auckland.se206.util;

import java.util.HashMap;

/** This class will house the various enums to be used in DifficultySettings */
public class Difficulties {

  public enum Accuracy {
    EASY(3),
    MEDIUM(2),
    HARD(1);

    // have a hashmap to store the values associate to each difficulty level
    private static final HashMap<Accuracy, Integer> difficultyLevels = new HashMap<>();

    private final int topNumGuesses;

    // store values into hashmap
    static {
      for (Accuracy difficulty : values()) {
        difficultyLevels.put(difficulty, difficulty.getTopNumGuesses());
      }
    }

    private Accuracy(int topNumGuesses) {
      this.topNumGuesses = topNumGuesses;
    }

    private int getTopNumGuesses() {
      return this.topNumGuesses;
    }

    // basically only have to call this method with which level we want to set our accuracy instance
    // field in DifficultySettings to
    public int getAccuracyRequired(Accuracy difficulty) {
      return difficultyLevels.get(difficulty);
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
