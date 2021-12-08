package Game;

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
                System.out.println("m");
            }
            if (msg != null) {
                if (msg.equals("T"))
                    tcp.sendMsg("T");
                else if (msg.startsWith("W")) {
                    System.out.println("OMG THE WINNER IS " + msg.replace("W", ""));
                    try {
                        Board.getInstance().playerWon(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.startsWith("F"))
                    System.out.println("A player was disconnected, press F to pay respect");
            }
        }
    }

    public void stop() {
        needsToStop = true;
    }
}
