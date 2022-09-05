package nz.ac.auckland.se206.util;

import java.util.ArrayList;

/**
 * This class will be used to create objects for each new user profile containing all information related to that specific profile.
 * We will be using the concept of json serialisation and de-serialisation in order to save and load exisiting user profiles in our app.
 */
public class User {
  
  // instance fields to track user associated stats and as well as the name
  private String userName;
  private int gamesWon;
  private int gamesLost;
  private int fastestWin;
  private ArrayList<String> wordHistory;

  // constructor to create a user profile with an associated name
  public User(String profileName){

    this.userName = profileName;

  }

}
