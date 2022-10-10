package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

/**
 * This class will handle the badges, intention is to always be creating an instance of this class
 * whenever the app is run so that we will always end up creating all the badges
 */
public class BadgeManager {

  private final HashMap<String, Badge> badges;

  // instantiation of all badges through creation of BadgeManager instance
  // also initialise all badges earned status to be false
  public BadgeManager() {

    this.badges = BadgeFactory.createBadgeList();
  }

  public Collection<Badge> getBadgesFromGame(List<GameEndInfo> gameHistory) {
    List<Badge> earnedBadges = new ArrayList<Badge>();

    for (Badge badge : badges.values()) {
      if (badge.earned(gameHistory)) {
        earnedBadges.add(badge);
      }
    }

    return earnedBadges;
  }

  /**
   * @return
   */
  public Collection<Badge> getAllBadges() {
    return this.badges.values();
  }

  Badge getBadgeFromId(String badgeId) throws InvalidBadgeIdException {
    if (badges.containsKey(badgeId)) {
      return badges.get(badgeId);
    } else {
      throw new InvalidBadgeIdException("There is no badge with this id");
    }
  }
}
