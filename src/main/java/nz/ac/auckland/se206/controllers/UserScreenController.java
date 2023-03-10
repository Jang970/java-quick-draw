package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.Profile;

public class UserScreenController {

  @FXML private Label statsHeaderLabel;
  @FXML private Label fastestWinLabel;
  @FXML private Label numGamesWonLabel;
  @FXML private Label numGamesLostLabel;
  @FXML private Label numCategoriesPlayedLabel;
  @FXML private Label bestRapidFireCountLabel;

  /** Method that is run to set up the UserScreen FXML everytime it is opened/run. */
  public void initialize() {

    // every time view is changes to user stats it gets the recent user stats and displays them
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USER) {

            Profile currentProfile = QuickDrawGameManager.getProfileManager().getCurrentProfile();

            // sets all labels of the user stats and users name
            statsHeaderLabel.setText(currentProfile.getName());

            // fastest win label
            if (currentProfile.getFastestCategoryPlayed() == null) {
              fastestWinLabel.setText(
                  "YOU HAVEN'T WON A GAME YET! KEEP PRACTISING, YOU GOT THIS! :)");
            } else {
              // this will be shown when a player has played and won at least one category
              fastestWinLabel.setText(
                  ("Your fastest win was in "
                          + currentProfile.getFastestCategoryPlayed().getTimeTaken()
                          + (currentProfile.getFastestCategoryPlayed().getTimeTaken() == 1
                              ? " second"
                              : " seconds")
                          + " when you had to draw '"
                          + currentProfile.getFastestCategoryPlayed().getCategory().getName()
                          + "'!")
                      .toUpperCase());
            }

            //  rest of user stats label
            numGamesWonLabel.setText(String.valueOf(currentProfile.getGamesWon()));
            numGamesLostLabel.setText(String.valueOf(currentProfile.getGamesLost()));
            // update number of categories label
            numCategoriesPlayedLabel.setText(
                String.valueOf(currentProfile.getGameHistory().size()));
            // update rapid fire label
            bestRapidFireCountLabel.setText(
                String.valueOf(currentProfile.getHighestRapidFireCount()));
          }
        });
  }

  /** Method that Switches view to category */
  @FXML
  private void onPlayAgain() {
    App.setView(View.GAMEMODES);
  }

  /** Method that Switches view to user profiles */
  @FXML
  private void onSwitchToProfiles() {
    App.setView(View.USERPROFILES);
  }

  /** Method that Switches view to category history */
  @FXML
  private void onSwitchToCategoryHistory() {
    App.setView(View.CATEGORYHISTORY);
  }

  /** Switches to the badges view */
  @FXML
  private void onSwitchToBadges() {
    App.setView(View.BADGES);
  }

  /** Switches to the settings view */
  @FXML
  private void onSwitchToSettings() {
    App.setView(View.DIFFICULTY);
  }
}
