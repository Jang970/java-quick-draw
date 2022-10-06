package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class TenConsecutiveWinsBadge extends Badge {

  public TenConsecutiveWinsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "10ConsecutiveWins";
    this.description = "Won 10 games in a row";
  }

  @Override
  public Boolean isEarned() {
    // TODO: implement logic
    return null;
  }
}
