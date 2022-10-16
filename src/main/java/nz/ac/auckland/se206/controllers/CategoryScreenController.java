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
import nz.ac.auckland.se206.QuickDrawGameManager;
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
  @FXML private Label currentGameModeLabel;

  private GameLogicManager gameLogicManager;
  private ProfileManager profileManager;

  /** Method that is run to set up the CategoryScreen FXML everytime it is opened/run. */
  public void initialize() {

    // get the gamelogic and profile managers
    gameLogicManager = QuickDrawGameManager.getGameLogicManager();
    profileManager = QuickDrawGameManager.getProfileManager();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORY) {
            // When the app laods changes to the catgory screen, we genereate a new category and
            // make display updates
            usernameLabel.setText("Hi, " + profileManager.getCurrentProfile().getName());
            initGameAndUpdateLabels();
          }
        });
  }

  /**
   * This method is used to initialise a new game and the GUI labels relating to displaying the
   * category to draw respectively
   */
  private void initGameAndUpdateLabels() {

    // Variables for readability
    Profile profile = profileManager.getCurrentProfile();
    GameMode gameMode = QuickDrawGameManager.getCurrentlySelectedGameMode();

    // Start the game with the correct settings. game mode etc.
    gameLogicManager.initializeGame(
        new GameProfile(profile.getSettings(), gameMode, profile.getGameHistory()));

    String currentCategoryDisplayString;

    // Updating the font size for the game mode. Needs to be smaller on hidden word mode.
    if (gameMode == GameMode.HIDDEN_WORD) {
      categoryLabel.setStyle("-fx-font-size: 40px");
      currentCategoryDisplayString = gameLogicManager.getCurrentCategory().getDescription();
    } else {
      // otherwise, if not hidden word, can keep font size original size
      categoryLabel.setStyle("-fx-font-size: 90px");
      currentCategoryDisplayString = gameLogicManager.getCurrentCategory().getName();
    }
    categoryLabel.setText(currentCategoryDisplayString.toUpperCase());

    // Little easter egg.
    if (ThreadLocalRandom.current().nextInt(100) == 0) {
      App.getTextToSpeech().speakAsync("Draw " + currentCategoryDisplayString + ". Or else");
    } else {
      App.getTextToSpeech().speakAsync("Draw " + currentCategoryDisplayString);
    }
    // set number of seconds allowed for player to draw
    int numSeconds = gameLogicManager.getCurrentGameProfile().settings().getTime().getTimeToDraw();

    // Display correct text for different game modes.
    if (gameMode == GameMode.ZEN) {
      drawTimeLabel.setText("DRAW");
    } else if (gameMode == GameMode.RAPID_FIRE) {
      drawTimeLabel.setText("START BY DRAWING");
    } else {
      // display time allowed for classic and hidden word mode
      drawTimeLabel.setText("DRAW IN " + numSeconds + " SECONDS");
    }

    // show user their current game mode
    currentGameModeLabel.setText("CURRENT GAME MODE: " + gameMode.name().replace('_', ' '));
  }

  /** Method relating to the button switch to the GameScreen FXML */
  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }

  /** Method relating to the button switch to the Home Screen */
  @FXML
  private void onGoHome() {
    App.setView(View.USER);
  }

  /** Method relating to the button to switch to the UserScreen FXML */
  @FXML
  private void onSwitchToSettings() {
    App.setView(View.DIFFICULTY);
  }

  /** Method relating to the button to switch to the UserScreen FXML */
  @FXML
  private void onChangeGameMode() {
    App.setView(View.GAMEMODES);
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

    // setting display values when the how to play pop up is shown
    howToPlayAlert.setTitle("How to Play");
    howToPlayAlert.setHeaderText("How to Play");
    // content to display on the pop up
    howToPlayAlert.setContentText(
        getGameModeHowToPlay(QuickDrawGameManager.getCurrentlySelectedGameMode()).toUpperCase());
    howToPlayAlert.showAndWait();
  }

  /**
   * Returns the string on how to play for each game mode.
   *
   * @param gameMode takes in the current game mode
   * @return the string on how to play for the given current game mode
   */
  private String getGameModeHowToPlay(GameMode gameMode) {
    // this will handle what message to display depending on the gamemode
    switch (gameMode) {
        // for classic mode
      case CLASSIC:
        return "Once you click the \"I'm Ready!\" button, "
            + "the timer will start immediately and you "
            + "can start drawing on the canvas. The word will be displayed at the top. Good luck!";
        // for zen mode
      case ZEN:
        return "Once you click the \"I'm Ready!\" button, "
            + "you can start drawing on the canvas for as long as you like. You can change the pencil colour as you please! Have fun :)";
        // for hidden word mode
      case HIDDEN_WORD:
        return "Once you click the \"I'm Ready!\" button, "
            + "the timer will start immediately and you "
            + "can start drawing on the canvas. The defintion will be displayed at the top. There are hints if you need them. Good luck!";
        // for rapid fire mode
      case RAPID_FIRE:
        return "Once you click the \"I'm Ready!\" button, "
            + "the timer will start immediately and you "
            + "can start drawing on the canvas. The new word will be given at the top as you soon as you win the current word. Good luck!";
    }
    // Return that the game mode is invalid. SHould never reach this point.
    return (String) App.expect("All cases are handled");
  }
}
