package nz.ac.auckland.se206.util;

public interface Transformer<T, U> {
  public U transform(T valueToTransform);
}
