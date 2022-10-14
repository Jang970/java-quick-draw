package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.Settings;
import nz.ac.auckland.se206.util.difficulties.Accuracy;
import nz.ac.auckland.se206.util.difficulties.Confidence;
import nz.ac.auckland.se206.util.difficulties.Time;
import nz.ac.auckland.se206.util.difficulties.WordChoice;

public class DifficultyScreenController {

  @FXML private ChoiceBox<String> accuracyChoiceBox;
  @FXML private ChoiceBox<String> wordsChoiceBox;
  @FXML private ChoiceBox<String> timeChoiceBox;
  @FXML private ChoiceBox<String> confidenceChoiceBox;

  private Settings settings;
  private View previousView;

  /**
   * Create choice boxes for each difficult setting, sets listener to choice boxes, and gets
   * selected game mode to display
   */
  public void initialize() {

    createChoiceBoxes();
    // reset to accuracy pane visible and rest not
    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.DIFFICULTY) {

            // set selected settings
            settings = QuickDrawGameManager.getProfileManager().getCurrentProfile().getSettings();
            accuracyChoiceBox
                .getSelectionModel()
                .select(settings.getAccuracy().getLabel().toUpperCase());
            wordsChoiceBox
                .getSelectionModel()
                .select(settings.getWordChoice().name().toUpperCase());
            timeChoiceBox.getSelectionModel().select(settings.getTime().getLabel().toUpperCase());
            confidenceChoiceBox
                .getSelectionModel()
                .select(settings.getConfidence().getLabel().toUpperCase());
          } else {
            previousView = newView;
          }
        });

    updateDifficultySettings();
  }

  /** adds listener to the choice boxes and gets the selected option and updates each settings */
  private void updateDifficultySettings() {

    updateValueOnBoxHelper(
        accuracyChoiceBox, (newValue) -> settings.updateAccuracy(Accuracy.valueOf(newValue)));
    updateValueOnBoxHelper(
        wordsChoiceBox, (newValue) -> settings.updateWordChoice(WordChoice.valueOf(newValue)));
    updateValueOnBoxHelper(
        timeChoiceBox, (newValue) -> settings.updateTime(Time.valueOf(newValue)));
    updateValueOnBoxHelper(
        confidenceChoiceBox, (newValue) -> settings.updateConfidence(Confidence.valueOf(newValue)));
  }

  /**
   * This is a simple helper method which registers an event on the choice box that takes in the
   * string of the new value.
   *
   * @param box the box to register the event on.
   * @param listener the listener of the event.
   */
  private void updateValueOnBoxHelper(ChoiceBox<String> box, EventListener<String> listener) {
    box.getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              listener.update(newValue);
            });
  }

  /** Creates choice boxes with the given difficulty options */
  private void createChoiceBoxes() {
    accuracyChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD");
    wordsChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    timeChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    confidenceChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
  }

  /** Switches back to user profile screen FXML */
  @FXML
  private void onBack() {
    App.setView(previousView);
  }
}
