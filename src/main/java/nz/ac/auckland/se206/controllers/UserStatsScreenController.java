package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.util.UserStats;

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
            try {
              // sets all labels of the user stats and users name
              statsHeaderLabel.setText(
                  App.getUserManager().getCurrentProfile().getName() + "'s Stats");

              //  user stats label
              UserStats currentUserStats = App.getUserManager().getCurrentProfile().getUserStats();
              fastestWinLabel.setText(String.valueOf(currentUserStats.getFastestWin()));
              numGamesWonLabel.setText(String.valueOf(currentUserStats.getGamesWon()));
              numGamesLostLabel.setText(String.valueOf(currentUserStats.getGamesLost()));
              numCategoriesPlayedLabel.setText(
                  String.valueOf(currentUserStats.getCategoryHistory().size()));
            } catch (IOException | URISyntaxException e) {
              e.printStackTrace();
            }
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
