package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public enum Maps {
    BattleField(Maps.class.getResource("/maps/battlefield.png").getPath(), 1280, 720);

    public Image image;

    Maps(String spritePath, int scaleX, int scaleY) {
        ImageIcon ii = new ImageIcon(spritePath);
        Image temp_image = ii.getImage();
        AffineTransform tx = AffineTransform.getScaleInstance(1, 1);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        temp_image = op.filter(Utils.toBufferedImage(temp_image), null);
        image = temp_image.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
        System.out.println("IMAGE:" + image);
    }
}
