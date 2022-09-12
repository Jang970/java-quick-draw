package nz.ac.auckland.se206.util;

import java.util.HashSet;
import java.util.Set;

/**
 * This class will (for now), be handling the showing of a user profile's stats.
 * This object will be returned by UserManager method: getUserStats
 * More functionalities could possibly added in the future
 */
public class UserStats {
  
  // instance fields
  private int gamesWon;
  private int gamesLost;
  private int fastestWin;
  private String bestCategory;
  private Set<String> wordHistory = new HashSet<String>();
  private User currentUser;

  // constructor, takes input of User object
  UserStats(User currentUser){
    this.currentUser = currentUser;
    setInstanceFields();
  }

  // Getter methods for instance fields

  public int gamesWon(){
    return this.gamesWon;
  }

  public int gamesLost(){
    return this.gamesLost;
  }

  public int fastestWin(){
    return this.fastestWin;
  }

  public String bestCategory(){
    return this.bestCategory;
  }

  public Set<String> wordHistory(){
    return this.wordHistory;
  }

  /**
   * Simple method to set all instance fields to store relevant stats of current user
   */
  private void setInstanceFields(){

    this.gamesWon = currentUser.getGamesWon();
    this.gamesLost = currentUser.getGamesLost();
    this.fastestWin = currentUser.getFastestWin();
    this.bestCategory = currentUser.getBestCategory();
    this.wordHistory = currentUser.getWordHistory();
    
  }
}
