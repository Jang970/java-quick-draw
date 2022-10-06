package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;
import nz.ac.auckland.se206.GameLogicManager.WinState;

public class JustInTimeBadge extends Badge {

  public JustInTimeBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "justInTime";
    this.description = "Won a game with two seconds left on the timer";
    this.id = 2;
  }

  @Override
  public Boolean isEarned() {
    return this.gameInfo.getSecondsRemaining() == 2 && this.gameInfo.getWinState() == WinState.WIN;
  }
}
