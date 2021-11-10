import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Networks {
    private Socket socket;
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 2212;
    private static Networks instance;
    private InputStreamReader reader;

    /**
     * creates a socket and connects it to the server.
     */
    public Networks() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            reader = new InputStreamReader(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Networks getInstance() {
        if (instance == null)
            instance = new Networks();
        return instance;
    }

    /**
     * receiving a message from the server
     *
     * @return - the message that was received
     */
    public String getMsg() {
        try {
            char n1 = (char) reader.read();
            char n2 = (char) reader.read();
            int num = Integer.parseInt("" + n1 + n2);
            String m = read(reader, num);

//            System.out.println("do it again!");
//            num = Integer.parseInt("" + ((char) reader.read()) + (char) reader.read());
//            m = read(reader, num);
//            System.out.println("num:" + num);
//            System.out.println("m " + m);

            return (m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String read(InputStreamReader reader, int bytes) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes; i++) {
            sb.append((char) reader.read());
        }
        return sb.toString();
    }

    /**
     * sends a message to the server
     *
     * @param msg - the message the client wants to send to the server
     */
    public void sendMsg(String msg) {
        try {
            OutputStream output = socket.getOutputStream();
            String l = msg.length() < 10 ? "0" + msg.length() : String.valueOf(msg.length());
            output.write((l + msg).getBytes(StandardCharsets.UTF_8));
//            System.out.println("sent: " + l + msg);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnects the client from the server
     */
    public void disconnect() {
        try {
            sendMsg("");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
