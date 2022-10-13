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
import nz.ac.auckland.se206.util.ProfileManager;

public class CategoryScreenController {

  @FXML private Button readyButton;
  @FXML private Label drawTimeLabel;
  @FXML private Label categoryLabel;
  @FXML private Label usernameLabel;

  private GameLogicManager gameLogicManager;
  private ProfileManager profileManager;
  private String currentCategoryDisplayString;

  /** Method that is run to set up the CategoryScreen FXML everytime it is opened/run. */
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

  /**
   * Method that will update the label that displays the time allowed to draw to the set time
   * relative to the user's saved Time difficulty
   */
  private void updateGameTimeLabel() {
    int numSeconds = gameLogicManager.getCurrentGameProfile().settings().getTime().getTimeToDraw();
    drawTimeLabel.setText("Draw in " + numSeconds + " seconds");
  }

  /**
   * This method is used to initialise a new game and the GUI labels relating to displaying the
   * category to draw respectively
   */
  private void initialiseGameAndUpdateLabels() {

    if (gameLogicManager.getCurrentGameProfile().gameMode() == GameMode.HIDDEN_WORD) {
      categoryLabel.setStyle("-fx-font-size: 40px");
      currentCategoryDisplayString = gameLogicManager.getCurrentCategory().getDescription();
    } else {
      categoryLabel.setStyle("-fx-font-size: 90px");
      currentCategoryDisplayString = gameLogicManager.getCurrentCategory().getName();
    }

    categoryLabel.setText(currentCategoryDisplayString.toUpperCase());

    if (ThreadLocalRandom.current().nextInt(100) == 0) {
      App.getTextToSpeech().speakAsync("Draw " + currentCategoryDisplayString + ". Or else");
    } else {
      App.getTextToSpeech().speakAsync("Draw " + currentCategoryDisplayString);
    }
  }

  /** Method relating to the button switch to the GameScreen FXML */
  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }

  /** Method relating to the button to switch to the UserScreen FXML */
  @FXML
  private void onSwitchToSettings() {
    App.setView(View.DIFFICULTY);
  }

  /** Method to show the how to play pop up to the user */
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
