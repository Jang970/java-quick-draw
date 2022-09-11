package nz.ac.auckland.se206.util;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class PredictionManager {

  private DataSource<BufferedImage> imageSource;
  private EventListener<List<Classification>> predictionListener;

  private final DoodlePrediction model;
  private final Thread pollResultThread;

  private long pollInterval;

  private boolean isMakingPredictions = false;

  /**
   * Creates a new PredictionManager. The manager uses the snapshot provider to get images and then
   * runs a prediction on those images. When the prediction is complete, the manager will send the
   * results to the classification listener via the <code>classificationReceived</code> method
   *
   * @param pollInterval - how often the manager will send queires to the server to get predictions
   *     in milliseconds. The poll interval is bound below by 10ms.
   * @param imageSource - a class which implements the {@link SnapshotProvider} interface.
   * @param predictionListener - a class which implements the {@link ClassificationListener}
   *     interface
   * @throws IOException - If there is an error in reading the input/output of the DL model.
   * @throws ModelException - If the model cannot be found on the file system.
   */
  public PredictionManager(long pollInterval, int numTopGuesses)
      throws IOException, ModelException {

    model = new DoodlePrediction();

    pollResultThread =
        new Thread() {
          {
            setDaemon(true);
          }

          @Override
          public void run() {
            // Inifinite loop
            while (true) {

              // TODO: Memoize the input image so we are not making unnecessary queires

              // Only makes calls to the model query if listening is enabled
              if (isMakingPredictions) {
                try {

                  // Safetly null checks never hurt anyone
                  if (predictionListener != null && imageSource != null) {
                    BufferedImage snapshot = PredictionManager.this.imageSource.getData();
                    if (snapshot != null) {
                      // Send the data back to the listener
                      PredictionManager.this.predictionListener.update(
                          model.getPredictions(snapshot, numTopGuesses));
                    } else {
                      System.out.println("Snapshot was null :/");
                    }
                  }

                } catch (TranslateException e) {
                  System.out.println("Prediction manager failed prediction");
                }
              }

              // Try sleep as to not hog the cpu. Cannot poll for any less than 10ms
              try {
                Thread.sleep(Math.max(pollInterval, 10));
              } catch (InterruptedException e) {
                System.out.println(
                    "Thread - " + Thread.currentThread().getName() + " was interrupted");
                // DO NOTHING, program has probably terminated
              }
            }
          }
        };

    // Start the thread.
    pollResultThread.start();
  }

  /**
   * Sets the snapshot provider. If you input null, nothing will happen
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
   * @param predictionListener A class which implements the ClassificationListener interface @see
   *     {@link ClassificationListener}
   */
  public void setPredictionListener(EventListener<List<Classification>> predictionListener) {
    if (predictionListener != null) {
      this.predictionListener = predictionListener;
    }
  }

  /**
   * Gets the poll interval
   *
   * @return the poll interval in milliseconds
   */
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

  /**
   * Start making predictions on the model and sending the results to the classification listener
   */
  public void startMakingPredictions() {
    isMakingPredictions = true;
  }

  /** Stop making predictions on the model. */
  public void stopMakingPredictions() {
    isMakingPredictions = false;
  }

  /**
   * Gets the listening state
   *
   * @return a boolean indicating whether the prediction manager is making predictions
   */
  public boolean isMakingPredictions() {
    return isMakingPredictions;
  }
}
