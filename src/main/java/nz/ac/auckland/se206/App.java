package nz.ac.auckland.se206;

import ai.djl.ModelException;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;
import nz.ac.auckland.se206.util.ProfileManager;

/** This is the entry point of the JavaFX application. */
public class App extends Application {

  public static enum View {
    HOME,
    USERPROFILES,
    NEWUSER,
    CATEGORY,
    GAME,
    USERSTATS
  }

  private static ViewManager<View> viewManager;
  private static GameLogicManager gameLogicManager;
  private static EventEmitter<WindowEvent> appTerminationEmitter = new EventEmitter<WindowEvent>();
  private static Stage stage;

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

  public static void main(final String[] args) {
    // Launch the JavaFX runtime
    launch();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   * @throws ModelException If there is an error with loading the doodle model.
   */
  @Override
  public void start(final Stage stage) throws IOException, ModelException {

    gameLogicManager = new GameLogicManager(10);
    gameLogicManager.setNumTopGuessNeededToWin(3);
    gameLogicManager.setGameLengthSeconds(60);

    profileManager = new ProfileManager("profiles.json");

    App.stage = stage;

    stage.setOnCloseRequest((e) -> appTerminationEmitter.emit(e));

    Parent defaultParent = loadFxml("home-screen");
    final Scene scene = new Scene(defaultParent, 600, 570);

    viewManager = new ViewManager<View>(scene);
    viewManager.addView(View.HOME, defaultParent);
    viewManager.addView(View.GAME, loadFxml("game-screen"));
    viewManager.addView(View.CATEGORY, loadFxml("category-screen"));
    viewManager.addView(View.USERPROFILES, loadFxml("userprofiles-screen"));
    viewManager.addView(View.NEWUSER, loadFxml("newuser-screen"));
    viewManager.addView(View.USERSTATS, loadFxml("userstats-screen"));

    stage.setTitle("Speedy Sketchers");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }
}
