import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeysAdapter extends KeyAdapter {
    Player player;

    public KeysAdapter(Player player) {
        this.player = player;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }
}
