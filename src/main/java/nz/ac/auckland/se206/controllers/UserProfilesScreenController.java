package nz.ac.auckland.se206.controllers;

import java.util.List;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.Profile;

public class UserProfilesScreenController {

  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;
  @FXML private VBox profilesVbox;

  private List<Profile> users;

  /** Creates pagination for UserProfilesScreen FXML */
  public void initialize() {

    // every time view is changed to user profiles pagination is created again based on the new user
    // profiles
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.USERPROFILES) {
            users = QuickDrawGameManager.getProfileManager().getProfiles();
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
   * @param pageIndex used to map the profile to the page number equal to its position in the list
   *     of profiles
   * @return vbox containing the user profile
   */
  public Pane createProfile(int pageIndex) {
    // create pane for every page
    Pane pane = new Pane();
    pane.getStyleClass().add("profilesPane");

    // create user buttons
    Button userIconButton = new Button("userIconButton");
    Button usernameButton = new Button(users.get(pageIndex).getName());

    // sets id's of button to corresponding user id
    userIconButton.setId(users.get(pageIndex).getId().toString());
    usernameButton.setId(users.get(pageIndex).getId().toString());

    // set style
    setButtonStyle(userIconButton, "userIconButton");
    setButtonStyle(usernameButton, "usernameButton");
    userIconButton.setStyle("-fx-background-color: " + getColor(users.get(pageIndex).getColour()));
    pane.setStyle("-fx-border-color: " + getColor(users.get(pageIndex).getColour()));

    // adds buttons to pane
    pane.getChildren().add(userIconButton);
    pane.getChildren().add(usernameButton);

    // set on action of buttons
    onUserButtonsClicked(userIconButton);
    onUserButtonsClicked(usernameButton);

    return pane;
  }

  /**
   * Returns the desired format for css color string
   *
   * @param color given background color of user
   * @return the colour chosen in a string format
   */
  private String getColor(String color) {
    return color.replace("0x", "#") + ";";
  }

  /**
   * Sets specific style class for each button and clears previous style classes
   *
   * @param button button we want to style
   * @param cssClass style class we want to use on the button
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
   * @param userButton button that the user clicks to select a profile to use
   */
  private void onUserButtonsClicked(Button userButton) {

    userButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            try {
              // sets current profile by obtaining button uuid
              QuickDrawGameManager.getProfileManager()
                  .setCurrentProfile(UUID.fromString(userButton.getId()));
            } catch (NumberFormatException e) {
              App.expect("Button ids should be able to generate valid UUIDs", e);
            }

            // gets controller to update category
            App.setView(View.USER);
          }
        });
  }

  /** Switches to new user screen */
  @FXML
  private void onCreateNewUser() {
    App.setView(View.NEWUSER);
  }
}
