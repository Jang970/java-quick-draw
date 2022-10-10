package nz.ac.auckland.se206.util;

import nz.ac.auckland.se206.util.Difficulties.Accuracy;
import nz.ac.auckland.se206.util.Difficulties.Confidence;
import nz.ac.auckland.se206.util.Difficulties.Time;

/**
 * This class will house the difficulty settings for a given profile This will be stored within the
 * Profile class It will store the difficulties set for Accuracy, Words, Time & Confidence
 */
public class DifficultySettings {

  // instance fields, we will have default difficulty values for each setting
  private int accuracyLevel = 3;
  private int timeToDraw = 60;
  private double confidenceLevel = 1;
  private CategoryType wordCategory = CategoryType.EASY;

  public DifficultySettings() {}

  // getters and setters for each instance field
  public int getAccuracy() {
    return this.accuracyLevel;
  }

  public int getTimeToDraw() {
    return this.timeToDraw;
  }

  public double getConfidence() {
    return this.confidenceLevel;
  }

  public CategoryType getCategoryOfWords() {
    return this.wordCategory;
  }

  public void changeAccuracyLevel(Accuracy newAccuracy) {
    this.accuracyLevel = newAccuracy.getTopNumGuesses();
  }

  public void changeTimeToDraw(Time newTime) {
    this.timeToDraw = newTime.getTimeToDraw();
  }

  public void changeConfidenceLevel(Confidence newConfidence) {
    this.confidenceLevel = newConfidence.getProbabilityLevel();
  }

  public void changeCategoryOfWords(CategoryType category) {
    this.wordCategory = category;
  }
}
