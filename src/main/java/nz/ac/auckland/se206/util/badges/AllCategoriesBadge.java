package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class AllCategoriesBadge extends Badge {

  public AllCategoriesBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "All Categories";
    this.description = "Played all categories of all difficulties";
    this.id = 3;
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
