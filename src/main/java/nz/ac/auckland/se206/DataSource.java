package nz.ac.auckland.se206;

/** A class complying with this ineterface should provide a method which provides an image */
public interface DataSource<Data> {
  Data getData();
}
