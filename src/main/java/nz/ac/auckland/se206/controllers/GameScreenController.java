package nz.ac.auckland.se206.controllers;

import ai.djl.modality.Classifications.Classification;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.fxmlutils.CanvasManager;
import nz.ac.auckland.se206.fxmlutils.CanvasManager.DrawMode;
import nz.ac.auckland.se206.gamelogicmanager.EndGameReason;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;
import nz.ac.auckland.se206.gamelogicmanager.GameProfile;
import nz.ac.auckland.se206.util.BufferedImageUtils;
import nz.ac.auckland.se206.util.Category;
import nz.ac.auckland.se206.util.Profile;
import org.apache.commons.lang3.StringUtils;

public class GameScreenController {

  @FXML private Canvas canvas;
  @FXML private Pane canvasContainerPane;

  @FXML private Button pencilButton;
  @FXML private Button eraserButton;
  @FXML private Button clearButton;
  @FXML private Button hintsButton;

  @FXML private Button changeGameModeButton;
  @FXML private Button userButton;
  @FXML private Button gameActionButton;
  @FXML private Button downloadImageButton;

  @FXML private Label timeRemainingLabel;
  @FXML private Label whatToDrawLabel;
  @FXML private Label gameModeLabel;

  @FXML private VBox guessLabelCol1;
  @FXML private VBox guessLabelCol2;
  @FXML private VBox predictionVbox;
  @FXML private VBox toolsVBox;

  @FXML private ColorPicker colorPicker;
  @FXML private ProgressBar predictionBar;
  @FXML private Alert hintAlert;

  private Label[] guessLabels = new Label[10];

  private CanvasManager canvasManager;
  private GameLogicManager gameLogicManager;
  private Media sound;
  private MediaPlayer mediaPlayer;

  /** Method that is run to set up the GameScreen FXML everytime it is opened/run. */
  public void initialize() {

    // This gets the guess labels from the two columns and concatenates them into a single array for
    // easy use.
    guessLabels =
        Stream.concat(guessLabelCol1.getChildren().stream(), guessLabelCol2.getChildren().stream())
            .toArray(Label[]::new);

    canvasManager = new CanvasManager(canvas);

    colorPicker = new ColorPicker(Color.BLACK);
    colorPicker.getStyleClass().add("canvasColorPicker");

    hintAlert = new Alert(AlertType.INFORMATION);
    DialogPane dialogPane = hintAlert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/css/application.css").toExternalForm());

    hintsButton = new Button("HINTS!");
    hintsButton.getStyleClass().add("hintsButton");
    hintsButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            displayPopup(getHint());
          }
        });

    gameLogicManager = QuickDrawGameManager.getGameLogicManager();

    // Creates a task which gives the game logic manager a way of accessing the canvas image.
    gameLogicManager.setImageSource(
        () -> {
          // access the image drawn on the canvas
          FutureTask<BufferedImage> getImage =
              new FutureTask<BufferedImage>(() -> canvasManager.getCurrentBlackAndWhiteSnapshot());
          Platform.runLater(getImage);
          // will check for any exceptions but should not really occur
          try {
            return getImage.get();
          } catch (InterruptedException | ExecutionException error) {
            App.expect("Threads are handled properly, so this error should not occur", error);
            return null;
          }
        });

    // Subscribe to relevant changes so we can update the display accordingly
    gameLogicManager.subscribeToPredictionsChange(
        (predictions) -> onPredictionsChange(predictions));
    gameLogicManager.subscribeToTimeChange((seconds) -> onTimeChange(seconds));
    gameLogicManager.subscribeToGameStart(() -> onGameStart());
    gameLogicManager.subscribeToGameEnd((gameInfo) -> onGameEnd(gameInfo));
    gameLogicManager.subscribeToCategoryChange((category) -> onCategoryUpdate(category));

    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.GAME) {
            // set color of user profile icon button
            setUserButtonStyle();
            setGameScreenGui(gameLogicManager.getCurrentGameProfile().gameMode());

            // When the view changes to game, we start a new game and clear the canvas
            gameLogicManager.startGame();
          } else {

            // Stops the game if the user leaves.
            gameLogicManager.stopGame();
          }
        });
  }

  /**
   * This method is called whenever the game logic manager picks a new category. It updates the game
   * screen information accordingly with the new category information.
   *
   * @param category the category that the game logic manager has picked.
   */
  private void onCategoryUpdate(Category category) {
    Platform.runLater(
        () -> {
          if (gameLogicManager.getCurrentGameProfile().gameMode() == GameMode.HIDDEN_WORD) {
            // Shows the description in hidden word mode.
            whatToDrawLabel.setText("TO DRAW: " + category.getDescription());
          } else {
            // Shows the relevant word in any other mode.
            if (gameLogicManager.isPlaying()
                && gameLogicManager.getCurrentGameProfile().gameMode() == GameMode.RAPID_FIRE) {
              App.getTextToSpeech().speakAsync("Draw " + category.getName());
            }
            whatToDrawLabel.setText("TO DRAW: " + category.getName());
          }
          canvasManager.clearCanvas();
        });
  }

  /**
   * Sets the game screen gui depending on game mode
   *
   * @param string the current game mode
   */
  private void setGameScreenGui(GameMode gameMode) {

    gameModeLabel.setText(gameMode.name().replace("_", " "));

    // Setups up the game screen GUI depending on the game mode.
    switch (gameMode) {
      case CLASSIC:
        whatToDrawLabel.setStyle("-fx-font-size: 35px");
        timeRemainingLabel.setVisible(true);

        if (toolsVBox.getChildren().contains(colorPicker)) {
          toolsVBox.getChildren().remove(colorPicker);
        }

        if (toolsVBox.getChildren().contains(hintsButton)) {
          toolsVBox.getChildren().remove(hintsButton);
        }

        canvasManager.setPenColor(Color.BLACK);

        break;
      case ZEN:
        whatToDrawLabel.getStyleClass().add("-fx-font-size: 35px");
        timeRemainingLabel.setVisible(false);

        if (!toolsVBox.getChildren().contains(colorPicker)) {
          toolsVBox.getChildren().add(0, colorPicker);
        }

        if (toolsVBox.getChildren().contains(hintsButton)) {
          toolsVBox.getChildren().remove(hintsButton);
        }

        colorPicker
            .valueProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  canvasManager.setPenColor(newValue);
                });

        break;
      case RAPID_FIRE:
        whatToDrawLabel.setStyle("-fx-font-size: 35px");
        timeRemainingLabel.setVisible(true);

        if (toolsVBox.getChildren().contains(colorPicker)) {
          toolsVBox.getChildren().remove(colorPicker);
        }

        canvasManager.setPenColor(Color.BLACK);
        break;
      case HIDDEN_WORD:
        // smaller font size for the hidden word mode.
        whatToDrawLabel.setStyle("-fx-font-size: 22px");
        timeRemainingLabel.setVisible(true);

        if (toolsVBox.getChildren().contains(colorPicker)) {
          toolsVBox.getChildren().remove(colorPicker);
        }

        if (!toolsVBox.getChildren().contains(hintsButton)) {
          toolsVBox.getChildren().add(hintsButton);
          // allows for main window to be clickable
          hintAlert.initOwner(App.getStage());
          hintAlert.initModality(Modality.NONE);
        }

        hintAlert.setContentText("");
        canvasManager.setPenColor(Color.BLACK);

        break;
    }
  }

  /** Displays hint pop up for hidden word mode */
  private void displayPopup(String hintText) {
    // shows an information alert pop up of the hint when button is clicked
    // setting display value

    // if its not showing then it shows
    if (!hintAlert.isShowing()) {
      hintAlert.setTitle("Hint");
      hintAlert.setHeaderText("Hint");
      hintAlert.setX(100);
      hintAlert.setY(600);
      hintAlert.show();
    }

    // sets context text to hint
    hintAlert.setContentText(hintText);
  }

  /**
   * Gets hint from hidden word by giving each letter of the word at a time
   *
   * @return
   */
  private String getHint() {

    // gets the most recent hint given from alert
    String hintText = hintAlert.getContentText();
    // get current category
    String category = QuickDrawGameManager.getGameLogicManager().getCurrentCategory().getName();

    // if hint text is blank then it will given empty state with dashes
    if (hintText.isBlank()) {
      hintText = StringUtils.repeat("_", category.length());
    }

    int indexOfLetter = hintText.indexOf("_", 0);

    // if theres no dashes then return category
    if (indexOfLetter == -1) {
      return category;
    }

    // replaces the first dash with the character in the position
    return hintText.replaceFirst("_", String.valueOf(category.charAt(indexOfLetter)));
  }

  /** Gets colour and sets css background colour */
  private void setUserButtonStyle() {
    userButton.setStyle(
        "-fx-background-color: "
            + QuickDrawGameManager.getProfileManager()
                .getCurrentProfile()
                .getColour()
                .replace("0x", "#")
            + ";");
  }

  /**
   * This method contains logic to handle what to do when the game ends depending on the result of
   * the game
   *
   * @param winState shows if the game was won, lost, cancelled or not applicable
   */
  private void onGameEnd(GameInfo gameInfo) {
    // Run this after the game ends
    Platform.runLater(
        () -> {
          // Update all dislay items correctly
          canvasManager.setDrawingEnabled(false);
          setCanvasButtonsDisabled(true);

          gameActionButton.setText("NEW GAME");
          whatToDrawLabel.setStyle("-fx-font-size: 35px");
          whatToDrawLabel.getStyleClass().add("stateHeaders");

          EndGameReason reasonForGameEnd = gameInfo.getReasonForGameEnd();
          GameMode gameMode = gameInfo.getGameMode();

          // The following logic decides how the game ending should be handled.

          if (gameMode == GameMode.HIDDEN_WORD || gameMode == GameMode.CLASSIC) {
            // Hidden word or classic mode

            if (reasonForGameEnd == EndGameReason.CORRECT_CATEOGRY) {
              whatToDrawLabel.setText("You got it!");
              playWinSound();
            } else if (reasonForGameEnd == EndGameReason.OUT_OF_TIME) {
              whatToDrawLabel.setText("Sorry, you ran out of time!");
              playLooseSound();
            } else if (reasonForGameEnd == EndGameReason.GAVE_UP_OR_CANCELLED) {
              playLooseSound();
              whatToDrawLabel.setText("Game stopped");
            }
          } else if (gameMode == GameMode.ZEN) {
            // Zen mode

            whatToDrawLabel.setText("What a lovely drawing :)");

          } else if (gameMode == GameMode.RAPID_FIRE) {
            // Rapid fire mode

            int numThingsDrawn = gameInfo.getCategoriesPlayed().size();
            if (numThingsDrawn == 0) {
              whatToDrawLabel.setText("Sorry, you ran out of time!");
              playLooseSound();
            } else {
              playWinSound();
              if (numThingsDrawn == 1) {
                whatToDrawLabel.setText("You drew 1 thing!");
              } else if (numThingsDrawn > 1) {
                whatToDrawLabel.setText("You drew " + numThingsDrawn + " things!");
              }
            }
          }
        });
  }

  /** This function plays an encouraging win sound. */
  private void playWinSound() {
    sound =
        new Media(getClass().getClassLoader().getResource("sounds/gameWin.mp3").toExternalForm());
    mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
  }

  /** This function plays a disappointing loosing sound. */
  private void playLooseSound() {
    sound =
        new Media(getClass().getClassLoader().getResource("sounds/gameLost.mp3").toExternalForm());
    mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
  }

  /**
   * This method contains logic that will be run when a game is started. It sets the game action
   * button text and enables drawing as well as a few other admin tasks
   */
  private void onGameStart() {
    Platform.runLater(
        () -> {
          // When the game starts, we do the following
          if (gameLogicManager.getCurrentGameProfile().gameMode() == GameMode.ZEN) {
            gameActionButton.setText("I'M DONE");
          } else {
            gameActionButton.setText("GIVE UP");
          }

          setCanvasButtonsDisabled(false);
          // allows player to draw on the canvas
          canvasManager.setDrawingEnabled(true);
          canvasManager.setDrawMode(DrawMode.DRAWING);
          gameActionButton.setDisable(false);
          whatToDrawLabel.getStyleClass().remove("stateHeaders");
        });
  }

  /**
   * This method handles what to do when we are given new predictions/ the predictions are updated
   * TODO: have to show confidence levels
   *
   * @param classificationList list of predictions to display
   */
  private void onPredictionsChange(List<Classification> classificationList) {
    Platform.runLater(
        () -> {
          double imageFilledFraction =
              BufferedImageUtils.getFilledFraction(
                  canvasManager.getCurrentBlackAndWhiteSnapshot(), 1);

          // This makes sure the canvas is more than 2% filled before allowing predictions to win
          // the game.
          gameLogicManager.setPredictionWinningEnabled(imageFilledFraction < 0.98);

          // This turns the list of classifications from the prediction model and replaces
          // underscores with spaces.
          List<String> normalisedClassfications =
              classificationList.stream()
                  .map((classification) -> classification.getClassName().replace('_', ' '))
                  .collect(Collectors.toList());

          // When we are given new predictions, we update the predictions text
          int range = Math.min(classificationList.size(), guessLabels.length);

          for (int i = 0; i < range; i++) {
            String guessText = normalisedClassfications.get(i);
            int percentage = (int) (classificationList.get(i).getProbability() * 100);
            guessLabels[i].setText(
                ((i + 1) + ": " + guessText).toUpperCase() + " (" + percentage + "%)");
          }

          String categoryToGuess = gameLogicManager.getCurrentCategory().getName();

          int posInList = 0;
          while (posInList < classificationList.size()
              && !normalisedClassfications.get(posInList).equals(categoryToGuess)) {
            posInList++;
          }
          posInList++;

          Double progress = 1 - ((double) posInList / classificationList.size());
          predictionBar.setProgress(progress);
        });
  }

  /**
   * This method handles functionality relating to when the time to draw changes - updates the time
   * label respectively
   *
   * @param numberSeconds time given to draw
   */
  void onTimeChange(int numberSeconds) {
    Platform.runLater(
        () -> {
          updateTimeRemainingLabel(numberSeconds);
        });
  }

  /**
   * This method will update the time label with the correct formatting to the given time input
   *
   * @param numberSeconds time to update label to
   */
  private void updateTimeRemainingLabel(int numberSeconds) {
    timeRemainingLabel.setText(
        "TIME REMAINING: " + String.format("%2d", numberSeconds).replace(' ', '0'));
  }

  /**
   * This method will disabled relevant buttons depending on the status of the boolean input
   *
   * @param disabled value depends on if we want to enable or disable the buttons
   */
  private void setCanvasButtonsDisabled(boolean disabled) {
    pencilButton.setDisable(disabled);
    eraserButton.setDisable(disabled);
    clearButton.setDisable(disabled);

    if (toolsVBox.getChildren().contains(hintsButton)) {
      hintsButton.setDisable(disabled);
    }

    if (toolsVBox.getChildren().contains(colorPicker)) {
      colorPicker.setDisable(disabled);
    }
  }

  /** Set the 'tool' in use to be the pencil so player can draw */
  @FXML
  private void onPencilSelected() {
    canvasManager.setDrawMode(DrawMode.DRAWING);
  }

  /** Set the 'tool' in use to be the eraser so player can erase */
  @FXML
  private void onEraserSelected() {
    canvasManager.setDrawMode(DrawMode.ERASING);
  }

  /** Remove everything on the canvas i.e return it to a blank state */
  @FXML
  private void onClearCanvas() {
    canvasManager.clearOnlyIfDrawingEnabled();
  }

  /** Method relating to the button switch to UserProfilesScreen FXML */
  @FXML
  private void onChangeGameMode() {
    App.setView(View.GAMEMODES);
  }

  /** Method relating to the button switch to UserScreen FXML */
  @FXML
  private void onUserHome() {
    App.setView(View.USER);
  }

  /**
   * Method relating to the Button that will cancel the current game if playing but otherwise will
   * return user to CategoryScreen FXML
   */
  @FXML
  private void onGameAction() {
    if (gameLogicManager.isPlaying()) {
      gameLogicManager.stopGame();
      // End the game
    } else {
      // Start new game
      Profile profile = QuickDrawGameManager.getProfileManager().getCurrentProfile();
      gameLogicManager.initializeGame(
          new GameProfile(
              profile.getSettings(),
              gameLogicManager.getCurrentGameProfile().gameMode(),
              profile.getGameHistory()));
      App.setView(View.CATEGORY);
    }
  }

  /** Method relating to the button that will allow user to save the image drawn */
  @FXML
  private void onDownloadImage() {
    try {
      canvasManager.saveCurrentSnapshotOnFile();
    } catch (IOException e) {
      System.out.println("Failed to download");
    }
  }
}
