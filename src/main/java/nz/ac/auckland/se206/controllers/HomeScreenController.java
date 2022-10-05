package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class HomeScreenController {
  @FXML private Button newGameButton;
  @FXML private ImageView mainMenu;
  @FXML private ToggleButton onOffSwitch;
  @FXML private Label quickDrawLabel;
  @FXML private Label editionLabel;
  @FXML private Label creditLabel;

  @FXML
  private void onStartNewGame() {

    if (App.getProfileManager().getProfiles().isEmpty()) {
      App.setView(View.NEWUSER);
    } else {
      App.setView(View.USERPROFILES);
    }
  }

  @FXML
  private void onSwitchToggle() {
    if (onOffSwitch.isSelected()) {
      onOffSwitch.setText("ON");
      mainMenu.getStyleClass().clear();
      mainMenu.getStyleClass().add("mainMenuOff");

      quickDrawLabel.setVisible(false);
      editionLabel.setVisible(false);
      creditLabel.setVisible(false);
    } else {
      onOffSwitch.setText("OFF");
      mainMenu.getStyleClass().clear();
      mainMenu.getStyleClass().add("mainMenuOn");

      quickDrawLabel.setVisible(true);
      editionLabel.setVisible(true);
      creditLabel.setVisible(true);
    }
  }
}
