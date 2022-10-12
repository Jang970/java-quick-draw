package nz.ac.auckland.se206.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nz.ac.auckland.se206.App;

/**
 * This class will be used to handle any functionalities we would like to do with User class
 * objects/instances An example would be saving a newly created user profile into our json file
 */
public class ProfileManager {

  private class ProfileListSaveObject {
    protected List<Profile> profileList;
    protected int activeProfileIndex;

    /**
     * Constructor for ProfileListSaveObject which we use to save to our json the list of profiles
     * and current profile in use.
     *
     * @param profileList list of all profiles
     * @param activeProfileIndex indicator of which profile in the list is being used
     */
    protected ProfileListSaveObject(List<Profile> profileList, int activeProfileIndex) {
      this.profileList = profiles;
      this.activeProfileIndex = activeProfileIndex;
    }
  }

  private List<Profile> profiles = new ArrayList<Profile>();
  private int currentProfileIndex = 0;
  private File profilesFile;
  private EmptyEventListener profileUpdateListener =
      () -> {
        this.saveChanges();
      };
  private CountdownTimer changeSaveCountdown = new CountdownTimer();

  /**
   * Constructor for ProfileManager class which will be used to handle all functionalities relating
   * to profiles for example, profile creation, saving and loading existing profiles.
   *
   * @param fileNameFullPath file path where we want to create the new file
   * @throws IOException - if the file exists but is a directory rather than a regular file, does
   *     not exist but cannot be created, or cannot be opened for any other reason
   */
  public ProfileManager(String fileNameFullPath) throws IOException {
    profilesFile = new File(fileNameFullPath);
    if (profilesFile.isDirectory()) {
      throw new IOException("File " + fileNameFullPath + " is a directory, not a json file");
    }
    loadProfilesFromFile();

    changeSaveCountdown.setOnComplete(
        () -> {
          saveProfilesToFile();
        });
  }

  /**
   * Call this method when it is required to create and save (serialise) a new user profile to json
   * file
   *
   * @param name name of user profile
   * @param colour chosen colour
   * @return boolean to indicate if creation was successful or not
   */
  public boolean createProfile(String name, String colour) {

    // check if username already exists, return false
    if (!profileWithNameAlreadyExists(name)) {
      // username is unique and new user can successfully be created, return true

      // creation of new user instance, adding to our user list
      Profile newProfile = new Profile(name, colour);
      newProfile.setOnChange(profileUpdateListener);

      profiles.add(newProfile);

      // lets assume we want to use the newly created profile
      setCurrentProfile(newProfile.getId());

      saveChanges();

      return true;
    }

    return false;
  }

  /**
   * Use this method to update the username of the current user profile in use
   *
   * @param newName new username to update to
   * @return boolean to indicate if update was successful or not
   */
  public boolean updateUserName(String newName) {

    // checking if username already exists, return false if it does
    if (profileWithNameAlreadyExists(newName)) {

      return false;
    } else { // username is unique, return true
      profiles.get(currentProfileIndex).updateName(newName);

      saveChanges();

      return true;
    }
  }

  /**
   * Use this method to set the reference of currentUser to wanted user profile / object Can also be
   * used to switch between user profiles
   *
   * @param userId unique ID of the User profile we want to set to / use
   */
  public void setCurrentProfile(UUID userId) {

    // iterate through and find user object with same ID and update our currentUserIndex instance
    // field
    for (int i = 0; i < profiles.size(); i++) {

      // checks if the two UUID's are equal
      if (profiles.get(i).getId().equals(userId)) {
        currentProfileIndex = i;
        break;
      }
    }

    saveChanges();
  }

  /**
   * Call this method when you want a list of all User objects that have been created and stored on
   * json file Can only call if the json file already exists
   *
   * @return arraylist of User objects
   */
  public List<Profile> getProfiles() {
    return profiles;
  }

  /**
   * Getter method to get the current User object profile in use
   *
   * @return object of class User that is the current profile being used
   */
  public Profile getCurrentProfile() {
    return profiles.get(currentProfileIndex);
  }

  private void saveChanges() {
    System.out.println("   * Requesting save change");
    changeSaveCountdown.startCountdown(1);
  }

  /** This method will handle serialising / saving to json file */
  private void saveProfilesToFile() {

    System.out.println(" + Saving profiles");

    Writer writer = null;
    try {
      writer = new FileWriter(profilesFile);
    } catch (IOException e) {
      App.expect("Profiles File should be guaranteed to not be a directory", e);
    }

    Gson gson = new GsonBuilder().create();
    gson.toJson(new ProfileListSaveObject(profiles, currentProfileIndex), writer);

    // flushing and closing the writer (cleaning)
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      // DO NOTHING, Does, not affect user experience
    }
  }

  /** This method will handle deserialsing / loading from json file */
  private void loadProfilesFromFile() {

    try {
      profilesFile.createNewFile();

      Reader reader = null;

      try {
        reader = new FileReader(profilesFile);
      } catch (FileNotFoundException e) {
        App.expect("File should definitely exist");
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ProfileListSaveObject>() {}.getType();

      // converting json string to arraylist of user objects
      ProfileListSaveObject obj = gson.fromJson(reader, listType);

      profiles = obj.profileList;

      for (Profile profile : profiles) {
        profile.setOnChange(profileUpdateListener);
      }

      currentProfileIndex = obj.activeProfileIndex;

      // close reader
      reader.close();

    } catch (Exception e) {
      // DO NOTHING
    }

    // users will return null if file is empty in this case users should be empty
    if (profiles == null) {
      profiles = new ArrayList<Profile>();
    }
  }

  /**
   * Helper method to check for duplicate usernames
   *
   * @return boolean - True if a duplicate exists
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
