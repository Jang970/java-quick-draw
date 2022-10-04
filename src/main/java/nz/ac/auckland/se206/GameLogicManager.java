package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import nz.ac.auckland.se206.fxmlutils.CanvasManager;
import nz.ac.auckland.se206.util.CountdownTimer;
import nz.ac.auckland.se206.util.DataSource;
import nz.ac.auckland.se206.util.EmptyEventEmitter;
import nz.ac.auckland.se206.util.EmptyEventListener;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.PredictionManager;

/**
 * This class contains all of the logic for the QuickDraw Game. It manages game state and
 * transitions between those states
 */
public class GameLogicManager {
  public record GameEndInfo(
      WinState winState, String category, int timeTaken, int secondsRemaining) {}

  public enum WinState {
    WIN,
    LOOSE,
    CANCEL,
  }

  private int gameLengthSeconds;
  private int numTopGuessNeededToWin;

  private Boolean isPlaying = false;

  private String categoryToGuess;

  private PredictionManager predictionManager;
  private CountdownTimer countdownTimer;

  private EventEmitter<List<Classification>> predictionChangeEmitter =
      new EventEmitter<List<Classification>>();
  private EventEmitter<GameEndInfo> gameEndedEmitter = new EventEmitter<GameEndInfo>();
  private EventEmitter<Integer> timeChangedEmitter = new EventEmitter<Integer>();
  private EmptyEventEmitter gameStartedEmitter = new EmptyEventEmitter();

  /**
   * @throws IOException If there is an error in reading the input/output of the DL model.
   * @throws ModelException If the model cannot be found on the file system.
   */
  public GameLogicManager() throws IOException, ModelException {

    countdownTimer = new CountdownTimer();
    countdownTimer.setOnChange(
        (secondsRemaining) -> {
          timeChangedEmitter.emit(secondsRemaining);
        });

    countdownTimer.setOnComplete(
        () -> {
          endGame(WinState.LOOSE);
        });

    predictionManager = new PredictionManager(100, 10);

    predictionManager.setPredictionListener(
        (predictions) -> {
          handlePredictionsReceived(predictions);
        });
  }

  private void endGame(WinState winState) {
    // get info
    int secondsRemaining = countdownTimer.getRemainingCount();
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

    // send the end game information
    gameEndedEmitter.emit(
        new GameEndInfo(
            winState,
            this.categoryToGuess,
            gameLengthSeconds - secondsRemaining - 1,
            secondsRemaining));

    isPlaying = false;
  }

  private void handlePredictionsReceived(List<Classification> predictions) {
    int range = Math.min(predictions.size(), numTopGuessNeededToWin);
    for (int i = 0; i < range; i++) {
      String prediction = predictions.get(i).getClassName().replace('_', ' ');
      // wins only if prediction matchs and if canvas is drawn on
      if (prediction.equals(categoryToGuess) && CanvasManager.getIsDrawn()) {
        endGame(WinState.WIN);
      }
    }

    this.predictionChangeEmitter.emit(predictions);
  }

  /** Ends the game if it is ongoing with a win state of CANCEL */
  public void cancelGame() {
    if (isPlaying) {
      this.endGame(WinState.CANCEL);
    }
  }

  /** Starts the countdown, and enables the prediction server */
  public void startGame() {
    countdownTimer.startCountdown(gameLengthSeconds);
    predictionManager.startMakingPredictions();
    gameStartedEmitter.emit();
    isPlaying = true;
  }

  public int getRemainingSeconds() {
    return countdownTimer.getRemainingCount();
  }

  /**
   * Gets the number of seconds that the game should run for when starting
   *
   * @return the number of seconds that the game should run for before ending
   */
  public int getGameLengthSeconds() {
    return gameLengthSeconds;
  }

  /**
   * Sets how long the game should last when starting a new game
   *
   * @param gameLengthSeconds the number of seconds the game should run for
   */
  public void setGameLengthSeconds(int gameLengthSeconds) {
    this.gameLengthSeconds = gameLengthSeconds;
  }

  /**
   * Gets where the correct guess needs to be in the top guesses in order to win.
   *
   * @return the number which if the player predictor gets the guess in that range, they win
   */
  public int getNumTopGuessNeededToWin() {
    return numTopGuessNeededToWin;
  }

  /**
   * Sets where the correct guess needs to be in the top guesses in order to win.
   *
   * @param numTopGuessNeededToWin the number which if the player predictor gets the guess in that
   *     range, they win
   */
  public void setNumTopGuessNeededToWin(int numTopGuessNeededToWin) {
    this.numTopGuessNeededToWin = numTopGuessNeededToWin;
  }

  /**
   * Sets an image source for the predictor. This is a function which returns a BufferedImage which
   * the prediction manager can call
   *
   * @param imageSource the image providing function
   */
  public void setImageSource(DataSource<BufferedImage> imageSource) {
    predictionManager.setImageSource(imageSource);
  }

  /**
   * @return the category that the player need to draw
   */
  public String getCurrentCategory() {
    return categoryToGuess;
  }

  public Boolean isPlaying() {
    return isPlaying;
  }

  public void subscribeToGameEnd(EventListener<GameEndInfo> listener) {
    gameEndedEmitter.subscribe(listener);
  }

  public void subscribeToGameStart(EmptyEventListener listener) {
    gameStartedEmitter.subscribe(listener);
  }

  public void subscribeToTimeChange(EventListener<Integer> listener) {
    timeChangedEmitter.subscribe(listener);
  }

  public void subscribeToPredictionsChange(EventListener<List<Classification>> listener) {
    predictionChangeEmitter.subscribe(listener);
  }

  public void initializeGame() {
    categoryToGuess = predictionManager.selectNewRandomEasyCategory();
  }
}
