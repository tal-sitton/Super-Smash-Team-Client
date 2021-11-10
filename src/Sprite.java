import java.awt.*;

public class Sprite {
    private final String name;
    private final String path;
    private final Color color;
    private final int scaleX;
    private final int scaleY;
    private final int defaultFrameRate;

    public Sprite(String name, String path, Color color, int scaleX, int scaleY, int defaultFrameRate) {
        this.color = color;
        this.name = name;
        this.path = path;
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

    public Color getColor() {
        return color;
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
