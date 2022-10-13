package nz.ac.auckland.se206.util;

public interface Transformer<T, U> {
  /**
   * This will take an input and transform it to desired output which will be returned
   *
   * @param valueToTransform the value/thing we want to be transformed
   * @return the inputted value transformed to what we want
   */
  public U transform(T valueToTransform);
}
