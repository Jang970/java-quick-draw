package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class UserStatsScreenController {

  // TODO: Uncomment the following when I can implement
  // private Label fastestWinLabel;
  // private Label numGamesWonLabel;
  // private Label numGamesLostLabel;
  // private Label numCategoriesPlayedLabel;

  public void initialize() {

    // every time view is changes to user stats it gets the recent user stats and displays them
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USERSTATS) {
            // TODO: get recent user stats and set labels

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
