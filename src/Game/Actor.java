package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * a class that represent an actor that moves and performs actions around the screen
 *
 * @see Player
 * @see Enemy
 */
public abstract class Actor {
    private final Sprite mySprite;
    protected String name;
    private int percentage = Constants.START_PERCENTAGE;
    protected int x;
    protected int y;
    private int w; // the width of the actor
    private int h; // the height of the actor
    private Image image; // the image of the sprite
    private int spriteFrame = 1; // the current sprite frame of the current action
    private String spritePath; // the path of the sprite
    protected Action currentAction;
    private LocalTime nextTimeToChangeFrame; // the next time the actor should change to the next frame of the sprite
    protected boolean isToLeft = false; // whether the actor is looking to the left
    protected boolean isAlive = true;
    private boolean finishDeathAnim = false;
    public boolean clearedAfterDeathAnim = false;

    /**
     * @param mySprite the sprite of the actor
     * @param name     the name of the actor
     */
    public Actor(Sprite mySprite, String name) {
        this.name = name;
        this.mySprite = mySprite;
        nextTimeToChangeFrame = LocalTime.now().plus(mySprite.getDefaultFrameRate(), ChronoUnit.MILLIS);
        this.spritePath = mySprite.getPath();
        startAction(Action.Idle);
        loadImage();
        x = (int) Constants.SCREEN_SIZE.getWidth() / 2 - w / 2;
        y = (int) Constants.SCREEN_SIZE.getHeight() / 2 - h / 2;
    }

    /**
     * loads the image from the {@link #spritePath}, scales it and rotates it if necessary
     */
    private void loadImage() {
        ImageIcon ii = new ImageIcon(spritePath);
        image = ii.getImage();

        AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
        if (isToLeft) {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-image.getWidth(null), 0);
        }

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(Utils.toBufferedImage(image), null);

        int scaleX = currentAction.SCALE != null ? currentAction.SCALE.getX() : mySprite.getScaleX();
        int scaleY = currentAction.SCALE != null ? currentAction.SCALE.getY() : mySprite.getScaleY();

        image = image.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);

        w = image.getWidth(null);
        h = image.getHeight(null);
    }

    /**
     * moves the actor according to the data that was received from the server
     *
     * @param characterInfo the data from the server
     */
    public void move(String characterInfo) {
        String actionInfo = currentAction.name();
        if (isAlive) {
            String positionInfo = characterInfo.split("@")[0];
            actionInfo = characterInfo.split("@")[1].split("%")[0];
            if (characterInfo.split("@").length > 2) {
                this.isAlive = Boolean.parseBoolean(characterInfo.split("@")[2]);
                System.out.println(this.name + " HAS DIED");
                die();
                return;
            }
            if (x != Integer.parseInt(positionInfo.split(",")[0]) || y != Integer.parseInt(positionInfo.split(",")[1])) {
                x = Integer.parseInt(positionInfo.split(",")[0]);
                y = Integer.parseInt(positionInfo.split(",")[1]);
            }
            isToLeft = positionInfo.split(",")[2].equals("left");
            this.percentage = Integer.parseInt(characterInfo.split("@")[1].split("%")[1]);
        }
        if (LocalTime.now().isAfter(nextTimeToChangeFrame)) {
            if (currentAction == Action.valueOf(actionInfo))
                nextSpriteFrame();
            else
                startAction(Action.valueOf(actionInfo));
            nextTimeToChangeFrame = LocalTime.now().plus((int) (mySprite.getDefaultFrameRate() * currentAction.SPEED_MULTIPLIER), ChronoUnit.MILLIS);
        }

    }

    public String getName() {
        return name;
    }

    public Sprite getSprite() {
        return mySprite;
    }

    public String getPercentage() {
        return String.valueOf(percentage);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public Image getImage() {
        return image;
    }

    public boolean hasFinishDeathAnim() {
        return finishDeathAnim;
    }

    public boolean isToLeft() {
        return isToLeft;
    }

    public void setToLeft(boolean toLeft) {
        isToLeft = toLeft;
    }

    public void startAction(Action action) {
        removeSpriteAction();
        if (action.father == Constants.NO_FATHER)
            spritePath += action.PATH;
        else
            spritePath = action.PATH;
        currentAction = action;
        spriteFrame = 0;
    }

    /**
     * removes the sprite action by changing the {@link #spritePath} to the root sprite dir
     */
    private void removeSpriteAction() {
        List<Integer> dirs = Utils.indexesOf(spritePath, "/");
        if (spritePath.endsWith(".png"))
            spritePath = spritePath.substring(0, dirs.get(dirs.size() - 2));
    }

    private void removeSpriteFrame() {
        if (spritePath.endsWith(".png"))
            spritePath = spritePath.substring(0, spritePath.lastIndexOf("/"));
    }


    public void nextSpriteFrame() {
        removeSpriteFrame();
        spriteFrame++;

        int files = Utils.numOfFilesInDir(spritePath);
        if (spriteFrame > files && currentAction.IS_ABLE_TO_REPLAY) {
            spriteFrame = 1;
        }
        if (currentAction.father == Constants.DEATH_FATHER && spriteFrame > files) {
            finishDeathAnim = true;
            return;
        } else if (!currentAction.IS_ABLE_TO_REPLAY && spriteFrame > files)
            spriteFrame--;

        spritePath += "/" + spriteFrame + ".png";
        loadImage();
    }

    /**
     * Starts the die animation and mve it to the correct position in the screen.
     */
    private void die() {
        isToLeft = false;
        x = Utils.clamp(x, 0, Constants.SCREEN_SIZE.width);
        y = Utils.clamp(y, 0, Constants.SCREEN_SIZE.height);

        if (x == 0) {
            startAction(Action.Dead_Left);
            y -= currentAction.SCALE.getY() / 2;
        } else if (x == Constants.SCREEN_SIZE.width) {
            startAction(Action.Dead_Right);
            x -= currentAction.SCALE.getX();
            y -= currentAction.SCALE.getY() / 2;
        } else if (y == 0) {
            startAction(Action.Dead_Up);
            x -= currentAction.SCALE.getX() / 2;
        } else {
            startAction(Action.Dead_Bottom);
            y -= currentAction.SCALE.getY();
            x -= currentAction.SCALE.getX() / 2;
        }
    }

}
