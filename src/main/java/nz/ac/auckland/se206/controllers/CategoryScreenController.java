package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.util.CategoryGenerator;
import nz.ac.auckland.se206.util.CategoryGenerator.Difficulty;

public class CategoryScreenController {

  @FXML private Button readyButton;
  @FXML private Label drawTimeLabel;
  @FXML private Label categoryLabel;

  private CategoryGenerator categoryGenerator;
  private TextToSpeech textToSpeech;
  private static String categoryToGuess;

  public void initialize() {
    // Gets game length to display
    GameScreenController gameScreen = App.getLoader("game-screen").getController();
    drawTimeLabel.setText("Draw in " + gameScreen.getGameLengthSeconds() + " seconds");

    textToSpeech = new TextToSpeech();
  }

  /** updated category and display on fxml every time this view is set */
  protected void updateCategory() {
    categoryGenerator = new CategoryGenerator("category_difficulty.csv");
    categoryToGuess = categoryGenerator.generateCategory(Difficulty.EASY);
    categoryLabel.setText(categoryToGuess);
    textToSpeech.speakAsync("Draw " + categoryToGuess);
  }

  /**
   * gets category to display on game screen
   *
   * @return categoryToGuess
   */
  protected static String getCategoryToGuess() {
    return categoryToGuess;
  }

  /** switches to game screen */
  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }
}
