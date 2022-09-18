package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class WordHistoryScreenController {

  @FXML private ListView wordHistoryListViewOne;
  @FXML private ListView wordHistoryListViewTwo;

  private List<String> wordHistory;

  public void initialize() {

    // gets word history and displays every time view is changed
    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.WORDHISTORY) {
            wordHistory =
                new ArrayList<String>(
                    App.getProfileManager().getCurrentProfile().getCategoryHistory());
            displayWordHistory();
          }
        });
  }

  /** Displays word history by splitting list into two even sublists */
  private void displayWordHistory() {

    // TODO: How to fix type safety problem?
    wordHistoryListViewOne.setItems(
        FXCollections.observableArrayList(wordHistory.subList(0, wordHistory.size() / 2)));

    wordHistoryListViewTwo.setItems(
        FXCollections.observableArrayList(
            wordHistory.subList(wordHistory.size() / 2, wordHistory.size())));
  }

  @FXML
  private void onPlayAgain() {
    App.setView(View.CATEGORY);
  }

  @FXML
  private void onBackToStats() {
    App.setView(View.USERSTATS);
  }
}
