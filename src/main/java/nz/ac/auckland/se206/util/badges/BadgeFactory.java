package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/** Making use of Factory design pattern to handle creation of badges */
public class BadgeFactory {

  private static Badge createBadge(int badgeNumber, GameEndInfo gameInfo) {

    switch (badgeNumber) {
      case 0:
        return new BeatTimeBadge(gameInfo);

      case 1:
        return new AllCategoriesBadge(gameInfo);

      case 2:
        return new MaxDifficultyBadge(gameInfo);

      case 3:
        return new JustInTimeBadge(gameInfo);

      case 4:
        return new CompleteGameBadge(gameInfo);

      case 5:
        return new Under5SecondsBadge(gameInfo);

      case 6:
        return new Under10SecondsBadge(gameInfo);

      case 7:
        return new Under15SecondsBadge(gameInfo);

      case 8:
        return new Under30SecondsBadge(gameInfo);

      case 9:
        return new TwoConsecutiveWinsBadge(gameInfo);

      case 10:
        return new FiveConsecutiveWinsBadge(gameInfo);

      case 11:
        return new TenConsecutiveWinsBadge(gameInfo);

      case 12:
        return new FifteenConsecutiveWinsBadge(gameInfo);

      default:
        System.out.println("Error! Ran out of badges.");
        return null;
    }
  }

  // method to create all badges and store in a list
  public static List<Badge> createListOfBadges(GameEndInfo gameInfo) {

    List<Badge> badges = new ArrayList<>();

    int numberOfBadges = 13;
    for (int i = 0; i < numberOfBadges; i++) {

      badges.add(createBadge(i, gameInfo));
    }

    return badges;
  }
}
