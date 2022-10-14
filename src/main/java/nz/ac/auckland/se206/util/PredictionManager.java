package nz.ac.auckland.se206.util;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import com.opencsv.exceptions.CsvException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class PredictionManager {

  private DataSource<BufferedImage> imageSource;
  private EventListener<List<Classification>> predictionListener;

  private final DoodlePrediction model;
  private final Thread pollResultThread;

  private long pollInterval;

  private boolean isMakingPredictions = false;

  private Set<Category> categories = new HashSet<Category>();

  /**
   * The prediction manager will be used to make predictions on the current drawing.
   *
   * @param pollInterval how often the manager will send queires to the server to get predictions in
   *     milliseconds. The poll interval is bound below by 10ms
   * @param numTopGuesses where the drawing has to be in the ML top guesses order for it to be a win
   *     i.e if it was 3, then the drawing must be top 3 in guesses.
   * @throws IOException If there is an error in reading the input/output of the DL model.
   * @throws ModelException If the model cannot be found on the file system.
   */
  public PredictionManager(long pollInterval, int numberOfPredictions)
      throws IOException, ModelException {

    try {
      // will load in all words from csv file and sort into respective types
      CsvObjectLoader<Category> loader =
          new CsvObjectLoader<Category>(
              (row) -> {
                CategoryType type = CategoryType.EASY;
                if (row[1].equals("E")) {
                  type = CategoryType.EASY;
                } else if (row[1].equals("M")) {
                  type = CategoryType.MEDIUM;
                } else if (row[1].equals("H")) {
                  type = CategoryType.HARD;
                }
                return new Category(row[0], row[2], type);
              });

      categories =
          new HashSet<Category>(loader.loadObjectsFromFile(App.getResourcePath("categories.csv")));

    } catch (CsvException e) {
      App.expect("Category CSV is in the resource folder and is not empty", e);
    }

    final int internalNumberOfPredictions =
        numberOfPredictions > 0 ? numberOfPredictions : categories.size();

    model = new DoodlePrediction();

    //  run predictions on drawing
    pollResultThread =
        new Thread() {
          {
            setDaemon(true);
          }

          @Override
          public void run() {
            while (true) {

              // TODO: Memoize the input image so we are not making unnecessary queires

              // update the listener relating to the event making the predictioner while
              // isMakingPredictions is true
              if (isMakingPredictions) {
                try {
                  if (predictionListener != null && imageSource != null) {
                    BufferedImage snapshot = imageSource.getData();
                    if (snapshot != null) {
                      predictionListener.update(
                          model.getPredictions(snapshot, internalNumberOfPredictions));
                    }
                  }
                } catch (TranslateException error) {
                  System.out.println("Prediction manager failed prediction: " + error.getMessage());
                }
              }

              try {
                // We add a small delay so as to not hog the cpu
                Thread.sleep(Math.max(pollInterval, 10));
              } catch (InterruptedException e) {
                System.out.println(
                    "Thread - " + Thread.currentThread().getName() + " was interrupted");
              }
            }
          }
        };

    pollResultThread.start();
  }

  /**
   * Gets the number of categories from the given csv
   *
   * @return
   */
  public Integer getNumberOfCategories() {
    return categories.size();
  }

  /**
   * Gets a set containing all the categories from the csv. Please do not modify this set!
   *
   * @return the set containing all the categories from the csv.
   */
  public Set<Category> getCategories() {
    return categories;
  }

  /**
   * Sets the image source which will be used to make predictions. If you input null, nothing will
   * happen
   *
   * @param imageSource a class which has an image providing function
   */
  public void setImageSource(DataSource<BufferedImage> imageSource) {
    if (imageSource != null) {
      this.imageSource = imageSource;
    }
  }

  /**
   * Sets the class which listens out for recent predictions in the model. If you input null,
   * nothing will happen.
   *
   * @param predictionListener A class which implements the EventListener Interface
   */
  public void setPredictionListener(EventListener<List<Classification>> predictionListener) {
    if (predictionListener != null) {
      this.predictionListener = predictionListener;
    }
  }

  public long getPredictionPollInterval() {
    return pollInterval;
  }

  /**
   * Sets the poll interval. The model is bound below by 10 so setting this below 10 will
   * effectively set the poll interval to 10ms
   *
   * @param pollInterval the poll interval in milliseconds
   */
  public void setPredictionPollInterval(long pollInterval) {
    this.pollInterval = pollInterval;
  }

  /** Start making predictions on the model and sending the results to the prediction listener */
  public void startMakingPredictions() {
    isMakingPredictions = true;
  }

  /** Stop making predictions on the model and sending the results to the prediction listener */
  public void stopMakingPredictions() {
    isMakingPredictions = false;
  }

  /**
   * Checking if predictions are being made on the model and sending the results to the prediction
   * listener
   *
   * @return true or false dependent if predictions are being made
   */
  public boolean isMakingPredictions() {
    return isMakingPredictions;
  }

  /**
   * This generates a new random category, updates the category for the class and returns the value
   * of the new category. It will not use any values in the provided set
   *
   * @param categoryFilter used to filter categories
   * @param includeEasy indicator if easy words should be included
   * @param includeMedium indicator if medium words should be included
   * @param includeHard indicator if hard words should be included
   * @return new random category generate
   * @throws FilterTooStrictException when all categories have been filtered
   */
  public Category getNewRandomCategory(
      Set<String> categoryFilter, boolean includeEasy, boolean includeMedium, boolean includeHard)
      throws FilterTooStrictException {

    // will check what words to include, this depends on the difficulty
    // Removes all the items which are also in the filter set (set subtraction)
    List<Category> possibleCategories =
        categories.stream()
            .filter(
                (category) -> {
                  CategoryType type = category.getCategoryType();

                  return ((type == CategoryType.EASY && includeEasy)
                          || (type == CategoryType.MEDIUM && includeMedium)
                          || (type == CategoryType.HARD && includeHard))
                      && !categoryFilter.contains(category.getName());
                })
            .collect(Collectors.toList());

    if (possibleCategories.isEmpty()) {
      throw new FilterTooStrictException("The filter filtered out all categories");
    }

    // Get random index from remaining items
    int randomIndexFromList = ThreadLocalRandom.current().nextInt(possibleCategories.size());

    return possibleCategories.get(randomIndexFromList);
  }
}
