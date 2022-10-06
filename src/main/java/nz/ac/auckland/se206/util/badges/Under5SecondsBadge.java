package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class Under5SecondsBadge extends Badge {

  //  private int timeTaken;

  public Under5SecondsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "under5Seconds";
    this.description = "Won a game in under 5 seconds";
    // this.timeTaken = gameInfo.getTimeTaken();
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
