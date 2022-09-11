package nz.ac.auckland.se206.util;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * This class will be used to handle any functionalities we would like to do with User class objects/instances
 * An example would be saving a newly created user profile into our json file
 */
public class UserManager {
  
  // instance fields
  private List<User> users = new ArrayList<User>();
  private User currentUser;

  /**
   * Call this method when it is required to create and save (serialise) a new user profile to json file
   * @param username name of user profile
   * @param colour chosen user colour
   * @throws IOException
   */
  public void createUserProfile(String username, Colour colour) throws IOException{

    // creation of new user instance, adding to our user list
    User user = new User(username, colour);
    users.add(user);

    serialise();

    // lets assume we want to use the newly created profile
    setCurrentProfile(user.getName());

  }

  /**
   * Call this method when you want a list of all User objects that have been created and stored on json file
   * Can only call if the json file already exists
   * @return arraylist of User objects
   * @throws IOException
   */
  public List<User> getExistingProfiles() throws IOException{

    deserialise();
    return users;
  }

  /**
   * Use this method when you want to save the stats of an existing user profile into json
   * @param currentUser the current user object in use and the object we want to update in our json file
   * @throws IOException
   */
  public void updateCurrentProfile(User currentUser) throws IOException{

    // first get list of all user profiles
    users = getExistingProfiles();

    // iterate through and find and delete user object with same username
    for (User user : users){

      if (user.getName().equals(currentUser.getName())){
        users.remove(user);
        break;
      }

    }

    // now 'replace' recently deleted user profile and serialise list
    users.add(currentUser);
    serialise();

  }

  /**
   * This method will handle serialising / saving to json file
   * @throws IOException
   */
  private void serialise() throws IOException{
    try(Writer writer = new FileWriter("UserProfiles.json")) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(users, writer);
    }
  }

  /**
   * This method will handle deserialsing / loading from json file 
   * @throws IOException
   */
  private void deserialise() throws IOException{

    // deserialising file containing existing profiles
    // de-serialisation
    // create file reader
    
    // firstly checking if the file exists
    try (Reader reader = Files.newBufferedReader(Paths.get("UserProfiles.json"))){
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<User>>(){}.getType();
        
      // converting json string to arraylist of user objects
      users = gson.fromJson(reader, listType);

      // close reader
      reader.close();
    } catch(FileNotFoundException e){
      // will throw an exception if file does not exist
    }

  }

  /**
   * Use this method to set the reference of currentUser to wanted user profile / object
   * Can also be used to switch between user profiles
   * @param username name of user profile we want to use
   * @throws IOException
   */
  public void setCurrentProfile(String username) throws IOException{

    users = getExistingProfiles();
    
    // iterate through and find user object with same username and update our currentUser instance field
    for (User user : users){

      if (user.getName().equals(currentUser.getName())){
        currentUser = user;
        break;
      }

    }

  }

  /**
   * Getter method to get the current User object profile in use
   * @return object / value stored in currentUser instance field
   */
  public User getCurrentProfile(){
    return this.currentUser;
  }

  /**
   * Use this method to get an ArrayList containing all the stats of the current user profile.
   * Only use when a user profile has been set
   * The arraylist structure will be as follows: 
   * [gamesWon, gamesLost, fastestWin, bestCategory, wordHistory]
   * Can simply index to get desired stat/s
   * @return arraylist of current user stats
   */
  public ArrayList<Object> getUserStats(){

    // add stats to arraylist which will also be returned
    ArrayList<Object> userStats = new ArrayList<>();
    appendStats(userStats);

    return userStats;

  }

  /**
   * Helper method that extracts away putting user stats into arraylist
   * 
   * @param userStats arraylist to add to
   */
  private void appendStats(ArrayList<Object> userStats){

    userStats.add(currentUser.getGamesWon());
    userStats.add(currentUser.getGamesLost());
    userStats.add(currentUser.getFastestWin());
    userStats.add(currentUser.getBestCategory());
    userStats.add(currentUser.getWordHistory());
  }

  /**
   * LIST OF TODOS
   * 
   * TODO: find efficient way to update stats of a user?
   * 
   */
}
