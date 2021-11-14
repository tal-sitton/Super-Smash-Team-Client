import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

enum SocketType {
    TCP, UDP
}

public class Networks {
    private final SocketType type;
    private static DatagramSocket UDP_SOCKET;
    private static Socket TCP_SOCKET;
    private final String SERVER_IP = "127.0.0.1";
    private InetAddress SERVER_IP_INET;
    private final int SERVER_TCP_PORT = 2212;
    private final int SERVER_UDP_PORT = 2213;
    private final int UDP_CLIENT_PORT = 2214;
    private static Networks udpInstance;
    private static Networks tcpInstance;
    private InputStreamReader reader;
    private final int BUFFER_SIZE = 1024;

    /**
     * creates a socket and connects it to the server.
     */
    public Networks(SocketType type) {
        this.type = type;
        try {
            if (type == SocketType.TCP) {
                TCP_SOCKET = new Socket(SERVER_IP, SERVER_TCP_PORT);
                reader = new InputStreamReader(TCP_SOCKET.getInputStream());
                return;
            }
            SERVER_IP_INET = InetAddress.getByName(SERVER_IP);
            UDP_SOCKET = new DatagramSocket(UDP_CLIENT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Networks getInstance(SocketType type) {
        if (type == SocketType.TCP) {
            if (tcpInstance == null)
                tcpInstance = new Networks(SocketType.TCP);
            return tcpInstance;
        }
        if (udpInstance == null)
            udpInstance = new Networks(SocketType.UDP);
        return udpInstance;
    }

    public int getPort() {
        if (type == SocketType.TCP)
            return TCP_SOCKET.getPort();
        return UDP_CLIENT_PORT;
    }

    public String getIP() {
        if (type == SocketType.TCP)
            return TCP_SOCKET.getLocalAddress().getHostAddress();
        return UDP_SOCKET.getLocalAddress().getHostAddress();
    }

    /**
     * receiving a message from the server
     *
     * @return - the message that was received
     */
    public String getMsg() {
        if (type == SocketType.TCP) {
            try {
                String m = readTCP(reader);
                return m;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            byte[] receive = new byte[BUFFER_SIZE];
            DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);
            UDP_SOCKET.receive(DpReceive);
            return readUDP(receive);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readTCP(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) reader.read());
        while (!String.valueOf(sb.charAt(sb.length() - 1)).equals(";")) {
            sb.append((char) reader.read());
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private String readUDP(byte[] arr) {
        if (arr == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (i < arr.length && arr[i] != 0) {
            ret.append((char) arr[i]);
            i++;
        }
        return ret.toString();
    }

    /**
     * sends a message to the server
     *
     * @param msg - the message the client wants to send to the server
     */
    public void sendMsg(String msg) {
        if (type == SocketType.TCP) {
            try {
                OutputStream output = TCP_SOCKET.getOutputStream();
                output.write(msg.getBytes(StandardCharsets.UTF_8));
                output.flush();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            byte[] buf = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket toSend = new DatagramPacket(buf, buf.length, SERVER_IP_INET, SERVER_UDP_PORT);
//            System.out.println(SERVER_IP_INET);
//            System.out.println(SERVER_UDP_PORT);
            UDP_SOCKET.send(toSend);
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
            UDP_SOCKET.close();
            TCP_SOCKET.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
