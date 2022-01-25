package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public enum ScreensAndMaps {
    Start(ScreensAndMaps.class.getResource("/screens/start/Game_Start.png").getPath(), 1280, 720),
    BattleField(ScreensAndMaps.class.getResource("/maps/battlefield.png").getPath(), 1280, 720);

    public Image image;

    ScreensAndMaps(String spritePath, int scaleX, int scaleY) {
        ImageIcon ii = new ImageIcon(spritePath);
        Image temp_image = ii.getImage();
        AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        temp_image = op.filter(Utils.toBufferedImage(temp_image), null);
        image = temp_image.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
    }
}
