package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class CompleteGameBadge implements Badge {

  private String name = "Complete Game";
  private String description = "Earned all badges available";
  private GameEndInfo gameInfo;

  public CompleteGameBadge(GameEndInfo gameInfo) {
    this.gameInfo = gameInfo;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
