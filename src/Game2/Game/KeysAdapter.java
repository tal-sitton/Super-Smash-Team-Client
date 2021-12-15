package Game2.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * the Key Adapter of the program <br>
 * uses the {@link Player#keyPressed(KeyEvent)} and {@link Player#keyReleased(KeyEvent)} (KeyEvent)} instead of the defaults
 *
 * @see Player
 */
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
