package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public enum ScreensAndMaps {
    //    Start(ScreensAndMaps.class.getResource("/screens/start/Game_Start.png").getPath(), 1280, 720),
    Start(Constants.res + "/screens/start/Game_Start.png", 1280, 720),
    //    Wait(ScreensAndMaps.class.getResource("/screens/wait/1.png").getPath(), 1280, 720),
    Wait(Constants.res + "/screens/wait/1.png", 1280, 720),
    //    BattleField(ScreensAndMaps.class.getResource("/maps/battlefield.png").getPath(), 1280, 720);
    BattleField(Constants.res + "/maps/battlefield.png", 1280, 720);

    public final Image image;

    ScreensAndMaps(String spritePath, int scaleX, int scaleY) {
        ImageIcon ii = new ImageIcon(spritePath);
        Image temp_image = ii.getImage();
        AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        temp_image = op.filter(Utils.toBufferedImage(temp_image), null);
        image = temp_image.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
    }
}
