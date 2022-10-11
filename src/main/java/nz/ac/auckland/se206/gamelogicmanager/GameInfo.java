package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Category;
import nz.ac.auckland.se206.util.Settings;

public class GameInfo {

  private EndGameState winState;
  private List<Category> categoriesPlayed;
  private Settings settings;
  private GameMode gameMode;
  private int timeTaken;
  private int secondsRemaining;

  GameInfo(
      EndGameState winState,
      List<Category> categoriesPlayed,
      int timeTaken,
      int secondsRemaining,
      Settings settingsUsed,
      GameMode gameMode) {
    this.winState = winState;
    this.categoriesPlayed = categoriesPlayed;
    this.settings = settingsUsed;
    this.gameMode = gameMode;
    this.timeTaken = timeTaken;
    this.secondsRemaining = secondsRemaining;
  }

  public EndGameState getWinState() {
    return winState;
  }

  public List<Category> getCategoriesPlayed() {
    return categoriesPlayed;
  }

  public Settings getSettings() {
    return settings;
  }

  public GameMode getGameMode() {
    return gameMode;
  }

  public int getTimeTaken() {
    return timeTaken;
  }

  public int getSecondsRemaining() {
    return secondsRemaining;
  }
}
