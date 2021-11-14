public class Ping implements Runnable {
    Networks tcp;

    public Ping(Networks tcp) {
        this.tcp = tcp;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("RUNNN");
            String m = tcp.getMsg();
            System.out.println("----JO---- " + m);
            tcp.sendMsg("T");
        }
    }
}
