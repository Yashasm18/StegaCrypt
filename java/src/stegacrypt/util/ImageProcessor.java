package stegacrypt.util;

import stegacrypt.core.StegoException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * ImageProcessor - Handles image file I/O operations.
 * Demonstrates: FILE I/O, EXCEPTION HANDLING, javax.imageio usage.
 */
public class ImageProcessor {

    public BufferedImage readImage(String path) throws StegoException {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new StegoException(StegoException.ErrorType.IMAGE_NOT_FOUND, path);
            }
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new StegoException(StegoException.ErrorType.IMAGE_READ_ERROR, path);
            }
            // Convert to ARGB for consistent pixel manipulation
            BufferedImage argbImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB
            );
            argbImage.getGraphics().drawImage(image, 0, 0, null);
            return argbImage;
        } catch (IOException e) {
            throw new StegoException(StegoException.ErrorType.IMAGE_READ_ERROR, e);
        }
    }

    public void writeImage(BufferedImage image, String path) throws StegoException {
        try {
            File outputFile = new File(path);
            File parent = outputFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!ImageIO.write(image, "png", outputFile)) {
                throw new StegoException(StegoException.ErrorType.IMAGE_WRITE_ERROR, path);
            }
        } catch (IOException e) {
            throw new StegoException(StegoException.ErrorType.IMAGE_WRITE_ERROR, e);
        }
    }
}
