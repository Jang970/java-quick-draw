package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;
import javafx.scene.Scene;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;

public class ViewManager<View extends Enum<View>> {
  public interface ViewChangeSubscription<View> {
    public void run(View view);
  }

  // TODO: Refactor so creating a view manager makes more sense

  private EventEmitter<View> viewChangeEmitter = new EventEmitter<View>();
  private HashMap<View, Parent> viewMap = new HashMap<View, Parent>();
  private Scene scene;

  /**
   * Constructs a new view manager. The view manager is intentionally tied to one scene
   *
   * @param scene this is a scene that the view manager will handle (control the root of)
   */
  public ViewManager(Scene scene) {
    this.scene = scene;
  }

  /**
   * This adds a new view that the manager will keep track of.
   *
   * @param view the user defined id of the view
   * @param root the parent node that will be reused
   */
  public void addView(View view, Parent root) {
    viewMap.put(view, root);
  }

  /**
   * This removes a view from the view manager. All data of the related node will be lost
   *
   * @param view the id of the view to remove
   */
  public void removeView(View view) {
    viewMap.remove(view);
  }

  /**
   * Sets the scene to the root associated with this view If the given view id has no associated
   * parent, it will leave the current view
   *
   * @param view the id of the view to load onto the scene
   */
  public void loadView(View view) {
    Parent parent = viewMap.get(view);

    if (parent != null) {
      scene.setRoot(parent);

      // Runs all registered subscription functions
      viewChangeEmitter.emit(view);
    }
  }

  public int subscribeToViewChange(EventListener<View> listener) {
    return viewChangeEmitter.subscribe(listener);
  }

  public void cancelSubsctiption(int subId) {
    viewChangeEmitter.unsubscribe(subId);
  }
}
