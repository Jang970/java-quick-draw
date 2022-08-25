package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class HomeScreenController {
  @FXML private Button newGameButton;

  @FXML
  private void onStartNewGame() {
    App.setView(View.GAME);
  }
}
