package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Settings;

public class GameInfo {

  private EndGameReason reasonForGameEnd;
  private List<CategoryPlayedInfo> categoriesPlayed;
  private CategoryPlayedInfo categoryPlayed;
  private Settings settings;
  private GameMode gameMode;

  GameInfo(
      EndGameReason winState,
      List<CategoryPlayedInfo> categoriesPlayed,
      Settings settingsUsed,
      GameMode gameMode) {

    assert gameMode != GameMode.RAPID_FIRE
        : "Only the rapid fire game mode takes a list of categories";

    this.gameMode = gameMode;
    this.reasonForGameEnd = winState;
    this.categoriesPlayed = categoriesPlayed;
    this.settings = settingsUsed;
  }

  GameInfo(
      EndGameReason winState,
      CategoryPlayedInfo categoryPlayed,
      Settings settingsUsed,
      GameMode gameMode) {

    assert gameMode != GameMode.RAPID_FIRE : "Rapid fire must take a list of categories played";

    this.gameMode = gameMode;
    this.reasonForGameEnd = winState;
    this.categoryPlayed = categoryPlayed;
    this.settings = settingsUsed;
  }

  /**
   * This method will get the status of game, if it was a win, loss or not applicable
   *
   * @return win state of the game
   */
  public EndGameReason getReasonForGameEnd() {
    return reasonForGameEnd;
  }

  /**
   * This method will a list of categories played
   *
   * @return list of categories played
   */
  public List<CategoryPlayedInfo> getCategoriesPlayed() {
    assert gameMode == GameMode.RAPID_FIRE
        : "List of categories played only applies to rapid fire game mode";

    return categoriesPlayed;
  }

  /**
   * This method will get the category played from the list of categories played at index 0
   *
   * @return category stored in index 0 of the list of categories
   */
  public CategoryPlayedInfo getCategoryPlayed() {
    assert gameMode != GameMode.RAPID_FIRE : "Rapid fire does not have a single category played";
    return categoryPlayed;
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
