package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

/** Making use of Factory design pattern to handle creation of badges */
public class BadgeFactory {

  // method to create all badges and store in a list
  public static HashMap<String, Badge> createBadgeList() {

    List<Badge> tempBadges = new ArrayList<Badge>();

    tempBadges.add(createNConsecutiveWinBadge(2));
    tempBadges.add(createNConsecutiveWinBadge(5));
    tempBadges.add(createNConsecutiveWinBadge(10));
    tempBadges.add(createNConsecutiveWinBadge(15));
    tempBadges.add(createNConsecutiveWinBadge(20));

    tempBadges.add(createJustInTimeBadge());

    tempBadges.add(createUnderNSecondsBadge(1));
    tempBadges.add(createUnderNSecondsBadge(2));
    tempBadges.add(createUnderNSecondsBadge(5));
    tempBadges.add(createUnderNSecondsBadge(10));

    tempBadges.add(createMaxDifficultyBadge());

    HashMap<String, Badge> badges = new HashMap<String, Badge>();

    for (Badge badge : tempBadges) {
      badges.put(badge.getId(), badge);
    }

    return badges;
  }

  private static Badge createMaxDifficultyBadge() {
    return new Badge(
        "max_dif",
        "Maximum difficulty",
        "The player won a game on the hardest difficulty settings") {

      @Override
      public boolean earned(List<GameEndInfo> gameHistory) {
        return false;
      }
    };
  }

  private static Badge createNConsecutiveWinBadge(int n) {
    return new Badge(
        "consec_" + "n", n + " Consecutive Wins", "The player won " + n + " games consecutively") {

      @Override
      public boolean earned(List<GameEndInfo> gameHistory) {
        if (gameHistory.size() >= n) {
          for (int i = 0; i < n; i++) {
            if (gameHistory.get(i).winState != EndGameState.WIN) {
              return false;
            }
          }
          return true;
        }
        return false;
      }
    };
  }

  private static Badge createUnderNSecondsBadge(int n) {
    return new Badge(
        "under" + n + "sec",
        "Under " + n + "seconds",
        "The player won a game in less than " + n + " seconds") {

      @Override
      public boolean earned(List<GameEndInfo> gameHistory) {
        return gameHistory.get(0).timeTaken <= n;
      }
    };
  }

  private static Badge createJustInTimeBadge() {
    return new Badge("just_in", "Just in time", "The player had lest then 2 seconds remaining") {

      @Override
      public boolean earned(List<GameEndInfo> gameHistory) {
        return gameHistory.get(0).secondsRemaining <= 2;
      }
    };
  }
}
