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
   * @param fileLocation the location of the csv with the values and their associated keys.
   */
  public CsvObjectLoader(Transformer<String[], T> rowTransformer) {
    this.rowTransformer = rowTransformer;
  }

  /**
   * This function takes a path to a csv file and loads in all the keys and values from that file.
   * It uses the tranformer functions passed in the constructor to turn strings into the desired
   * values. If either of these functions returns null, the line of the csv file is rejected
   *
   * @param pathToFile
   * @param reverseOrder set this to true if the keys come after the values in the csv
   * @return the map from keys to values
   * @throws IOException
   * @throws CsvException
   */
  public List<T> loadObjectsFromFile(String pathToFile, boolean reverseOrder)
      throws IOException, CsvException {

    CSVReader reader = new CSVReader(new FileReader(pathToFile));

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
