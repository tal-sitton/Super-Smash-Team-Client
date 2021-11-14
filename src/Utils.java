import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * utils I use throughout the code
 */
public class Utils {

    /**
     * searches in what indexes a subString is in a String
     *
     * @param str The source String
     * @param sub The subString to search
     * @return List of indexes that the substring located in
     */
    public static List<Integer> indexesOf(String str, String sub) {
        List<Integer> indexes = new ArrayList<>();

        for (int index = str.indexOf(sub); index >= 0; index = str.indexOf(sub, index + 1)) {
            indexes.add(index);
        }
        return indexes;
    }

    /**
     * converts a Sprite name to a sprite
     *
     * @param name the name of the sprite
     * @return the correct sprite
     */
    public static Sprite SpriteNameToSprite(String name) {
        for (Sprite sprite : Constants.SPRITES) {
            if (sprite.getName().equals(name))
                return sprite;
        }
        return null;
    }

    /**
     * scans a directory to see how many files it contains
     *
     * @param pathOfDir The directory to scan
     * @return The number of files in the directory
     */
    public static int numOfFilesInDir(String pathOfDir) {
        System.out.println(pathOfDir);
        File directory = new File(pathOfDir);
        return Objects.requireNonNull(directory.list()).length;
    }

    /**
     * converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
