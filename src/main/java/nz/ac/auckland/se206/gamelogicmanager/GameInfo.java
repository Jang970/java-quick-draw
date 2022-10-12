package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Settings;

public class GameInfo {

  private EndGameState winState;
  private List<CategoryPlayedInfo> categoriesPlayed;
  private Settings settings;
  private GameMode gameMode;

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

  public EndGameState getWinState() {
    return winState;
  }

  public List<CategoryPlayedInfo> getCategoriesPlayed() {
    return categoriesPlayed;
  }

  public CategoryPlayedInfo getCategoryPlayed() {
    return categoriesPlayed.get(0);
  }

  public Settings getSettings() {
    return settings;
  }

  public GameMode getGameMode() {
    return gameMode;
  }
}
