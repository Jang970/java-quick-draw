package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class BadgesScreenController {
  @FXML private Pane badgesPane0;
  @FXML private Pane badgesPane1;
  @FXML private Pagination badgesPagination;

  private List<Pane> badgesPanes;

  /** Creates pagination */
  public void initialize() {

    // List of badges pane (badges layed out for slider)
    badgesPanes = new ArrayList<Pane>();
    badgesPanes.add(badgesPane0);
    badgesPanes.add(badgesPane1);

    createBadgesPagination();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.BADGES) {

            // GET ALL EARNED BADGES AND SET DISABLE TO FALSE (fx:id must be same as badge
            // name)
            // for (badge : badges){
            // ImageView badgeImage = (ImageView) App.getStage().getScene().lookup("#" +
            // badge.getName())
            // badgeImage.setDisable(false);
            // }
          }
        });
  }

  /** creates each badges pane for pagination */
  private void createBadgesPagination() {

    // sets style has bullets (no numbers)
    badgesPagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);

    // creates pages length of profiles list
    badgesPagination.setPageFactory(
        (Integer pageIndex) -> {
          if (pageIndex >= badgesPanes.size()) {
            return null;
          } else {
            // creates each profiles
            return badgesPanes.get(pageIndex);
          }
        });
    // sets max page count
    badgesPagination.setPageCount(badgesPanes.size());
  }

  @FXML
  private void onLetsPlay() {
    App.setView(View.CATEGORY);
  }

  @FXML
  private void onSwitchToProfile() {
    App.setView(View.USER);
  }
}
