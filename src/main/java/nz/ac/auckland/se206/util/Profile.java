package nz.ac.auckland.se206.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This class will be used to create objects for each new profile containing all information related
 * to that specific profile. We will be using the concept of json serialisation and de-serialisation
 * in order to save and load exisiting profiles in our app.
 */
public class Profile {

  private String name;
  private UUID id;
  private String colour;

  private int gamesWon = 0;
  private int gamesLost = 0;
  private int fastestWinTime = -1;
  private String categoryOfFastestWin;
  private Set<String> categoryHistory = new HashSet<String>();
  private int numberOfHistoryResets = 0;
  private DifficultySettings difficultySettings = new DifficultySettings();

  public Profile(String name, String colour) {

    this.name = name;
    this.id = UUID.randomUUID();
    this.colour = colour;
  }

  public String getName() {
    return name;
  }

  public UUID getId() {
    return id;
  }

  public String getColour() {
    return colour;
  }

  public DifficultySettings getSettings() {
    return this.difficultySettings;
  }

  public void updateName(String newName) {
    name = newName;
  }

  public void updateColour(String newColour) {
    colour = newColour;
  }

  public void incrementGamesWon() {
    gamesWon++;
  }

  public void incrementGamesLost() {
    gamesLost++;
  }

  /**
   * This method will update the fastestWin and bestCategory variables Can be called when the player
   * wins at a faster time than the current time stored in fastestWin
   *
   * @param newPossibleFastestWinTime the time to update fastestWin to
   * @param category the word that the profile had to draw
   * @return true or false depending if update was successful or not
   */
  public Boolean updateFastestGameIfBeatsCurrent(int newPossibleFastestWinTime, String category) {

    // This should be fairly self explanatory
    if (newPossibleFastestWinTime < fastestWinTime || fastestWinTime == -1) {
      fastestWinTime = newPossibleFastestWinTime;
      categoryOfFastestWin = category;

      return true;
    } else {
      return false;
    }
  }

  /**
   * This method will be used to add the current category/word to draw to list of previous words Can
   * be called everytime a new category appears
   *
   * @param category current category/word that profile must draw
   */
  public void addToCategoryHistory(String category) {
    categoryHistory.add(category);
  }

  /** Use this to reset the category history to 0 and increment the number of resets by 1 */
  public void resetCategoryHistory() {
    categoryHistory.clear();
    numberOfHistoryResets++;
  }

  /**
   * @return the number of times the category history was reset
   */
  public int getNumResets() {
    return numberOfHistoryResets;
  }

  public int getGamesWon() {
    return gamesWon;
  }

  public int getGamesLost() {
    return gamesLost;
  }

  /**
   * If the player has not had a fastest win, this will be -1
   *
   * @return the fastest win time
   */
  public int getFastestWin() {
    return fastestWinTime;
  }

  public String getCategoryOfFastestWin() {
    return categoryOfFastestWin;
  }

  public Set<String> getCategoryHistory() {
    return categoryHistory;
  }
}
