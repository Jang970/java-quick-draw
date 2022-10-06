package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class FiveConsecutiveWinsBadge extends Badge {

  public FiveConsecutiveWinsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "5ConsecutiveWins";
    this.description = "Won 5 games in a row";
  }

  @Override
  public Boolean isEarned() {
    // TODO: implement logic
    return null;
  }
}
