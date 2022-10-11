package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Category;
import nz.ac.auckland.se206.util.Settings;

public class GameInfo {

  public EndGameState winState;
  public List<Category> categoriesPlayed;
  public Settings settings;
  public GameMode gameMode;
  public int timeTaken;
  public int secondsRemaining;

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
}
