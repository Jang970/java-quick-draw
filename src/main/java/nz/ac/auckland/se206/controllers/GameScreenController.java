package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
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

  private Label[] guessLabels = new Label[10];

  private CanvasManager canvasManager;
  private TextToSpeech textToSpeech;
  private GameLogicManager gameLogicManager;

  /**
   * JavaFX calls this method once the GUI elements are loaded.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    guessLabels =
        Stream.concat(guessLabelCol1.getChildren().stream(), guessLabelCol2.getChildren().stream())
            .toArray(Label[]::new);

    canvasManager = new CanvasManager(canvas);
    textToSpeech = new TextToSpeech();

    gameLogicManager = App.getGameLogicManager();

    gameLogicManager.setImageSource(
        () -> {
          FutureTask<BufferedImage> getImage =
              new FutureTask<BufferedImage>(() -> canvasManager.getCurrentSnapshot());
          Platform.runLater(getImage);
          try {
            return getImage.get();
          } catch (InterruptedException | ExecutionException error) {
            System.out.println("There was an error in getting an image: " + error.getMessage());
            return null;
          }
        });

    gameLogicManager.subscribeToPredictionsChange(
        (List<Classification> predictions) -> onPredictionsChange(predictions));
    gameLogicManager.subscribeToTimeChange((Integer seconds) -> onTimeChange(seconds));
    gameLogicManager.subscribeToGameStart(() -> onGameStart());
    gameLogicManager.subscribeToGameEnd((WinState winState) -> onGameEnd(winState));

    App.subscribeToAppTermination(
        (e) -> {
          textToSpeech.terminate();
        });

    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.GAME) {
            // When the view changes to game, we start a new game and clear the canvas
            gameLogicManager.startGame();
            whatToDrawLabel.setText("To Draw: " + gameLogicManager.getCurrentCategory());
            canvasManager.clearCanvas();
          } else {
            gameLogicManager.cancelGame();
          }
        });
  }

  private void setUserButtonStyle() {
    try {
      userButton.setStyle(
          "-fx-background-color: "
              + App.getUserManager().getCurrentProfile().getColour().replace("0x", "#")
              + ";");
    } catch (IOException | URISyntaxException e1) {
      e1.printStackTrace();
    }
  }

  private void onGameEnd(WinState winState) {
    Platform.runLater(
        () -> {
          canvasManager.setDrawingEnabled(false);
          setCanvasButtonsDisabled(true);

          updateTimeRemainingLabel(0);
          gameActionButton.setText("New Game");

          // TODO: Results are not being displayed!
          if (winState == WinState.WIN) {
            textToSpeech.speakAsync("You got it!");
          } else if (winState == WinState.LOOSE) {
            textToSpeech.speakAsync("Sorry, you ran out of time!");
          }
        });
  }

  private void onGameStart() {
    Platform.runLater(
        () -> {
          gameActionButton.setText("Cancel Game");
          setCanvasButtonsDisabled(false);
          canvasManager.setDrawingEnabled(true);
          gameActionButton.setDisable(false);
        });
  }

  private void onPredictionsChange(List<Classification> classificationList) {
    Platform.runLater(
        () -> {
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
    App.setView(View.USERSTATS);
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
      System.out.println("Failed to download");
    }
  }
}
