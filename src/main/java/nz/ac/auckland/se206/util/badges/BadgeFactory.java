package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;

/** Making use of Factory design pattern to handle creation of badges */
public class BadgeFactory {

  // method to create all badges and store in a list
  public static List<Badge> createBadgeList() {

    List<Badge> badges = new ArrayList<Badge>();

    badges.add(createNConsecutiveWinBadge(2));
    badges.add(createNConsecutiveWinBadge(5));
    badges.add(createNConsecutiveWinBadge(10));
    badges.add(createNConsecutiveWinBadge(15));
    badges.add(createNConsecutiveWinBadge(20));

    badges.add(createJustInTimeBadge());

    badges.add(createUnderNSecondsBadge(1));
    badges.add(createUnderNSecondsBadge(2));
    badges.add(createUnderNSecondsBadge(5));
    badges.add(createUnderNSecondsBadge(10));

    badges.add(createMaxDifficultyBadge());

    return badges;
  }

  private static Badge createMaxDifficultyBadge() {
    return new Badge(
        "max_dif",
        "Maximum difficulty",
        "The player won a game on the hardest difficulty settings") {

      @Override
      public boolean earned(List<GameInfo> gameHistory) {
        return false;
      }
    };
  }

  private static Badge createNConsecutiveWinBadge(int n) {
    return new Badge(
        "consec_" + "n", n + " Consecutive Wins", "The player won " + n + " games consecutively") {

      @Override
      public boolean earned(List<GameInfo> gameHistory) {
        if (gameHistory.size() >= n) {
          for (int i = 0; i < n; i++) {
            if (gameHistory.get(i).getWinState() != EndGameState.WIN) {
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
      public boolean earned(List<GameInfo> gameHistory) {
        return gameHistory.get(0).getTimeTaken() <= n;
      }
    };
  }

  private static Badge createJustInTimeBadge() {
    return new Badge("just_in", "Just in time", "The player had lest then 2 seconds remaining") {

      @Override
      public boolean earned(List<GameInfo> gameHistory) {
        return gameHistory.get(0).getSecondsRemaining() <= 2;
      }
    };
  }
}
