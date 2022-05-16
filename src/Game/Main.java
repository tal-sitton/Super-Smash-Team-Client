package Game;

import javax.swing.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static Game.Miner.*;

public class Main extends JFrame {

    public Main(String name, boolean wasd) {
        initUI(name, wasd);
    }

    private void initUI(String name, boolean wasd) {
        Board board = Board.getInstance(name);
        board.setLayout(null);
        add(board);

        setTitle("Game " + name + " " + wasd);
        setSize(Constants.SCREEN_SIZE);

        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("NICE");

    }


    public static void main(String[] args) {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {
            }
        }));
        createMiner();
        Main ms = new Main("GAME1", false);
        ms.setVisible(true);
    }
}
