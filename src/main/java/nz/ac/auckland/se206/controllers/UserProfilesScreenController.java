package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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

public class UserProfilesScreenController {

  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;
  @FXML private VBox profilesVBox;
  @FXML private Pane newUserPane;
  @FXML private TextField usernameTextField;
  @FXML private ColorPicker colorPicker;

  // TODO: Each user can choose a color for their box?
  private Color[] colors =
      new Color[] {
        Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET
      };

  // TODO: This would be a string of usernames I need.
  final String[] username =
      new String[] {
        "this is a test 1",
        "this is a test 2",
        "this is a test 3",
        "this is a test 4",
        "this is a test 5",
        "this is a test 6",
        "this is a test 7",
      };

  /** Creates pagination */
  // TODO: this should happen every time the view is called (so it creates new user profile)
  public void initialize() {
    // TODO: call profile manager and get usernames and colours of every user
    // TODO: clear username, color picker (everytime it goes back to scene)
    profilesPagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
    profilesPagination.setPageFactory(
        (Integer pageIndex) -> {
          if (pageIndex >= username.length) {
            return null;
          } else {
            return createProfile(pageIndex);
          }
        });
    profilesPagination.setPageCount(username.length);
  }

  /**
   * Creates each page (profiles) from username and colors given
   *
   * @param pageIndex
   * @return
   */
  public VBox createProfile(int pageIndex) {
    VBox box = new VBox(5);
    box.setAlignment(Pos.CENTER);

    Button userButton = new Button(username[pageIndex]);
    userButton.setId(username[pageIndex]);

    userButton.getStyleClass().clear();
    userButton.getStyleClass().add("userButton");
    String color = "-fx-background-color: " + colors[pageIndex].toString().replace("0x", "#") + ";";
    userButton.setStyle(color);

    box.getChildren().add(userButton);

    onUserButtonClicked(userButton);

    return box;
  }

  /**
   * When profile is clicked it switches to category screen to play and sends user profile details
   * to profile manager
   *
   * @param userButton
   */
  private void onUserButtonClicked(Button userButton) {

    userButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            // TODO: send username to profile manager
            System.out.println(userButton.getId());

            // gets controller to update category
            changeView();
          }
        });
  }

  /** Switches to new user pane */
  @FXML
  private void onCreateUser() {

    profilesVBox.setVisible(false);
    newUserPane.setVisible(true);
  }

  /** Checks if user input is invalid, takes the inputs and then starts playing */
  @FXML
  private void onStartGame() {

    // TODO: Better way to check if color picker has been selected or not
    if (!usernameTextField.getText().isBlank()
        && !colorPicker.getValue().equals(Color.TRANSPARENT)) {

      // TODO: send username and color to profile manager
      System.out.println(usernameTextField.getText() + " " + colorPicker.getValue().toString());

      changeView();
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

  /** Changes view to category */
  private void changeView() {
    // gets controller to update category
    CategoryScreenController categoryScreen = App.getLoader("category-screen").getController();
    categoryScreen.updateCategory();
    App.setView(View.CATEGORY);
  }
}
