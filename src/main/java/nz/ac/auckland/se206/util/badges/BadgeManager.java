package nz.ac.auckland.se206.util.badges;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;

/**
 * This class will handle the badges, intention is to always be creating an instance of this class
 * whenever the app is run so that we will always end up creating all the badges
 */
public class BadgeManager {

  private final HashMap<String, Badge> badgeIdMap;
  private final List<Badge> badgeList;

  // instantiation of all badges through creation of BadgeManager instance
  // also initialise all badges earned status to be false
  public BadgeManager() {
    this.badgeList = BadgeFactory.createBadgeList();

    badgeIdMap = new HashMap<String, Badge>();
    for (Badge badge : badgeList) {
      badgeIdMap.put(badge.getId(), badge);
    }
  }

  public Set<Badge> getBadgesFromGame(List<GameInfo> gameHistory) {
    Set<Badge> earnedBadges = new HashSet<Badge>();

    for (Badge badge : badgeList) {
      if (badge.earned(gameHistory)) {
        earnedBadges.add(badge);
      }
    }

    return earnedBadges;
  }

  /**
   * @return
   */
  public List<Badge> getAllBadges() {
    return this.badgeList;
  }

  Badge getBadgeFromId(String badgeId) throws InvalidBadgeIdException {
    if (badgeIdMap.containsKey(badgeId)) {
      return badgeIdMap.get(badgeId);
    } else {
      throw new InvalidBadgeIdException("There is no badge with this id");
    }
  }
}
