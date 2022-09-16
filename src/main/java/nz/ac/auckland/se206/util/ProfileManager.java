package nz.ac.auckland.se206.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class will be used to handle any functionalities we would like to do with User class
 * objects/instances An example would be saving a newly created user profile into our json file
 */
public class ProfileManager {

  private List<Profile> profiles = new ArrayList<Profile>();
  private int currentProfileIndex = 0;
  private File profilesFile;

  public ProfileManager(String fileName) {
    profilesFile = new File(ProfileManager.class.getResource("/").getFile() + fileName);
    loadProfilesFromFile();
  }

  /**
   * Call this method when it is required to create and save (serialise) a new user profile to json
   * file
   *
   * @param name name of user profile
   * @param colour chosen colour
   * @return boolean to indicate if creation was successful or not
   * @throws IOException
   * @throws URISyntaxException
   */
  public boolean createProfile(String name, String colour) throws IOException, URISyntaxException {

    // check if username already exists, return false
    if (!profileWithNameAlreadyExists(name)) {
      // username is unique and new user can successfully be created, return true

      // creation of new user instance, adding to our user list
      Profile newProfile = new Profile(name, colour);
      profiles.add(newProfile);

      // lets assume we want to use the newly created profile
      setCurrentProfile(newProfile.getID());

      saveProfilesToFile();

      return true;
    }

    return false;
  }

  /**
   * Use this method to update the username of the current user profile in use
   *
   * @param newName new username to update to
   * @return boolean to indicate if update was successful or not
   * @throws IOException
   * @throws URISyntaxException
   */
  public boolean updateUserName(String newName) throws IOException, URISyntaxException {

    // checking if username already exists, return false if it does
    if (profileWithNameAlreadyExists(newName)) {

      return false;
    } else { // username is unique, return true
      profiles.get(currentProfileIndex).updateName(newName);
      saveProfilesToFile();

      return true;
    }
  }

  /**
   * Use this method to set the reference of currentUser to wanted user profile / object Can also be
   * used to switch between user profiles
   *
   * @param userID unique ID of the User profile we want to set to / use
   * @throws IOException
   * @throws URISyntaxException
   */
  public void setCurrentProfile(UUID userID) {

    // iterate through and find user object with same ID and update our currentUserIndex instance
    // field
    for (int i = 0; i < profiles.size(); i++) {

      // checks if the two UUID's are equal
      if (profiles.get(i).getID().equals(userID)) {
        currentProfileIndex = i;
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
   * @throws URISyntaxException
   */
  public List<Profile> getProfiles() {
    return profiles;
  }

  /**
   * Getter method to get the current User object profile in use
   *
   * @return object of class User that is the current profile being used
   * @throws IOException
   * @throws URISyntaxException
   */
  public Profile getCurrentProfile() {
    return profiles.get(currentProfileIndex);
  }

  /**
   * This method will handle serialising / saving to json file
   *
   * @throws IOException
   */
  public void saveProfilesToFile() {

    try {
      Writer writer = new FileWriter(profilesFile);
      Gson gson = new GsonBuilder().create();
      gson.toJson(profiles, writer);

      // flushing and closing the writer (cleaning)
      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.out.println(
          "There was an unexpected error saving profiles to file! - " + e.getMessage());
    }
  }

  /**
   * This method will handle deserialsing / loading from json file
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  private void loadProfilesFromFile() {

    // deserialising file containing existing profiles
    // de-serialisation

    try {
      profilesFile.createNewFile();
      Reader reader = new FileReader(profilesFile);
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<Profile>>() {}.getType();

      // converting json string to arraylist of user objects
      profiles = gson.fromJson(reader, listType);

      // close reader
      reader.close();
    } catch (IOException e) {
      System.out.println("There was an error loading from the json file " + e.getMessage());
    }

    // users will return null if file is empty in this case users should return empty
    if (profiles == null) {
      profiles = new ArrayList<Profile>();
    }
  }

  /**
   * Helper method to check for duplicate usernames
   *
   * @return boolean - True if a duplicate exists
   * @throws IOException
   * @throws URISyntaxException
   */
  private boolean profileWithNameAlreadyExists(String userName) {

    // iterate through all users and if username already exists, return true
    for (Profile user : profiles) {
      if (user.getName().equals(userName)) {
        return true;
      }
    }

    return false;
  }
}
