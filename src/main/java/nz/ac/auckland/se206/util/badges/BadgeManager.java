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
}
