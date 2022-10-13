package nz.ac.auckland.se206.gamelogicmanager;

import nz.ac.auckland.se206.util.Category;

public class CategoryPlayedInfo {

  private int timeTaken;
  private int secondsRemaining;
  private Category category;

  /**
   * This is the constructor for the CategoryPlayedInfo which stores the category, the time taken
   * and the number of seconds remaining when the game ended.
   *
   * @param timeTaken time taken for the game
   * @param secondsRemaining time left in the game
   * @param category category played
   */
  CategoryPlayedInfo(int timeTaken, int secondsRemaining, Category category) {
    this.timeTaken = timeTaken;
    this.secondsRemaining = secondsRemaining;
    this.category = category;
  }

  public int getTimeTaken() {
    return timeTaken;
  }

  public int getSecondsRemaining() {
    return secondsRemaining;
  }

  public Category getCategory() {
    return category;
  }
}
