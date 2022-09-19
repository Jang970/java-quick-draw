package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;

public class WordHistoryScreenController {

  @FXML private ListView<String> wordHistoryListViewOne;
  @FXML private ListView<String> wordHistoryListViewTwo;
  @FXML private HBox historyHbox;

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

            // TODO: Find a better way to do this resizing
            // dynamically resize list hbox height (doesn't show more cells than necessary)
            historyHbox.setPrefHeight((wordHistory.size() + 1) * 15 + 2);
          }
        });
  }

  /** Displays word history by splitting list into two even sublists */
  private void displayWordHistory() {

    // split list evenly into two lists
    wordHistoryListViewOne.setItems(
        FXCollections.observableArrayList(wordHistory.subList(0, (wordHistory.size() + 1) / 2)));

    wordHistoryListViewTwo.setItems(
        FXCollections.observableArrayList(
            wordHistory.subList((wordHistory.size() + 1) / 2, wordHistory.size())));
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
