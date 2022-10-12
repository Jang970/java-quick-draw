package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;
import nz.ac.auckland.se206.util.Profile;
import nz.ac.auckland.se206.util.Settings;
import nz.ac.auckland.se206.util.difficulties.Accuracy;
import nz.ac.auckland.se206.util.difficulties.Confidence;
import nz.ac.auckland.se206.util.difficulties.Time;
import nz.ac.auckland.se206.util.difficulties.WordChoice;

/** Making use of Factory design pattern to handle creation of badges */
public class BadgeFactory {

  private static Set<String> badgeIds;

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
    badges.add(createGotAllBadgesBadge());

    for (Badge badge : badges) {
      if (badgeIds.contains(badge.getId())) {
        App.expect("Each badge ids should be unique");
      }
      badgeIds.add(badge.getId());
    }

    return badges;
  }

  private static Badge createGotAllBadgesBadge() {
    return new Badge("won_all", "Won All Badges", "The player has won every badge in the game") {

      @Override
      public boolean earned(Profile profile) {
        Set<String> profileBadgeIds = profile.getEarnedBadgeIds();
        for (String badgeId : BadgeFactory.badgeIds) {
          if (!badgeId.equals("won_all") && !profileBadgeIds.contains(badgeId)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  private static Badge createMaxDifficultyBadge() {
    return new Badge(
        "max_dif",
        "Maximum difficulty",
        "The player won a game on the hardest difficulty settings") {

      @Override
      public boolean earned(Profile profile) {
        Settings settings = profile.getMostRecentGame().getSettings();

        return settings.getAccuracy() == Accuracy.HARD
            && settings.getConfidence() == Confidence.MASTER
            && settings.getTime() == Time.MASTER
            && settings.getWordChoice() == WordChoice.MASTER;
      }
    };
  }

  private static Badge createNConsecutiveWinBadge(int n) {
    return new Badge(
        "consec" + n, n + " Consecutive Wins", "The player won " + n + " games consecutively") {

      @Override
      public boolean earned(Profile profile) {
        List<GameInfo> gameHistory = profile.getGameHistory();

        ListIterator<GameInfo> gameHistoryIterator = gameHistory.listIterator(gameHistory.size());
        int count = 0;

        while (gameHistoryIterator.hasPrevious() && count < n) {
          GameInfo game = gameHistoryIterator.previous();
          if (game.getGameMode() != GameMode.ZEN && game.getWinState() != EndGameState.WIN) {
            return false;
          }
        }
        return true;
      }
    };
  }

  private static Badge createUnderNSecondsBadge(int n) {
    return new Badge(
        "under" + n + "sec",
        "Under " + n + " seconds",
        "The player won a game in less than " + n + " seconds") {

      @Override
      public boolean earned(Profile profile) {
        return profile.getMostRecentGame().getTimeTaken() <= n;
      }
    };
  }

  private static Badge createJustInTimeBadge() {
    return new Badge("just_in", "Just in time", "The player had less than 2 seconds remaining") {

      @Override
      public boolean earned(Profile profile) {
        return profile.getMostRecentGame().getGameMode() != GameMode.ZEN
            && profile.getMostRecentGame().getSecondsRemaining() <= 2;
      }
    };
  }
}
