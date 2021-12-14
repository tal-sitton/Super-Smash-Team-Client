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
    TCP, UDP
}

/**
 * a class that manage everything related to networking. <br>
 * there is a singleton for udp and a singleton for tcp
 */
public class Networks {
    private final SocketType type; // the type of the socket

    private static Networks udpInstance; //the instance of the udp
    private static Networks tcpInstance; //the instance of the tcp

    private static DatagramSocket UDP_SOCKET; //the udp socket
    private static Socket TCP_SOCKET; //the tcp socket

//    private final String SERVER_IP = "0:0:0:0:0:0:0:1"; //the server's ip
    private final String SERVER_IP = "fe80:0:0:0:c802:222f:2a01:44c6"; //the server's ip
    private final int SERVER_TCP_PORT = 2212; //the server's tcp port
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
            SERVER_IP_INET = InetAddress.getByName(SERVER_IP);
            UDP_SOCKET = new DatagramSocket(UDP_CLIENT_PORT);
            UDP_SOCKET.setSoTimeout(100);

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
    public String getMsg() throws SocketTimeoutException, SocketException {
        System.out.println("GET MSG: " + type);
        if (type == SocketType.TCP) {
            try {
                return readTCP();
            } catch (Exception e) {
                if (e instanceof SocketTimeoutException)
                    throw (SocketTimeoutException) e;
            }
            return null;
        }
        try {
            return readUDP();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException)
                throw (SocketTimeoutException) e;
            else if (e instanceof SocketException)
                throw (SocketException) e;
            e.printStackTrace();
        }
        return null;
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
        while (!String.valueOf(sb.charAt(sb.length() - 1)).equals(";")) {
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
        UDP_SOCKET.receive(DpReceive);
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (i < receive.length && receive[i] != 0) {
            ret.append((char) receive[i]);
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
            UDP_SOCKET.send(toSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
