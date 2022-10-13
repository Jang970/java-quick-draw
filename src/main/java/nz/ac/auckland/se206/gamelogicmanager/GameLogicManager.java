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
   * This is the constructor for the GameLogicManager class which contains and handles all the logic
   * for the game.
   *
   * @throws IOException If there is an error in reading the input/output of the DL model.
   * @throws ModelException If the model cannot be found on the file system.
   */
  public GameLogicManager() throws IOException, ModelException {

    // initialise countdown timer
    countdownTimer = new CountdownTimer();
    countdownTimer.setOnChange(
        (secondsRemaining) -> {
          timeChangedEmitter.emit(secondsRemaining);
        });
    countdownTimer.setOnComplete(
        () -> {
          onOutOfTime();
        });

    // initialise the prediction manager
    predictionManager = new PredictionManager(100, 10);
    predictionManager.setPredictionListener(
        (predictions) -> {
          // set variables relative to the difficulty set
          int topNumGuessesNeededToWin =
              currentGameProfile.settings().getAccuracy().getTopNumGuesses();
          double confidenceNeededToWin =
              currentGameProfile.settings().getConfidence().getProbabilityPercentage();
          // make the predictions and format
          int range = Math.min(predictions.size(), topNumGuessesNeededToWin);
          for (int i = 0; i < range; i++) {
            String prediction = predictions.get(i).getClassName().replace('_', ' ');
            double confidence = predictions.get(i).getProbability();
            // wins only if prediction matchs and if canvas is drawn on
            if (prediction.equals(categoryToGuess.getName())
                && confidence >= confidenceNeededToWin
                && CanvasManager.getIsDrawn()) {
              onCorrectPrediction();
            }
          }

          this.predictionChangeEmitter.emit(predictions);
        });
  }

  ///////////////////////////// GAME STATE TRANSISTIONS /////////////////////////////

  /**
   * This method will get the game ready to be played using the current profile
   *
   * @param profile current profile playing
   */
  public void initializeGame(GameProfile profile) {
    currentGameProfile = profile;

    // logic to get a set of all categories played by a user
    Set<String> categories =
        profile.gameHistory().stream()
            .flatMap(
                (game) ->
                    game.getCategoriesPlayed().stream()
                        .map((categoryPlayed) -> categoryPlayed.getCategory().getName()))
            .collect(Collectors.toSet());

    WordChoice wordChoice = profile.settings().getWordChoice();

    try {
      // check which categories to include in generating a new category to play
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
    // start the count, prediction and send data if the game started
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

  /**
   * This helper method is used to end the game depending on if the game is won, cancelled or lost
   *
   * @param winState the state of the game when it ends
   */
  private void endGame(EndGameState winState) {
    // get info
    int secondsRemaining = countdownTimer.getRemainingCount();
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

    // send the end game information
    gameEndedEmitter.emit(
        new GameInfo(
            winState,
            List.of(
                new CategoryPlayedInfo(
                    currentGameProfile.settings().getTime().getTimeToDraw() - secondsRemaining - 1,
                    secondsRemaining,
                    categoryToGuess)),
            currentGameProfile.settings(),
            currentGameProfile.gameMode()));

    isPlaying = false;
  }

  /**
   * This method will be called when the player wins and will end the game and set the end game
   * state to be WIN
   */
  private void onCorrectPrediction() {
    endGame(EndGameState.WIN);
  }

  /**
   * This method will be called when the player losses and will end the game and set the end game
   * state to be LOOSE
   */
  private void onOutOfTime() {
    endGame(EndGameState.LOOSE);
  }

  ///////////////////////////// GETTING AND SETTING GAME DATA /////////////////////////////

  /**
   * This method will return the current count of the countdownTimer
   *
   * @return current count of countdownTimer
   */
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
   * This method will get the category that the player need to draw
   *
   * @return the category that the player need to draw
   */
  public Category getCurrentCategory() {
    return categoryToGuess;
  }

  /**
   * This method will get the state of the isPlaying variable
   *
   * @return True or False
   */
  public Boolean isPlaying() {
    return isPlaying;
  }

  /**
   * This method will return the current game profile
   *
   * @return the current game profile
   */
  public GameProfile getCurrentGameProfile() {
    return currentGameProfile;
  }

  /**
   * This method allows us to add a listener to event emitter gameEndedEmitter to keep track of when
   * the tgame ends
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToGameEnd(EventListener<GameInfo> listener) {
    gameEndedEmitter.subscribe(listener);
  }

  /**
   * This method allows us to add a listener to event emitter gameStartedEmitter to keep track of
   * when the game starts
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToGameStart(EmptyEventListener listener) {
    gameStartedEmitter.subscribe(listener);
  }

  /**
   * This method allows us to add a listener to event emitter timeChangedEmitter to keep track of
   * when the time changes
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToTimeChange(EventListener<Integer> listener) {
    timeChangedEmitter.subscribe(listener);
  }

  /**
   * This method allows us to add a listener to event emitter predictionChangeEmitter to keep track
   * of when the prediction changes
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToPredictionsChange(EventListener<List<Classification>> listener) {
    predictionChangeEmitter.subscribe(listener);
  }

  /**
   * This method allows us to add a listener to event emitter categoryChangeEmitter to keep track of
   * when the category changes
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToCategoryChange(EventListener<Category> listener) {
    categoryChangeEmitter.subscribe(listener);
  }
}
