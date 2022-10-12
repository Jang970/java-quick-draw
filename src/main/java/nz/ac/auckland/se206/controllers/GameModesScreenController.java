package nz.ac.auckland.se206.controllers;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class GameModesScreenController {
  @FXML private ImageView craneImageView;
  @FXML private Label descriptionLabel;

  private SequentialTransition sequence;

  /**
   * Creates animation to move crane to the specified moveX position
   *
   * @param moveX the given postion to move x in (left or right)
   */
  private void moveCrane(int moveX) {

    TranslateTransition translateOne = new TranslateTransition();
    translateOne.setFromX(0);
    translateOne.setFromY(0);
    translateOne.setByX(moveX);
    translateOne.setDuration(Duration.seconds(1));

    TranslateTransition translateTwo = new TranslateTransition();
    translateTwo.setFromX(moveX);
    translateTwo.setFromY(0);
    translateTwo.setByY(30);
    translateTwo.setDuration(Duration.seconds(0.5));
    translateTwo.play();

    sequence = new SequentialTransition(craneImageView, translateOne, translateTwo);
    sequence.play();
  }

  /** moves crane back to intial position so it always start from middle */
  private void moveCraneBack() {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(craneImageView);
    translate.setFromX(0);
    translate.setFromY(0);
    translate.setByX(0);
    translate.setByY(0);
    translate.setDuration(Duration.seconds(0.5));
    translate.play();
  }

  /** sets game mode as classic and goes to category screen */
  @FXML
  private void onSelectClassic() {
    // TODO: set mode as classic
    moveCrane(-180);
    sequence.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.CATEGORY);
        });
  }

  /** sets game mode as hidden word and goes to category screen */
  @FXML
  private void onSelectHiddenWord() {
    // TODO: set mode as hidden word
    moveCrane(180);
    sequence.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.CATEGORY);
        });
  }

  /** sets game mode as zen and goes to category screen */
  @FXML
  private void onSelectZen() {
    // TODO: set mode as zen
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(craneImageView);
    translate.setFromX(0);
    translate.setFromY(0);
    translate.setByY(30);
    translate.setDuration(Duration.seconds(0.5));
    translate.play();

    translate.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.CATEGORY);
        });
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

  /** Switch to user profile screen FXML */
  @FXML
  private void onBackToUserScreen() {
    App.setView(View.USER);
  }
}
