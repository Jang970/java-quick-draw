package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.util.User;
import nz.ac.auckland.se206.util.UserManager;

public class UserProfilesScreenController {

  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;
  @FXML private VBox profilesVBox;

  private List<User> users;
  private UserManager userManager;

  /** Creates pagination */
  public void initialize() {

    // every time view is changed to user profiles pagination is created again based on the new user
    // profiles
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USERPROFILES) {
            try {
              users = App.getUserManager().getExistingProfiles();
            } catch (IOException | URISyntaxException e) {
              e.printStackTrace();
            }
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
          System.out.println(pageIndex);
          if (pageIndex >= users.size()) {
            return null;
          } else {
            // creates each profiles
            return createProfile(pageIndex);
          }
        });
    // sets max page count
    profilesPagination.setPageCount(users.size());
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
    Button usernameButton = new Button(users.get(pageIndex).getName());

    // sets id's of button to corresponding user id
    userIconButton.setId(String.valueOf(users.get(pageIndex).getID()));
    usernameButton.setId(String.valueOf(users.get(pageIndex).getID()));

    // set style
    setButtonStyle(userIconButton, "userIconButton");
    setButtonStyle(usernameButton, "usernameButton");
    userIconButton.setStyle(getBackgroundColor(users.get(pageIndex).getColour()));

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
  private String getBackgroundColor(String color) {
    return "-fx-background-color: " + color.replace("0x", "#") + ";";
  }

  /**
   * Sets specific style class for each button and clears previous style classes
   *
   * @param button
   */
  private void setButtonStyle(Button button, String cssClass) {

    // Clears current button style so it doesn't have css style of the general buttons
    button.getStyleClass().clear();
    // adds new style class to button
    button.getStyleClass().add(cssClass);
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

            try {
              userManager.setCurrentProfile(Integer.parseInt(userButton.getId()));
            } catch (NumberFormatException | IOException | URISyntaxException e) {
              e.printStackTrace();
            }

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