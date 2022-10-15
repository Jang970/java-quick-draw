package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
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
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
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

            // We get all the categories played from the profile manager
            categoriesPlayed =
                new ArrayList<Category>(
                    QuickDrawGameManager.getProfileManager()
                        .getCurrentProfile()
                        .getAllPlayedCategories());

            // bind the scroll bars and then set the history lists
            bindScrollBars();
            setCategoryHistoryLists();
          }
        });

    // Initializes the two list views
    initialiseListView(categoryHistoryListViewOne);
    initialiseListView(categoryHistoryListViewTwo);
  }

  /**
   * This sets up the data source for each category history list.
   *
   * @param categoryHistoryList the list to initialize the data for
   */
  private void initialiseListView(ListView<Category> categoryHistoryList) {
    // We set how the cells use the data from the category history.
    categoryHistoryList.setCellFactory(
        lv -> {
          ListCell<Category> cell =
              new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                  super.updateItem(item, empty);
                  if (!empty) {
                    // Set the text to the name of the category.
                    setText(item.getName());
                  }
                }
              };
          // When the mouse is clicked, we want to set the view
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

  /** Binds the scroll bars of the two lists so they scroll together */
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
