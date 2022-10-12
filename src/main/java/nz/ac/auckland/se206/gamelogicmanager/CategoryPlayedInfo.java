package nz.ac.auckland.se206.gamelogicmanager;

import nz.ac.auckland.se206.util.Category;

public class CategoryPlayedInfo {

  private int timeTaken;
  private int secondsRemaining;
  private Category category;

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
