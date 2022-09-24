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
import nz.ac.auckland.se206.GameLogicManager.FilterTooStrictException;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.util.Profile;

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
            usernameLabel.setText("Hi, " + App.getProfileManager().getCurrentProfile().getName());
            generateNewCategoryAndUpdateLabel();
            updateGameTimeLabel();
          }
        });

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

  private void generateNewCategoryAndUpdateLabel() {

    Profile profile = App.getProfileManager().getCurrentProfile();

    String newCategory;
    try {
      newCategory = gameLogicManager.selectNewRandomCategory(profile.getCategoryHistory());
    } catch (FilterTooStrictException e) {
      profile.resetCategoryHistory();
      newCategory = gameLogicManager.selectNewRandomCategory();
    }

    categoryLabel.setText(newCategory);
    textToSpeech.speakAsync("Draw " + newCategory);
  }

  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }

  @FXML
  private void onPressHowToPlay() {
    // shows an information alert pop up on how to play when button is clicked
    Alert howToPlayAlert = new Alert(AlertType.INFORMATION);
    DialogPane dialogPane = howToPlayAlert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/css/application.css").toExternalForm());

    howToPlayAlert.setTitle("How to Play");
    howToPlayAlert.setHeaderText("How to Play");
    howToPlayAlert.setContentText(
        "Once you click the \"I'm Ready!\" button, the timer will start immediately and you can start drawing on the canvas. Good luck!");
    howToPlayAlert.showAndWait();
  }
}
