package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;

public class DifficultyScreenController {

  public enum DifficultyType {
    ACCURACY,
    WORDS,
    TIME,
    CONFIDENCE
  }

  @FXML Pane accuracyPane;
  @FXML Pane wordsPane;
  @FXML Pane timePane;
  @FXML Pane confidencePane;

  private DifficultyType currentType;
  @FXML private EventEmitter<Pane> difficultyTypesPaneEmitter = new EventEmitter<Pane>();

  public void initialize() {

    // reset to accuracy pane visible and rest not
    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.DIFFICULTY) {

            reset();
          }
        });

    // on every pane change it retrieves the current settings for each difficulty type
    subscribeToDifficultyTypesPaneChange(
        (Pane currentPane) -> {

          // TODO: get previous difficulty settings

          // e.g.
          // Button accuracyButton =
          // (Button) App.getStage().getScene().lookup(getAccuracyDifficulty());
          // accuracyButton.getStyleClass().add("selectedDifficulty");

        });
  }

  /** Resets the fxml to show accuracy pane first */
  private void reset() {
    currentType = DifficultyType.ACCURACY;

    accuracyPane.setVisible(true);
    wordsPane.setVisible(false);
    timePane.setVisible(false);
    confidencePane.setVisible(false);
    difficultyTypesPaneEmitter.emit(accuracyPane);
  }

  /** Based on current difficulty type the fxml moves "forward" to the next difficulty type */
  private void goForward() {
    switch (currentType) {
      case ACCURACY:
        accuracyPane.setVisible(false);
        wordsPane.setVisible(true);

        currentType = DifficultyType.WORDS;
        difficultyTypesPaneEmitter.emit(wordsPane);
        break;

      case WORDS:
        wordsPane.setVisible(false);
        timePane.setVisible(true);

        currentType = DifficultyType.TIME;
        difficultyTypesPaneEmitter.emit(timePane);
        break;

      case TIME:
        timePane.setVisible(false);
        confidencePane.setVisible(true);

        currentType = DifficultyType.CONFIDENCE;
        difficultyTypesPaneEmitter.emit(confidencePane);
        break;

      case CONFIDENCE:
        App.setView(View.CATEGORY);
        break;

      default:
        break;
    }
  }

  public void subscribeToDifficultyTypesPaneChange(EventListener<Pane> listener) {
    difficultyTypesPaneEmitter.subscribe(listener);
  }

  /** Based on current difficulty type the fxml moves "backward" to the previous difficulty type */
  @FXML
  private void onGoBack() {
    switch (currentType) {
      case ACCURACY:
        // TODO: CHANGE TO GAME MODE ONCE IMPLEMENTED
        App.setView(View.USER);
        break;

      case WORDS:
        wordsPane.setVisible(false);
        accuracyPane.setVisible(true);

        currentType = DifficultyType.ACCURACY;
        difficultyTypesPaneEmitter.emit(accuracyPane);
        break;

      case TIME:
        timePane.setVisible(false);
        wordsPane.setVisible(true);

        currentType = DifficultyType.WORDS;
        difficultyTypesPaneEmitter.emit(wordsPane);
        break;

      case CONFIDENCE:
        confidencePane.setVisible(false);
        timePane.setVisible(true);

        currentType = DifficultyType.TIME;
        difficultyTypesPaneEmitter.emit(timePane);
        break;

      default:
        break;
    }
  }

  // TODO: This is so bad need to fix!!

  // these are all the corresponding buttons to each difficulty type. When each button is clicked
  // the corresponding difficulty setting is sent
  @FXML
  private void onEasyAccuracy() {
    // TODO: set easy accuracy
    goForward();
  }

  @FXML
  private void onMediumAccuracy() {
    // TODO: set medium accuracy
    goForward();
  }

  @FXML
  private void onHardAccuracy() {
    // TODO: send hard accuracy
    goForward();
  }

  @FXML
  private void onEasyWords() {
    goForward();
  }

  @FXML
  private void onMediumWords() {
    goForward();
  }

  @FXML
  private void onHardWords() {
    goForward();
  }

  @FXML
  private void onMasterWords() {
    goForward();
  }

  @FXML
  private void onEasyTime() {
    goForward();
  }

  @FXML
  private void onMediumTime() {
    goForward();
  }

  @FXML
  private void onHardTime() {
    goForward();
  }

  @FXML
  private void onMasterTime() {
    goForward();
  }

  @FXML
  private void onEasyConfidence() {
    goForward();
  }

  @FXML
  private void onMediumConfidence() {
    goForward();
  }

  @FXML
  private void onHardConfidence() {
    goForward();
  }

  @FXML
  private void onMasterConfidence() {
    goForward();
  }
}
