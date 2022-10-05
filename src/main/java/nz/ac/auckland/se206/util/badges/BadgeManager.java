package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/** This class will handle anything to do with the badges */
public class BadgeManager {

  List<Badge> badges = new ArrayList<>();

  public BadgeManager(GameEndInfo gameInfo) {

    this.badges = BadgeFactory.createListOfBadges(gameInfo);
  }

  /**
   * This method allows you to get the name of a specific badge
   *
   * @param badgeId ID of the badge you want
   * @return name of badge
   */
  public String getBadgeName(int badgeId) {

    for (Badge badge : badges) {

      if (badge.getId() == badgeId) {

        return badge.getName();
      }
    }

    return null;
  }

  /**
   * This method allows you to get the description of a badge
   *
   * @param badgeId ID of the badge
   * @return description of badge
   */
  public String getBadgeDesc(int badgeId) {

    for (Badge badge : badges) {

      if (badge.getId() == badgeId) {

        return badge.getDescription();
      }
    }

    return null;
  }
}
