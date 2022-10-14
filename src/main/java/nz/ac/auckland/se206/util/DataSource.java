package nz.ac.auckland.se206.util;

public interface DataSource<D> {
  /**
   * This will get the data
   *
   * @return data wanted
   */
  D getData();
}
