package nz.ac.auckland.se206.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.gamelogicmanager.CategoryPlayedInfo;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;

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

  public Profile(String name, String colour) {
    this.name = name;
    this.id = UUID.randomUUID();
    this.colour = colour;
  }

  protected void setOnChange(EmptyEventListener listener) {
    onChange = listener;
  }

  private void emitChange() {
    if (onChange != null) {
      onChange.update();
    }
  }

  public String getName() {
    return name;
  }

  public UUID getId() {
    return id;
  }

  public String getColour() {
    return colour;
  }

  public Settings getSettings() {
    return this.difficultySettings;
  }

  public void updateName(String newName) {
    name = newName;
    emitChange();
  }

  public void updateColour(String newColour) {
    colour = newColour;
    emitChange();
  }

  /**
   * This method will be used to add the current category/word to draw to list of previous words Can
   * be called everytime a new category appears
   *
   * @param gameInfo current category/word that profile must draw
   */
  public void addGameToHistory(GameInfo gameInfo) {
    gameHistory.add(gameInfo);

    emitChange();
  }

  /** Use this to reset the category history to 0 and increment the number of resets by 1 */
  public void resetStats() {
    gameHistory.clear();
    numberOfHistoryResets++;

    emitChange();
  }

  /**
   * @return the number of times the category history was reset
   */
  public int getNumResets() {
    return numberOfHistoryResets;
  }

  public int getGamesWon() {
    int gamesWon = 0;
    for (GameInfo game : gameHistory) {
      if (game.getWinState() == EndGameState.WIN) {
        gamesWon++;
      }
    }
    return gamesWon;
  }

  public int getGamesLost() {
    int gamesLost = 0;
    for (GameInfo game : gameHistory) {
      if (game.getWinState() == EndGameState.LOOSE || game.getWinState() == EndGameState.GIVE_UP) {
        gamesLost++;
      }
    }
    return gamesLost;
  }

  /** If the player has not had a fastest win, this will be null */
  public CategoryPlayedInfo getFastestCategoryPlayed() {
    CategoryPlayedInfo bestGame = null;

    // The fancy stuff is just taking a list of games and extracting their lists of categories
    // plsyed into a new list.
    for (CategoryPlayedInfo categoryPlayed :
        gameHistory.stream()
            .flatMap((game) -> game.getCategoriesPlayed().stream())
            .collect(Collectors.toList())) {

      if (bestGame == null || categoryPlayed.getTimeTaken() < bestGame.getTimeTaken()) {
        bestGame = categoryPlayed;
      }
    }
    return bestGame;
  }

  public List<GameInfo> getGameHistory() {
    return gameHistory;
  }

  public GameInfo getMostRecentGame() {
    return gameHistory.isEmpty() ? null : gameHistory.get(gameHistory.size() - 1);
  }

  /**
   * @param badge the badge to award to the player
   * @return true if the player did not have the badge and false if they did have the badge
   */
  public void awardBadge(String badgeId) {
    if (!this.badgesEarned.contains(badgeId)) {
      this.badgesEarned.add(badgeId);
      emitChange();
    }
  }

  public void awardBadges(Collection<String> badgeIds) {
    for (String badgeId : badgeIds) {
      this.awardBadge(badgeId);
    }
    emitChange();
  }

  public Set<String> getEarnedBadgeIds() {
    return this.badgesEarned;
  }
}
