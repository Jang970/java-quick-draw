package nz.ac.auckland.se206.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.App.View;
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.Profile;

public class LeaderboardScreenController {
  @FXML private TableView<Profile> leaderboard;
  @FXML private TableColumn<Profile, Integer> rankColumn;
  @FXML private TableColumn<Profile, String> namesColumn;
  @FXML private TableColumn<Profile, Integer> percentageColumn;

  /** Creates leaderboard and refereshes leaderboard on view change to leaderboard screen */
  public void initialize() {

    createLeaderboardTable();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.LEADERBOARD) {
            // TODO: Leaderboard not sorting by win percentage
            // refreshes leaderboard in case win percentage changes and if new profiles are created
            leaderboard.refresh();
          }
        });
  }

  /** Creates the leaderboard table based on profile class */
  private void createLeaderboardTable() {

    // rank column cells are set based on index of leaderboard table
    rankColumn.setCellValueFactory(
        new Callback<CellDataFeatures<Profile, Integer>, ObservableValue<Integer>>() {
          @Override
          public ObservableValue<Integer> call(CellDataFeatures<Profile, Integer> profile) {
            return new ReadOnlyObjectWrapper<Integer>(
                leaderboard.getItems().indexOf(profile.getValue()) + 1);
          }
        });

    // ensures users canot resize, reorder and sort columns
    rankColumn.setResizable(false);
    rankColumn.setReorderable(false);
    rankColumn.setSortable(false);

    // names column is the profile names
    namesColumn.setCellValueFactory(new PropertyValueFactory<Profile, String>("name"));
    namesColumn.setSortable(false);
    namesColumn.setReorderable(false);
    namesColumn.setResizable(false);

    // percetane column is the win percentage of each profile
    percentageColumn.setCellValueFactory(
        new PropertyValueFactory<Profile, Integer>("winPercentage"));
    percentageColumn.setReorderable(false);
    percentageColumn.setSortType(TableColumn.SortType.DESCENDING);
    percentageColumn.setResizable(false);

    // sets the items in the leaderboard as all current profiles
    leaderboard.setItems(
        FXCollections.observableList(QuickDrawGameManager.getProfileManager().getProfiles()));
    // sorts tables by percentage column
    leaderboard.getSortOrder().add(percentageColumn);
  }

  /** Switches view to user profiles */
  @FXML
  private void onBackToProfiles() {
    App.setView(View.USERPROFILES);
  }
}
