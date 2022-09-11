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

  private Map<Difficulty, List<String>> categories;

  /**
   * @param fileLocation the location of the csv with the categories and their associated difficulty
   *     values.
   */
  public CategoryGenerator(String fileName) {
    categories = new HashMap<Difficulty, List<String>>();

    categories.put(Difficulty.EASY, new ArrayList<String>());
    categories.put(Difficulty.MEDIUM, new ArrayList<String>());
    categories.put(Difficulty.HARD, new ArrayList<String>());

    loadCategoriesFromFile(fileName);
  }

  /**
   * Takes a csv file and loads all the categories from the file into the store. The CSV should have
   * two columns with no header: category, difficulty. category is a string of the category name and
   * difficulty should be E, M or H representing Easy, Medium or Hard respectively
   *
   * @param fileName the name of the csv file to load the categories from
   */
  public void loadCategoriesFromFile(String fileName) {
    String pathToFile = getClass().getClassLoader().getResource(fileName).getFile();
    try {

      CSVReader reader = new CSVReader(new FileReader(pathToFile));

      // Each string array: [entry, difficulty] e.g. ["Hat", "E"]
      List<String[]> entries = reader.readAll();

      List<String> easyList = categories.get(Difficulty.EASY);
      List<String> mediumList = categories.get(Difficulty.MEDIUM);
      List<String> hardList = categories.get(Difficulty.HARD);

      easyList.clear();
      mediumList.clear();
      hardList.clear();

      for (String[] entry : entries) {
        String categoryString = entry[0];
        String difficultyString = entry[1];

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
   * Returns a random category from the list of categories with the input diffuculty
   *
   * @param difficulty the difficulty level to use
   * @return the category as a string
   */
  public String getRandomCategory(Difficulty difficulty) {
    // TODO: Allow that if user selects M, it picks randomly from M or E, and if user selects H, it
    // choses randomly from E, M or H
    List<String> options = categories.get(difficulty);
    int randomIndex = ThreadLocalRandom.current().nextInt(options.size());
    return options.get(randomIndex);
  }
}
