package nz.ac.auckland.se206.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class UserProfilesScreenController {
  @FXML private Button newGameButton;
  @FXML private Pagination profilesPagination;

  // TODO: Each user can choose a color for their box?
  private Color[] color =
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

  public void initialize() {
    profilesPagination.setPageFactory(
        (Integer pageIndex) -> {
          if (pageIndex >= username.length) {
            return null;
          } else {
            return createPage(pageIndex);
          }
        });
    profilesPagination.setPageCount(username.length);
  }

  public VBox createPage(int pageIndex) {
    VBox box = new VBox(5);

    Button userButton = new Button(username[pageIndex]);
    userButton.setId(username[pageIndex]);

    // TODO: Extract these style
    userButton.getStyleClass().clear();
    userButton.setBackground(new Background(new BackgroundFill(color[pageIndex], null, null)));

    box.getChildren().add(userButton);

    return box;
  }

  @FXML
  private void onStartNewGame() {
    // gets controller to update category
    CategoryScreenController categoryScreen = App.getLoader("category-screen").getController();
    categoryScreen.updateCategory();
    App.setView(View.CATEGORY);
  }
}
