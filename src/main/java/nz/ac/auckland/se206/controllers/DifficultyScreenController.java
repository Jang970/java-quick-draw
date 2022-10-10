package nz.ac.auckland.se206.controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class DifficultyScreenController {

  @FXML private ChoiceBox<String> accuracyChoiceBox;
  @FXML private ChoiceBox<String> wordsChoiceBox;
  @FXML private ChoiceBox<String> timeChoiceBox;
  @FXML private ChoiceBox<String> confidenceChoiceBox;

  public void initialize() {

    createChoiceBoxes();
    // reset to accuracy pane visible and rest not
    App.subscribeToViewChange(
        (View newView) -> {
          if (newView == View.DIFFICULTY) {
            // TODO: get previous setting
            // e.g. replace medium with words.getDifficulty() etc.
            wordsChoiceBox.getSelectionModel().select("MEDIUM");
          }
        });

    // TODO: probably could make this cleaner not sure though
    accuracyChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              // TODO: send setting here
              System.out.println(newValue);
            });

    wordsChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              // TODO: send word setting here
              System.out.println(newValue);
            });

    timeChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              // TODO: send time setting here
              System.out.println(newValue);
            });

    confidenceChoiceBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
              // TODO: send confidence setting here
              System.out.println(newValue);
            });
  }

  private void createChoiceBoxes() {

    accuracyChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD");
    wordsChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    timeChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
    confidenceChoiceBox.getItems().addAll("EASY", "MEDIUM", "HARD", "MASTER");
  }

  @FXML
  private void onGoBack() {
    App.setView(View.GAMEMODES);
  }

  @FXML
  private void onSwitchToCategory() {
    App.setView(View.CATEGORY);
  }
}
