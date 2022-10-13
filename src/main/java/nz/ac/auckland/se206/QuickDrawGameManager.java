package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.util.Profile;
import nz.ac.auckland.se206.util.ProfileManager;
import nz.ac.auckland.se206.util.badges.Badge;
import nz.ac.auckland.se206.util.badges.BadgeManager;

public class QuickDrawGameManager {
  private static final GameLogicManager gameLogicManager = createGameLogicManager();
  private static final ProfileManager profileManager = createProfileManager();
  private static final BadgeManager badgeManager = new BadgeManager();

  /**
   * This method will create and return a ProfileManager instance to be used through the application
   *
   * @return ProfileManager instance created
   */
  private static ProfileManager createProfileManager() {

    File userProfiles = new File(".userprofiles");
    // create folder to store json file in if not already existing
    userProfiles.mkdir();

    try {
      // this creates the json file containing the list of user profiles
      return new ProfileManager(userProfiles.getAbsolutePath() + File.separator + "profiles.json");
    } catch (IOException e2) {
      App.expect("profiles.json is a file name, not a directory", e2);
      return null;
    }
  }

  /**
   * This method will create and return a GameLogicManager instance to be used throughout the
   * application
   *
   * @return GameLogicManager instance created
   */
  private static GameLogicManager createGameLogicManager() {
    try {
      // Try create the game logic manager
      return new GameLogicManager();
    } catch (IOException | ModelException e1) {
      App.expect("The machine learning model exists on file", e1);
      return null;
    }
  }

  /**
   * This method will get the ProfileManager instance
   *
   * @return profile manager instance
   */
  public static ProfileManager getProfileManager() {
    return profileManager;
  }

  /**
   * This method will get the gameLogicManager instance
   *
   * @return game logic manager instance
   */
  public static GameLogicManager getGameLogicManager() {
    return gameLogicManager;
  }

  public static BadgeManager getBadgeManager() {
    return badgeManager;
  }

  public static void initGame() {
    // Update profile details when the game ends and save to file
    gameLogicManager.subscribeToGameEnd(
        (gameInfo) -> {
          Profile currentProfile = profileManager.getCurrentProfile();
          currentProfile.addGameToHistory(gameInfo);

          Set<String> existingBadges = currentProfile.getEarnedBadgeIds();

          for (Badge badge : badgeManager.getAllBadges()) {
            if (!existingBadges.contains(badge.getId())) {
              if (badge.earned(currentProfile)) {
                currentProfile.awardBadge(badge.getId());
              }
            }
          }
        });
  }
}
