package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class MaxDifficultyBadge extends Badge {

  public MaxDifficultyBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "maxDifficulty";
    this.description = "Won a game on max difficulty for each setting";
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }
}
