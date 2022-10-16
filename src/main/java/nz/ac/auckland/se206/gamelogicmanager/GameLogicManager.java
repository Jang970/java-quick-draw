package nz.ac.auckland.se206.gamelogicmanager;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import nz.ac.auckland.se206.App;
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
  private Boolean overrideCategory = false;

  private Category categoryToGuess;

  // This is used to track the categories played in rapid fire mode
  private List<CategoryPlayedInfo> categoriesPlayedInThisGame;

  private PredictionManager predictionManager;
  private CountdownTimer countdownTimer;

  private GameProfile currentGameProfile;

  // This counter is uded to count how long it took to get each category.
  private int gameTimeCounter = 0;

  private EventEmitter<List<Classification>> predictionReceivedEmitter =
      new EventEmitter<List<Classification>>();
  private EventEmitter<Category> categoryChangeEmitter = new EventEmitter<Category>();

  private EventEmitter<GameInfo> gameEndedEmitter = new EventEmitter<GameInfo>();
  private EventEmitter<CategoryPlayedInfo> correctPredictionEmitter =
      new EventEmitter<CategoryPlayedInfo>();
  private EventEmitter<Integer> timeChangedEmitter = new EventEmitter<Integer>();
  private EmptyEventEmitter gameStartedEmitter = new EmptyEventEmitter();

  private boolean sentPredictionMessage = false;
  private boolean predictionDetectionEnabled = true;

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
    // update the listener to show that the time has changed
    countdownTimer.setOnChange(
        (secondsRemaining) -> {
          timeChangedEmitter.emit(secondsRemaining);
          gameTimeCounter++;
        });
    // if count is complete then we run out of time and lose
    countdownTimer.setOnComplete(
        () -> {
          onOutOfTime();
        });

    // initialise the prediction manager
    predictionManager = new PredictionManager(100, -1);

    predictionManager.setPredictionListener(
        (predictions) -> {
          predictionReceivedEmitter.emit(predictions);

          if (predictionDetectionEnabled) {
            // set accuracy required
            int topNumGuessesNeededToWin =
                currentGameProfile.settings().getAccuracy().getTopNumGuesses();

            // set confidence required
            double confidenceNeededToWin =
                currentGameProfile.settings().getConfidence().getProbabilityPercentage();

            int range = Math.min(predictions.size(), topNumGuessesNeededToWin);

            // get confidence levels
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
        });
  }

  ///////////////////////////// GAME STATE TRANSISTIONS /////////////////////////////

  /**
   * This method will get the game ready to be played using the given profile
   *
   * @param profile current profile playing
   */
  public void initializeGame(GameProfile profile) {

    // stop the previous game if there was one.
    stopGame();

    currentGameProfile = profile;
    categoriesPlayedInThisGame = new ArrayList<CategoryPlayedInfo>();

    // Select a new category to play.
    if (!overrideCategory) {
      selectNewRandomCategory();
    } else {
      overrideCategory = false;
    }
  }

  /**
   * This method takes the categories played in the game and the game history to get a list of
   * categories that have already been played. It then picks a new category to play that has not yet
   * been played.
   */
  private void selectNewRandomCategory() {

    // A map to store the previous categories and their associated counts.
    HashMap<String, Integer> categoriesPlayedCounter = new HashMap<String, Integer>();

    // Initializes the map with all the categories that a player could have played to 0.
    for (Category category : predictionManager.getCategories()) {
      categoriesPlayedCounter.put(category.getName(), 0);
    }

    // OK so what we are doing here is kinda whack. We count how many times each category has been
    // played. Then we figure out which category/ies have been played the least. We then add any
    // categories which have been played more to the filter set. The game will not pick categories
    // from the filter set
    // This for loop is responsible for adding the category counts to the map.
    for (GameInfo game : currentGameProfile.gameHistory()) {

      GameMode mode = game.getGameMode();

      if (mode == GameMode.RAPID_FIRE) {
        // Iterate through categories played in rapid fire
        for (CategoryPlayedInfo category : game.getCategoriesPlayed()) {
          String name = category.getCategory().getName();
          int count = categoriesPlayedCounter.get(name) + 1;
          categoriesPlayedCounter.put(name, count);
        }
      } else if (mode == GameMode.CLASSIC || mode == GameMode.HIDDEN_WORD || mode == GameMode.ZEN) {
        // Add single category if not rapid fire.
        CategoryPlayedInfo category = game.getCategoryPlayed();
        String name = category.getCategory().getName();
        int count = categoriesPlayedCounter.get(name) + 1;
        categoriesPlayedCounter.put(name, count);
      }
    }

    // This loop then adds all the categories which have been played in this game so far to the
    // counter map. We dont want rapid fire to pick the same category twice.
    for (CategoryPlayedInfo category : categoriesPlayedInThisGame) {
      String name = category.getCategory().getName();
      int count = categoriesPlayedCounter.get(name) + 1;
      categoriesPlayedCounter.put(name, count);
    }

    // We then find the lowest count in the map in this loop.
    int lowestCount = Integer.MAX_VALUE;
    for (Entry<String, Integer> entry : categoriesPlayedCounter.entrySet()) {
      if (entry.getValue() < lowestCount) {
        lowestCount = entry.getValue();
      }
    }

    // Finally, we add all the categories which have been played more times than the lowest count to
    // the filter set.
    HashSet<String> mostPlayedCategories = new HashSet<String>();
    for (Entry<String, Integer> entry : categoriesPlayedCounter.entrySet()) {
      if (entry.getValue() > lowestCount) {
        mostPlayedCategories.add(entry.getKey());
      }
    }

    // The filter set now contains all the categories that have been played the most.
    WordChoice wordChoice = currentGameProfile.settings().getWordChoice();

    // TODO: Remove irrelvant categories from the list before figuring out lowest count.

    try {
      // check which categories to include in generating a new category to play
      categoryToGuess =
          predictionManager.getNewRandomCategory(
              mostPlayedCategories,
              wordChoice.categoryShouldBeIncluded(CategoryType.EASY),
              wordChoice.categoryShouldBeIncluded(CategoryType.MEDIUM),
              wordChoice.categoryShouldBeIncluded(CategoryType.HARD));
      categoryChangeEmitter.emit(categoryToGuess);
    } catch (FilterTooStrictException e) {
      App.expect("The filter should always be safe", e);
    }
  }

  /** Starts the countdown, and enables the prediction server */
  public void startGame() {
    if (currentGameProfile.gameMode() != GameMode.ZEN) {
      // In zen mode, we don't set a timer. We do in every other mode.
      countdownTimer.startCountdown(currentGameProfile.settings().getTime().getTimeToDraw());
    }
    // reset timer and start making predictions.
    gameTimeCounter = 0;
    predictionManager.startMakingPredictions();
    gameStartedEmitter.emit();
    isPlaying = true;
  }

  /** Ends the game if it is ongoing with a win state of CANCEL */
  public void stopGame() {
    if (isPlaying) {
      endGame(EndGameReason.GAVE_UP_OR_CANCELLED);
    }
  }

  /**
   * This helper method is used to end the game depending on if the game is won, cancelled or lost
   *
   * @param winState the state of the game when it ends
   */
  private void endGame(EndGameReason winState) {

    // get info and end the predictions and counter
    int secondsRemaining = countdownTimer.getRemainingCount();
    predictionManager.stopMakingPredictions();
    countdownTimer.cancelCountdown();

    // send the end game information
    if (currentGameProfile.gameMode() == GameMode.RAPID_FIRE) {
      // On rapid fire, we use the special game info constructor.
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              categoriesPlayedInThisGame,
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));
    } else if (currentGameProfile.gameMode() == GameMode.ZEN) {
      // If game mode is zen, we set the time remaining to -1.
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              new CategoryPlayedInfo(gameTimeCounter, -1, categoryToGuess),
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));

    } else {
      // otherwise, set as normal for hidden word and classic modes
      gameEndedEmitter.emit(
          new GameInfo(
              winState,
              new CategoryPlayedInfo(gameTimeCounter, secondsRemaining, categoryToGuess),
              currentGameProfile.settings(),
              currentGameProfile.gameMode()));
    }

    // Disable some flags so its ready for the next game.
    isPlaying = false;
    predictionDetectionEnabled = true;
  }

  /**
   * This method will be called when the player wins and will end the game and set the end game
   * state to be WIN
   */
  private void onCorrectPrediction() {

    // Variables for convenience.
    CategoryPlayedInfo categoryPlayed = null;
    GameMode mode = currentGameProfile.gameMode();

    if (mode == GameMode.ZEN) {
      // Zen mode has no time remaining concept so we use -1 to signify it is not applicable.
      categoryPlayed = new CategoryPlayedInfo(gameTimeCounter, -1, categoryToGuess);
    } else if (mode == GameMode.CLASSIC
        || mode == GameMode.HIDDEN_WORD
        || mode == GameMode.RAPID_FIRE) {

      // In other modes, we simply get the time until the counter runs out.
      int secondsRemaining = countdownTimer.getRemainingCount();
      categoryPlayed = new CategoryPlayedInfo(gameTimeCounter, secondsRemaining, categoryToGuess);
    }

    if (mode == GameMode.RAPID_FIRE) {
      // In rapid fire mode, we reset the counter and add the category to the list of categories
      // played.
      gameTimeCounter = 0;
      categoriesPlayedInThisGame.add(categoryPlayed);
      selectNewRandomCategory();
    }

    // null check
    assert categoryPlayed != null : "CategoryPlayed should not be null";

    correctPredictionEmitter.emit(categoryPlayed);

    if (mode == GameMode.CLASSIC || mode == GameMode.HIDDEN_WORD) {
      // End the game if classic of hidden word mode.
      endGame(EndGameReason.CORRECT_CATEOGRY);
    }
  }

  /**
   * This method will be called when the player losses and will end the game and set the end game
   * state to be LOOSE
   */
  private void onOutOfTime() {
    // Assert thta it is indeed not zen mode
    assert currentGameProfile.gameMode() != GameMode.ZEN
        : "Zen mode is not timed so we should not 'run out of time'";

    // End the game if it is not zen mode.
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
  public void setPredictionDetectionEnabled(boolean enabled) {
    predictionDetectionEnabled = enabled;
  }

  /**
   * This method returns whether or not the geme is currently listening to the prediction data.
   *
   * @return a boolean indicating whether or not the prediction data is currently being listened to
   */
  public boolean getPredictionDetectionEnabled() {
    return predictionDetectionEnabled;
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
   * This method allows us to subscribe to the player getting the drawing right using the given
   * settings.
   *
   * @param listener the event listener which getts the information about the category played.
   */
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
    predictionReceivedEmitter.subscribe(listener);
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

  /**
   * Returns the number of categories that the game can select from.
   *
   * @return an integer which is the number of categories that the game can select from.
   */
  public int getNumberOfCategories() {
    return predictionManager.getNumberOfCategories();
  }

  /**
   * This method returns a set of all the categories that the game can select from.
   *
   * @return the set of all the categories that the game can select from.
   */
  public Set<Category> getAllCategories() {
    return predictionManager.getCategories();
  }

  /**
   * This method is used to manually override the category that is played for the next game. This
   * will be reset after the game is initialised. If the player initialises the game, it will use
   * the reset category. If the game is initialised again, a different word will be used.
   *
   * @param newCategory new category to set the category to play to
   */
  public void forceCategoryForNextInitialisation(Category category) {
    overrideCategory = true;
    categoryToGuess = category;
    categoryChangeEmitter.emit(category);
  }

  /**
   * Disabled the category override for next initialisation. This ensures the game will select a new
   * random category.
   */
  public void disableForcedCategory() {
    overrideCategory = false;
  }
}
