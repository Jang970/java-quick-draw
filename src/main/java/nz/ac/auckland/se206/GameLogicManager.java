package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import com.opencsv.exceptions.CsvException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.util.*;

/**
 * This class contains all of the logic for the QuickDraw Game. It manages game state and
 * transitions between those states
 */
public class GameLogicManager {

  public class FilterTooStrictException extends Exception {
    public FilterTooStrictException(String message) {
      super(message);
    }
  }

  public class GameEndInfo {
    private WinState winState;

    private String category;

    private int timeTaken;
    private int secondsRemaining;

    GameEndInfo(WinState winState, String category, int timeTaken, int secondsRemaining) {
      this.winState = winState;
      this.category = category;
      this.timeTaken = timeTaken;
      this.secondsRemaining = secondsRemaining;
    }

    public int getTimeTaken() {
      return timeTaken;
    }

    public int getSecondsRemaining() {
      return secondsRemaining;
    }

    public WinState getWinState() {
      return winState;
    }

    public String getCategory() {
      return category;
    }
  }

  public enum WinState {
    WIN,
    LOOSE,
    CANCEL,
  }

  public enum CategoryType {
    EASY,
    MEDIUM,
    HARD,
  }

  private Map<CategoryType, List<String>> categories;

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
   * @param numPredictions the number of predictions the game should make for each snapshot image
   * @throws IOException If there is an error in reading the input/output of the DL model.
   * @throws ModelException If the model cannot be found on the file system.
   * @throws CsvException
   */
  public GameLogicManager(int numPredictions) throws IOException, ModelException, CsvException {

    categories =
        new CSVKeyValuePairLoader<CategoryType, String>(
                (keyString) -> {
                  if (keyString.equals("E")) return CategoryType.EASY;
                  if (keyString.equals("M")) return CategoryType.MEDIUM;
                  if (keyString.equals("H")) return CategoryType.HARD;
                  return null;
                },
                (v) -> v)
            .loadCategoriesFromFile(App.getResourcePath("category_difficulty.csv"), true);

    if (categories.isEmpty()) {
      throw new CsvException("The csv is not valid");
    }

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
          handlePredictionsReceived(predictions);
        });
  }

  private void endGame(WinState winState) {
    int secondsRemaining = countdownTimer.getRemainingCount();

    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

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
      if (prediction.equals(categoryToGuess)) {
        endGame(WinState.WIN);
      }
    }

    this.predictionChangeEmitter.emit(predictions);
  }

  /**
   * This generates a new random category, updates the category for the class and returns the value
   * of the new category. It will not use any values in the provided set
   *
   * @param categoryFilter
   * @return
   */
  public String selectNewRandomCategory(Set<String> categoryFilter)
      throws FilterTooStrictException {

    List<String> easyCategories = categories.get(CategoryType.EASY);

    List<String> possibleCategories =
        easyCategories.stream()
            .filter((category) -> !categoryFilter.contains(category))
            .collect(Collectors.toList());

    if (possibleCategories.isEmpty())
      throw new FilterTooStrictException("The filter filtered out all categories");

    int randomIndexFromList = ThreadLocalRandom.current().nextInt(possibleCategories.size());

    categoryToGuess = possibleCategories.get(randomIndexFromList);

    return categoryToGuess;
  }

  public String selectNewRandomCategory() {
    try {
      return this.selectNewRandomCategory(new HashSet<String>());
    } catch (FilterTooStrictException e) {
      System.exit(0);
      return "";
    }
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
}
