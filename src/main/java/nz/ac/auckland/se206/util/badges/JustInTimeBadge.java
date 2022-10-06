package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

public class JustInTimeBadge extends Badge {

  public JustInTimeBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "justInTime";
    this.description = "Won a game with two seconds left on the timer";
  }

  @Override
  public Boolean isEarned() {
    // TODO: implement logic
    return null;
  }
}
