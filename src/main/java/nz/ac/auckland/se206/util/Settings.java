package nz.ac.auckland.se206.util;

import nz.ac.auckland.se206.util.difficulties.Accuracy;
import nz.ac.auckland.se206.util.difficulties.Confidence;
import nz.ac.auckland.se206.util.difficulties.Time;
import nz.ac.auckland.se206.util.difficulties.WordChoice;

/**
 * This class will house the settings for a given profile This will be stored within the Profile
 * class It will store the difficulties set for Accuracy, Words, Time & Confidence
 */
public class Settings {

  // instance fields, we will have default difficulty values for each setting
  private Accuracy accuracy;
  private Time time;
  private Confidence confidence;
  private WordChoice wordChoice;

  public Settings() {
    this.accuracy = Accuracy.EASY;
    this.time = Time.EASY;
    this.confidence = Confidence.EASY;
    this.wordChoice = WordChoice.EASY;
  }

  public Settings(Accuracy accuracy, Time time, Confidence confidence, WordChoice wordChoice) {
    this.accuracy = accuracy;
    this.time = time;
    this.confidence = confidence;
    this.wordChoice = wordChoice;
  }

  // getters and setters for each instance field

  /**
   * Getters will return the enum and we can just use the enum methods to get the respective
   * difficulty requirement and label
   *
   * @return Difficulty enum saved
   */
  public Accuracy getAccuracy() {
    return this.accuracy;
  }

  public Time getTime() {
    return this.time;
  }

  public Confidence getConfidence() {
    return this.confidence;
  }

  public WordChoice getWordChoice() {
    return this.wordChoice;
  }

  /**
   * Update methods can be used when you want to save new difficulty settings for a user
   *
   * @param newAcc new difficulty enum to save
   */
  public void updateAccuracy(Accuracy newAcc) {
    this.accuracy = newAcc;
  }

  public void updateTime(Time newTime) {
    this.time = newTime;
  }

  public void updateConfidence(Confidence newConf) {
    this.confidence = newConf;
  }

  public void updateWordChoice(WordChoice wordChoice) {
    this.wordChoice = wordChoice;
  }
}
