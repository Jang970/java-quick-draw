package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;
import nz.ac.auckland.se206.util.Profile;

/**
 * This class will handle the badges, intention is to always be creating an instance of this class
 * whenever the app is run so that we will always end up creating all the badges
 */
public class BadgeManager {

  List<Badge> badges = new ArrayList<>();
  // this list of booleans will store the result of each badge's isEarned
  // will be useful when we want to save / load a profile's earned badges as we do not have to
  // figure out how to save enums using gson
  // we can just instead save a list of booleans and load badges earned from that
  private List<Boolean> badgesStatus = new ArrayList<>();

  // instantiation of all badges through creation of BadgeManager instance
  // also initialise all badges earned status to be false
  public BadgeManager(GameEndInfo gameInfo) {

    this.badges = BadgeFactory.createListOfBadges(gameInfo);
    initialiseBadgesStatusField();
  }

  /**
   * This method will return a list of all badges that have been earned. Will give null if no badges
   * have been earned
   *
   * <p>If a profile has logged back in, you would ideally use loadBadgesStatus first and then this
   * method
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

  public List<Badge> getAllBadges() {
    return this.badges;
  }

  /**
   * This method can be used when we want to check and save badges earned by a profile into our
   * profile class so that it can be save in our json file
   *
   * @param currentProfile profile we want to save to
   */
  public void updateAndSaveBadgesStatus(Profile currentProfile) {

    for (int i = 0; i < badges.size(); i++) {

      if (badges.get(i).isEarned()) {
        badgesStatus.set(i, true);
      }
    }
    currentProfile.setBadgesStatus(badgesStatus);
  }

  /**
   * This method can be used when we want to load in a profile's existing badges earned. Ideally to
   * be used after updating and saving badgesStatus at first run. You can then use this to get a
   * profile's existing badges if wanted.
   *
   * @param currentProfile
   */
  public void loadBadgesStatus(Profile currentProfile) {

    this.badgesStatus = currentProfile.getBadgesStatus();
  }

  public List<Boolean> getBadgesStatus() {
    return this.badgesStatus;
  }

  // simple helper method that sets all fields in List to false
  private void initialiseBadgesStatusField() {

    for (int i = 0; i < badges.size(); i++) {

      badgesStatus.add(false);
    }
  }
}
