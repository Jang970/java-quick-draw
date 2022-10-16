package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.badges.Badge;

public class BadgesScreenController {

  @FXML private Pane badgesPane0;
  @FXML private Pane badgesPane1;
  @FXML private Pane rootPane;
  @FXML private Pagination badgesPagination;

  private List<Pane> badgesPanes;
  private HashMap<String, ImageView> badgesImageViews;

  /** Creates pagination of Badge Screen FXML */
  public void initialize() {

    // List of badges pane (badges layed out for slider)
    badgesPanes = new ArrayList<Pane>();
    badgesPanes.add(badgesPane0);
    badgesPanes.add(badgesPane1);

    // intialise hashmap and get all image views and their ids to store
    badgesImageViews = new HashMap<String, ImageView>();
    getAllBadgesImageViews();

    createBadgesPagination();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.BADGES) {

            reset();

            // goes through all earned badges and makes their image views visible
            for (String badgeId :
                QuickDrawGameManager.getProfileManager().getCurrentProfile().getEarnedBadgeIds()) {
              badgesImageViews.get(badgeId).setDisable(false);
            }
          }
        });
  }

  /**
   * gets all image views in every pane and puts it into hashmap where key is the id and the value
   * is the image view node
   */
  private void getAllBadgesImageViews() {
    //  puts it into hashmap where key is the id and the value
    for (Pane badgePane : badgesPanes) {
      for (Node children : badgePane.getChildren()) {
        if (children instanceof ImageView) {
          badgesImageViews.put(children.getId(), (ImageView) children);
        }
      }
    }
  }

  /** sets all image views in every pane as disabled */
  private void reset() {
    // disable every pane
    for (Badge badge : QuickDrawGameManager.getBadgeManager().getAllBadges()) {
      badgesImageViews.get(badge.getId()).setDisable(true);
    }
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

  /** switch to game modes FXML screen */
  @FXML
  private void onLetsPlay() {
    App.setView(View.GAMEMODES);
  }

  /** switches to user profile FXML screen */
  @FXML
  private void onSwitchToProfile() {
    App.setView(View.USER);
  }
}
