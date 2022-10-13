package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.gamelogicmanager.EndGameReason;
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

  private static Set<String> badgeIds = new HashSet<String>();

  /**
   * This method creates all badges in our game
   *
   * @return list of badges
   */
  public static List<Badge> createBadgeList() {

    List<Badge> badges = new ArrayList<Badge>();

    badges.add(createNConsecutiveWinBadge(2));
    badges.add(createNConsecutiveWinBadge(5));
    badges.add(createNConsecutiveWinBadge(10));
    badges.add(createNConsecutiveWinBadge(20));

    badges.add(createJustInTimeBadge());

    badges.add(createUnderNSecondsBadge(1));
    badges.add(createUnderNSecondsBadge(2));
    badges.add(createUnderNSecondsBadge(5));
    badges.add(createUnderNSecondsBadge(10));

    badges.add(createMaxDifficultyBadge());
    badges.add(createPlayedAllCategoriesBadge());

    // This one has to go last
    badges.add(createGotAllBadgesBadge());

    for (Badge badge : badges) {
      assert !badgeIds.contains(badge.getId()) : "Each badge ids should be unique";
      badgeIds.add(badge.getId());
    }

    return badges;
  }

  /**
   * This creates the got all badges badge
   *
   * @return instance of type Badge representing the got all badges badge
   */
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

  /**
   * This create the played all categories badge
   *
   * @return instance of the the type badge representing the all categories badge
   */
  private static Badge createPlayedAllCategoriesBadge() {
    return new Badge(
        "all_categories",
        "Drawns All Categories",
        "The player has won a game with each category in classic or hidden word mode") {

      @Override
      public boolean earned(Profile profile) {

        Set<String> categoryHistory = new HashSet<String>();

        List<GameInfo> gameHistory =
            QuickDrawGameManager.getProfileManager().getCurrentProfile().getGameHistory();

        for (GameInfo game : gameHistory) {
          if (game.getReasonForGameEnd() == EndGameReason.WIN
              && (game.getGameMode() == GameMode.HIDDEN_WORD
                  || game.getGameMode() == GameMode.CLASSIC)) {
            categoryHistory.add(game.getCategoryPlayed().getCategory().getName());
          }
        }

        if (categoryHistory.size()
            != QuickDrawGameManager.getGameLogicManager().getNumberOfCategories()) {
          return false;
        }

        return true;
      }
    };
  }

  /**
   * This creates the max difficulty badge
   *
   * @return instance of type Badge representing the max difficulty badge
   */
  private static Badge createMaxDifficultyBadge() {
    return new Badge(
        "max_dif",
        "Maximum difficulty",
        "The player won a game on the hardest difficulty settings") {

      @Override
      public boolean earned(Profile profile) {
        GameInfo game = profile.getMostRecentGame();
        Settings settings = game.getSettings();

        return settings.getAccuracy() == Accuracy.HARD
            && settings.getConfidence() == Confidence.MASTER
            && settings.getTime() == Time.MASTER
            && settings.getWordChoice() == WordChoice.MASTER
            && game.getReasonForGameEnd() == EndGameReason.WIN;
      }
    };
  }

  /**
   * This creates the consecutive wins badges
   *
   * @return instance of type Badge representing the consecutive wins badges
   */
  private static Badge createNConsecutiveWinBadge(int n) {
    return new Badge(
        "consec" + n,
        n + " Consecutive Wins",
        "The player won " + n + " games consecutively in hidden word or classic mode") {

      @Override
      public boolean earned(Profile profile) {
        List<GameInfo> gameHistory = profile.getGameHistory();

        ListIterator<GameInfo> gameHistoryIterator = gameHistory.listIterator(gameHistory.size());
        int count = 0;

        while (gameHistoryIterator.hasPrevious()) {
          GameInfo game = gameHistoryIterator.previous();
          GameMode mode = game.getGameMode();
          if (mode == GameMode.CLASSIC || mode == GameMode.HIDDEN_WORD) {

            if (game.getReasonForGameEnd() == EndGameReason.WIN) {
              count++;
              if (count >= n) {
                return true;
              }
            } else {
              return false;
            }
          }
        }

        return false;
      }
    };
  }

  /**
   * This creates the under n seconds badges
   *
   * @return instance of type Badge representing the under n seconds badges
   */
  private static Badge createUnderNSecondsBadge(int n) {
    return new Badge(
        "under" + n + "sec",
        "Under " + n + " seconds",
        "The player won a game in less than " + n + " seconds") {

      @Override
      public boolean earned(Profile profile) {

        GameInfo game = profile.getMostRecentGame();
        GameMode gameMode = game.getGameMode();

        return (gameMode == GameMode.CLASSIC || gameMode == GameMode.HIDDEN_WORD)
            && (game.getCategoryPlayed().getTimeTaken() <= n)
            && (game.getReasonForGameEnd() == EndGameReason.WIN);
      }
    };
  }

  /**
   * This creates the just in time badge
   *
   * @return instance of type Badge representing the just in time badge
   */
  private static Badge createJustInTimeBadge() {
    return new Badge("just_in", "Just in time", "The player had less than 2 seconds remaining") {

      @Override
      public boolean earned(Profile profile) {

        GameInfo game = profile.getMostRecentGame();
        GameMode gameMode = game.getGameMode();

        return (gameMode == GameMode.CLASSIC || gameMode == GameMode.HIDDEN_WORD)
            && (game.getCategoryPlayed().getSecondsRemaining() <= 2)
            && (game.getReasonForGameEnd() == EndGameReason.WIN);
      }
    };
  }
}
