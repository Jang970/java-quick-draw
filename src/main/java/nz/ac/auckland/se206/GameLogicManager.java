package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import nz.ac.auckland.se206.util.*;
import nz.ac.auckland.se206.util.CategoryGenerator.Difficulty;

/**
 * This class contains all of the logic for the QuickDraw Game. It manages game state and
 * transitions between those states
 */
public class GameLogicManager {

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
  private CategoryGenerator categoryGenerator;

  private EventEmitter<List<Classification>> predictionChangeEmitter =
      new EventEmitter<List<Classification>>();
  private EventEmitter<WinState> gameEndedEmitter = new EventEmitter<WinState>();
  private EventEmitter<Integer> timeChangedEmitter = new EventEmitter<Integer>();
  private EmptyEventEmitter gameStartedEmitter = new EmptyEventEmitter();

  /**
   * @param numPredictions the number of predictions the game should make for each snapshot image
   * @throws IOException If there is an error in reading the input/output of the DL model.
   * @throws ModelException If the model cannot be found on the file system.
   */
  public GameLogicManager(int numPredictions) throws IOException, ModelException {

    categoryGenerator = new CategoryGenerator("category_difficulty.csv");

    countdownTimer = new CountdownTimer();
    countdownTimer.setOnChange(
        (secondsRemaining) -> {
          timeChangedEmitter.emit(secondsRemaining);
        });

    countdownTimer.setOnComplete(
        () -> {
          endGame(WinState.LOOSE);
        });

    predictionManager = new PredictionManager(100, numPredictions);

    predictionManager.setPredictionListener(
        (predictions) -> {
          handleNewPredictions(predictions);
        });
  }

  private void endGame(WinState winState) {
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();
    gameEndedEmitter.emit(winState);
    isPlaying = false;
  }

  private void handleNewPredictions(List<Classification> predictions) {
    int range = Math.min(predictions.size(), numTopGuessNeededToWin);
    for (int i = 0; i < range; i++) {
      String prediction = predictions.get(i).getClassName().replace('_', ' ');
      if (prediction.equals(categoryToGuess)) {
        endGame(WinState.WIN);
      }
    }

    this.predictionChangeEmitter.emit(predictions);
  }

  /**
   * Selects a new random category and sets it as the category to guess
   *
   * @return the new category
   */
  public String selectNewRandomCategory() {
    categoryToGuess = categoryGenerator.getRandomCategory(Difficulty.EASY);
    return categoryToGuess;
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

  /**
   * Gets the number of seconds until the game ends
   *
   * @return the number of seconds until the game ends
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

  public void subscribeToGameEnd(EventListener<WinState> listener) {
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
}
