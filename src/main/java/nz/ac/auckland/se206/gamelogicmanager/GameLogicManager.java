package nz.ac.auckland.se206.gamelogicmanager;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.QuickDrawGameManager;
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

  // This is used to track the categories played in rapid fire mode
  private List<CategoryPlayedInfo> categoriesPlayedInThisGame = new ArrayList<CategoryPlayedInfo>();

  private PredictionManager predictionManager;
  private CountdownTimer countdownTimer;

  private GameProfile currentGameProfile;

  private int gameTimeCounter = 0;

  private EventEmitter<List<Classification>> predictionChangeEmitter =
      new EventEmitter<List<Classification>>();
  private EventEmitter<Category> categoryChangeEmitter = new EventEmitter<Category>();

  private EventEmitter<GameInfo> gameEndedEmitter = new EventEmitter<GameInfo>();
  private EventEmitter<CategoryPlayedInfo> correctPredictionEmitter =
      new EventEmitter<CategoryPlayedInfo>();
  private EventEmitter<Integer> timeChangedEmitter = new EventEmitter<Integer>();
  private EmptyEventEmitter gameStartedEmitter = new EmptyEventEmitter();

  private boolean sentPredictionMessage = false;
  private boolean predictionWinningEnabled = true;

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
          gameTimeCounter++;
        });
    countdownTimer.setOnComplete(
        () -> {
          onOutOfTime();
        });

    // initialise the prediction manager
    predictionManager = new PredictionManager(100, -1);

    predictionManager.setPredictionListener(
        (predictions) -> {
          if (predictionWinningEnabled) {
            int topNumGuessesNeededToWin =
                currentGameProfile.settings().getAccuracy().getTopNumGuesses();

            double confidenceNeededToWin =
                currentGameProfile.settings().getConfidence().getProbabilityPercentage();

            int range = Math.min(predictions.size(), topNumGuessesNeededToWin);

            for (int i = 0; i < range; i++) {
              String prediction = predictions.get(i).getClassName().replace('_', ' ');
              double confidence = predictions.get(i).getProbability();

              if (prediction.equals(categoryToGuess.getName())
                  && confidence >= confidenceNeededToWin) {
                if (!sentPredictionMessage) {
                  // Checks if sent so we don't spam the onCorrectPrediction
                  sentPredictionMessage = true;
                  onCorrectPrediction();
                }
              } else {
                // Resets when the prediction is no longer true.
                sentPredictionMessage = false;
              }
            }
          }

          predictionChangeEmitter.emit(predictions);
        });
  }

  ///////////////////////////// GAME STATE TRANSISTIONS /////////////////////////////

  /**
   * This method will get the game ready to be played using the given profile
   *
   * @param profile current profile playing
   */
  public void initializeGame(GameProfile profile) {

    stopGame();

    currentGameProfile = profile;

    categoriesPlayedInThisGame.clear();

    selectNewRandomCategory();
  }

  private void selectNewRandomCategory() {

    Set<String> allCategoriesPlayed = new HashSet<String>();

    List<CategoryPlayedInfo> previouslyPlayedCategories =
        QuickDrawGameManager.getCategoriesPlayedForGameMode(
            currentGameProfile.gameHistory(), currentGameProfile.gameMode());

    for (CategoryPlayedInfo category : previouslyPlayedCategories) {
      allCategoriesPlayed.add(category.getCategory().getName());
    }

    for (CategoryPlayedInfo category : categoriesPlayedInThisGame) {
      allCategoriesPlayed.add(category.getCategory().getName());
    }

    WordChoice wordChoice = currentGameProfile.settings().getWordChoice();

    try {
      categoryToGuess =
          predictionManager.getNewRandomCategory(
              allCategoriesPlayed,
              wordChoice.categoryShouldBeIncluded(CategoryType.EASY),
              wordChoice.categoryShouldBeIncluded(CategoryType.MEDIUM),
              wordChoice.categoryShouldBeIncluded(CategoryType.HARD));
      categoryChangeEmitter.emit(categoryToGuess);
    } catch (FilterTooStrictException e) {
      // TODO: Handle this properly
      App.expect("Player has not played every category", e);
    }
  }

  /** Starts the countdown, and enables the prediction server */
  public void startGame() {
    if (currentGameProfile.gameMode() != GameMode.ZEN) {
      countdownTimer.startCountdown(currentGameProfile.settings().getTime().getTimeToDraw());
    }
    gameTimeCounter = 0;
    categoriesPlayedInThisGame.clear();
    predictionManager.startMakingPredictions();
    gameStartedEmitter.emit();
    isPlaying = true;
  }

  /** Ends the game if it is ongoing with a win state of CANCEL */
  public void stopGame() {
    if (isPlaying) {
      if (currentGameProfile.gameMode() == GameMode.ZEN) {
        endGame(EndGameReason.GAVE_UP_OR_CANCELLED);
      }
    }
  }

  /**
   * This helper method is used to end the game depending on if the game is won, cancelled or lost
   *
   * @param winState the state of the game when it ends
   */
  private void endGame(EndGameReason winState) {

    // get info
    int secondsRemaining = countdownTimer.getRemainingCount();
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

    // send the end game information
    if (currentGameProfile.gameMode() == GameMode.RAPID_FIRE) {
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              categoriesPlayedInThisGame,
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));
    } else if (currentGameProfile.gameMode() == GameMode.ZEN) {
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              new CategoryPlayedInfo(gameTimeCounter, -1, categoryToGuess),
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));

    } else {
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              new CategoryPlayedInfo(gameTimeCounter, secondsRemaining, categoryToGuess),
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));
    }

    isPlaying = false;
    predictionWinningEnabled = true;
  }

  /**
   * This method will be called when the player wins and will end the game and set the end game
   * state to be WIN
   */
  private void onCorrectPrediction() {

    CategoryPlayedInfo categoryPlayed = null;
    GameMode mode = currentGameProfile.gameMode();

    if (mode == GameMode.ZEN) {
      categoryPlayed = new CategoryPlayedInfo(gameTimeCounter, -1, categoryToGuess);
    } else if (mode == GameMode.CLASSIC
        || mode == GameMode.HIDDEN_WORD
        || mode == GameMode.RAPID_FIRE) {

      int secondsRemaining = countdownTimer.getRemainingCount();
      categoryPlayed = new CategoryPlayedInfo(gameTimeCounter, secondsRemaining, categoryToGuess);
    }

    if (mode == GameMode.RAPID_FIRE) {
      gameTimeCounter = 0;
      categoriesPlayedInThisGame.add(categoryPlayed);
      selectNewRandomCategory();
    }

    assert categoryPlayed != null : "CategoryPlayed should not be null";

    correctPredictionEmitter.emit(categoryPlayed);

    if (mode == GameMode.CLASSIC || mode == GameMode.HIDDEN_WORD) {
      endGame(EndGameReason.CORRECT_CATEOGRY);
    }
  }

  /**
   * This method will be called when the player losses and will end the game and set the end game
   * state to be LOOSE
   */
  private void onOutOfTime() {
    assert currentGameProfile.gameMode() != GameMode.ZEN
        : "Zen mode is not timed so we should not 'run out of time'";

    GameMode gameMode = currentGameProfile.gameMode();
    if (gameMode == GameMode.CLASSIC
        || gameMode == GameMode.HIDDEN_WORD
        || gameMode == GameMode.RAPID_FIRE) {
      endGame(EndGameReason.OUT_OF_TIME);
    }
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
   * This method enables or disabled the prediction win detector. While it is disables, the
   * prediction manager will continue to make predictions but the game will simply not check to see
   * if you have won.
   *
   * @param enabled whether or not the prediction win detector is enabled
   */
  public void setPredictionWinningEnabled(boolean enabled) {
    predictionWinningEnabled = enabled;
  }

  /**
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

  // TODO: Add unsubscribe methods

  /**
   * This method allows us to add a listener to event emitter gameEndedEmitter to keep track of when
   * the tgame ends
   *
   * @param listener the EventListener to be notified when an event is emitted
   */
  public void subscribeToGameEnd(EventListener<GameInfo> listener) {
    gameEndedEmitter.subscribe(listener);
  }

  public void subscribeToCorrectPrediction(EventListener<CategoryPlayedInfo> listener) {
    correctPredictionEmitter.subscribe(listener);
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

  public Integer getNumberOfCategories() {
    return predictionManager.getNumberOfCategories();
  }
}
