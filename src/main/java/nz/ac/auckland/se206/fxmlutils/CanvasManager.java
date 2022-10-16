package nz.ac.auckland.se206.fxmlutils;

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
import nz.ac.auckland.se206.QuickDrawGameManager;
import nz.ac.auckland.se206.util.BufferedImageUtils;

/**
 * The purpose of this class is to isolate all functionality of the canvas from the controller
 * through a simple interface.
 */
public class CanvasManager {

  public enum DrawMode {
    DRAWING,
    ERASING
  }

  private final GraphicsContext context;
  private final Canvas canvas;
  private DrawMode drawMode = DrawMode.DRAWING;

  private boolean drawingEnabled = false;

  private double prevX;
  private double prevY;

  /** This helps keep track of the mouse being held down and released */
  private boolean isHolding = false;

  private Color penColor = Color.BLACK;

  /**
   * The canvas manager is bound to one canvas which is given through this constructor
   *
   * @param canvas canvas to be used in app
   */
  public CanvasManager(Canvas canvas) {
    this.canvas = canvas;
    this.context = canvas.getGraphicsContext2D();

    // initialise / setup the canvas for the game
    canvas.setOnMouseDragged((e) -> handleDragEvent(e));
    // update when the user stops holding their button
    canvas.setOnMouseReleased((e) -> isHolding = false);
    canvas.setOnMouseClicked((e) -> handleClickEvent(e));
    canvas.setOnMouseMoved(
        e -> {
          setCursor();
        });

    clearCanvas();
  }

  /**
   * This method well get the current draw mode for the canvas
   *
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

  /** Sets the cursor based on the drawmode or default if drawing is not enabled */
  private void setCursor() {
    // this logic handles the switching between pencil and eraser
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
        if (drawMode == DrawMode.DRAWING) {
          context.setStroke(penColor);
          context.setLineWidth(brushSize);
        } else if (drawMode == DrawMode.ERASING) {
          // if user wants to erase their drawing
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
    // draw on the canvas when relevant criteria is met
    if (drawingEnabled && drawMode == DrawMode.DRAWING) {
      int circleRadius = 6;
      context.setFill(penColor);
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
   * Get the current snapshot of the canvas in black and white.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentBlackAndWhiteSnapshot() {
    return BufferedImageUtils.convertColourToBlackAndWhite(getCurrentColourSnapshot());
  }

  /**
   * Get the current snapshot of the canvas in colour
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  public BufferedImage getCurrentColourSnapshot() {
    return SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
  }

  /**
   * Save the current snapshot on a bitmap file.
   *
   * @return The file of the saved image.
   * @throws IOException If the image cannot be saved.
   */
  public File saveCurrentSnapshotOnFile() throws IOException {

    // making use of FileChooser that will allow user to create a folder containing their drawing
    // and save it anywhere on their pc
    FileChooser fileChooser = new FileChooser();

    // set tile of file chooser window
    fileChooser.setTitle("Save Image");
    // set the name of the file to save the image to
    fileChooser.setInitialFileName(
        QuickDrawGameManager.getProfileManager().getCurrentProfile().getName()
            + "'s "
            + QuickDrawGameManager.getGameLogicManager().getCurrentCategory().getName()
            + " drawing.png");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));

    // writing to the file if it is not null
    final File file = fileChooser.showSaveDialog(App.getStage());
    if (file != null) {
      ImageIO.write(getCurrentColourSnapshot(), "png", file);
    }

    return file;
  }

  /**
   * This method returns a boolean value indicating whether or not drawing is currently enabled for
   * the canvas.
   *
   * @return a boolean indicating whether drawing is enabled
   */
  public boolean isDrawingEnabled() {
    return drawingEnabled;
  }

  /**
   * This methods allows the user to enable or disable drawing for the canvas. When drawing is
   * disabled, the user will not be able to draw.
   *
   * @param drawingEnabled a boolean indicating whether drawing should be enabled
   */
  public void setDrawingEnabled(boolean drawingEnabled) {
    this.drawingEnabled = drawingEnabled;
  }

  /**
   * Sets the pen colour to a given colour
   *
   * @param color the colour to set the pen colour to
   */
  public void setPenColor(Color color) {
    this.penColor = color;
  }
}
