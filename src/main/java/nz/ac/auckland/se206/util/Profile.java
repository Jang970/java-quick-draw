package nz.ac.auckland.se206.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import nz.ac.auckland.se206.gamelogicmanager.CategoryPlayedInfo;
import nz.ac.auckland.se206.gamelogicmanager.EndGameReason;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;

/**
 * This class will be used to create objects for each new profile containing all information related
 * to that specific profile. We will be using the concept of json serialisation and de-serialisation
 * in order to save and load exisiting profiles in our app.
 */
public class Profile {

  private String name;
  private UUID id;
  private String colour;

  private List<GameInfo> gameHistory = new ArrayList<GameInfo>();

  // this list of booleans will store the result of each badge's isEarned
  // will be useful when we want to save / load a profile's earned badges as we do not have to
  // figure out how to save enums using gson
  // we can just instead save a list of booleans and load badges earned from that
  private Set<String> badgesEarned = new HashSet<String>();

  private int numberOfHistoryResets = 0;
  private Settings difficultySettings = new Settings();

  private transient EmptyEventListener onChange;

  /**
   * Constructor for the Profile class which takes in the name and chosen colour
   *
   * @param name name of new profile
   * @param colour colour of new profile chosen by user
   */
  public Profile(String name, String colour) {
    this.name = name;
    this.id = UUID.randomUUID();
    this.colour = colour;
  }

  /**
   * Method that will allow for us to set the profile instance field onChange to a given
   * EmptyEventListener of our choice
   *
   * @param listener EmptyEventListener we want to set the instance field onChange to.
   */
  protected void setOnChange(EmptyEventListener listener) {
    onChange = listener;
  }

  /** Helper method that will dispatch listener data only if onChange is not null */
  private void emitChange() {
    if (onChange != null) {
      onChange.update();
    }
  }

  /**
   * This method will retrieve the name of the profile
   *
   * @return string of the name of profile
   */
  public String getName() {
    return name;
  }

  /**
   * This method will retrieve the unique ID given to the profile
   *
   * @return UUID unique ID of the profile
   */
  public UUID getId() {
    return id;
  }

  /**
   * This method will retrieve the colour associated to the profile as a string
   *
   * @return String of the colour associated to the profile instance
   */
  public String getColour() {
    return colour;
  }

  /**
   * This method will get the difficulty settings of the profile
   *
   * @return Settings object/instance that contains the difficulty settings of the profile
   */
  public Settings getSettings() {
    return this.difficultySettings;
  }

  /**
   * Use this method when the user wants to change their profile name.
   *
   * @param newName the new name they want to change to
   */
  public void updateName(String newName) {
    name = newName;
    emitChange();
  }

  /**
   * Use this method when the user wants to change their profile colour
   *
   * @param newColour the new colour they would like to change to
   */
  public void updateColour(String newColour) {
    colour = newColour;
    emitChange();
  }

  /**
   * This method will be used to add the current category/word to draw to list of previous words Can
   * be called everytime a new category appears
   *
   * @param gameInfo info of the game to add to history
   */
  public void addGameToHistory(GameInfo gameInfo) {
    // we add to the profiles history of games and simultaneously increment gamesWon or gamesLost
    // depending on the win state of the game
    gameHistory.add(gameInfo);

    emitChange();
  }

  /** Use this method to reset the category history to 0 and increment the number of resets by 1 */
  public void resetStats() {
    gameHistory.clear();
    numberOfHistoryResets++;

    emitChange();
  }

  /**
   * This method will retrieve the number of times all categories have been played by the profile
   *
   * @return the number of times the category history was reset
   */
  public int getNumResets() {
    return numberOfHistoryResets;
  }

  /**
   * This method will get the number of games the profile has won
   *
   * @return number of games won by profile
   */
  public int getGamesWon() {
    int gamesWon = 0;

    for (GameInfo game : gameHistory) {
      if (game.getGameMode() == GameMode.CLASSIC || game.getGameMode() == GameMode.HIDDEN_WORD) {
        if (game.getReasonForGameEnd() == EndGameReason.CORRECT_CATEOGRY) {
          gamesWon++;
        }
      } else if (game.getGameMode() == GameMode.RAPID_FIRE) {
        if (game.getCategoriesPlayed().size() > 0) {
          gamesWon++;
        }
      }
    }
    return gamesWon;
  }

  /**
   * This method will get the number of games the profile has lost
   *
   * @return number of games lost by profile
   */
  public int getGamesLost() {
    int gamesLost = 0;
    for (GameInfo game : gameHistory) {
      if (game.getGameMode() == GameMode.HIDDEN_WORD || game.getGameMode() == GameMode.CLASSIC) {
        if (game.getReasonForGameEnd() == EndGameReason.OUT_OF_TIME
            || game.getReasonForGameEnd() == EndGameReason.GAVE_UP_OR_CANCELLED) {
          gamesLost++;
        }
      } else if (game.getGameMode() == GameMode.RAPID_FIRE) {
        if (game.getCategoriesPlayed().size() == 0) {
          gamesLost++;
        }
      }
    }
    return gamesLost;
  }

  /**
   * This method wil get the win percentage of the profile
   *
   * @return win percentage of the profile in string format or returns 0 percentage is NaN
   */
  public int getWinPercentage() {
    int gamesWon = getGamesWon();
    int gamesLost = getGamesLost();
    int totalGames = gamesWon + gamesLost;
    int winPercentage = (int) (((double) gamesWon / (double) totalGames) * 100);

    if (Double.isNaN(winPercentage)) {
      return 0;
    }

    return winPercentage;
  }

  /** If the player has not had a fastest win, this will be null */

  /**
   * This method will get the fastest category played by the profile in classic or hidden word mode
   *
   * @return the fastest category played by the profile
   */
  public CategoryPlayedInfo getFastestCategoryPlayed() {

    /** If the player has not had a fastest win, this will be null */
    CategoryPlayedInfo bestGame = null;

    for (GameInfo game : gameHistory) {
      if (game.getGameMode() == GameMode.CLASSIC || game.getGameMode() == GameMode.HIDDEN_WORD) {

        CategoryPlayedInfo categoryPlayed = game.getCategoryPlayed();
        if (game.getReasonForGameEnd() == EndGameReason.CORRECT_CATEOGRY) {
          if (bestGame == null || categoryPlayed.getTimeTaken() < bestGame.getTimeTaken()) {
            bestGame = categoryPlayed;
          }
        }

      } else if (game.getGameMode() == GameMode.RAPID_FIRE) {
        for (CategoryPlayedInfo categoryPlayed : game.getCategoriesPlayed()) {
          if (bestGame == null || categoryPlayed.getTimeTaken() < bestGame.getTimeTaken()) {
            bestGame = categoryPlayed;
          }
        }
      }
    }

    return bestGame;
  }

  public int getHighestRapidFireCount() {
    int highestCount = 0;
    for (GameInfo gameInfo : gameHistory) {
      if (gameInfo.getGameMode() == GameMode.RAPID_FIRE) {
        if (gameInfo.getCategoriesPlayed().size() > highestCount) {
          highestCount = gameInfo.getCategoriesPlayed().size();
        }
      }
    }
    return highestCount;
  }

  /**
   * This method will get the past games of the profile
   *
   * @return list of past games of the profile
   */
  public List<GameInfo> getGameHistory() {
    return gameHistory;
  }

  public GameInfo getMostRecentGame() {
    return gameHistory.isEmpty() ? null : gameHistory.get(gameHistory.size() - 1);
  }

  /**
   * This method will take a badge Id which correlates to the badge we want to award the profile. It
   * will also check if the profile already has the badge.
   *
   * @param badgeId the badge to award to the player
   */
  public void awardBadge(String badgeId) {
    if (!this.badgesEarned.contains(badgeId)) {
      this.badgesEarned.add(badgeId);
      emitChange();
    }
  }

  /**
   * This method goes through the list of all badges and calls the awardBadge method for each badge
   *
   * @param badgeIds list of all badgeIDs associated to the badges we want to check
   */
  public void awardBadges(Collection<String> badgeIds) {
    for (String badgeId : badgeIds) {
      this.awardBadge(badgeId);
    }
    emitChange();
  }

  /**
   * This method will get all the badges earned by the profile
   *
   * @return Set of all badges the profile currently has earned
   */
  public Set<String> getEarnedBadgeIds() {
    return this.badgesEarned;
  }
}
