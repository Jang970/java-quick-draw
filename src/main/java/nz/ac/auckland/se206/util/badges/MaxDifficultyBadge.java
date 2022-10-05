package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

public class MaxDifficultyBadge implements Badge {

  private String name = "Max Difficulty";
  private String description = "Won a game on max difficulty for each setting";
  private GameEndInfo gameInfo;

  public MaxDifficultyBadge(GameEndInfo gameInfo) {
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
