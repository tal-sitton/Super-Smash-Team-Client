/**
 * a class that respond to any ping packet the server sends
 */
public class Ping implements Runnable {
    Networks tcp;

    public Ping(Networks tcp) {
        this.tcp = tcp;
    }

    /**
     * Gets a ping msg from the server and sends another msg to it.
     */
    @Override
    public void run() {
        while (true) {
            tcp.getMsg();
            tcp.sendMsg("T");
        }
    }
}
