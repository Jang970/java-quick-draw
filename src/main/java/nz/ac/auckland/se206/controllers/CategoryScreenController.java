package nz.ac.auckland.se206.controllers;

import java.util.concurrent.ThreadLocalRandom;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;
import nz.ac.auckland.se206.gamelogicmanager.GameProfile;
import nz.ac.auckland.se206.util.Profile;
import nz.ac.auckland.se206.util.ProfileManager;

public class CategoryScreenController {

  @FXML private Button readyButton;
  @FXML private Label drawTimeLabel;
  @FXML private Label categoryLabel;
  @FXML private Label usernameLabel;

  private GameLogicManager gameLogicManager;
  private ProfileManager profileManager;

  public void initialize() {

    gameLogicManager = App.getGameLogicManager();
    profileManager = App.getProfileManager();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORY) {
            // When the app laods changes to the catgory screen, we genereate a new category and
            // make display updates
            usernameLabel.setText("Hi, " + profileManager.getCurrentProfile().getName());
            initialiseGameAndUpdateLabels();
            updateGameTimeLabel();
          }
        });
  }

  private void updateGameTimeLabel() {
    int numSeconds = gameLogicManager.getCurrentGameProfile().settings().getTime().getTimeToDraw();
    drawTimeLabel.setText("Draw in " + numSeconds + " seconds");
  }

  private void initialiseGameAndUpdateLabels() {

    // We need to make sure that we are generating a new category which the player has not already
    // played.

    Profile currentProfile = profileManager.getCurrentProfile();

    gameLogicManager.initializeGame(
        new GameProfile(
            currentProfile.getSettings(), GameMode.BASIC, currentProfile.getGameHistory()));

    categoryLabel.setText(gameLogicManager.getCurrentCategory().name);

    if (ThreadLocalRandom.current().nextInt(100) == 0) {
      App.getTextToSpeech()
          .speakAsync("Draw " + gameLogicManager.getCurrentCategory().name + ". Or else");
    } else {
      App.getTextToSpeech().speakAsync("Draw " + gameLogicManager.getCurrentCategory().name);
    }
  }

  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }

  @FXML
  private void onSwitchToSettings() {
    App.setView(View.DIFFICULTY);
  }

  @FXML
  private void onPressHowToPlay() {
    // shows an information alert pop up on how to play when button is clicked
    Alert howToPlayAlert = new Alert(AlertType.INFORMATION);
    DialogPane dialogPane = howToPlayAlert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/css/application.css").toExternalForm());

    // setting display values
    howToPlayAlert.setTitle("How to Play");
    howToPlayAlert.setHeaderText("How to Play");
    howToPlayAlert.setContentText(
        "Once you click the \"I'm Ready!\" button, "
            + "the timer will start immediately and you "
            + "can start drawing on the canvas. Good luck!");
    howToPlayAlert.showAndWait();
  }
}
