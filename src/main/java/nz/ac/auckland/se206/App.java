package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.ViewManager.ViewChangeSubscription;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static enum View {
    HOME,
    GAME
  }

  private static ViewManager<View> viewManager;
  private static Stage stage;

  public static void subscribeToViewChange(ViewChangeSubscription<View> runnable) {
    viewManager.subscribeToViewChange(runnable);
  }

  public static void setView(View view) {
    viewManager.loadView(view);
  }

  public static void main(final String[] args) {
    launch();
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
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * Gets the stage of the javafx app
   *
   * @return the stage
   */
  public static Stage getStage() {
    return stage;
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    App.stage = stage;
    Parent defaultParent = loadFxml("home-screen");
    final Scene scene = new Scene(defaultParent, 640, 480);

    // We know this class only runs once so it is safe to do this.
    viewManager = new ViewManager<View>(scene);
    viewManager.addView(View.GAME, loadFxml("game-screen"));
    viewManager.addView(View.HOME, defaultParent);

    stage.setTitle("Speedy Sketchers");

    stage.setResizable(false); // The UI is not currently repsonsive

    stage.setScene(scene);
    stage.show();
  }
}
