package Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * the Key Adapter of the program <br>
 * uses the {@link Player#keyPressed(KeyEvent)} and {@link Player#keyReleased(KeyEvent)} (KeyEvent)} instead of the defaults
 *
 * @see Player
 */
public class KeysAdapter extends KeyAdapter {
    private Player player;

    private static KeysAdapter instance = null;

    private KeysAdapter() {
    }

    public static KeysAdapter getInstance() {
        if (instance == null)
            instance = new KeysAdapter();
        return instance;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Board.getInstance().wantLogin = true;
        else
            player.keyPressed(e);
    }
}
