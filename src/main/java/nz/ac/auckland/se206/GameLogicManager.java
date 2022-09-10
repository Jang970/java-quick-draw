package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import nz.ac.auckland.se206.util.*;
import nz.ac.auckland.se206.util.CategoryGenerator.Difficulty;

/**
 * This class contains all of the logic for the QuickDraw Game It manages game state and transitions
 * between those states
 */
public class GameLogicManager {

  public enum WinState {
    WIN,
    LOOSE,
    CANCEL,
  }

  // TODO: Extract these into a settings page
  // TODO: Make timer seconds more readable
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

  public GameLogicManager(int numPredictions) throws IOException, ModelException {

    categoryGenerator = new CategoryGenerator("category_difficulty.csv");

    countdownTimer = new CountdownTimer();
    countdownTimer.setOnChange(
        (int secondsRemaining) -> {
          timeChangedEmitter.emit(secondsRemaining);
        });

    countdownTimer.setOnComplete(
        () -> {
          endGame(WinState.LOOSE);
        });

    // The prediction manager takes care of everything to do with guessing the
    // drawing
    predictionManager = new PredictionManager(100, numPredictions);

    predictionManager.setPredictionListener(
        (List<Classification> predictions) -> {
          handleNewPredictions(predictions);
        });
  }

  public int getGameLengthSeconds() {
    return gameLengthSeconds;
  }

  public void setGameLengthSeconds(int gameLengthSeconds) {
    this.gameLengthSeconds = gameLengthSeconds;
  }

  public int getNumTopGuessNeededToWin() {
    return numTopGuessNeededToWin;
  }

  public void setNumTopGuessNeededToWin(int numTopGuessNeededToWin) {
    this.numTopGuessNeededToWin = numTopGuessNeededToWin;
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

  public void setImageSource(DataSource<BufferedImage> imageSource) {
    predictionManager.setImageSource(imageSource);
  }

  public String getCurrentCategory() {
    return categoryToGuess;
  }

  public String selectNewRandomCategory() {
    categoryToGuess = categoryGenerator.generateCategory(Difficulty.EASY);
    return categoryToGuess;
  }

  public void cancelGame() {
    if (isPlaying) {
      this.endGame(WinState.CANCEL);
    }
  }

  public Boolean isPlaying() {
    return isPlaying;
  }

  public void startGame() {
    countdownTimer.startCountdown(gameLengthSeconds);
    predictionManager.startMakingPredictions();
    gameStartedEmitter.emit();
    isPlaying = true;
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

  public void subscriveToPredictionsChange(EventListener<List<Classification>> listener) {
    predictionChangeEmitter.subscribe(listener);
  }
}
