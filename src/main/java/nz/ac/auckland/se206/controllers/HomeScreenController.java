package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;

public class HomeScreenController {
  @FXML private Button newGameButton;
  @FXML private ImageView mainMenu;
  @FXML private ToggleButton onOffSwitch;
  @FXML private Label quickDrawLabel;
  @FXML private Label editionLabel;
  @FXML private Label creditLabel;

  private MediaPlayer player;

  public void initialize() {
    Media sound =
        new Media(
            getClass().getClassLoader().getResource("sounds/gameOpener.mp3").toExternalForm());
    player = new MediaPlayer(sound);
    player.setCycleCount(AudioClip.INDEFINITE);
    player.play();
  }

  /**
   * Method relating to the button that will either navigate to NewUserScreen FXML if there are no
   * existing profiles but otherwise will go to UserProfilesScreen FXML
   */
  @FXML
  private void onStartNewGame() {

    // Fade out music effects
    Timeline timeline =
        new Timeline(new KeyFrame(Duration.seconds(3), new KeyValue(player.volumeProperty(), 0)));
    timeline.play();
    timeline.setOnFinished(
        e -> {
          player.stop();
        });

    // Set the appropriate view
    if (QuickDrawGameManager.getProfileManager().getProfiles().isEmpty()) {
      App.setView(View.NEWUSER);
    } else {
      App.setView(View.USERPROFILES);
    }
  }

  /** This method simple turns the intro light on and off */
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
