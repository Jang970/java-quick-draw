package nz.ac.auckland.se206.util;

public interface EventListener<D> {
  /**
   * This will update the listener with new data inputted
   *
   * @param data new data to update to
   */
  void update(D data);
}
