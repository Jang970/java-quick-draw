package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class NewUserScreenController {

  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;
  @FXML private VBox profilesVBox;
  @FXML private Pane newUserPane;
  @FXML private TextField usernameTextField;
  @FXML private ColorPicker colorPicker;

  /** Creates pagination */
  public void initialize() {

    // every time view is changed to user profiles pagination is created again based on the new user
    // profiles
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.NEWUSER) {

            // doesn't store previous user values
            usernameTextField.clear();
            colorPicker.setValue(Color.TRANSPARENT);
          }
        });
  }

  /**
   * When start game is clicked (on new user pane) it switches to category screen after checking if
   * inputs are valid.
   */
  @FXML
  private void onCreateProfile() {

    // TODO: Better way to check if color picker has been selected or not

    // TODO: check if username is not dupilicated
    // checks if username and colour picker has been selected
    if (!usernameTextField.getText().isBlank()
        && !colorPicker.getValue().equals(Color.TRANSPARENT)) {

      try {
        App.getUserManager()
            .createUserProfile(usernameTextField.getText(), colorPicker.getValue().toString());
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }

      // changes view to user profiles
      App.setView(View.USERPROFILES);

    } else {
      // shows an alert pop up if username is blank or spaces and/or color hasn't been chosen
      Alert errorAlert = new Alert(AlertType.ERROR);
      DialogPane dialogPane = errorAlert.getDialogPane();
      dialogPane
          .getStylesheets()
          .add(getClass().getResource("/css/application.css").toExternalForm());

      errorAlert.setTitle("Invalid Input");
      errorAlert.setContentText("Please enter a valid username or color.");
      errorAlert.showAndWait();
    }
  }
}
