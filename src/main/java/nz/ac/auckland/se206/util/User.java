package nz.ac.auckland.se206.util;

import java.util.UUID;

/**
 * This class will be used to create objects for each new user profile containing all information
 * related to that specific profile. We will be using the concept of json serialisation and
 * de-serialisation in order to save and load exisiting user profiles in our app.
 */
public class User {

  // instance fields to track user associated stats and as well as the name
  private String name;
  private UUID id;
  private String colour;
  // object of class UserStats
  private UserStats userStats = new UserStats();

  // constructor to create a user profile with an associated name and colour
  public User(String profileName, String colour) {

    this.name = profileName;
    // generates a random UUID for each user profile
    this.id = UUID.randomUUID();
    this.colour = colour;
  }

  /** Getter methods for our instance fields */
  public String getName() {
    return this.name;
  }

  public UUID getID() {
    return this.id;
  }

  public String getColour() {
    return this.colour;
  }

  public UserStats getUserStats() {
    return this.userStats;
  }

  // method to change the username of user profile
  public void changeUserName(String newName) {
    this.name = newName;
  }
}
