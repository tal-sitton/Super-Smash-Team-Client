package Game;

import javax.swing.*;

public class Main extends JFrame {

    public Main(String name, boolean wasd) {

        initUI(name, wasd);
    }

    private void initUI(String name, boolean wasd) {
        JPanel panel = Board.getInstance(name);
        panel.setLayout(null);
        add(panel);

        setTitle("Game " + name + " " + wasd);
        setSize(Constants.SCREEN_SIZE);

        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        Main ms = new Main("GAME1", false);
        ms.setVisible(true);
    }
}
