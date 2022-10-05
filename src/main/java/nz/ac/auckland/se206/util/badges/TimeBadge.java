package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/** This badge will be contain the information regarding under x seconds win badge type. */
public class TimeBadge extends Badge {

  private int timeTaken;
  private int highestTier;

  public TimeBadge(GameEndInfo gameInfo) {
    super(gameInfo);
    this.name = "Under x Seconds";
    this.description = "Won a game in under x seconds";
    this.id = 1;
    this.timeTaken = gameInfo.getTimeTaken();
    this.highestTier = updateBadgeInfo();
  }

  @Override
  public Boolean isEarned() {
    // TODO Implement Logic
    return null;
  }

  // TODO: have to check if this method is plausible
  public int getHighestTier() {

    return this.highestTier;
  }

  // will update name and description with the highest tier taking the top priority
  // will also indicate which tier the badge is
  private int updateBadgeInfo() {

    if (timeTaken <= 5) {

      this.name = this.name.replace("x", "5");
      this.description = this.description.replace("x", "5");
      return 5;

    } else if ((timeTaken > 5) && (timeTaken <= 15)) {

      this.name = this.name.replace("x", "15");
      this.description = this.description.replace("x", "15");
      return 15;

    } else if ((timeTaken > 15) && (timeTaken <= 30)) {

      this.name = this.name.replace("x", "30");
      this.description = this.description.replace("x", "30");
      return 30;

    } else {

      return -1;
    }
  }
}
