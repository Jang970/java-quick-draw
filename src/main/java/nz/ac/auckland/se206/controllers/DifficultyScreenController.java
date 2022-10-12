package nz.ac.auckland.se206.controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
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

  public void initialize() {

    createChoiceBoxes();
    // reset to accuracy pane visible and rest not
    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.DIFFICULTY) {

            // set selected settings
            settings = App.getProfileManager().getCurrentProfile().getSettings();
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
          }
        });

    updateDifficultySettings();
  }

  /** adds listener to the choice boxes and gets the selected option and updates each settings */
  private void updateDifficultySettings() {

    // TODO: how to make this cleaner??
    accuracyChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              ;
              settings.updateAccuracy(Accuracy.valueOf(newValue));
            });

    wordsChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              settings.updateWordChoice(WordChoice.valueOf(newValue));
            });

    timeChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              settings.updateTime(Time.valueOf(newValue));
            });

    confidenceChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              settings.updateConfidence(Confidence.valueOf(newValue));
            });
  }

  /** Creates choice boxes with the given difficulty options */
  private void createChoiceBoxes() {

    accuracyChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD");
    wordsChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    timeChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    confidenceChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
  }

  @FXML
  private void onBackToProfile() {
    App.setView(View.USER);
  }

  @FXML
  private void onSwitchToGameModes() {
    App.setView(View.GAMEMODES);
  }
}
