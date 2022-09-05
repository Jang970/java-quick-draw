package nz.ac.auckland.se206.util;

import java.util.ArrayList;

/**
 * This class will be used to create objects for each new user profile containing all information related to that specific profile.
 * We will be using the concept of json serialisation and de-serialisation in order to save and load exisiting user profiles in our app.
 */
public class User {
  
  // instance fields to track user associated stats and as well as the name
  private String userName;
  private int gamesWon = 0;
  private int gamesLost = 0;
  private int fastestWin = 59;
  private ArrayList<String> wordHistory;

  // constructor to create a user profile with an associated name
  public User(String profileName){

    this.userName = profileName;

  }

  /**
   * method that will simply increase tally of user games won 
   * method can possibly be called everytime the boolean, playerDidWin is true
   */
  public void updateGamesWon(){
    gamesWon++;
  }

  /**
   * method that will simply increase tally of user games lost
   * method can possibly be called everytime the user runs out of time
   */
  public void updateGamesLost(){
    gamesLost++;
  }

  /**
   * This method will update the fastestWin variable
   * Can be called when the player wins at a faster time than the current time stored in fastestWin
   * @param currentTime the time to update fastestWin to, it should be smaller than current fastestWin
   */
  public void updateFastestWin(int currentTime){
    this.fastestWin = currentTime;
  }

  /** Getter methods for games won lost and fastest win */
  public int getGamesWon(){
    return this.gamesWon;
  }

  public int getGamesLost(){
    return this.gamesLost;
  }

  public int getFastestWin(){
    return this.fastestWin;
  }

}
