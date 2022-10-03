package nz.ac.auckland.se206.util;

import nz.ac.auckland.se206.GameLogicManager.CategoryType;

/**
 * This class will house the difficulty settings for a given profile This will be stored within the
 * Profile class It will store the difficulties set for Accuracy, Words, Time & Confidence
 */
public class DifficultySettings {

  // instance fields, we will have default difficulty values for each setting
  private int topNumGuesses = 3;
  private int timeToDraw = 60;
  private double confidenceLevel = 1;
  private CategoryType wordCategory = CategoryType.EASY;

  public DifficultySettings() {}
}
