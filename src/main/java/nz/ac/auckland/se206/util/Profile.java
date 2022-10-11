package nz.ac.auckland.se206.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;

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

  private GameInfo fastestGame;

  private List<GameInfo> gameHistory = new ArrayList<GameInfo>();

  private int numberOfHistoryResets = 0;
  private Settings difficultySettings = new Settings();

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

  public Settings getSettings() {
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
   * This method will be used to add the current category/word to draw to list of previous words Can
   * be called everytime a new category appears
   *
   * @param gameInfo current category/word that profile must draw
   */
  public void addGameToHistory(GameInfo gameInfo) {

    // This should be fairly self explanatory
    if (gameInfo.winState == EndGameState.WIN
        && (fastestGame == null || gameInfo.timeTaken < fastestGame.timeTaken)) {
      fastestGame = gameInfo;
    }

    gameHistory.add(gameInfo);
  }

  /** Use this to reset the category history to 0 and increment the number of resets by 1 */
  public void resetStats() {
    gameHistory.clear();

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
  public GameInfo getFastestGame() {
    return this.fastestGame;
  }

  public List<GameInfo> getGameHistory() {
    return gameHistory;
  }
}
