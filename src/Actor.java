import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class Actor {
    private final Sprite mySprite;
    protected String name;
    private int percentage = Constants.START_PERCENTAGE;
    protected int x;
    protected int y;
    private int w;
    private int h;
    private Image image;
    private int spriteFrame = 1;
    private String spritePath;
    protected Action currentAction;
    private LocalTime nextTimeToChangeFrame;
    protected boolean isToLeft = false;

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

    private void loadImage() {
        ImageIcon ii = new ImageIcon(spritePath);
        image = ii.getImage();

        if (isToLeft) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-image.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(Utils.toBufferedImage(image), null);
        } else {
            AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            image = op.filter(Utils.toBufferedImage(image), null);
        }

        image = image.getScaledInstance(mySprite.getScaleX(), mySprite.getScaleY(), Image.SCALE_SMOOTH);


        w = image.getWidth(null);
        h = image.getHeight(null);
    }

    public void move(String characterInfo) {
        String positionInfo = characterInfo.split("@")[0];
        String actionInfo = characterInfo.split("@")[1].split("%")[0];
        this.percentage = Integer.parseInt(characterInfo.split("@")[1].split("%")[1]);
        System.out.println(percentage);
        if (LocalTime.now().isAfter(nextTimeToChangeFrame)) {
            if (currentAction == Action.valueOf(actionInfo))
                nextSpriteFrame();
            else
                startAction(Action.valueOf(actionInfo));
            nextTimeToChangeFrame = LocalTime.now().plus((int) (mySprite.getDefaultFrameRate() * currentAction.SPEED_MULTIPLIER), ChronoUnit.MILLIS);
        }
        if (x != Integer.parseInt(positionInfo.split(",")[0]) || y != Integer.parseInt(positionInfo.split(",")[1])) {
            x = Integer.parseInt(positionInfo.split(",")[0]);
            y = Integer.parseInt(positionInfo.split(",")[1]);
        }
//        if ((isToLeft && positionInfo.split(",")[2].equals("left")) || (!isToLeft && positionInfo.split(",")[2].equals("right")))
        isToLeft = positionInfo.split(",")[2].equals("left");

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

    public boolean isToLeft() {
        return isToLeft;
    }

    public void setToLeft(boolean toLeft) {
        isToLeft = toLeft;
    }

    public void startAction(Action action) {
        removeSpriteAction();
        spritePath += action.PATH;
//        System.out.println(spritePath);
        currentAction = action;
        spriteFrame = 0;
    }

    private void removeSpriteAction() {
        List<Integer> dirs = Utils.indexesOf(spritePath, "/");
        if (dirs.size() > 2)
            spritePath = spritePath.substring(0, dirs.get(2));
//        System.out.println(spritePath);
    }

    private void removeSpriteFrame() {
        if (spritePath.endsWith(".png"))
            spritePath = spritePath.substring(0, spritePath.lastIndexOf("/"));
//        System.out.println(spritePath);
    }

    public void nextSpriteFrame() {
        removeSpriteFrame();
        spriteFrame++;

        if (spriteFrame > Utils.numOfFilesInDir(spritePath) && currentAction.IS_ABLE_TO_REPLAY) {
            spriteFrame = 1;
        }
        if (!currentAction.IS_ABLE_TO_REPLAY && spriteFrame > Utils.numOfFilesInDir(spritePath))
            spriteFrame--;

        spritePath += "/" + spriteFrame + ".png";
//        System.out.println(spritePath);
        loadImage();
    }

}
