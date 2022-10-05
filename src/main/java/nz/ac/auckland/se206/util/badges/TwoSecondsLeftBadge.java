package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;
import nz.ac.auckland.se206.GameLogicManager.WinState;

public class TwoSecondsLeftBadge implements Badge {

  private String name = "Two Seconds Left";
  private String description = "Won a game with two seconds left on the timer";
  private GameEndInfo gameInfo;

  public TwoSecondsLeftBadge(GameEndInfo gameInfo) {
    this.gameInfo = gameInfo;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public Boolean isEarned() {
    return gameInfo.getSecondsRemaining() == 2 && gameInfo.getWinState() == WinState.WIN;
  }
}
