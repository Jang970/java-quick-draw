package nz.ac.auckland.se206.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class loads a csv file with key value pairs into a map */
public class CSVKeyValuePairLoader<K, V> {

  private Transformer<String, K> keyTransformer;
  private Transformer<String, V> valueTransformer;

  /**
   * @param fileLocation the location of the csv with the values and their associated keys.
   */
  public CSVKeyValuePairLoader(
      Transformer<String, K> keyTransformer, Transformer<String, V> valueTransformer) {
    this.keyTransformer = keyTransformer;
    this.valueTransformer = valueTransformer;
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
  public Map<K, List<V>> loadCategoriesFromFile(String pathToFile, boolean reverseOrder)
      throws IOException, CsvException {

    CSVReader reader = new CSVReader(new FileReader(pathToFile));

    List<String[]> entries = reader.readAll();

    Map<K, List<V>> map = new HashMap<>();

    for (String[] entry : entries) {
      K key = keyTransformer.transform(entry[reverseOrder ? 1 : 0]);
      V value = valueTransformer.transform(entry[reverseOrder ? 0 : 1]);

      if (key != null && value != null) {
        if (map.containsKey(key)) {
          map.get(key).add(value);
        } else {
          List<V> newList = new ArrayList<V>();
          newList.add(value);
          map.put(key, newList);
        }
      }
    }

    return map;
  }
}
