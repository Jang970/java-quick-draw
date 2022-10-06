package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class BeatTimeBadge extends Badge {

  public BeatTimeBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "beatTime";
    this.description = "Beat your previous fastest win time";
  }

  @Override
  public Boolean isEarned() {

    // TODO: implement logic
    return null;
  }
}
