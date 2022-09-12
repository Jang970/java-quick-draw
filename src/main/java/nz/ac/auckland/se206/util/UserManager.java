package nz.ac.auckland.se206.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * This class will be used to handle any functionalities we would like to do with User class
 * objects/instances An example would be saving a newly created user profile into our json file
 */
public class UserManager {

  // instance fields
  private List<User> users = new ArrayList<User>();
  private int currentUserIndex = 0;

  /**
   * Call this method when it is required to create and save (serialise) a new user profile to json
   * file
   *
   * @param username name of user profile
   * @param colour chosen colour
   * @return boolean to indicate if creation was successful or not
   * @throws IOException
   */
  public boolean createUserProfile(String username, Color colour) throws IOException {

    // check if username already exists, return false
    if (userWithNameAlreadyExists(username)) {

      return false;

    } else { // username is unique and new user can successfully be created, return true

      // creation of new user instance, adding to our user list
      User user = new User(username, colour);
      users.add(user);

      serialise();

      // lets assume we want to use the newly created profile
      setCurrentProfile(user.getID());

      return true;
    }
  }

  /**
   * Use this method to update the username of the current user profile in use
   *
   * @param newName new username to update to
   * @return boolean to indicate if update was successful or not
   * @throws IOException
   */
  public boolean updateUserName(String newName) throws IOException {

    // checking if username already exists, return false if it does
    if (userWithNameAlreadyExists(newName)) {

      return false;

    } else { // username is unique, return true

      this.users.get(currentUserIndex).changeUserName(newName);
      return true;
    }
  }

  /**
   * Use this method when you want to save the stats of an existing user profile into json
   *
   * @throws IOException
   */
  public void updateCurrentProfile() throws IOException {

    // store current user in use
    User currUser = this.users.get(currentUserIndex);

    // get and update list of all user profiles
    users = getExistingProfiles();

    // then replace User at given currentUserIndex in our list with updated values
    users.set(currentUserIndex, currUser);

    // serialise list to save changes
    serialise();
  }

  /**
   * Use this method to set the reference of currentUser to wanted user profile / object Can also be
   * used to switch between user profiles
   *
   * @param userID unique ID of the User profile we want to set to / use
   * @throws IOException
   */
  public void setCurrentProfile(int userID) throws IOException {

    users = getExistingProfiles();

    int length = users.size();

    // iterate through and find user object with same ID and update our currentUserIndex instance
    // field
    for (int i = 0; i < length; i++) {

      if (users.get(i).getID() == userID) {
        currentUserIndex = i;
        break;
      }
    }
  }

  /**
   * Call this method when you want a list of all User objects that have been created and stored on
   * json file Can only call if the json file already exists
   *
   * @return arraylist of User objects
   * @throws IOException
   */
  public List<User> getExistingProfiles() throws IOException {

    deserialise();
    return users;
  }

  /**
   * Getter method to get the current User object profile in use
   *
   * @return object of class User that is the current profile being used
   * @throws IOException
   */
  public User getCurrentProfile() throws IOException {
    return getExistingProfiles().get(currentUserIndex);
  }

  /**
   * Use this method to get an object of class UserStats containing simple getter methods for user
   * to get wanted user stats Only use when a user profile has been set
   *
   * <p>Can simply use UserStats class methods to get and update desired stats
   *
   * @return object of class UserStats containing stats of the current user profile
   */
  public UserStats getUserStats() {

    return users.get(currentUserIndex).getUserStats();
  }

  /**
   * This method will handle serialising / saving to json file
   *
   * @throws IOException
   */
  private void serialise() throws IOException {
    try (Writer writer = new FileWriter("UserProfiles.json")) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(users, writer);
    }
  }

  /**
   * This method will handle deserialsing / loading from json file
   *
   * @throws IOException
   */
  private void deserialise() throws IOException {

    // deserialising file containing existing profiles
    // de-serialisation
    // create file reader

    // firstly checking if the file exists
    try (Reader reader = Files.newBufferedReader(Paths.get("UserProfiles.json"))) {
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<User>>() {}.getType();

      // converting json string to arraylist of user objects
      users = gson.fromJson(reader, listType);

      // close reader
      reader.close();
    } catch (FileNotFoundException e) {
      // will throw an exception if file does not exist
    }
  }

  /**
   * Helper method to check for duplicate usernames
   *
   * @return boolean - True if a duplicate exists
   * @throws IOException
   */
  private boolean userWithNameAlreadyExists(String userName) throws IOException {

    // update users
    users = getExistingProfiles();

    // iterate through all users and if username already exists, return true
    for (User user : users) {

      if (user.getName().equals(userName)) {
        return true;
      }
    }

    return false;
  }
}
