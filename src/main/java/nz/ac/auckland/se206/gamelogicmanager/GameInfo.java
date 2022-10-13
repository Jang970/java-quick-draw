package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Settings;

public class GameInfo {

  private EndGameState winState;
  private List<CategoryPlayedInfo> categoriesPlayed;
  private Settings settings;
  private GameMode gameMode;

  /**
   * This is the GameInfo constructor which stores the relevant game info needed to award badges
   *
   * @param winState if player won lost or other
   * @param categoriesPlayed all categories played
   * @param settingsUsed the difficulties the game was played
   * @param gameMode the gamemode the game was played on
   */
  GameInfo(
      EndGameState winState,
      List<CategoryPlayedInfo> categoriesPlayed,
      Settings settingsUsed,
      GameMode gameMode) {
    this.winState = winState;
    this.categoriesPlayed = categoriesPlayed;
    this.settings = settingsUsed;
    this.gameMode = gameMode;
  }

  /**
   * This method will get the status of game, if it was a win, loss or not applicable
   *
   * @return win state of the game
   */
  public EndGameState getWinState() {
    return winState;
  }

  /**
   * This method will a list of categories played
   *
   * @return list of categories played
   */
  public List<CategoryPlayedInfo> getCategoriesPlayed() {
    return categoriesPlayed;
  }

  /**
   * This method will get the category played from the list of categories played at index 0
   *
   * @return category stored in index 0 of the list of categories
   */
  public CategoryPlayedInfo getCategoryPlayed() {
    return categoriesPlayed.get(0);
  }

  /**
   * This method will get the settings used during the game
   *
   * @return settings used during the game
   */
  public Settings getSettings() {
    return settings;
  }

  /**
   * This method will get the gamemode of the game played
   *
   * @return gamemode of the game played
   */
  public GameMode getGameMode() {
    return gameMode;
  }
}
