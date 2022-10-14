package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.gamelogicmanager.CategoryPlayedInfo;
import nz.ac.auckland.se206.gamelogicmanager.GameInfo;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.gamelogicmanager.GameMode;
import nz.ac.auckland.se206.util.Category;

public class CategoryHistoryScreenController {

  @FXML private ListView<Category> categoryHistoryListViewOne;
  @FXML private ListView<Category> categoryHistoryListViewTwo;
  @FXML private HBox historyHbox;
  @FXML private ImageView ballImageView;

  // A list of the categories with no duplicates
  private List<Category> categoriesPlayed;

  private GameLogicManager gameLogicManager;

  /** Method that is run to set up the CategoryHistoryScreen FXML everytime it is opened/run. */
  public void initialize() {

    gameLogicManager = QuickDrawGameManager.getGameLogicManager();

    // gets category history and displays every time view is changed
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORYHISTORY) {

            // first get all categories played by profile as a set and of type Category
            Set<Category> tempCategories = new HashSet<Category>();

            List<GameInfo> gameHistory =
                QuickDrawGameManager.getProfileManager().getCurrentProfile().getGameHistory();

            // Adds categories to the set (removing duplicates)
            for (GameInfo game : gameHistory) {
              if (game.getGameMode() == GameMode.HIDDEN_WORD
                  || game.getGameMode() == GameMode.CLASSIC
                  || game.getGameMode() == GameMode.ZEN) {
                tempCategories.add(game.getCategoryPlayed().getCategory());
              } else if (game.getGameMode() == GameMode.RAPID_FIRE) {
                for (CategoryPlayedInfo categoryPlayed : game.getCategoriesPlayed()) {
                  tempCategories.add(categoryPlayed.getCategory());
                }
              }
            }

            categoriesPlayed = new ArrayList<Category>(tempCategories);

            bindScrollBars();

            setCategoryHistoryLists();
          }
        });

    initialiseListView(categoryHistoryListViewOne);
    initialiseListView(categoryHistoryListViewTwo);
  }

  private void initialiseListView(ListView<Category> categoryHistoryList) {
    categoryHistoryList.setCellFactory(
        lv -> {
          ListCell<Category> cell =
              new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                  super.updateItem(item, empty);
                  if (!empty) {
                    setText(item.getName());
                  }
                }
              };
          cell.setOnMouseClicked(
              e -> {
                if (!cell.isEmpty()) {

                  gameLogicManager.forceCategoryForNextInitialisation(cell.getItem());
                  // change view and reset boolean value so that when they play a new game other
                  // than replaying a word, a new random category is generated

                  App.setView(View.CATEGORY);

                  e.consume();
                }
              });
          return cell;
        });
  }

  /** Binds the scroll bars of the two lists */
  private void bindScrollBars() {
    // style the two lists
    categoryHistoryListViewOne.applyCss();
    categoryHistoryListViewTwo.applyCss();

    // create scroll bars and bind together so they scroll at the same time
    ScrollBar sb1 = (ScrollBar) categoryHistoryListViewOne.lookup(".scroll-bar");
    ScrollBar sb2 = (ScrollBar) categoryHistoryListViewTwo.lookup(".scroll-bar");
    sb1.valueProperty().bindBidirectional(sb2.valueProperty());
  }

  /** Displays word history by splitting list into two even sublists */
  private void setCategoryHistoryLists() {

    // split list evenly into two lists
    categoryHistoryListViewOne.setItems(
        FXCollections.observableArrayList(
            categoriesPlayed.subList(0, (categoriesPlayed.size() + 1) / 2)));

    categoryHistoryListViewTwo.setItems(
        FXCollections.observableArrayList(
            categoriesPlayed.subList((categoriesPlayed.size() + 1) / 2, categoriesPlayed.size())));
  }

  /** Method relating to the button switch to the CategoryScreen FXML */
  @FXML
  private void onPlayAgain() {
    App.setView(View.GAMEMODES);
  }

  /** Method relating to the button switch to the UserScreen FXML */
  @FXML
  private void onBackToStats() {
    App.setView(View.USER);
  }
}
