package nz.ac.auckland.se206.util;

import java.util.HashMap;
import java.util.Map;

public class EventEmitter<D> {

  private Map<Integer, EventListener<D>> eventListeners = new HashMap<Integer, EventListener<D>>();
  private int idCount = 0;

  /**
   * Dispatches data to all the listeners.
   *
   * @param data the data to be sent to the observers
   */
  public void emit(D data) {
    for (EventListener<D> eventListener : eventListeners.values()) {
      eventListener.update(data);
    }
  }

  /**
   * Allows a listener to be notified upon this EventEmitter dispatching an update. Make sure to
   * unsubscribe from the EventEmitter on clean up using the returned value
   *
   * @param listener the EventListener to be notified when an event is emitted
   * @return the subscription ID for unsubscribing
   */
  public int subscribe(EventListener<D> listener) {
    int id = idCount;
    idCount = idCount + 1;

    this.eventListeners.put(id, listener);
    return id;
  }

  /**
   * Allows listeners to unsubscribe from events using the subscription id that was returned when
   * the listener subscribed
   *
   * @param subscription the id returned from the subscription which you would like to unsubscribe
   */
  public void unsubscribe(int subscription) {
    eventListeners.remove(subscription);
  }
}
