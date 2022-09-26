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
import nz.ac.auckland.se206.GameLogicManager;
import nz.ac.auckland.se206.GameLogicManager.WinState;
import nz.ac.auckland.se206.fxmlutils.CanvasManager;
import nz.ac.auckland.se206.fxmlutils.CanvasManager.DrawMode;
import nz.ac.auckland.se206.speech.TextToSpeech;

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
  private TextToSpeech textToSpeech;
  private GameLogicManager gameLogicManager;

  public void initialize() {

    // This gets the guess labels from the two columns and concatenates them into a single array for
    // easy use.
    guessLabels =
        Stream.concat(guessLabelCol1.getChildren().stream(), guessLabelCol2.getChildren().stream())
            .toArray(Label[]::new);

    canvasManager = new CanvasManager(canvas);
    textToSpeech = new TextToSpeech();

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

    App.subscribeToAppTermination(
        (e) -> {
          textToSpeech.terminate();
        });

    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.GAME) {
            // set color of user profile icon button
            setUserButtonStyle();
            // When the view changes to game, we start a new game and clear the canvas
            gameLogicManager.startGame();
            whatToDrawLabel.setText("To Draw: " + gameLogicManager.getCurrentCategory());
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

  private void onGameEnd(WinState winState) {
    // Run this after the game ends
    Platform.runLater(
        () -> {

          // Update all dislay items correctly

          canvasManager.setDrawingEnabled(false);
          setCanvasButtonsDisabled(true);

          gameActionButton.setText("New Game");
          whatToDrawLabel.getStyleClass().add("stateHeaders");

          if (winState == WinState.WIN) {
            whatToDrawLabel.setText("You got it!");
            textToSpeech.speakAsync("You got it!");
          } else if (winState == WinState.LOOSE) {
            whatToDrawLabel.setText("Sorry, you ran out of time!");
            textToSpeech.speakAsync("Sorry, you ran out of time!");
          } else {
            whatToDrawLabel.setText("Game cancelled.");
          }
        });
  }

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

  void onTimeChange(int numberSeconds) {
    Platform.runLater(
        () -> {
          updateTimeRemainingLabel(numberSeconds);
        });
  }

  private void updateTimeRemainingLabel(int numberSeconds) {
    timeRemainingLabel.setText(
        "Time Remaining: " + String.format("%2d", numberSeconds).replace(' ', '0'));
  }

  private void setCanvasButtonsDisabled(boolean disabled) {
    pencilButton.setDisable(disabled);
    eraserButton.setDisable(disabled);
    clearButton.setDisable(disabled);
  }

  @FXML
  private void onPencilSelected() {
    canvasManager.setDrawMode(DrawMode.DRAWING);
  }

  @FXML
  private void onEraserSelected() {
    canvasManager.setDrawMode(DrawMode.ERASING);
  }

  @FXML
  private void onClearCanvas() {
    canvasManager.clearOnlyIfDrawingEnabled();
  }

  @FXML
  private void onUserProfiles() {
    App.setView(View.USERPROFILES);
  }

  @FXML
  private void onUserStats() {
    App.setView(View.USER);
  }

  @FXML
  private void onGameAction() {
    if (gameLogicManager.isPlaying()) {
      gameLogicManager.cancelGame();
    } else {
      App.setView(View.CATEGORY);
    }
  }

  // TODO: Figure out why it breaks when cancelled
  @FXML
  private void onDownloadImage() {
    try {
      canvasManager.saveCurrentSnapshotOnFile();
    } catch (IOException e) {
      // TODO: Handle cancellation
      System.out.println("Failed to download");
    }
  }
}
