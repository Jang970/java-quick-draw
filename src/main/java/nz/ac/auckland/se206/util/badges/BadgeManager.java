package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/** This class will handle anything to do with the badges */
public class BadgeManager {

  List<Badge> badges = new ArrayList<>();

  // instantiation of all badges through creation of BadgeManager instance
  public BadgeManager(GameEndInfo gameInfo) {

    this.badges = BadgeFactory.createListOfBadges(gameInfo);
  }

  /**
   * This method will return a list of all badges that have been earned. Will give null if no badges
   * have been earned
   *
   * @return list of badges earned
   */
  public List<Badge> getAllBadgesEarned() {

    List<Badge> badgesEarned = new ArrayList<>();

    // loop through all badges and append to our new list if their requirement has been met
    for (Badge badge : badges) {

      if (badge.isEarned()) {

        badgesEarned.add(badge);
      }
    }

    if (badgesEarned.isEmpty()) {
      return null;
    }

    return badgesEarned;
  }
}
