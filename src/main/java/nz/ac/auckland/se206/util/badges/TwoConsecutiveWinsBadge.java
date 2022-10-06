package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class TwoConsecutiveWinsBadge extends Badge {

  public TwoConsecutiveWinsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "2ConsecutiveWins";
    this.description = "Won 2 games in a row";
  }

  @Override
  public Boolean isEarned() {
    // TODO: implement logic
    return null;
  }
}
