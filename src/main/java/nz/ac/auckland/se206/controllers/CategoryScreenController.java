package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.GameLogicManager;
import nz.ac.auckland.se206.GameLogicManager.FilterTooStrictException;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.util.Profile;

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
            // When the app laods changes to the catgory screen, we genereate a new category and
            // make display updates
            usernameLabel.setText("Hi, " + App.getProfileManager().getCurrentProfile().getName());
            generateNewCategoryAndUpdateLabel();
            updateGameTimeLabel();
          }
        });

    // When text the app terminates, we turn the text to speech off.
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

    Profile profile = App.getProfileManager().getCurrentProfile();

    // We need to make sure that we are generating a new category which the player has not already
    // played.
    String newCategory;
    try {
      newCategory = gameLogicManager.selectNewRandomCategory(profile.getCategoryHistory());
    } catch (FilterTooStrictException e) {
      // reset the category history if they have played every word
      profile.resetCategoryHistory();
      newCategory = gameLogicManager.selectNewRandomCategory();
    }

    categoryLabel.setText(newCategory);
    textToSpeech.speakAsync("Draw " + newCategory);
  }

  @FXML
  private void onReadyToPlay() {
    App.setView(View.GAME);
  }
}
