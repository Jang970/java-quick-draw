package nz.ac.auckland.se206.gamelogicmanager;

import nz.ac.auckland.se206.util.Category;

public class CategoryPlayedInfo {

  private int timeTaken;
  private int secondsRemaining;
  private Category category;

  /**
   * This method constructs a new category played object. This represents a single category that the
   * player has drawn.
   *
   * @param timeTaken the time it took to draw the category.
   * @param secondsRemaining the number of seconds the player had before they ran out of time.
   * @param category the category that the player had to draw.
   */
  CategoryPlayedInfo(int timeTaken, int secondsRemaining, Category category) {
    this.timeTaken = timeTaken;
    this.secondsRemaining = secondsRemaining;
    this.category = category;
  }

  /**
   * Gets the time taken to draw the category
   *
   * @return the time taken to draw the category
   */
  public int getTimeTaken() {
    return timeTaken;
  }

  /**
   * Gets the seconds remaining when the player got the category.
   *
   * @return the seconds remaining when the player got the category.
   */
  public int getSecondsRemaining() {
    return secondsRemaining;
  }

  /**
   * Gets the category that the player had to draw.
   *
   * @return the category that the player had to draw.
   */
  public Category getCategory() {
    return category;
  }

  @Override
  public String toString() {
    return category.toString();
  }
}
