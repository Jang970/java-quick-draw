package nz.ac.auckland.se206.util;

import nz.ac.auckland.se206.util.difficulties.Accuracy;
import nz.ac.auckland.se206.util.difficulties.Confidence;
import nz.ac.auckland.se206.util.difficulties.Time;

/**
 * This class will house the difficulty settings for a given profile This will be stored within the
 * Profile class It will store the difficulties set for Accuracy, Words, Time & Confidence
 */
public class DifficultySettings {

  // instance fields, we will have default difficulty values for each setting
  private int accuracyLevel = Accuracy.EASY.getTopNumGuesses();
  private String accuracyLabel = Accuracy.EASY.getLabel();

  private int timeToDraw = Time.EASY.getTimeToDraw();
  private String timeLabel = Time.EASY.getLabel();

  private double confidenceLevel = Confidence.EASY.getProbabilityLevel();
  private String confidenceLabel = Confidence.EASY.getLabel();

  private CategoryType wordCategory = CategoryType.EASY;

  public DifficultySettings() {}

  // getters and setters for each instance field
  public int getAccuracyLevel() {
    return this.accuracyLevel;
  }

  public String getAccuracyLabel() {
    return this.accuracyLabel;
  }

  public int getTimeToDraw() {
    return this.timeToDraw;
  }

  public String getTimeLabel() {
    return this.timeLabel;
  }

  public double getConfidence() {
    return this.confidenceLevel;
  }

  public String getConfidenceLabel() {
    return this.confidenceLabel;
  }

  public CategoryType getCategoryOfWords() {
    return this.wordCategory;
  }

  /**
   * these methods will update the corresponding difficulty level and label to the respective input
   * given
   *
   * @param newAccuracy e.g Accuracy.MEDIUM This will update accuracyLevel to 2 and the label to
   *     mediumAccuracy
   */
  public void updateAccuracyLevel(Accuracy newAccuracy) {
    this.accuracyLevel = newAccuracy.getTopNumGuesses();
    this.accuracyLabel = newAccuracy.getLabel();
  }

  public void updateTimeToDraw(Time newTime) {
    this.timeToDraw = newTime.getTimeToDraw();
    this.timeLabel = newTime.getLabel();
  }

  public void updateConfidenceLevel(Confidence newConfidence) {
    this.confidenceLevel = newConfidence.getProbabilityLevel();
    this.confidenceLabel = newConfidence.getLabel();
  }

  public void updateCategoryOfWords(CategoryType category) {
    this.wordCategory = category;
  }
}
