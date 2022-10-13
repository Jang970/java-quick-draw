package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.util.Category;

public class CategoryHistoryScreenController {

  @FXML private ListView<String> categoryHistoryListViewOne;
  @FXML private ListView<String> categoryHistoryListViewTwo;
  @FXML private HBox historyHbox;
  @FXML private ImageView ballImageView;

  private List<String> categoryHistoryAsString;
  private List<Category> categoriesPlayed;
  private GameLogicManager gameLogicManager;

  /** Method that is run to set up the CategoryHistoryScreen FXML everytime it is opened/run. */
  public void initialize() {

    gameLogicManager = App.getGameLogicManager();

    // gets category history and displays every time view is changed
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.CATEGORYHISTORY) {

            // added new List that will store categories as the Category instance
            categoriesPlayed =
                App.getProfileManager().getCurrentProfile().getGameHistory().stream()
                    .flatMap(
                        (game) -> game.getCategoriesPlayed().stream().map(cat -> cat.getCategory()))
                    .collect(Collectors.toList());

            categoryHistoryAsString =
                App.getProfileManager().getCurrentProfile().getGameHistory().stream()
                    .distinct()
                    .flatMap(
                        (game) ->
                            game.getCategoriesPlayed().stream()
                                .map(cat -> cat.getCategory().getName()))
                    .collect(Collectors.toList());

            // removed any duplicates found in our list of strings
            List<String> noDups = new ArrayList<>();
            HashSet<String> lookUp = new HashSet<>();

            for (String category : categoryHistoryAsString) {
              if (lookUp.add(category)) {
                noDups.add(category);
              }
            }

            categoryHistoryAsString = noDups;

            bindScrollBars();

            setCategoryHistoryLists();
          }
        });

    setOnCellClick(categoryHistoryListViewOne);
    setOnCellClick(categoryHistoryListViewTwo);
  }

  private void setOnCellClick(ListView<String> categoryHistoryList) {
    categoryHistoryList.setCellFactory(
        lv -> {
          ListCell<String> cell =
              new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                  super.updateItem(item, empty);
                  setText(item);
                }
              };
          cell.setOnMouseClicked(
              e -> {
                if (!cell.isEmpty()) {
                  // TODO: Send category word to category screen
                  System.out.println("You clicked on " + cell.getItem());

                  // search for category clicked in our list of Category objects via the name
                  // if a match was found then we set the category in gameLogicManager to that and
                  // also switch the view
                  for (Category category : categoriesPlayed) {

                    if (category.getName().equals(cell.getItem())) {
                      gameLogicManager.setCategory(category);
                      break;
                    }
                  }

                  // change view and reset boolean value so that when they play a new game other
                  // than replaying a word, a new random category is generated
                  App.setView(View.CATEGORY);
                  gameLogicManager.updateReplayWord(false);

                  e.consume();
                }
              });
          return cell;
        });
  }

  /** Binds the scroll bars of the two lists */
  private void bindScrollBars() {
    categoryHistoryListViewOne.applyCss();
    categoryHistoryListViewTwo.applyCss();

    ScrollBar sb1 = (ScrollBar) categoryHistoryListViewOne.lookup(".scroll-bar");
    ScrollBar sb2 = (ScrollBar) categoryHistoryListViewTwo.lookup(".scroll-bar");
    sb1.valueProperty().bindBidirectional(sb2.valueProperty());
  }

  /** Displays word history by splitting list into two even sublists */
  private void setCategoryHistoryLists() {

    // split list evenly into two lists
    categoryHistoryListViewOne.setItems(
        FXCollections.observableArrayList(
            categoryHistoryAsString.subList(0, (categoryHistoryAsString.size() + 1) / 2)));

    categoryHistoryListViewTwo.setItems(
        FXCollections.observableArrayList(
            categoryHistoryAsString.subList(
                (categoryHistoryAsString.size() + 1) / 2, categoryHistoryAsString.size())));
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
