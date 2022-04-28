package Game;

import javax.swing.*;

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
        Main ms = new Main("GAME1", false);
        ms.setVisible(true);
    }
}
