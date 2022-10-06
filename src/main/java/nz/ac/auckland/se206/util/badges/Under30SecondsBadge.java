package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class Under30SecondsBadge extends Badge {

  //  private int timeTaken;

  public Under30SecondsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "under30Seconds";
    this.description = "Won a game in under 30 seconds";
    // this.timeTaken = gameInfo.getTimeTaken();
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
