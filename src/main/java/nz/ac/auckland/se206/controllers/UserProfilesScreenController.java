package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class UserProfilesScreenController {

  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;
  @FXML private VBox profilesVBox;

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
  public void initialize() {

    // every time view is changed to user profiles pagination is created again based on the new user
    // profiles
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USERPROFILES) {

            // TODO: call profile manager and get usernames and colours of every user
            createProfilesPagination();
          }
        });
  }

  /** Creates a new profile pagination by creating profile buttons */
  private void createProfilesPagination() {

    // sets style has bullets (no numbers)
    profilesPagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);

    // creates pages length of profiles list
    profilesPagination.setPageFactory(
        (Integer pageIndex) -> {
          if (pageIndex >= username.length) {
            return null;
          } else {
            // creates each profiles
            return createProfile(pageIndex);
          }
        });
    // sets max page count
    profilesPagination.setPageCount(username.length);
  }

  /**
   * Creates each page (profile) from given username and colors
   *
   * @param pageIndex
   * @return
   */
  public VBox createProfile(int pageIndex) {
    // create vBox for ever page
    VBox box = new VBox(5);
    box.setAlignment(Pos.CENTER);

    // create user buttons
    Button userIconButton = new Button("userIconButton");
    Button usernameButton = new Button(username[pageIndex]);

    // set style
    setButtonStyle(userIconButton);
    setButtonStyle(usernameButton);
    userIconButton.setStyle(getBackgroundColor(colors[pageIndex]));

    // adds buttons to vBox
    box.getChildren().add(userIconButton);
    box.getChildren().add(usernameButton);

    // set on action of buttons
    onUserButtonsClicked(userIconButton);
    onUserButtonsClicked(usernameButton);

    return box;
  }

  /**
   * Returns the desired format for css background color string
   *
   * @param color given background color of user
   * @return
   */
  private String getBackgroundColor(Color color) {
    return "-fx-background-color: " + color.toString().replace("0x", "#") + ";";
  }

  /**
   * Sets specific style class for each button and clears previous style classes
   *
   * @param button
   */
  private void setButtonStyle(Button button) {

    // Clears current button style so it doesn't have css style of the general buttons
    button.getStyleClass().clear();
    // adds new style class to button
    button.getStyleClass().add(button.getText());
  }

  /**
   * When profile is clicked it switches to category screen to play and sends user profile details
   * to profile manager
   *
   * @param userButton
   */
  private void onUserButtonsClicked(Button userButton) {

    userButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            // TODO: send username to profile manager

            // gets controller to update category
            App.setView(View.CATEGORY);
          }
        });
  }

  /** Switches to new user screen */
  @FXML
  private void onNewUser() {
    App.setView(View.NEWUSER);
  }
}
