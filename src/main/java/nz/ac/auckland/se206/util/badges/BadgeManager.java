package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

/** This class will handle anything to do with the badges */
public class BadgeManager {

  List<Badge> badges = new ArrayList<>();
  // this list of booleans will store the result of each badge's isEarned
  // will be useful when we want to save / load a profile's earned badges as we do not have to
  // figure out how to save enums using gson
  // we can just instead save a list of booleans and load badges earned from that
  List<Boolean> badgesStatus = new ArrayList<>();

  // instantiation of all badges through creation of BadgeManager instance
  public BadgeManager(GameEndInfo gameInfo) {

    this.badges = BadgeFactory.createListOfBadges(gameInfo);
    initialiseBadgesStatusField();
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
    for (int i = 0; i < badges.size(); i++) {

      if (badgesStatus.get(i)) {
        badgesEarned.add(badges.get(i));
      }
    }

    if (badgesEarned.isEmpty()) {
      return null;
    }

    return badgesEarned;
  }

  public void saveBadgesStatus() {

    for (int i = 0; i < badges.size(); i++) {

      if (badges.get(i).isEarned()) {
        badgesStatus.set(i, true);
      }
    }
  }

  public void loadBadgesStatus() {

    List<Badge> badgesEarned = new ArrayList<>();

    for (int i = 0; i < badges.size(); i++) {

      if (badgesStatus.get(i)) {
        badgesEarned.add(badges.get(i));
      }
    }
  }

  private void initialiseBadgesStatusField() {

    for (int i = 0; i < badges.size(); i++) {

      badgesStatus.add(false);
    }
  }
}
