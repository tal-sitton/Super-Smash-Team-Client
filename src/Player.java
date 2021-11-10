import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Player extends Actor implements Runnable {

    private volatile String msgToSend;
    private volatile Set<Integer> active_keys = new HashSet<>();
    private final Map<Integer, String> msgs = new HashMap<>();

    public Player(Sprite mySprite, String name, boolean wasd) {
        super(mySprite, name);
        if (!wasd) {
            msgs.put(KeyEvent.VK_LEFT, Constants.MOVE_LEFT_COMMAND);
            msgs.put(KeyEvent.VK_RIGHT, Constants.MOVE_RIGHT_COMMAND);
            msgs.put(KeyEvent.VK_UP, Action.Jump.toString());
            msgs.put(KeyEvent.VK_Q, Constants.A_COMMAND);
            msgs.put(KeyEvent.VK_DOWN, "*");
        } else {
            msgs.put(KeyEvent.VK_A, Constants.MOVE_LEFT_COMMAND);
            msgs.put(KeyEvent.VK_D, Constants.MOVE_RIGHT_COMMAND);
            msgs.put(KeyEvent.VK_W, Action.Jump.toString());
            msgs.put(KeyEvent.VK_Q, Constants.A_COMMAND);
            msgs.put(KeyEvent.VK_S, "*");
        }
    }

    /**
     * move the player when key is pressed
     */

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        if (msgs.containsKey(key))
            active_keys.add(key);
    }

    /**
     * stop moving when the key is released
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        active_keys.remove(key);
    }

    @Override
    public void run() {
        System.out.println("player thread running!");
        while (true) {
            if (!active_keys.isEmpty()) {
                for (int key : active_keys) {
                    String msg = msgs.get(key);
                    Networks.getInstance().sendMsg(msg);
                }
            } else
                Networks.getInstance().sendMsg(Constants.RELEASED_COMMAND);

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
