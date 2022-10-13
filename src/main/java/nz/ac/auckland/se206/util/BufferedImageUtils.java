package nz.ac.auckland.se206.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BufferedImageUtils {

  public static double getFilledFraction(BufferedImage image, int brightnessThreshold) {
    Raster raster = image.getRaster();
    int colouredPixels = 0;

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

    return (double) colouredPixels / (double) (image.getWidth() * image.getHeight());
  }

  public static BufferedImage convertColourToBlackAndWhite(BufferedImage image) {
    final BufferedImage blackAndWhiteImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    WritableRaster originalRaster = image.getRaster();
    WritableRaster bwRaster = blackAndWhiteImage.getRaster();

    int[] pixels = new int[4];

    for (int y = 0; y < originalRaster.getHeight(); y++) {
      for (int x = 0; x < originalRaster.getWidth(); x++) {
        originalRaster.getPixel(x, y, pixels);
        boolean isBlack = pixels[0] < 255 || pixels[1] < 255 || pixels[2] < 255;
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
