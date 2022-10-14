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
  @FXML private TableColumn<Profile, Integer> ratioColumn;

  public void initialize() {

    createLeaderboardTable();

    App.subscribeToViewChange(
        (View view) -> {
          if (view == View.LEADERBOARD) {
            leaderboard.refresh();
          }
        });
  }

  private void createLeaderboardTable() {

    rankColumn.setCellValueFactory(
        new Callback<CellDataFeatures<Profile, Integer>, ObservableValue<Integer>>() {
          @Override
          public ObservableValue<Integer> call(CellDataFeatures<Profile, Integer> profile) {
            return new ReadOnlyObjectWrapper<Integer>(
                leaderboard.getItems().indexOf(profile.getValue()) + 1);
          }
        });

    rankColumn.setResizable(false);
    rankColumn.setReorderable(false);
    rankColumn.setSortable(false);

    namesColumn.setCellValueFactory(new PropertyValueFactory<Profile, String>("name"));
    namesColumn.setSortable(false);
    namesColumn.setReorderable(false);
    namesColumn.setResizable(false);

    ratioColumn.setCellValueFactory(new PropertyValueFactory<Profile, Integer>("winPercentage"));
    // ratioColumn.setSortable(true);
    ratioColumn.setReorderable(false);
    ratioColumn.setSortType(TableColumn.SortType.DESCENDING);
    ratioColumn.setResizable(false);

    leaderboard.setItems(
        FXCollections.observableList(QuickDrawGameManager.getProfileManager().getProfiles()));
    leaderboard.getSortOrder().add(ratioColumn);
  }

  /** Switches view to user profiles */
  @FXML
  private void onBackToProfiles() {
    App.setView(View.USERPROFILES);
  }
}
