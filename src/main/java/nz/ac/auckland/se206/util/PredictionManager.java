package nz.ac.auckland.se206.util;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class PredictionManager {

  /** This class is a thread which runs a while loop in the background to get predictions. */
  private final class ModelQueryThread extends Thread {

    private int numTopGuesses;

    // Constructor sets the daemon status to true immediately
    private ModelQueryThread(int numTopGuesses) {
      this.numTopGuesses = numTopGuesses;
      // Make this a background thread.
      setDaemon(true);
    }

    @Override
    public void run() {
      // Inifinite loop
      while (true) {

        // TODO: Memoize the input image so we are not making unnecessary queires

        // Only makes calls to the model query if listening is enabled
        if (isListening) {
          try {

            // Safetly null checks never hurt anyone
            if (classificationListener != null && snapshotProvider != null) {
              BufferedImage snapshot = snapshotProvider.getCurrentSnapshot();
              if (snapshot != null) {
                // Send the data back to the listener
                classificationListener.classificationReceived(
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
          System.out.println("Thread - " + Thread.currentThread().getName() + " was interrupted");
          // DO NOTHING, program has probably terminated
        }
      }
    }
  }

  public interface SnapshotProvider {
    /**
     * @return a BufferedImage that you want to make a prediction on
     */
    BufferedImage getCurrentSnapshot();
  }

  public interface ClassificationListener {
    /**
     * @param classificationList a list of the top classifications from the model
     */
    void classificationReceived(List<Classification> classificationList);
  }

  private SnapshotProvider snapshotProvider;
  private ClassificationListener classificationListener;

  private final DoodlePrediction model;
  private final Thread pollResultThread;

  private long pollInterval;

  private boolean isListening = false;

  /**
   * Creates a new PredictionManager. The manager uses the snapshot provider to get images and then
   * runs a prediction on those images. When the prediction is complete, the manager will send the
   * results to the classification listener via the <code>classificationReceived</code> method
   *
   * @param pollInterval - how often the manager will send queires to the server to get predictions
   *     in milliseconds. The poll interval is bound below by 10ms.
   * @param snapshotProvider - a class which implements the {@link SnapshotProvider} interface.
   * @param classificationListener - a class which implements the {@link ClassificationListener}
   *     interface
   * @throws IOException - If there is an error in reading the input/output of the DL model.
   * @throws ModelException - If the model cannot be found on the file system.
   */
  public PredictionManager(
      long pollInterval,
      int numTopGuesses,
      SnapshotProvider snapshotProvider,
      ClassificationListener classificationListener)
      throws IOException, ModelException {

    this.classificationListener = classificationListener;
    this.snapshotProvider = snapshotProvider;
    this.pollInterval = pollInterval;

    model = new DoodlePrediction();

    // Create a new thread which takes the current image
    pollResultThread = new ModelQueryThread(numTopGuesses);

    // Start the thread.
    pollResultThread.start();
  }

  /**
   * Gets the current snapshot provider. Returns null if there is no snapshot provider.
   *
   * @return a class which implements the SnapshotProvider interface.
   */
  public SnapshotProvider getSnapshotProvider() {
    return snapshotProvider;
  }

  /**
   * Sets the snapshot provider. If you input null, nothing will happen
   *
   * @param snapshotProvider a class which implements the SnapshotProvider interface @see {@link
   *     SnapshotProvider}
   */
  public void setSnapshotProvider(SnapshotProvider snapshotProvider) {
    if (snapshotProvider != null) {
      this.snapshotProvider = snapshotProvider;
    }
  }

  /**
   * Gets the class which is currently listening for updated predictions on the snapshot given by
   * the snapshot provider. Returns null if there is no classification listener.
   *
   * @return A class which implements the ClassificationListener interface.
   */
  public ClassificationListener getClassificationListener() {
    return classificationListener;
  }

  /**
   * Sets the class which listens out for recent predictions in the model. If you input null,
   * nothing will happen.
   *
   * @param classificationListener A class which implements the ClassificationListener
   *     interface @see {@link ClassificationListener}
   */
  public void setClassificationReceiver(ClassificationListener classificationListener) {
    if (classificationListener != null) {
      this.classificationListener = classificationListener;
    }
  }

  /**
   * Gets the poll interval
   *
   * @return the poll interval in milliseconds
   */
  public long getPollInterval() {
    return pollInterval;
  }

  /**
   * Sets the poll interval. The model is bound below by 10 so setting this below 10 will
   * effectively set the poll interval to 10ms
   *
   * @param pollInterval the poll interval in milliseconds
   */
  public void setPollInterval(long pollInterval) {
    this.pollInterval = pollInterval;
  }

  /**
   * Start making predictions on the model and sending the results to the classification listener
   */
  public void startListening() {
    isListening = true;
  }

  /** Stop making predictions on the model. */
  public void stopListening() {
    isListening = false;
  }

  /**
   * Gets the listening state
   *
   * @return a boolean indicating whether the prediction manager is making predictions
   */
  public boolean isListening() {
    return isListening;
  }
}
