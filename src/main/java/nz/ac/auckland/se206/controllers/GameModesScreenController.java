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

  private void moveCrane(int moveX) {

    TranslateTransition translateOne = new TranslateTransition();
    translateOne.setFromX(0);
    translateOne.setFromY(0);
    translateOne.setByX(moveX);
    translateOne.setDuration(Duration.seconds(2));

    TranslateTransition translateTwo = new TranslateTransition();
    translateTwo.setFromX(moveX);
    translateTwo.setFromY(0);
    translateTwo.setByY(30);
    translateTwo.setDuration(Duration.seconds(2));
    translateTwo.play();

    sequence = new SequentialTransition(craneImageView, translateOne, translateTwo);
    sequence.play();
  }

  private void moveCraneBack() {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(craneImageView);
    translate.setFromX(0);
    translate.setFromY(0);
    translate.setByX(0);
    translate.setByY(0);
    translate.setDuration(Duration.seconds(1));
    translate.play();
  }

  @FXML
  private void onSelectClassic() {
    // TODO: set mode as classic
    moveCrane(-180);
    sequence.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.DIFFICULTY);
        });
  }

  @FXML
  private void onSelectHiddenWord() {
    // TODO: set mode as hidden word
    moveCrane(180);
    sequence.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.DIFFICULTY);
        });
  }

  @FXML
  private void onSelectZen() {
    // TODO: set mode as zen
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(craneImageView);
    translate.setFromX(0);
    translate.setFromY(0);
    translate.setByY(30);
    translate.setDuration(Duration.seconds(2));
    translate.play();

    translate.setOnFinished(
        e -> {
          moveCraneBack();
          App.setView(View.CATEGORY);
        });
  }

  @FXML
  private void onMouseExited() {
    descriptionLabel.setText(
        "CLICK ON THE GAME MODE TO PLAY.\nHOVER OVER EACH GAME MODE TO SEE DESCRIPTION.");
  }

  @FXML
  private void onHoverClassic() {
    descriptionLabel.setText(
        "CLASSIC MODE IS THE ORIGINAL GAME. THERE IS TIMER AND YOU GET GIVEN THE WORD TO DRAW.");
  }

  @FXML
  private void onHoverZen() {
    descriptionLabel.setText("ZEN MODE HAS NO TIMER AND YOU CAN DRAW ENDLESSLY");
  }

  @FXML
  private void onHoverHiddenWord() {
    descriptionLabel.setText(
        "HIDDEN MODE YOU GET THE DEFINITION OF THE WORD INSTEAD OF THE WORD ITSELF");
  }

  @FXML
  private void onBackToUserScreen() {
    App.setView(View.USER);
  }
}
