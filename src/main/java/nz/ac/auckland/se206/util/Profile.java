package nz.ac.auckland.se206.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

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

  private GameEndInfo fastestGame;

  private List<GameEndInfo> previousGames = new ArrayList<GameEndInfo>();

  private int numberOfHistoryResets = 0;

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
  public void addGameToHistory(GameEndInfo gameInfo) {

    // This should be fairly self explanatory
    if (gameInfo.winState == EndGameState.WIN
        && (fastestGame == null || gameInfo.timeTaken < fastestGame.timeTaken)) {
      fastestGame = gameInfo;
    }

    previousGames.add(gameInfo);
  }

  /** Use this to reset the category history to 0 and increment the number of resets by 1 */
  public void resetStats() {
    previousGames.clear();

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
  public GameEndInfo getFastestGame() {
    return this.fastestGame;
  }

  public Set<String> getCategoryHistory() {
    return previousGames.stream()
        .map((game) -> game.category.categoryString)
        .collect(Collectors.toSet());
  }
}
