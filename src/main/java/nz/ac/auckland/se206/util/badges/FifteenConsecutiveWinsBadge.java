package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class FifteenConsecutiveWinsBadge extends Badge {

  public FifteenConsecutiveWinsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "15ConsecutiveWins";
    this.description = "Won 15 games in a row";
  }

  @Override
  public Boolean isEarned() {
    // TODO: implement logic
    return null;
  }
}
