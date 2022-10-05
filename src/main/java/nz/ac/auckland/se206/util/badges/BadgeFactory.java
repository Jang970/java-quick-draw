package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/** Making use of Factory design pattern to handle creation of badges */
public class BadgeFactory {

  public static Badge createBadge(int type, GameEndInfo gameInfo) {

    switch (type) {
      case 1:
        return new TimeBadge(gameInfo);

      case 2:
        return new AllCategoriesBadge(gameInfo);

      case 3:
        return new MaxDifficultyBadge(gameInfo);

      case 4:
        return new TwoSecondsLeftBadge(gameInfo);

      case 5:
        return new CompleteGameBadge(gameInfo);

      default:
        System.out.println("Error! No Badge of that type found");
        return null;
    }
  }

  // method to create all badges and store in a list
  public static List<Badge> createListOfBadges(GameEndInfo gameInfo) {

    List<Badge> badges = new ArrayList<>();

    badges.add(new TimeBadge(gameInfo));
    badges.add(new AllCategoriesBadge(gameInfo));
    badges.add(new MaxDifficultyBadge(gameInfo));
    badges.add(new TwoSecondsLeftBadge(gameInfo));
    badges.add(new CompleteGameBadge(gameInfo));

    return badges;
  }
}
