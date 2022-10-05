package nz.ac.auckland.se206.fxmlutils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.util.EventEmitter;
import nz.ac.auckland.se206.util.EventListener;

/**
 * The purpose of this class is to isolate all functionality of the canvas from the controller
 * through a simple interface.
 *
 * <p>It handles everything to do with drawing on, erasing on and clearning the canvas
 *
 * <p>//TODO: Perhaps this functionality should be abstracted as this violates the single
 * responsibility principle It also provides a method which gives the user an image from the pointed
 * canvas
 */
public class CanvasManager {

  public enum DrawMode {
    DRAWING,
    ERASING
  }

  // tracks if canvas is drawn on or not
  private static boolean isDrawn = false;
  private static EventEmitter<Boolean> canvasDrawnEmitter = new EventEmitter<Boolean>();

  /**
   * Returns if the canvas has been drawn or not
   *
   * @return
   */
  public static boolean getIsDrawn() {
    return isDrawn;
  }

  /**
   * Subscribes to canvas drawn to keep track of when canvas is drawn on
   *
   * @param listener
   */
  public static void subscribeToCanvasDrawn(EventListener<Boolean> listener) {
    canvasDrawnEmitter.subscribe(listener);
  }

  private final GraphicsContext context;
  private final Canvas canvas;
  private DrawMode drawMode = DrawMode.DRAWING;

  private boolean drawingEnabled = false;

  private double prevX;
  private double prevY;

  /** This helps keep track of the mouse being held down and released */
  private boolean isHolding = false;

  /**
   * The canvas manager is bound to one canvas which is given through this constructor
   *
   * @param canvas
   */
  public CanvasManager(Canvas canvas) {
    this.canvas = canvas;
    this.context = canvas.getGraphicsContext2D();

    canvas.setOnMouseDragged((e) -> handleDragEvent(e));
    canvas.setOnMouseReleased((e) -> isHolding = false);
    canvas.setOnMouseClicked((e) -> handleClickEvent(e));
    canvas.setOnMouseMoved(
        e -> {
          setCursor();
        });

    clearCanvas();
  }

  /**
   * @return the active draw mode for the canvas
   */
  public DrawMode getDrawMode() {
    return drawMode;
  }

  /**
   * Sets the draw mode of the canvas
   *
   * @param drawMode the new drawmode
   */
  public void setDrawMode(DrawMode drawMode) {
    this.drawMode = drawMode;
    this.isHolding = false;
  }

  /**
   * Sets the cursor based on the drawmode or default if drawing is not enabled
   *
   * @param drawMode
   */
  private void setCursor() {

    if (!isDrawingEnabled()) {
      canvas.setCursor(Cursor.DEFAULT);
    } else if (drawMode == DrawMode.DRAWING) {
      canvas.setCursor(Cursor.CROSSHAIR);
    } else {
      // gets eraser icon for cursor
      Image eraserImage = new Image("/images/eraserIcon.png", 20, 20, true, true);
      // sets hotspot as center of eraser image
      Cursor eraser =
          new ImageCursor(eraserImage, eraserImage.getWidth() / 2, eraserImage.getHeight() / 2);

      canvas.setCursor(eraser);
    }
  }

  /**
   * This is the main drawing function. It should be run whenever the mouse is dragged (in a new
   * position from previous while being held down) It uses the current and previous mouse positions
   * to interpolate lines
   *
   * @param event the MouseEvent
   */
  private void handleDragEvent(MouseEvent event) {

    if (drawingEnabled) {

      // Brush size (you can change this, it should not be too small or too large).
      final double brushSize = 5.0;

      // Get x and y from mouse position
      final double newX = event.getX() - brushSize / 2;
      final double newY = event.getY() - brushSize / 2;

      // TODO: Make path smoother

      if (isHolding) {
        context.beginPath();

        // Sets the stroke colour and width depending on the draw mode

        // TODO: Extract this to either already be set when drawmode is updated or
        // extract to simple function
        if (drawMode == DrawMode.DRAWING) {
          context.setStroke(Color.BLACK);
          context.setLineWidth(brushSize);
          isDrawn = true;
          canvasDrawnEmitter.emit(isDrawn);
        } else if (drawMode == DrawMode.ERASING) {
          // TODO: Extract this hard coded specific color
          context.setStroke(Color.rgb(235, 233, 221));
          context.setLineWidth(brushSize * 5);
        }

        // Using context api to draw a stroke between the last and current points
        // detected
        context.moveTo(prevX, prevY);
        context.lineTo(newX, newY);
        context.stroke();
        context.setImageSmoothing(true);
        context.closePath();
      } else if (!isHolding) {
        // This is so it only draws between the start point
        // of the drag and new points rather than some previous point
        // Remove the if and else clauses to see what im talking about
        isHolding = true;
      }

      prevX = newX;
      prevY = newY;
    }
  }

  /**
   * This function should be run whenever the mouse is clicked.
   *
   * @param event the click even
   */
  private void handleClickEvent(MouseEvent event) {
    if (drawingEnabled && drawMode == DrawMode.DRAWING) {
      int circleRadius = 6;
      isDrawn = true;
      context.fillOval(
          event.getX() - circleRadius, event.getY() - circleRadius, circleRadius, circleRadius);
    }
  }

  /** Runs the clear function if drawing is enabled. */
  public void clearOnlyIfDrawingEnabled() {
    if (drawingEnabled) {
      clearCanvas();
      drawMode = DrawMode.DRAWING;
    }
  }

  /** Simply sets all the pixels to white */
  public void clearCanvas() {
    context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    isHolding = false;
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /**
   * Save the current snapshot on a bitmap file. // TODO: Extract this to another class as it
   * violates the single responsibility principle
   *
   * @return The file of the saved image.
   * @throws IOException If the image cannot be saved.
   */
  public File saveCurrentSnapshotOnFile() throws IOException {

    // making use of FileChooser that will allow user to create a folder containing their drawing
    // and save it anywhere on their pc
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Image");
    fileChooser.setInitialFileName(
        App.getProfileManager().getCurrentProfile().getName()
            + "'s "
            + App.getGameLogicManager().getCurrentCategory().categoryString
            + " drawing");

    final File directory = fileChooser.showSaveDialog(App.getStage());

    if (directory != null) {
      directory.mkdir();
      // save image to a file in created folder
      final File imageToClassify =
          new File(directory.getAbsolutePath() + "/snapshot" + System.currentTimeMillis() + ".bmp");
      // save the image to a file
      ImageIO.write(getCurrentSnapshot(), "bmp", imageToClassify);

      return imageToClassify;
    }

    return null;
  }

  /**
   * Gets the isDrawingEnabled state
   *
   * @return a boolean indicating whether drawing is enabled
   */
  public boolean isDrawingEnabled() {
    return drawingEnabled;
  }

  /**
   * Sets the isDrawingEnabled state
   *
   * @param drawingEnabled a boolean indicating whether drawing should be enabled
   */
  public void setDrawingEnabled(boolean drawingEnabled) {
    this.drawingEnabled = drawingEnabled;
  }

  /** resets is drawn to false */
  public void resetIsDrawn() {
    isDrawn = false;
    canvasDrawnEmitter.emit(isDrawn);
  }
}
