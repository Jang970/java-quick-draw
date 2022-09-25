package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.se206.GameLogicManager.WinState;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.Profile;
import nz.ac.auckland.se206.util.ProfileManager;

/** This is the entry point of the JavaFX application. */
public class App extends Application {

  public static enum View {
    HOME,
    USERPROFILES,
    NEWUSER,
    CATEGORY,
    GAME,
    USERSTATS,
    CATEGORYHISTORY
  }

  private static ViewManager<View> viewManager;
  private static GameLogicManager gameLogicManager;
  private static EventEmitter<WindowEvent> appTerminationEmitter = new EventEmitter<WindowEvent>();
  private static Stage stage;
  // used for creation of folder to store user profiles
  private static File userProfiles = new File(".userprofiles");

  private static ProfileManager profileManager;

  public static ProfileManager getProfileManager() {
    return profileManager;
  }

  public static GameLogicManager getGameLogicManager() {
    return gameLogicManager;
  }

  public static Stage getStage() {
    return stage;
  }

  public static void setView(View view) {
    viewManager.loadView(view);
  }

  public static int subscribeToViewChange(EventListener<View> listener) {
    return viewManager.subscribeToViewChange(listener);
  }

  public static void unsubscribeFromViewChange(int id) {
    viewManager.unsubscribeFromViewChange(id);
  }

  public static int subscribeToAppTermination(EventListener<WindowEvent> listener) {
    return appTerminationEmitter.subscribe(listener);
  }

  public static void unsubscribeFromAppTermination(int id) {
    appTerminationEmitter.unsubscribe(id);
  }

  /**
   * Use this function in places where code should never have to reach for some reason. EG you know
   * a catch block will never run. If this code is reached, it will exit the app and print the
   * message for why it should not have been reached (which you need to provide)
   *
   * @param whyThisShouldNeverRun
   */
  public static Object expect(String whyThisShouldNeverRun) {
    System.out.println(
        "Unexpected crash as the following expectation was not upheld: " + whyThisShouldNeverRun);
    System.exit(1);
    // This statement should never run as we exit the program
    return null;
  }

  /**
   * Use this function in places where code should never have to reach for some reason. EG you know
   * a catch block will never run. If this code is reached, it will exit the app and print the
   * message for why it should not have been reached (which you need to provide)
   *
   * @param whyThisShouldNeverRun
   * @param exception
   */
  public static Object expect(String whyThisShouldNeverRun, Exception exception) {
    System.out.println(
        "Unexpected crash as the following expectation was not upheld: " + whyThisShouldNeverRun);
    System.out.println("Exception Message: " + exception.getMessage());
    exception.printStackTrace();
    System.exit(1);
    // This statement should never run as we exit the program
    return null;
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static String getResourcePath(String relativePathInResourceFolder) {
    return App.class.getResource("/").getFile() + "/" + relativePathInResourceFolder;
  }

  public static void main(final String[] args) {
    // Launch the JavaFX runtime
    launch();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   */
  @Override
  public void start(final Stage stage) {

    try {
      // Try create the game logic manager
      gameLogicManager = new GameLogicManager(10);
    } catch (IOException | ModelException e1) {
      App.expect("The machine learning model exists on file", e1);
    }

    // Setting up initial settings
    gameLogicManager.setNumTopGuessNeededToWin(3);
    gameLogicManager.setGameLengthSeconds(60);

    // create folder to store json file in if not already existing
    userProfiles.mkdir();

    try {
      // this creates the json file containing the list of user profiles
      profileManager =
          new ProfileManager(userProfiles.getAbsolutePath() + File.separator + "profiles.json");
    } catch (IOException e2) {
      App.expect("profiles.json is a file name, not a directory", e2);
    }

    // Update profile details when the game ends and save to file
    gameLogicManager.subscribeToGameEnd(
        (gameInfo) -> {
          Profile currentProfile = profileManager.getCurrentProfile();

          if (gameInfo.getWinState() == WinState.WIN) {

            currentProfile.updateFastestGameIfBeatsCurrent(
                gameInfo.getTimeTaken(), gameInfo.getCategory());

            currentProfile.incrementGamesWon();

          } else if (gameInfo.getWinState() == WinState.LOOSE) {
            currentProfile.incrementGamesLost();
          }

          currentProfile.addToCategoryHistory(gameInfo.getCategory());

          profileManager.saveProfilesToFile();
        });

    App.stage = stage;

    stage.setOnCloseRequest((e) -> appTerminationEmitter.emit(e));

    Parent defaultParent;
    Scene scene = null;

    // JavaFX Admin
    try {

      defaultParent = loadFxml("home-screen");
      scene = new Scene(defaultParent, 600, 570);

      viewManager = new ViewManager<View>(scene);
      viewManager.addView(View.HOME, defaultParent);
      viewManager.addView(View.GAME, loadFxml("game-screen"));
      viewManager.addView(View.CATEGORY, loadFxml("category-screen"));
      viewManager.addView(View.USERPROFILES, loadFxml("userprofiles-screen"));
      viewManager.addView(View.NEWUSER, loadFxml("newuser-screen"));
      viewManager.addView(View.USERSTATS, loadFxml("userstats-screen"));
      viewManager.addView(View.CATEGORYHISTORY, loadFxml("categoryhistory-screen"));

    } catch (IOException e1) {
      App.expect("All of the previously listed files should exists", e1);
    }

    stage.setTitle("Speedy Sketchers");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }
}
