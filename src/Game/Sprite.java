package Game;

import java.awt.*;

/**
 * a class that represents Character Sprites
 */
public class Sprite {
    private final String name;
    private final String path;
    private final SpriteColor color;
    private final int scaleX;
    private final int scaleY;
    private final int defaultFrameRate;

    public enum SpriteColor {
        RED(Color.RED), BLUE(Color.BLUE),
        PINK(255, 0, 255), ORANGE(254, 102, 69),
        LIME(196, 219, 23), PINE(2, 119, 106),
        LIGHT_RED(228, 114, 105), BROWN(76, 34, 16);
        public final Color color;

        SpriteColor(Color color) {
            this.color = color;
        }

        SpriteColor(int r, int g, int b) {
            this.color = new Color(r, g, b);
        }

    }

    /**
     * @param name             the name of the sprite
     * @param color            the color of the sprite
     * @param scaleX           the width the sprite should be
     * @param scaleY           the height the sprite should be
     * @param defaultFrameRate the default frame-rate of sprite
     */
    public Sprite(String name, SpriteColor color, int scaleX, int scaleY, int defaultFrameRate) {
        this.color = color;
        this.name = name;
        this.path = Constants.res + "\\sprites\\" + name;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.defaultFrameRate = defaultFrameRate;
    }


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public SpriteColor getSpriteColor() {
        return color;
    }

    public Color getColor() {
        return color.color;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getDefaultFrameRate() {
        return defaultFrameRate;
    }

    @Override
    public String toString() {
        return name;
    }
}
