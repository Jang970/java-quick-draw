package nz.ac.auckland.se206.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will be used to create objects for each new user profile containing all information related to that specific profile.
 * We will be using the concept of json serialisation and de-serialisation in order to save and load exisiting user profiles in our app.
 */
public class User {

  // static variable to implement unique user ID
  private static int count = 0;
  
  // instance fields to track user associated stats and as well as the name
  private String name;
  private int id = 0;
  private int gamesWon = 0;
  private int gamesLost = 0;
  private int fastestWin = 59;
  private String bestCategory;
  private List<String> wordHistory = new ArrayList<String>();
  private Colour colour;

  // constructor to create a user profile with an associated name and colour
  public User(String profileName, Colour colour){

    this.name = profileName;
    this.id = count++;
    this.colour = colour;

  }

  /**
   * method that will simply increase tally of user games won 
   * method can possibly be called everytime the boolean, playerDidWin is true
   */
  public void incrementGamesWon(){
    gamesWon++;
  }

  /**
   * method that will simply increase tally of user games lost
   * method can possibly be called everytime the user runs out of time
   */
  public void incrementGamesLost(){
    gamesLost++;
  }

  /**
   * This method will update the fastestWin and bestCategory variables
   * Can be called when the player wins at a faster time than the current time stored in fastestWin
   * @param currentTime the time to update fastestWin to
   * @param currentCategory the word that the user had to draw
   */
  public void updateFastestGame(int currentTime, String currentCategory){
    
    // make sure currentTime is actually faster than fastestWin
    if (currentTime < fastestWin){

      this.fastestWin = currentTime;
      this.bestCategory = currentCategory;

    } 

    // TODO: Maybe prompt or alert somehow that fastestWin is not being updated?

  }
  
  /**
   * This method will be used to add the current category/word to draw to list of previous words
   * Can be called everytime a new category appears
   * @param category current category/word that user must draw
   */
  public void addToCategoryHistory(String category){
    wordHistory.add(category);
  }

    /** Getter methods for our instance fields */

    public String getName(){
      return this.name;
    }

    public int getID(){
      return this.id;
    }
  
    public int getGamesWon(){
      return this.gamesWon;
    }
  
    public int getGamesLost(){
      return this.gamesLost;
    }
  
    public int getFastestWin(){
      return this.fastestWin;
    }

    public String getBestCategory(){
      return this.bestCategory;
    }
  
    public List<String> getWordHistory(){
      return this.wordHistory;
    }

    public Colour getColour(){
      return this.colour;
    }

    // method to change the username of user profile
    public void changeUserName(String newName){
      this.name = newName;
    }

}
