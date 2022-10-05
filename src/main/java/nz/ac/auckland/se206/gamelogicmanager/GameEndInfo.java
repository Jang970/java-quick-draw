package nz.ac.auckland.se206.gamelogicmanager;

import nz.ac.auckland.se206.util.Category;

public class GameEndInfo {

  public EndGameState winState;
  public Category category;
  public int timeTaken;
  public int secondsRemaining;

  GameEndInfo(EndGameState winState, Category category, int timeTaken, int secondsRemaining) {
    this.winState = winState;
    this.category = category;
    this.timeTaken = timeTaken;
    this.secondsRemaining = secondsRemaining;
  }
}
