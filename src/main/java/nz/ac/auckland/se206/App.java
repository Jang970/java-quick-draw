package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.se206.gamelogicmanager.EndGameState;
import nz.ac.auckland.se206.gamelogicmanager.GameLogicManager;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.Profile;
import nz.ac.auckland.se206.util.ProfileManager;
import nz.ac.auckland.se206.util.badges.Badge;
import nz.ac.auckland.se206.util.badges.BadgeManager;

/** This is the entry point of the JavaFX application. */
public class App extends Application {

  public static enum View {
    HOME,
    USERPROFILES,
    NEWUSER,
    CATEGORY,
    GAME,
    USER,
    CATEGORYHISTORY
  }

  private static Stage stage;
  private static final EventEmitter<WindowEvent> appTerminationEmitter =
      new EventEmitter<WindowEvent>();
  // used for creation of folder to store user profiles

  private static ViewManager<View> viewManager;
  private static final GameLogicManager gameLogicManager = createGameLogicManager();
  private static final ProfileManager profileManager = createProfileManager();
  private static final BadgeManager badgeManager = new BadgeManager();
  private static final TextToSpeech textToSpeech = new TextToSpeech();

  private static ProfileManager createProfileManager() {

    File userProfiles = new File(".userprofiles");
    // create folder to store json file in if not already existing
    userProfiles.mkdir();

    try {
      // this creates the json file containing the list of user profiles
      return new ProfileManager(userProfiles.getAbsolutePath() + File.separator + "profiles.json");
    } catch (IOException e2) {
      App.expect("profiles.json is a file name, not a directory", e2);
      return null;
    }
  }

  private static GameLogicManager createGameLogicManager() {
    try {
      // Try create the game logic manager
      return new GameLogicManager();
    } catch (IOException | ModelException e1) {
      App.expect("The machine learning model exists on file", e1);
      return null;
    }
  }

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

    // Update profile details when the game ends and save to file
    gameLogicManager.subscribeToGameEnd(
        (gameInfo) -> {
          Profile currentProfile = profileManager.getCurrentProfile();

          if (gameInfo.getWinState() == EndGameState.WIN) {

            currentProfile.incrementGamesWon();

          } else if (gameInfo.getWinState() == EndGameState.LOOSE) {
            currentProfile.incrementGamesLost();
          }

          currentProfile.addGameToHistory(gameInfo);

          Set<String> existingBadges = currentProfile.getEarnedBadgeIds();

          for (Badge badge : badgeManager.getAllBadges()) {
            if (!existingBadges.contains(badge.getId())) {
              if (badge.earned(currentProfile)) {
                currentProfile.awardBadge(badge.getId());
              }
            }
          }

          profileManager.saveProfilesToFile();
        });

    App.stage = stage;

    stage.setOnCloseRequest(
        (e) -> {
          textToSpeech.terminate();
          appTerminationEmitter.emit(e);
        });

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
      viewManager.addView(View.USER, loadFxml("user-screen"));
      viewManager.addView(View.CATEGORYHISTORY, loadFxml("categoryhistory-screen"));

    } catch (IOException e1) {
      App.expect("All of the previously listed files should exists", e1);
    }

    stage.setTitle("Speedy Sketchers");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }

  public static TextToSpeech getTextToSpeech() {
    return textToSpeech;
  }
}
