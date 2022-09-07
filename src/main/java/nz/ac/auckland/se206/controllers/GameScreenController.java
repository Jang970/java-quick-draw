package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.GameLogicManager;
import nz.ac.auckland.se206.GameLogicManager.WinState;
import nz.ac.auckland.se206.controllers.CanvasManager.DrawMode;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class GameScreenController {

  @FXML private Canvas canvas;
  @FXML private Pane canvasContainerPane;

  @FXML private Button pencilButton;
  @FXML private Button eraserButton;
  @FXML private Button clearButton;

  @FXML private Button returnHomeButton;
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
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    // Get guess labels
    int labelIndex = 0;
    // TODO: Remove repetition (possibily through another method)
    for (Node child : guessLabelCol1.getChildren()) {
      if (child instanceof Label) {
        guessLabels[labelIndex] = (Label) child;
        labelIndex++;
      }
    }
    for (Node child : guessLabelCol2.getChildren()) {
      if (child instanceof Label) {
        guessLabels[labelIndex] = (Label) child;
        labelIndex++;
      }
    }

    canvasManager = new CanvasManager(canvas);
    textToSpeech = new TextToSpeech();

    gameLogicManager = App.getGameLogicManager();

    gameLogicManager.setImageSource(
        () -> {
          final FutureTask<BufferedImage> query =
              new FutureTask<BufferedImage>(
                  new Callable<BufferedImage>() {
                    @Override
                    public BufferedImage call() throws Exception {
                      return canvasManager.getCurrentSnapshot();
                    }
                  });
          Platform.runLater(query);
          try {
            return query.get();
          } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
            System.exit(0);
            return null;
          }
        });

    gameLogicManager.subscriveToPredictionsChange(
        (List<Classification> predictions) -> onPredictionsChange(predictions));
    gameLogicManager.subscribeToTimeChange((Integer seconds) -> onTimeChange(seconds));
    gameLogicManager.subscribeToGameStart(() -> onGameStart());
    gameLogicManager.subscribeToGameEnd((WinState winState) -> onGameEnd(winState));

    // TODO: Make this an app event emitter that we can subscribe to
    App.getStage()
        .setOnCloseRequest(
            (e) -> {
              System.out.println("Terminating application");
              textToSpeech.terminate();
            });

    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.HOME || newView == View.CATEGORY) {
            gameLogicManager.cancelGame();
          } else if (newView == View.GAME) {
            // When the view changes to game, we start a new game and clear the canvas
            gameLogicManager.startGame();
            whatToDrawLabel.setText("To Draw: " + gameLogicManager.getCurrentCategory());
            canvasManager.clearCanvas();
          }
        });
  }

  private void onGameEnd(WinState winState) {
    Platform.runLater(
        () -> {
          canvasManager.setDrawingEnabled(false);
          setCanvasButtonsDisabled(true);
          updateTimeRemainingLabel(0);

          gameActionButton.setText("New Game");

          if (winState == WinState.WIN) {
            textToSpeech.speakAsync("You got it!");
          } else {
            textToSpeech.speakAsync("Sorry, you ran out of time!");
          }
        });
  }

  private void onGameStart() {
    setCanvasButtonsDisabled(false);
    canvasManager.setDrawingEnabled(true);
    gameActionButton.setDisable(false);
  }

  /**
   * This method updates the guess labels with the top guesses
   *
   * @param classificationList the list of top guesses from the model with percentage likelihood
   */
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

  /**
   * This function sets the timer label to the time based on the number of seconds given
   *
   * @param numberSeconds the number of seconds remaining on the timer
   */
  void onTimeChange(int numberSeconds) {
    Platform.runLater(
        () -> {

          // Update the time label with the new minutes and seconds.
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

  // The following methods are associated with button presses

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
  private void onReturnHome() {
    App.setView(View.HOME);
    gameLogicManager.cancelGame();
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
