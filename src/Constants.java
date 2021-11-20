import java.awt.*;

/**
 * All the constants I use throughout the code
 */
public final class Constants {
    /**
     * Sprites data
     */
//    public static final Sprite SPI = new Sprite("SPI", "src/resources/spi", Color.RED, 60, 60, 150);
    public static final Sprite SPI = new Sprite("SPI", "src/resources/spi", Color.RED, 60, 60, 150);
    public static final Sprite[] SPRITES = {SPI};

    public static final int NO_FATHER = -1;
    public static final int DEATH_FATHER = 1;

    /**
     * Sprites poses dirs
     */
    public static final String IDLE = "/Idle/1.png";
    public static final String WALK = "/Walk/1.png";
    public static final String JUMP = "/Jump/1.png";
    public static final String A_ = "/A_move/1.png";
    public static final String A_AIR = "/A_move_air/1.png";
    //    public static final String FALL = "/Fall/1.png";
    public static final String HURT = "/Hurt/1.png";
    public static final String FALL = "/Jump/1.png";
    public static final String PUNCH_UP = "/PunchUp/1.png"; //todo not real
    public static final String BLOCK = "/Block/1.png";
    public static final String KO = "/KO/1.png";


    public static final String DEATH_BOTTOM = "src/resources/death/Death_Bottom/1.png";
    public static final String DEATH_UP = "src/resources/death/Death_Up/1.png";
    public static final String DEATH_LEFT = "src/resources/death/Death_Left/1.png";
    public static final String DEATH_RIGHT = "src/resources/death/Death_Right/1.png";
    /**
     * Game Data
     */
    public static final Dimension SCREEN_SIZE = new Dimension(1280, 720);
    public static final int START_PERCENTAGE = 1;

    /**
     * Rectangle Data
     */
    public static final Dimension REC_SIZE = new Dimension(200, 65);
    public static final Point FIRST_REC_PLACE = new Point(0, 600);
//    public static final Point[] REC_PLACE = {new Point(100, 600), new Point(250, 600)};

    public static Point getRecPlace(int i) {
        return new Point(FIRST_REC_PLACE.getX() + REC_SIZE.width * i + 100, 600);
    }

    public static Point namePlace(int i, String pName) {
        return new Point((int) (Constants.getRecPlace(i).getX() + REC_SIZE.width / 2 - pName.length() * 4 - pName.length() * 0.05 * 4), (int) Constants.getRecPlace(i).getY() - REC_SIZE.height);
    }

    public static Point PercentagePlace(int i, String txt) {
        return new Point((int) (Constants.getRecPlace(i).getX() + REC_SIZE.width / 2 - txt.length() * 4 - txt.length() * 0.05 * 4), (int) Constants.getRecPlace(i).getY() - REC_SIZE.height + 25);
    }

    /**
     * Network Data
     */
    public static final String MOVE_RIGHT_COMMAND = "mr";
    public static final String MOVE_LEFT_COMMAND = "ml";
    public static final String RELEASED_COMMAND = "rel";
    public static final String A_COMMAND = "A_";
    public static final String DOWN_COMMAND = "down";
}
