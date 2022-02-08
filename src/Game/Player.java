package Game;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * a class that represents an {@link Actor} that the user <b>can</b> move
 *
 * @see Actor
 */
public class Player extends Actor implements Runnable {

    private volatile Set<Integer> active_keys = new HashSet<>();
    private final Map<Integer, String> msgs = new HashMap<>();
    private boolean needsToStop = false;

    /**
     * @param mySprite the sprite of the player
     * @param name     the name of the player
     */
    public Player(Sprite mySprite, String name) {
        super(mySprite, name);
        msgs.put(KeyEvent.VK_LEFT, Constants.MOVE_LEFT_COMMAND);
        msgs.put(KeyEvent.VK_RIGHT, Constants.MOVE_RIGHT_COMMAND);
        msgs.put(KeyEvent.VK_UP, Action.Jump.toString());
        msgs.put(KeyEvent.VK_Q, Constants.A_COMMAND);
        msgs.put(KeyEvent.VK_DOWN, Constants.DOWN_COMMAND);
        msgs.put(KeyEvent.VK_R, "?");
    }

    /**
     * adds the key pressed to the list
     */

    public void keyPressed(KeyEvent e) {
        if (super.isAlive) {
            System.out.println("KEYYYYY");
            int key = e.getKeyCode();
            if (msgs.containsKey(key))
                active_keys.add(key);
            else if (key == KeyEvent.VK_ENTER) {
                Board.getInstance().wantLogin = true;
                System.out.println("mmmm");
            }
        }
    }

    /**
     * removes the key pressed from the list when it was released
     */
    public void keyReleased(KeyEvent e) {
        if (super.isAlive) {
            int key = e.getKeyCode();
            active_keys.remove(key);
        }
    }

    /**
     * updates the server what keys the player has pressed
     */
    @Override
    public void run() {
        System.out.println("player thread running!");
        while (super.isAlive && !needsToStop) {
            try {
                if (!active_keys.isEmpty()) {
                    for (int key : active_keys) {
                        String msg = msgs.get(key);
                        Networks.getInstance(SocketType.UDP).sendMsg(msg);
                    }
                } else
                    Networks.getInstance(SocketType.UDP).sendMsg(Constants.RELEASED_COMMAND);

                TimeUnit.MILLISECONDS.sleep(50);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        needsToStop = true;
    }
}
