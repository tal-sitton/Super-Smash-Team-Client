package Game;

import java.util.concurrent.TimeUnit;

/**
 * a class that respond to any ping packet the server sends
 */
public class Ping implements Runnable {
    private Networks tcp;
    private boolean needsToStop = false;

    public Ping(Networks tcp) {
        this.tcp = tcp;
    }

    /**
     * Gets a ping msg from the server and sends another msg to it.
     */
    @Override
    public void run() {
        tcp.startedPinging();
        while (!needsToStop) {
            String msg = null;
            try {
                msg = tcp.getMsg();
            } catch (Exception e) {
                System.out.println("E: " + e);
            }
            if (msg != null && msg.length() > 0) {
                switch (msg.charAt(0)) {
                    case 'T':
                        tcp.sendMsg("T"); //regular ping message
                        break;
                    case 'I':
                        System.out.println("FIRST MESSAGE!");
                        System.out.println(msg);
                        System.out.println(msg.substring(1));
                        Board.getInstance().createBoard1(msg.substring(1));
                        break;
                    case 'S':
                        System.out.println("SECOND MESSAGE");
                        System.out.println(msg);
                        System.out.println(msg.substring(1));
                        Board.getInstance().createBoard2(msg.substring(1));
                        break;
                    case 'W':
                        System.out.println("OMG THE WINNER IS " + msg.replace("W", ""));
                        Board.getInstance().playerWon(msg);
                        break;
                    case 'F':
                        System.out.println("A player was disconnected, press F to pay respect");
                        Board.getInstance().playerWon("F");
                        break;
                    default:
                        System.out.println("NONE OF ABOVE: " + msg);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        needsToStop = true;
        tcp.sendMsg("F");
    }
}
