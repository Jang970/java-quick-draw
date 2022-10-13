package nz.ac.auckland.se206.util.badges;

import java.util.HashMap;
import java.util.List;

/**
 * This class will handle the badges, intention is to always be creating an instance of this class
 * whenever the app is run so that we will always end up creating all the badges
 */
public class BadgeManager {

  private final HashMap<String, Badge> badgeIdMap;
  private final List<Badge> badgeList;

  /**
   * instantiation of all badges through creation of BadgeManager instance also initialise all
   * badges earned status to be false
   */
  public BadgeManager() {
    this.badgeList = BadgeFactory.createBadgeList();

    badgeIdMap = new HashMap<String, Badge>();
    for (Badge badge : badgeList) {
      badgeIdMap.put(badge.getId(), badge);
    }
  }

  /**
   * This method will get all badges in our app
   *
   * @return list of all badges
   */
  public List<Badge> getAllBadges() {
    return this.badgeList;
  }

  /**
   * This method will allow you to get a badge using its unique ID
   *
   * @param badgeId id of the badge we want
   * @return badge we wanted
   * @throws InvalidBadgeIdException thrown when the ID entered does not have a badge associated
   *     with it
   */
  public Badge getBadgeFromId(String badgeId) throws InvalidBadgeIdException {
    if (badgeIdMap.containsKey(badgeId)) {
      return badgeIdMap.get(badgeId);
    } else {
      throw new InvalidBadgeIdException("There is no badge with this id");
    }
  }
}
