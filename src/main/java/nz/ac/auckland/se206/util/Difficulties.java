package nz.ac.auckland.se206.util;

import java.util.HashMap;

/** This class will house the various enums to be used in DifficultySettings */
public class Difficulties {

  public enum Accuracy {
    EASY(3),
    MEDIUM(2),
    HARD(1);

    // have a hashmap to store the values associate to each difficulty level
    private static final HashMap<Accuracy, Integer> AccuracyLevels = new HashMap<>();

    private final int topNumGuesses;

    // store values into hashmap
    static {
      for (Accuracy level : values()) {
        AccuracyLevels.put(level, level.getTopNumGuesses());
      }
    }

    private Accuracy(int topNumGuesses) {
      this.topNumGuesses = topNumGuesses;
    }

    private int getTopNumGuesses() {
      return this.topNumGuesses;
    }

    // basically only have to call this method with which level we want to set our accuracy instance
    // field in Difficulty Settings to
    public int getAccuracyLevel(Accuracy level) {
      return AccuracyLevels.get(level);
    }
  }
}
