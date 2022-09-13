package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class HomeScreenController {
  @FXML private Button newGameButton;

  @FXML
  private void onStartNewGame() throws IOException, URISyntaxException {

    if (App.getUserManager().getExistingProfiles().isEmpty()) {
      App.setView(View.NEWUSER);
    } else {
      App.setView(View.USERPROFILES);
    }
  }
}
