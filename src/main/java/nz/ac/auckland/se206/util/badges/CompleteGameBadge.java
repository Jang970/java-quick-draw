package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class CompleteGameBadge extends Badge {

  public CompleteGameBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "Complete Game";
    this.description = "Earned all badges available";
    this.id = 5;
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
