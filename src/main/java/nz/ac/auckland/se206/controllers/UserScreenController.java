package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.util.Profile;

public class UserScreenController {

  @FXML private Label statsHeaderLabel;
  @FXML private Label fastestWinLabel;
  @FXML private Label numGamesWonLabel;
  @FXML private Label numGamesLostLabel;
  @FXML private Label numCategoriesPlayedLabel;

  /** Method that is run to set up the UserScreen FXML everytime it is opened/run. */
  public void initialize() {

    // every time view is changes to user stats it gets the recent user stats and displays them
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USER) {

            Profile currentProfile = App.getProfileManager().getCurrentProfile();

            // sets all labels of the user stats and users name
            statsHeaderLabel.setText(currentProfile.getName());

            // fastest win label
            if (currentProfile.getFastestCategoryPlayed() == null) {
              fastestWinLabel.setText(
                  "You haven't won a game yet! Keep practising, you got this :)");
            } else {
              fastestWinLabel.setText(
                  "Your fastest win is in "
                      + currentProfile.getFastestCategoryPlayed().getTimeTaken()
                      + " seconds when you had to draw '"
                      + currentProfile.getFastestCategoryPlayed().getCategory().getName()
                      + "'!");
            }

            //  rest of user stats label
            numGamesWonLabel.setText(String.valueOf(currentProfile.getGamesWon()));
            numGamesLostLabel.setText(String.valueOf(currentProfile.getGamesLost()));
            numCategoriesPlayedLabel.setText(
                String.valueOf(currentProfile.getGameHistory().size()));
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

  /** Switches view to category history */
  @FXML
  private void onSwitchToCategoryHistory() {
    App.setView(View.CATEGORYHISTORY);
  }
}
