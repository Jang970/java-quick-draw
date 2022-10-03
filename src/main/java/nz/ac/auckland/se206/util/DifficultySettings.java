package nz.ac.auckland.se206.util;

import nz.ac.auckland.se206.GameLogicManager.CategoryType;

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

  public void changeAccuracyLevel(int newAccuracy) {
    this.accuracyLevel = newAccuracy;
  }

  public void changeTimeToDraw(int newTime) {
    this.timeToDraw = newTime;
  }

  public void changeConfidenceLevel(double newConfidence) {
    this.confidenceLevel = newConfidence;
  }

  public void changeCategoryOfWords(CategoryType category) {
    this.wordCategory = category;
  }
}
