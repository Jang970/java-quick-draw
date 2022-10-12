package nz.ac.auckland.se206.util.badges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;
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
    badges.add(createPlayedAllCategories());

    // This one has to go last
    badges.add(createGotAllBadgesBadge());

    for (Badge badge : badges) {
      if (badgeIds.contains(badge.getId())) {
        App.expect("Each badge ids should be unique");
      }
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
  private static Badge createPlayedAllCategories() {
    return new Badge(
        "all_categories",
        "Played All Categories Badges",
        "The player has played all categories in the game") {

      @Override
      public boolean earned(Profile profile) {
        Set<String> categoryHistory =
            App.getProfileManager().getCurrentProfile().getGameHistory().stream()
                .flatMap(
                    (game) ->
                        game.getCategoriesPlayed().stream().map(cat -> cat.getCategory().getName()))
                .collect(Collectors.toSet());

        if (categoryHistory.size() != App.getGameLogicManager().getNumberOfCategories()) {
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
        Settings settings = profile.getMostRecentGame().getSettings();

        return settings.getAccuracy() == Accuracy.HARD
            && settings.getConfidence() == Confidence.MASTER
            && settings.getTime() == Time.MASTER
            && settings.getWordChoice() == WordChoice.MASTER
            && game.getWinState() == EndGameState.WIN;
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
        "consec" + n, n + " Consecutive Wins", "The player won " + n + " games consecutively") {

      @Override
      public boolean earned(Profile profile) {
        List<GameInfo> gameHistory = profile.getGameHistory();

        ListIterator<GameInfo> gameHistoryIterator = gameHistory.listIterator(gameHistory.size());
        int count = 0;

        while (gameHistoryIterator.hasPrevious()) {
          GameInfo game = gameHistoryIterator.previous();
          if (game.getGameMode() != GameMode.ZEN && game.getWinState() != EndGameState.WIN) {
            return false;
          } else if (game.getGameMode() != GameMode.ZEN && game.getWinState() == EndGameState.WIN) {
            count++;
            if (count >= n) {
              return true;
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
        return (profile.getMostRecentGame().getCategoryPlayed().getTimeTaken() <= n)
            && (profile.getMostRecentGame().getWinState() == EndGameState.WIN);
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

        return (gameMode == GameMode.BASIC || gameMode == GameMode.HIDDEN_WORD)
            && (game.getCategoryPlayed().getSecondsRemaining() <= 2)
            && (game.getWinState() == EndGameState.WIN);
      }
    };
  }
}
