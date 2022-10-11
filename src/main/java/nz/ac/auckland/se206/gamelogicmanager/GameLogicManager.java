package nz.ac.auckland.se206.gamelogicmanager;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.fxmlutils.CanvasManager;
import nz.ac.auckland.se206.util.Category;
import nz.ac.auckland.se206.util.CategoryType;
import nz.ac.auckland.se206.util.CountdownTimer;
import nz.ac.auckland.se206.util.DataSource;
import nz.ac.auckland.se206.util.EmptyEventEmitter;
import nz.ac.auckland.se206.util.EmptyEventListener;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.FilterTooStrictException;
import nz.ac.auckland.se206.util.PredictionManager;
import nz.ac.auckland.se206.util.difficulties.WordChoice;

/**
 * This class contains all of the logic for the QuickDraw Game. It manages game state and
 * transitions between those states
 */
public class GameLogicManager {
  private Boolean isPlaying = false;

  private Category categoryToGuess;

  private PredictionManager predictionManager;
  private CountdownTimer countdownTimer;

  private GameProfile currentGameProfile;

  private EventEmitter<List<Classification>> predictionChangeEmitter =
      new EventEmitter<List<Classification>>();
  private EventEmitter<Category> categoryChangeEmitter = new EventEmitter<Category>();

  private EventEmitter<GameInfo> gameEndedEmitter = new EventEmitter<GameInfo>();
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
          onOutOfTime();
        });

    predictionManager = new PredictionManager(100, 10);
    predictionManager.setPredictionListener(
        (predictions) -> {
          int topNumGuessesNeededToWin =
              currentGameProfile.settings().getAccuracy().getTopNumGuesses();
          double confidenceNeededToWin =
              currentGameProfile.settings().getConfidence().getProbabilityPercentage();
          int range = Math.min(predictions.size(), topNumGuessesNeededToWin);
          for (int i = 0; i < range; i++) {
            String prediction = predictions.get(i).getClassName().replace('_', ' ');
            double confidence = predictions.get(i).getProbability();
            // wins only if prediction matchs and if canvas is drawn on
            if (prediction.equals(categoryToGuess.name)
                && confidence >= confidenceNeededToWin
                && CanvasManager.getIsDrawn()) {
              onCorrectPrediction();
            }
          }

          this.predictionChangeEmitter.emit(predictions);
        });
  }

  ///////////////////////////// GAME STATE TRANSISTIONS /////////////////////////////

  public void initializeGame(GameProfile profile) {
    currentGameProfile = profile;

    Set<String> categories =
        profile.gameHistory().stream()
            .flatMap((game) -> game.categoriesPlayed.stream().map((cat) -> cat.name))
            .collect(Collectors.toSet());

    WordChoice wordChoice = profile.settings().getWordChoice();

    try {
      categoryToGuess =
          predictionManager.getNewRandomCategory(
              categories,
              wordChoice.categoryShouldBeIncluded(CategoryType.EASY),
              wordChoice.categoryShouldBeIncluded(CategoryType.MEDIUM),
              wordChoice.categoryShouldBeIncluded(CategoryType.HARD));
    } catch (FilterTooStrictException e) {
      e.printStackTrace();
    }
  }

  /** Starts the countdown, and enables the prediction server */
  public void startGame() {
    countdownTimer.startCountdown(currentGameProfile.settings().getTime().getTimeToDraw());
    predictionManager.startMakingPredictions();
    gameStartedEmitter.emit();
    isPlaying = true;
  }

  /** Ends the game if it is ongoing with a win state of CANCEL */
  public void cancelGame() {
    if (isPlaying) {
      this.endGame(EndGameState.CANCEL);
    }
  }

  private void endGame(EndGameState winState) {
    // get info
    int secondsRemaining = countdownTimer.getRemainingCount();
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

    // send the end game information
    gameEndedEmitter.emit(
        new GameInfo(
            winState,
            List.of(categoryToGuess),
            currentGameProfile.settings().getTime().getTimeToDraw() - secondsRemaining - 1,
            secondsRemaining,
            currentGameProfile.settings(),
            currentGameProfile.gameMode()));

    isPlaying = false;
  }

  private void onCorrectPrediction() {
    endGame(EndGameState.WIN);
  }

  private void onOutOfTime() {
    endGame(EndGameState.LOOSE);
  }

  ///////////////////////////// GETTING AND SETTING GAME DATA /////////////////////////////

  public int getRemainingSeconds() {
    return countdownTimer.getRemainingCount();
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
  public Category getCurrentCategory() {
    return categoryToGuess;
  }

  public Boolean isPlaying() {
    return isPlaying;
  }

  public GameProfile getCurrentGameProfile() {
    return currentGameProfile;
  }

  public void subscribeToGameEnd(EventListener<GameInfo> listener) {
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

  public void subscribeToCategoryChange(EventListener<Category> listener) {
    categoryChangeEmitter.subscribe(listener);
  }
}
