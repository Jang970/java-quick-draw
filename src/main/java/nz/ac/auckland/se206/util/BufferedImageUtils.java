package nz.ac.auckland.se206.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BufferedImageUtils {

  /**
   * This method takes a BufferedImage and returns the percentage of the image that is filled from 0
   * (not filled) to 1 (filled)
   *
   * @param image the image to check
   * @param brightnessThreshold how bright a pixel needs to be to be considered filled
   * @return the percentage of the image that is filled.
   */
  public static double getFilledFraction(BufferedImage image, int brightnessThreshold) {
    Raster raster = image.getRaster();
    int colouredPixels = 0;

    // Iterate through each pixel and check if it is filled.
    int[] pixels = new int[4];
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        raster.getPixel(x, y, pixels);
        if (pixels[0] >= brightnessThreshold
            || pixels[1] >= brightnessThreshold
            || pixels[2] >= brightnessThreshold) {
          colouredPixels++;
        }
      }
    }

    // Return ration of fileld to total.
    return (double) colouredPixels / (double) (image.getWidth() * image.getHeight());
  }

  /**
   * This method takes a coloured BufferedImage and returns a new BlackAndWhite BufferedImage. If
   * the pixel has some colour, it will be black. Otherwise white.
   *
   * @param image the image to convert
   * @return the new black and white image.
   */
  public static BufferedImage convertColourToBlackAndWhite(BufferedImage image) {
    final BufferedImage blackAndWhiteImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    // Ger rasters that we can edit from the images.
    WritableRaster originalRaster = image.getRaster();
    WritableRaster bwRaster = blackAndWhiteImage.getRaster();

    int[] pixels = new int[4];

    // Iterate through all the pixels
    for (int y = 0; y < originalRaster.getHeight(); y++) {
      for (int x = 0; x < originalRaster.getWidth(); x++) {
        originalRaster.getPixel(x, y, pixels);
        boolean isBlack = pixels[0] < 220 || pixels[1] < 220 || pixels[2] < 220;
        if (isBlack) {
          bwRaster.setPixel(x, y, new int[] {0, 0, 0, 0});
        } else {
          bwRaster.setPixel(x, y, new int[] {255, 255, 255, 255});
        }
      }
    }

    return blackAndWhiteImage;
  }
}
