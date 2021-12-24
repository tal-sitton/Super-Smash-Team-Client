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
//        try {
//            System.out.println("START");
//            Thread.sleep(5000);
//            System.out.println("END SLEEP");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
                        System.out.println("PING");
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
//            if (msg != null) {
//                if (msg.equals("T"))
//                    tcp.sendMsg("T");
//                else if (msg.startsWith("W")) {
//                    System.out.println("OMG THE WINNER IS " + msg.replace("W", ""));
//                    Board.getInstance().playerWon(msg);
//                } else if (msg.startsWith("F")) {
//                    System.out.println("A player was disconnected, press F to pay respect");
//                    Board.getInstance().playerWon("F");
//                }
            }
        }
    }

    public void stop() {
        needsToStop = true;
    }
}
