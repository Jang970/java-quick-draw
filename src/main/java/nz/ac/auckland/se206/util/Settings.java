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

  private Accuracy accuracy;
  private Time time;
  private Confidence confidence;
  private WordChoice wordChoice;

  /**
   * Constructor with no parameters, will be used when we want to set default difficulty settings.
   */
  public Settings() {
    this.accuracy = Accuracy.EASY;
    this.time = Time.EASY;
    this.confidence = Confidence.EASY;
    this.wordChoice = WordChoice.EASY;
  }

  /**
   * Constructor of Settings with parameters for each difficulty type. Can be used when we want
   * initialise settings with the non-default.
   *
   * @param accuracy accuracy difficulty level we want to save / set to
   * @param time time difficulty level we want to save / set to
   * @param confidence confidence difficulty we want to save / set to
   * @param wordChoice wordChoice difficulty we want to save / set to
   */
  public Settings(Accuracy accuracy, Time time, Confidence confidence, WordChoice wordChoice) {
    this.accuracy = accuracy;
    this.time = time;
    this.confidence = confidence;
    this.wordChoice = wordChoice;
  }

  /**
   * This will return the accuracy enum stored and we can just use the enum methods to get the
   * respective difficulty requirement and label
   *
   * @return accuracy enum saved for the profile
   */
  public Accuracy getAccuracy() {
    return this.accuracy;
  }

  /**
   * This will return the time enum stored and we can just use the enum methods to get the
   * respective difficulty requirement and label
   *
   * @return time enum saved for the profile
   */
  public Time getTime() {
    return this.time;
  }

  /**
   * This will return the confidence enum stored and we can just use the enum methods to get the
   * respective difficulty requirement and label
   *
   * @return confidence enum saved for the profile
   */
  public Confidence getConfidence() {
    return this.confidence;
  }

  /**
   * This will return the word choice enum stored and we can just use the enum methods to get the
   * respective difficulty requirement and label
   *
   * @return word choice enum saved for the profile
   */
  public WordChoice getWordChoice() {
    return this.wordChoice;
  }

  /**
   * Use when you want to save/update to a new accuracy level.
   *
   * @param newAcc new accuracy difficulty level to save
   */
  public void updateAccuracy(Accuracy newAcc) {
    this.accuracy = newAcc;
  }

  /**
   * Use when you want to save/update to a new time level.
   *
   * @param newAcc new time difficulty level to save
   */
  public void updateTime(Time newTime) {
    this.time = newTime;
  }

  /**
   * Use when you want to save/update to a new confidence level.
   *
   * @param newAcc new confidence difficulty level to save
   */
  public void updateConfidence(Confidence newConf) {
    this.confidence = newConf;
  }

  /**
   * Use when you want to save/update to a new word choice level.
   *
   * @param newAcc new word choice difficulty level to save
   */
  public void updateWordChoice(WordChoice wordChoice) {
    this.wordChoice = wordChoice;
  }
}
