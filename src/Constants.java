import java.awt.*;

/**
 * All the constants I use throughout the code
 */
public final class Constants {
    /**
     * Sprites data
     */
//    public static final Sprite SPRITE_1 = new Sprite("SPRITE_1", "src/resources/sprite-1", 250, 250, 30);
//    public static final Sprite SPRITE_2 = new Sprite("SPRITE_2", "src/resources/sprite-2", 250, 250, 30);
//    public static final Sprite SPRITE_3 = new Sprite("SPRITE_3", "src/resources/sprite-3", 250, 250, 30);
//    public static final Sprite SPRITE_4 = new Sprite("SPRITE_4", "src/resources/sprite-4", 250, 250, 30);
//    public static final Sprite SPRITE_5 = new Sprite("SPRITE_5", "src/resources/sprite-5", 250, 250, 30);
//    public static final Sprite SPRITE_6 = new Sprite("SPRITE_6", "src/resources/sprite-6", 250, 250, 30);
//    public static final Sprite SPRITE_7 = new Sprite("SPRITE_7", "src/resources/sprite-7", 250, 250, 30);
//    public static final Sprite[] SPRITES = {SPRITE_1, SPRITE_2, SPRITE_3, SPRITE_4, SPRITE_5, SPRITE_6, SPRITE_7};
    public static final Sprite SPI = new Sprite("SPI", "src/resources/spi", Color.RED, 60, 60, 150);
    public static final Sprite[] SPRITES = {SPI};

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
    public static final String PUNCH = "/A_move/1.png";
    public static final String PUNCH_UP = "/PunchUp/1.png";
    public static final String BLOCK = "/Block/1.png";
    public static final String KO = "/Ko/1.png";

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
        return new Point((int) (Constants.getRecPlace(i).getX() + REC_SIZE.width / 2 - pName.length() * 4 - pName.length() * 0.05 * 4), Constants.getRecPlace(i).getY() - REC_SIZE.height);
    }

    public static Point PercentagePlace(int i, String txt) {
        return new Point((int) (Constants.getRecPlace(i).getX() + REC_SIZE.width / 2 - txt.length() * 4 - txt.length() * 0.05 * 4), Constants.getRecPlace(i).getY() - REC_SIZE.height + 25);
    }

    /**
     * Network Data
     */
    public static final String MOVE_RIGHT_COMMAND = "mr";
    public static final String MOVE_LEFT_COMMAND = "ml";
    public static final String RELEASED_COMMAND = "rel";
    public static final String A_COMMAND = "A_";
}
