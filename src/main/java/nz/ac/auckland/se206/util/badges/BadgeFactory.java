package nz.ac.auckland.se206.util.badges;

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
}
