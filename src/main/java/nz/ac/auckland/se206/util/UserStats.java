package nz.ac.auckland.se206.util;

import java.util.HashSet;
import java.util.Set;

/**
 * This class will handle all functionalities relating to a user profile's stats. An object of this
 * class will be returned by UserManager method: getUserStats
 */
public class UserStats {

  // instance fields
  private int gamesWon = 0;
  private int gamesLost = 0;
  private int fastestWin = 59;
  private String bestCategory;
  private Set<String> wordHistory = new HashSet<String>();

  // default constructor
  UserStats() {}

  /**
   * method that will simply increase tally of user games won method can possibly be called
   * everytime the boolean, playerDidWin is true
   */
  public void incrementGamesWon() {
    gamesWon++;
  }

  /**
   * method that will simply increase tally of user games lost method can possibly be called
   * everytime the user runs out of time
   */
  public void incrementGamesLost() {
    gamesLost++;
  }

  /**
   * This method will update the fastestWin and bestCategory variables Can be called when the player
   * wins at a faster time than the current time stored in fastestWin
   *
   * @param currentTime the time to update fastestWin to
   * @param currentCategory the word that the user had to draw
   * @return true or false depending if update was successful or not
   */
  public Boolean updateFastestGame(int currentTime, String currentCategory) {

    // make sure currentTime is actually faster than fastestWin
    if (currentTime < fastestWin) {

      this.fastestWin = currentTime;
      this.bestCategory = currentCategory;

      return true;

    } else {

      return false;
    }
  }

  /**
   * This method will be used to add the current category/word to draw to list of previous words Can
   * be called everytime a new category appears
   *
   * @param category current category/word that user must draw
   */
  public void addToCategoryHistory(String category) {
    wordHistory.add(category);
  }

  // Getter methods for instance fields

  public int getGamesWon() {
    return this.gamesWon;
  }

  public int getGamesLost() {
    return this.gamesLost;
  }

  public int getFastestWin() {
    return this.fastestWin;
  }

  public String getBestCategory() {
    return this.bestCategory;
  }

  public Set<String> getCategoryHistory() {
    return this.wordHistory;
  }
}
