package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;
import nz.ac.auckland.se206.GameLogicManager.WinState;

public class TwoSecondsLeftBadge extends Badge {

  public TwoSecondsLeftBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "Two Seconds Left";
    this.description = "Won a game with two seconds left on the timer";
  }

  @Override
  public Boolean isEarned() {
    return this.gameInfo.getSecondsRemaining() == 2 && this.gameInfo.getWinState() == WinState.WIN;
  }
}
