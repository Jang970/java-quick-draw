package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class HomeScreenController {
  @FXML private Button newGameButton;

  /**
   * Method relating to the button that will either navigate to NewUserScreen FXML if there are no
   * existing profiles but otherwise will go to UserProfilesScreen FXML
   */
  @FXML
  private void onStartNewGame() {

    // will go to the relevant FXML depending if there are existing profiles or not
    if (App.getProfileManager().getProfiles().isEmpty()) {
      App.setView(View.NEWUSER);
    } else {
      App.setView(View.USERPROFILES);
    }
  }
}
