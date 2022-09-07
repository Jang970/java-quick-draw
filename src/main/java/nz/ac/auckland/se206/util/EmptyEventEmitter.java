package nz.ac.auckland.se206.util;

import java.util.HashMap;
import java.util.Map;

public class EmptyEventEmitter {

  private Map<Integer, EmptyEventListener> eventListeners =
      new HashMap<Integer, EmptyEventListener>();
  private int idCount = 0;

  /**
   * Dispatches data to all the observers
   *
   * @param data the data to be sent to the observers
   */
  public void emit() {
    for (EmptyEventListener observer : eventListeners.values()) {
      observer.update();
    }
  }

  /**
   * Allows a listener to be notified upon this observable dispatching an update. Make sure to
   * unsubscribe from the observable on clean up using the returned value
   *
   * @param listener the observer to be notified when an event is emitted
   * @return the subscription ID for unsubscribing
   */
  public int subscribe(EmptyEventListener listener) {
    int id = idCount;
    idCount = idCount + 1;

    this.eventListeners.put(id, listener);
    return id;
  }

  public void unsubscribe(int subscription) {
    eventListeners.remove(subscription);
  }
}
