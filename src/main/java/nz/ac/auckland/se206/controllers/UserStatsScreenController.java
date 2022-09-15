package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.util.Profile;

public class UserStatsScreenController {

  @FXML private Label statsHeaderLabel;
  @FXML private Label fastestWinLabel;
  @FXML private Label numGamesWonLabel;
  @FXML private Label numGamesLostLabel;
  @FXML private Label numCategoriesPlayedLabel;

  public void initialize() {

    // every time view is changes to user stats it gets the recent user stats and displays them
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USERSTATS) {
            // sets all labels of the user stats and users name
            statsHeaderLabel.setText(
                App.getProfileManager().getCurrentProfile().getName() + "'s Stats");

            //  user stats label
            Profile currentProfile = App.getProfileManager().getCurrentProfile();
            fastestWinLabel.setText(String.valueOf(currentProfile.getFastestWin()));
            numGamesWonLabel.setText(String.valueOf(currentProfile.getGamesWon()));
            numGamesLostLabel.setText(String.valueOf(currentProfile.getGamesLost()));
            numCategoriesPlayedLabel.setText(
                String.valueOf(currentProfile.getCategoryHistory().size()));
          }
        });
  }

  /** Switches view to category */
  @FXML
  private void onPlayAgain() {
    App.setView(View.CATEGORY);
  }

  /** Switches view to user profiles */
  @FXML
  private void onSwitchToProfiles() {
    App.setView(View.USERPROFILES);
  }
}
