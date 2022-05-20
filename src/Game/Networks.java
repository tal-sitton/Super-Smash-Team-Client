package Game;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * an enum that represents the two type of sockets
 */
enum SocketType {
    TCP, UDP, Miner
}

/**
 * a class that manage everything related to networking. <br>
 * there is a singleton for udp and a singleton for tcp
 */
public class Networks {
    public final SocketType type; // the type of the socket

    private static Networks udpInstance; //the instance of the udp
    private static Networks tcpInstance; //the instance of the tcp
    private static Networks tcpMinerInstance; //the instance of the tcp

    private static DatagramSocket UDP_SOCKET; //the udp socket
    private static Socket TCP_SOCKET; //the tcp socket
    private static Socket TCP_MINER_SOCKET; //the tcp socket

    private final String SERVER_IP = "192.168.173.18"; //the server's ip
    private final int SERVER_TCP_PORT = 2212; //the server's tcp port
    private final int SERVER_TCP_MINER_PORT = 2231; //the server's tcp's miner port
    private static int SERVER_UDP_PORT; //the server's udp port
    private int UDP_CLIENT_PORT = 2214; //the client's udp port
    private InetAddress SERVER_IP_INET; // an InetAddress object that represents the server IP

    private InputStreamReader reader; // the inputStreamReader for reading from the tcp socket
    private final int BUFFER_SIZE = 1024; //the Buffer size for reading from the udp socket

    /**
     * creates a socket according to the need <br>
     * if the udp port is already taken, it changes the port
     *
     * @param type the type of the socket
     */
    public Networks(SocketType type) throws IOException {
        this.type = type;
        try {
            if (type == SocketType.TCP) {
                TCP_SOCKET = new Socket(SERVER_IP, SERVER_TCP_PORT);
                reader = new InputStreamReader(TCP_SOCKET.getInputStream());
                return;
            }
            if (type == SocketType.Miner) {
                TCP_MINER_SOCKET = new Socket(SERVER_IP, SERVER_TCP_MINER_PORT);
                reader = new InputStreamReader(TCP_MINER_SOCKET.getInputStream());
                return;
            }
            SERVER_IP_INET = InetAddress.getByName(SERVER_IP);
            UDP_SOCKET = new DatagramSocket(UDP_CLIENT_PORT);
            UDP_SOCKET.setSoTimeout(500);

        } catch (SocketException e1) {

            if (e1 instanceof BindException) {
                for (int i = 0; i < 10; i++) {
                    try {
                        UDP_CLIENT_PORT += 1;
                        UDP_SOCKET = new DatagramSocket(UDP_CLIENT_PORT);
                        break;
                    } catch (SocketException e2) {
                        if (!(e2 instanceof BindException)) {
                            throw e2;
                        }
                    }
                }
            } else
                throw e1;
        }
    }

    /**
     * @param type the type of the socket that is wanted
     * @return the instance of the needed socket, created one if it's the first time
     */
    public static Networks getInstance(SocketType type) throws IOException {
        if (type == SocketType.TCP) {
            if (tcpInstance == null)
                tcpInstance = new Networks(SocketType.TCP);
            return tcpInstance;
        }
        if (type == SocketType.Miner) {
            if (tcpMinerInstance == null)
                tcpMinerInstance = new Networks(SocketType.Miner);
            return tcpMinerInstance;
        }
        if (udpInstance == null)
            udpInstance = new Networks(SocketType.UDP);
        return udpInstance;
    }

    /**
     * @return the port of the socket
     */
    public int getPort() {
        if (type == SocketType.TCP)
            return TCP_SOCKET.getPort();
        return UDP_CLIENT_PORT;
    }

    /**
     * @return the ip of the socket
     */
    public String getIP() {
        if (type == SocketType.TCP)
            return TCP_SOCKET.getLocalAddress().getHostAddress();
        return UDP_SOCKET.getLocalAddress().getHostAddress();
    }

    public void startedPinging() {
        try {
            TCP_SOCKET.setSoTimeout(100);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return - the message that was received from the server
     */
    public String getMsg() throws IOException {
        if (type == SocketType.TCP || type == SocketType.Miner) {
            return readTCP();
        }
        return readUDP();
    }

    /**
     * gets the message from the tcp inputStream
     *
     * @return the message from the inputStream
     * @throws IOException If an I/O error occurs when reading from the inputStream
     */
    private String readTCP() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) reader.read());
        sb.append((char) reader.read());
        while (!sb.substring(sb.length() - 1, sb.length()).equals(";")) {
            sb.append((char) reader.read());
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * reads from the udp socket a {@link #BUFFER_SIZE} amount of bytes
     *
     * @return the message from the udp socket
     * @throws IOException If an I/O error occurs when reading from the socket
     */
    private String readUDP() throws IOException {
        byte[] receive = new byte[BUFFER_SIZE];
        DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);
        System.out.println("starts receiving");
        UDP_SOCKET.receive(DpReceive);
        System.out.println("done receiving");
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (i < receive.length && receive[i] != 0) {
            ret.append((char) receive[i]);
            i++;
        }
        return ret.toString();
    }

    public void sendMsg(byte[] msg) {
        if (type == SocketType.TCP || type == SocketType.Miner) {
            Socket socket = type == SocketType.TCP ? TCP_SOCKET : TCP_MINER_SOCKET;
            try {
                OutputStream output = socket.getOutputStream();
                output.write(msg);
                output.flush();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        DatagramPacket toSend = new DatagramPacket(msg, msg.length, SERVER_IP_INET, SERVER_UDP_PORT);
        try {
            UDP_SOCKET.send(toSend);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * sends a message to the server
     *
     * @param msg - the message the client wants to send to the server
     */
    public void sendMsg(String msg) {
        sendMsg(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static void setServerUdpPort(int port) {
        SERVER_UDP_PORT = port;
    }

    /**
     * disconnects the client from the server
     */
    public void closeAll() {
        try {
            UDP_SOCKET.close();
            TCP_SOCKET.close();
            udpInstance = null;
            tcpInstance = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
