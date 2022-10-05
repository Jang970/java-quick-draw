package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class AllCategoriesBadge implements Badge {

  private String name = "All Categories";
  private String description = "Played all categories of all difficulties";
  private GameEndInfo gameInfo;

  public AllCategoriesBadge(GameEndInfo gameInfo) {
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
