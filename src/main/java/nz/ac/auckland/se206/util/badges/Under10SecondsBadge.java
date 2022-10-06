package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class Under10SecondsBadge extends Badge {

  //  private int timeTaken;
  public Under10SecondsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "under10Seconds";
    this.description = "Won a game in under 10 seconds";
    // this.timeTaken = gameInfo.getTimeTaken();
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
