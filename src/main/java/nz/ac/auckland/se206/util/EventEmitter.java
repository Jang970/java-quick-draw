package nz.ac.auckland.se206.util;

import java.util.HashMap;
import java.util.Map;

public class EventEmitter<Data> {

  private Map<Integer, EventListener<Data>> eventListeners =
      new HashMap<Integer, EventListener<Data>>();
  private int idCount = 0;

  /**
   * Dispatches data to all the observers
   *
   * @param data the data to be sent to the observers
   */
  public void emit(Data data) {
    for (EventListener<Data> eventListener : eventListeners.values()) {
      eventListener.update(data);
    }
  }

  /**
   * Allows a listener to be notified upon this emitter dispatches an update. Make sure to
   * unsubscribe from the emitter on clean up using the returned value
   *
   * @param listener to be notified when an event is emitted
   * @return the subscription ID for unsubscribing
   */
  public int subscribe(EventListener<Data> listener) {
    int id = idCount;
    idCount = idCount + 1;

    this.eventListeners.put(id, listener);
    return id;
  }

  public void unsubscribe(int subscription) {
    eventListeners.remove(subscription);
  }
}
