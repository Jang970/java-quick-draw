package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.GameLogicManager;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CategoryScreenController {

  @FXML private Button readyButton;
  @FXML private Label drawTimeLabel;
  @FXML private Label categoryLabel;
  @FXML private Label usernameLabel;

  private TextToSpeech textToSpeech;
  private GameLogicManager gameLogicManager;

  public void initialize() {

    gameLogicManager = App.getGameLogicManager();
    textToSpeech = new TextToSpeech();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORY) {
            // When the app laods changes to the catgory screen, we genereate a new category and
            // make display updates
            usernameLabel.setText("Hi, " + App.getProfileManager().getCurrentProfile().getName());
            initialiseGameAndUpdateLabels();
            updateGameTimeLabel();
          }
        });

    // When text the app terminates, we turn the text to speech off.
    App.subscribeToAppTermination(
        (e) -> {
          // TODO: Make the TextToSpeech class handle this automatically
          textToSpeech.terminate();
        });
  }

  private void updateGameTimeLabel() {
    int numSeconds = gameLogicManager.getGameLengthSeconds();
    drawTimeLabel.setText("Draw in " + numSeconds + " seconds");
  }

  private void initialiseGameAndUpdateLabels() {

    // We need to make sure that we are generating a new category which the player has not already
    // played.

    gameLogicManager.initializeGame();

    categoryLabel.setText(gameLogicManager.getCurrentCategory());
    textToSpeech.speakAsync("Draw " + gameLogicManager.getCurrentCategory());
  }

  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }

  @FXML
  private void onBackToUserScreen() {
    App.setView(View.USER);
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
