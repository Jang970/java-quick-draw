package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
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
  @FXML private Label usernameLabel;

  private TextToSpeech textToSpeech;
  private GameLogicManager gameLogicManager;

  public void initialize() {

    gameLogicManager = App.getGameLogicManager();
    textToSpeech = new TextToSpeech();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORY) {
            try {
              usernameLabel.setText("Hi, " + App.getUserManager().getCurrentProfile().getName());
            } catch (IOException | URISyntaxException e1) {
              e1.printStackTrace();
            }
            generateNewCategoryAndUpdateLabel();
            updateGameTimeLabel();
          }
        });

    App.subscribeToAppTermination(
        (e) -> {
          // TODO: Make the TextToSpeech class handle this automatically
          textToSpeech.terminate();
        });
  }

  private void updateGameTimeLabel() {
    int numSeconds = gameLogicManager.getGameLengthSeconds();

    drawTimeLabel.setText("Draw in " + numSeconds + " seconds");
  }

  private void generateNewCategoryAndUpdateLabel() {
    String newCategory = gameLogicManager.selectNewRandomCategory();

    categoryLabel.setText(newCategory);
    textToSpeech.speakAsync("Draw " + newCategory);
  }

  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }
}
