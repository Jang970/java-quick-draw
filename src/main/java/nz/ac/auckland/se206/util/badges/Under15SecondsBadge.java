package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class Under15SecondsBadge extends Badge {

  //  private int timeTaken;

  public Under15SecondsBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "under15Seconds";
    this.description = "Won a game in under 15 seconds";
    // this.timeTaken = gameInfo.getTimeTaken();
  }

  @Override
  public Boolean isEarned() {
    return (gameInfo.winState == EndGameState.WIN) && (gameInfo.timeTaken < 15);
  }
}
