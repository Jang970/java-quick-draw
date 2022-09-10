package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.GameLogicManager;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CategoryScreenController {

  @FXML private Button readyButton;
  @FXML private Label drawTimeLabel;
  @FXML private Label categoryLabel;

  private TextToSpeech textToSpeech;
  private GameLogicManager gameLogicManager;

  public void initialize() {
    // Gets game length to display
    gameLogicManager = App.getGameLogicManager();
    textToSpeech = new TextToSpeech();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORY) {
            updateCategory();
            updateGameTime();
          }
        });
  }

  private void updateGameTime() {
    int numSeconds = gameLogicManager.getGameLengthSeconds();

    drawTimeLabel.setText("Draw in " + numSeconds + " seconds");
  }

  /** updated category and display on fxml every time this view is set */
  private void updateCategory() {
    String newCategory = gameLogicManager.selectNewRandomCategory();

    categoryLabel.setText(newCategory);
    textToSpeech.speakAsync("Draw " + newCategory);
  }

  /** switches to game screen */
  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }
}
