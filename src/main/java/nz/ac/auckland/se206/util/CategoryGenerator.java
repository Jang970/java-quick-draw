package nz.ac.auckland.se206.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CategoryGenerator {

  public enum Difficulty {
    EASY,
    MEDIUM,
    HARD,
  }

  private Difficulty defaultDifficulty = Difficulty.EASY;
  private Map<Difficulty, List<String>> categories;

  /**
   * @param fileLocation the location of the csv with the categories and their associated difficulty
   *     values.
   */
  public CategoryGenerator(String fileName) {
    categories = new HashMap<Difficulty, List<String>>();

    // Creates an arraylist for each category
    categories.put(Difficulty.EASY, new ArrayList<String>());
    categories.put(Difficulty.MEDIUM, new ArrayList<String>());
    categories.put(Difficulty.HARD, new ArrayList<String>());

    // Get the global path to the csv with the categories and their associated
    // difficulty
    String pathToFile = getClass().getClassLoader().getResource(fileName).getFile();
    try {

      CSVReader reader = new CSVReader(new FileReader(pathToFile));

      // The first element is the entry and the second element is the difficulty
      List<String[]> entries = reader.readAll();

      // Slight speed optimisation by not running the hash function on every loop
      List<String> easyList = categories.get(Difficulty.EASY);
      List<String> mediumList = categories.get(Difficulty.MEDIUM);
      List<String> hardList = categories.get(Difficulty.HARD);

      for (String[] entry : entries) {
        String categoryString = entry[0];
        String difficultyString = entry[1];

        // Iterate through all csv values and add them to the relevant category
        if (difficultyString.equals("E")) {
          easyList.add(categoryString);
        }
        if (difficultyString.equals("M")) {
          mediumList.add(categoryString);
        }
        if (difficultyString.equals("H")) {
          hardList.add(categoryString);
        }
      }

    } catch (IOException | CsvException e) {
      System.out.println("Could not find csv");
    }
  }

  /**
   * @return the current default difficulty
   */
  public Difficulty getDefaultDifficulty() {
    return defaultDifficulty;
  }

  /**
   * Sets the difficulty of the generator. The generator will only generate items with the given
   * difficulty
   *
   * @param defaultDifficulty
   */
  public void setDefaultDifficulty(Difficulty defaultDifficulty) {
    this.defaultDifficulty = defaultDifficulty;
  }

  /**
   * Generates a random category to use.
   *
   * @param difficulty the difficulty level to use
   * @return the category as a string
   */
  public String generateCategory(Difficulty difficulty) {

    List<String> options = categories.get(difficulty);

    // Get random index from list
    int idx = ThreadLocalRandom.current().nextInt(options.size());

    return options.get(idx);
  }

  /**
   * Generates a category with the set difficulty.
   *
   * @return the category as a string
   */
  public String generateCategory() {
    return generateCategory(defaultDifficulty);
  }
}
