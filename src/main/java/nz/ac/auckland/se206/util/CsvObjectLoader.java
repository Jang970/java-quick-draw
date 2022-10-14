package nz.ac.auckland.se206.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/** This class loads a csv file with key value pairs into a map */
public class CsvObjectLoader<T> {

  private final Transformer<String[], T> rowTransformer;

  /**
   * Constructor for class CsvObjectLoader which will be used to read data from a csv file.
   *
   * @param rowTransformer transformer function to turn strings into our desired values
   */
  public CsvObjectLoader(Transformer<String[], T> rowTransformer) {
    this.rowTransformer = rowTransformer;
  }

  /**
   * This function takes a transformer which converts an array of strings to a object. It then
   * applies the transformation on each row of a CSV and returns the list.
   *
   * @param pathToFile path to the csv file we want to read from <<<<<<< HEAD
   * @param reverseOrder set this to true if the keys come after the values in the csv
   * @return the map from keys to values
   * @throws IOException if the file to be read cannot be found
   * @throws CsvException if the file is not in csv format =======
   * @return the list of transformed objecys
   * @throws IOException
   * @throws CsvException >>>>>>> main
   */
  public List<T> loadObjectsFromFile(String pathToFile) throws IOException, CsvException {

    // get data from the CSV
    CSVReader reader = new CSVReader(new FileReader(pathToFile));

    // then for all data read in, apply transformation from string into wanted values
    // then convert result to a type List
    List<T> result =
        reader.readAll().stream()
            .map(
                (String[] arr) -> {
                  return rowTransformer.transform(arr);
                })
            .collect(Collectors.toList());

    reader.close();

    return result;
  }
}
