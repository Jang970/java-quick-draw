package nz.ac.auckland.se206.controllers;

import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.fxmlutils.CanvasManager;
import nz.ac.auckland.se206.fxmlutils.CanvasManager.DrawMode;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;

public class GameScreenController {

  @FXML private Canvas canvas;
  @FXML private Pane canvasContainerPane;

  @FXML private Button pencilButton;
  @FXML private Button eraserButton;
  @FXML private Button clearButton;

  @FXML private Button userProfilesButton;
  @FXML private Button userButton;
  @FXML private Button gameActionButton;
  @FXML private Button downloadImageButton;

  @FXML private Label timeRemainingLabel;
  @FXML private Label whatToDrawLabel;

  @FXML private VBox guessLabelCol1;
  @FXML private VBox guessLabelCol2;
  @FXML private VBox predictionVbox;

  private Label[] guessLabels = new Label[10];

  private CanvasManager canvasManager;
  private GameLogicManager gameLogicManager;

  /** Method that is run to set up the GameScreen FXML everytime it is opened/run. */
  public void initialize() {

    // This gets the guess labels from the two columns and concatenates them into a single array for
    // easy use.
    guessLabels =
        Stream.concat(guessLabelCol1.getChildren().stream(), guessLabelCol2.getChildren().stream())
            .toArray(Label[]::new);

    canvasManager = new CanvasManager(canvas);

    gameLogicManager = App.getGameLogicManager();

    // Creates a task which gives the game logic manager a way of accessing the canvas image.
    gameLogicManager.setImageSource(
        () -> {
          FutureTask<BufferedImage> getImage =
              new FutureTask<BufferedImage>(() -> canvasManager.getCurrentSnapshot());
          Platform.runLater(getImage);
          try {
            return getImage.get();
          } catch (InterruptedException | ExecutionException error) {
            App.expect("Threads are handled properly, so this error should not occur", error);
            return null;
          }
        });

    // Subscribe to relevant changes so we can update the display accordingly
    gameLogicManager.subscribeToPredictionsChange(
        (List<Classification> predictions) -> onPredictionsChange(predictions));
    gameLogicManager.subscribeToTimeChange((Integer seconds) -> onTimeChange(seconds));
    gameLogicManager.subscribeToGameStart(() -> onGameStart());
    gameLogicManager.subscribeToGameEnd((gameInfo) -> onGameEnd(gameInfo.getWinState()));

    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.GAME) {
            // set color of user profile icon button
            setUserButtonStyle();
            // When the view changes to game, we start a new game and clear the canvas
            gameLogicManager.startGame();
            whatToDrawLabel.setText("To Draw: " + gameLogicManager.getCurrentCategory().getName());
            canvasManager.clearCanvas();
            canvasManager.resetIsDrawn();

            // doesnt cancel if just looking at user stats
          } else {
            gameLogicManager.cancelGame();
          }
        });

    // when canvas drawn changes between true and false have predictionsVbox do the same.
    // only starts showing predictions when user draws
    CanvasManager.subscribeToCanvasDrawn(
        (Boolean isDrawn) -> {
          predictionVbox.setVisible(isDrawn);
        });
  }

  /** Gets colour and sets css background colour */
  private void setUserButtonStyle() {
    userButton.setStyle(
        "-fx-background-color: "
            + App.getProfileManager().getCurrentProfile().getColour().replace("0x", "#")
            + ";");
  }

  /**
   * This method contains logic to handle what to do when the game ends depending on the result of
   * the game
   *
   * @param winState shows if the game was won, lost, cancelled or not applicable
   */
  private void onGameEnd(EndGameState winState) {
    // Run this after the game ends
    Platform.runLater(
        () -> {

          // Update all dislay items correctly

          canvasManager.setDrawingEnabled(false);
          setCanvasButtonsDisabled(true);

          gameActionButton.setText("New Game");
          whatToDrawLabel.getStyleClass().add("stateHeaders");

          if (winState == EndGameState.WIN) {
            whatToDrawLabel.setText("You got it!");
            App.getTextToSpeech().speakAsync("You got it!");
          } else if (winState == EndGameState.LOOSE) {
            whatToDrawLabel.setText("Sorry, you ran out of time!");
            App.getTextToSpeech().speakAsync("Sorry, you ran out of time!");
          } else {
            whatToDrawLabel.setText("Game cancelled.");
          }
        });
  }

  /** This method contains logic that will be run when a game is started */
  private void onGameStart() {
    Platform.runLater(
        () -> {
          // When the game starts, we do the following
          gameActionButton.setText("Cancel Game");
          setCanvasButtonsDisabled(false);
          canvasManager.setDrawingEnabled(true);
          canvasManager.setDrawMode(DrawMode.DRAWING);
          gameActionButton.setDisable(false);
          whatToDrawLabel.getStyleClass().remove("stateHeaders");
        });
  }

  /**
   * This method handles what to do when we are given new predictions/ the predictions are updated
   * TODO: have to show confidence levels
   *
   * @param classificationList list of predictions to display
   */
  private void onPredictionsChange(List<Classification> classificationList) {
    Platform.runLater(
        () -> {
          // When we are given new predictions, we update the predictions text
          int range = Math.min(classificationList.size(), guessLabels.length);

          for (int i = 0; i < range; i++) {
            String guessText = classificationList.get(i).getClassName().replace('_', ' ');
            guessLabels[i].setText((i + 1) + ": " + guessText);
          }
        });
  }

  /**
   * This method handles functionality relating to when the time to draw changes - updates the time
   * label respectively
   *
   * @param numberSeconds time given to draw
   */
  void onTimeChange(int numberSeconds) {
    Platform.runLater(
        () -> {
          updateTimeRemainingLabel(numberSeconds);
        });
  }

  /**
   * This method will update the time label with the correct formatting to the given time input
   *
   * @param numberSeconds time to update label to
   */
  private void updateTimeRemainingLabel(int numberSeconds) {
    timeRemainingLabel.setText(
        "Time Remaining: " + String.format("%2d", numberSeconds).replace(' ', '0'));
  }

  /**
   * This method will disabled relevant buttons depending on the status of the boolean input
   *
   * @param disabled value depends on if we want to enable or disable the buttons
   */
  private void setCanvasButtonsDisabled(boolean disabled) {
    pencilButton.setDisable(disabled);
    eraserButton.setDisable(disabled);
    clearButton.setDisable(disabled);
  }

  /** Set the 'tool' in use to be the pencil so player can draw */
  @FXML
  private void onPencilSelected() {
    canvasManager.setDrawMode(DrawMode.DRAWING);
  }

  /** Set the 'tool' in use to be the eraser so player can erase */
  @FXML
  private void onEraserSelected() {
    canvasManager.setDrawMode(DrawMode.ERASING);
  }

  /** Remove everything on the canvas i.e return it to a blank state */
  @FXML
  private void onClearCanvas() {
    canvasManager.clearOnlyIfDrawingEnabled();
  }

  /** Method relating to the button switch to UserProfilesScreen FXML */
  @FXML
  private void onUserProfiles() {
    App.setView(View.USERPROFILES);
  }

  /** Method relating to the button switch to UserScreen FXML */
  @FXML
  private void onUserStats() {
    App.setView(View.USER);
  }

  /**
   * Method relating to the Button that will cancel the current game if playing but otherwise will
   * return user to CategoryScreen FXML
   */
  @FXML
  private void onGameAction() {
    if (gameLogicManager.isPlaying()) {
      gameLogicManager.cancelGame();
    } else {
      App.setView(View.CATEGORY);
    }
  }

  /** Method relating to the button that will allow user to save the image drawn */
  @FXML
  private void onDownloadImage() {
    try {
      canvasManager.saveCurrentSnapshotOnFile();
    } catch (IOException e) {
      System.out.println("Failed to download");
    }
  }
}
