package nz.ac.auckland.se206.controllers;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;

public class GameModesScreenController {
  @FXML private ImageView craneImageView;
  @FXML private Label descriptionLabel;

  private SequentialTransition sequence;

  /**
   * Creates animation to move crane to the specified moveX position
   *
   * @param moveX the given postion to move x in (left or right)
   */
  private void moveCrane(int moveX, int moveY) {

    // Horizontal transition
    TranslateTransition translateOne = new TranslateTransition();
    translateOne.setFromX(0);
    translateOne.setFromY(0);
    translateOne.setByX(moveX);
    translateOne.setDuration(Duration.seconds(0.5));

    // Vertical transition
    TranslateTransition translateTwo = new TranslateTransition();
    translateTwo.setFromX(moveX);
    translateTwo.setFromY(0);
    translateTwo.setByY(moveY);
    // set how long the transition goes for
    translateTwo.setDuration(Duration.seconds(0.5));
    translateTwo.play();

    // Get transitions to play one after another.
    sequence = new SequentialTransition(craneImageView, translateOne, translateTwo);
    sequence.play();
  }

  /** moves crane back to intial position so it always start from middle */
  private void moveCraneBack() {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(craneImageView);
    // Start and end positions
    translate.setFromX(0);
    translate.setFromY(0);
    translate.setByX(0);
    translate.setByY(0);
    // How long it will take the crane to translate.
    translate.setDuration(Duration.seconds(0.5));
    translate.play();
  }

  private void moveCraneAndSelectGameMode(GameMode mode, int x, int y) {
    QuickDrawGameManager.setCurrentlySelectedGameMode(mode);
    // Moves the crane to the desired location
    moveCrane(x, y);
    // Sets the view to category screen when it gets there
    sequence.setOnFinished(
        (e) -> {
          moveCraneBack();
          App.setView(View.CATEGORY);
        });
  }

  /** sets game mode as classic and goes to category screen */
  @FXML
  private void onSelectClassic() {
    moveCraneAndSelectGameMode(GameMode.CLASSIC, -40, 30);
  }

  /** sets game mode as hidden word and goes to category screen */
  @FXML
  private void onSelectHiddenWord() {
    moveCraneAndSelectGameMode(GameMode.HIDDEN_WORD, -215, 25);
  }

  /** sets game mode as zen and goes to category screen */
  @FXML
  private void onSelectZen() {
    moveCraneAndSelectGameMode(GameMode.ZEN, 105, 30);
  }

  /** sets game mode as rapid and goes to category screen */
  @FXML
  private void onSelectRapid() {
    moveCraneAndSelectGameMode(GameMode.RAPID_FIRE, 255, 30);
  }

  /** Creats a hover transiton which displays instructions */
  @FXML
  private void onMouseExited() {
    descriptionLabel.setText(
        "CLICK ON THE GAME MODE TO PLAY.\nHOVER OVER EACH GAME MODE TO SEE DESCRIPTION.");
  }

  /** Explains how to play classic game mode */
  @FXML
  private void onHoverClassic() {
    descriptionLabel.setText(
        "CLASSIC MODE IS THE ORIGINAL GAME. THERE IS A TIMER AND YOU GET GIVEN THE WORD TO DRAW.");
  }

  /** Explains how to play zen game mode */
  @FXML
  private void onHoverZen() {
    descriptionLabel.setText("ZEN MODE HAS NO TIMER AND YOU CAN DRAW FOR AS LONG AS YOU LIKE");
  }

  /** Explains how to play hidden word game mode */
  @FXML
  private void onHoverHiddenWord() {
    descriptionLabel.setText(
        "HIDDEN MODE YOU GET THE DEFINITION OF THE WORD INSTEAD OF THE WORD ITSELF");
  }

  /** Explains how to play rapid fire game mode */
  @FXML
  private void onHoverRapid() {
    descriptionLabel.setText("RAPID MODE YOU HAVE TO TRY DRAW AS MANY WORDS BEFORE TIMER RUNS OUT");
  }

  /** Switch to user profile screen FXML */
  @FXML
  private void onBackToUserScreen() {
    App.setView(View.USER);
  }
}
