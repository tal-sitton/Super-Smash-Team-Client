package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a class that represents the game and everything in it
 */
public class Board extends JPanel implements ActionListener {

    private static Board instance = null;
    private final int DELAY = 1;
    private final String PLAYER_NAME;
    private Timer timer;
    private final Ping pinger;
    private final Player player;
    private final List<Enemy> enemyList = new ArrayList<>();
    private final Networks udp;
    private final Networks tcp;
    private int p_number;
    private final Font font;
    private List<JLabel> percentagesLabels = new ArrayList<>();
    private boolean initDrawing = false;
    private boolean gameStarted = false;
    private boolean showedStart = false;
    private static GUIActions nextGUIAction = GUIActions.NOTHING;
    private static String winner;
    private boolean running = true;
    private final Object[] optionsForMessage;

    /**
     * Creates a new Game Game.Board (aka Panel) with everything in it: e.g. the players, the HUD, and managing everything
     *
     * @param pName the player's name
     * @param wasd  whether the user wants to play with wasd or the arrows
     */
    public Board(String pName, boolean wasd) throws IOException {
        font = createFont();
        PLAYER_NAME = pName;
        optionsForMessage = new Object[]{"Return to Matchmaking",
                "Quit"};
        tcp = Networks.getInstance(SocketType.TCP);
        int serverUdpPort = Integer.parseInt(tcp.getMsg());
        Networks.setServerUdpPort(serverUdpPort);
        udp = Networks.getInstance(SocketType.UDP);
        player = new Player(Constants.SPI, PLAYER_NAME, wasd);
        tcp.sendMsg(player.getSprite().getName() + "," + PLAYER_NAME + "," + tcp.getIP() + "," + udp.getPort());
        System.out.println("setup pinger");
        pinger = new Ping(tcp);
        Thread th = new Thread(pinger);
        th.start();
        System.out.println("started pinger");
        initBoard();
        //@Todo here we can do things before game starts. like wait screens
    }

    public static Board getInstance(String pName, boolean wasd) {
        if (instance == null) {
            try {
                instance = new Board(pName, wasd);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return instance;
    }

    public static Board getInstance() {
        return instance;
    }

    private String[] startInfo;

    public void createBoard1(String msg) {
        startInfo = msg.split(","); //[p_number,max_index]
        p_number = Integer.parseInt(startInfo[0]);
    }

    public void createBoard2(String msg) {
        String[] enemyInfo = msg.split(",,,"); //[e1_character&e1_name , e2_character&e2_name]

        System.out.println("enemyInfo 0: " + enemyInfo[0]);
        System.out.println("info 0: " + startInfo[1]);

        for (int i = 0; i < Integer.parseInt(startInfo[1]) - 1; i++) {
            String sprite = enemyInfo[i].split("&&&")[0];
            String name = enemyInfo[i].split("&&&")[1].replace(",", "");
            enemyList.add(new Enemy(Utils.SpriteNameToSprite(sprite), name));
        }
        Thread playerThread = new Thread(player);
        playerThread.start();
        gameStarted = true;
        repaint();
    }

    /**
     * @return the default font the game uses
     */
    private static Font createFont() {
        Font f = new Font("ROBOTO", Font.PLAIN, 20);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, 0.05);
        return f.deriveFont(attributes);
    }

    /**
     * initialize the board: adds the keyListener, and the background
     */
    private void initBoard() {
        addKeyListener(new KeysAdapter(player));
        setBackground(Color.black);
        setFocusable(true);

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameStarted)
            doDrawing(g);
        else if (!showedStart) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(ScreensAndMaps.Start.image, 0, 0, this);
            showedStart = true;
        }
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * the real {@link #paintComponent(Graphics)} method. <br>
     * Draws the player and the enemy in the GUI
     *
     * @param g the Graphics Object
     */
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (!initDrawing) {
            removeAll();
            initPlayerData();
            initDrawing = true;
        }

        g2d.drawImage(ScreensAndMaps.BattleField.image, 0, 0, this);

        if (player.isAlive || !player.hasFinishDeathAnim()) {
            g2d.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        enemyList.forEach(enemy -> {
            if (enemy.isAlive || !enemy.hasFinishDeathAnim()) {
                g2d.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
        });

        drawBasicData(g2d);
    }

    /**
     * Draws the HUD of the game (e.g. the rectangles at the bottom of the screen that shows the name and the percentage of each player)
     *
     * @param g2d the Graphics Object
     */
    private void drawBasicData(Graphics2D g2d) {
        g2d.setColor(player.getSprite().getColor());
        g2d.fillRoundRect(Constants.getRecPlace(0).getX(), Constants.getRecPlace(0).getY(), Constants.REC_SIZE.width + 1, Constants.REC_SIZE.height + 1, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(Constants.getRecPlace(0).getX(), Constants.getRecPlace(0).getY(), Constants.REC_SIZE.width, Constants.REC_SIZE.height, 10, 10);
        percentagesLabels.get(0).setText(player.getPercentage());

        for (int i = 1; i <= enemyList.size(); i++) {
            g2d.setColor(enemyList.get(i - 1).getSprite().getColor());
            g2d.fillRoundRect(Constants.getRecPlace(i).getX(), Constants.getRecPlace(i).getY(), Constants.REC_SIZE.width + 1, Constants.REC_SIZE.height + 1, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(Constants.getRecPlace(i).getX(), Constants.getRecPlace(i).getY(), Constants.REC_SIZE.width, Constants.REC_SIZE.height, 10, 10);
            System.out.println("IIIIIII" + i);
            percentagesLabels.get(i).setText(enemyList.get(i - 1).getPercentage());

        }
    }

    /**
     * initialize the HUD for each player (e.g. creating the labels, the rectangles etc.)
     */
    private void initPlayerData() {
        System.out.println("DRAWING");
        createJLabel(PLAYER_NAME, 0, true);
        percentagesLabels.add(createJLabel(player.getPercentage(), 0, false));

        for (int i = 1; i <= enemyList.size(); i++) {
            percentagesLabels.add(createJLabel(enemyList.get(i - 1).getPercentage(), i, false));
            createJLabel(enemyList.get(i - 1).name, i, true);
        }
        System.out.println("MMMM" + percentagesLabels.size());
    }

    /**
     * creates a basic JLabel
     *
     * @param txt         the text to be placed inside the JLabel
     * @param actorIndex  the index of the actor
     * @param isNameLabel whether the label should represent a name of a player
     * @return the JLabel that has been created
     */
    private JLabel createJLabel(String txt, int actorIndex, boolean isNameLabel) {
        JLabel label = new JLabel();
        label.setText(txt);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        label.setSize(new Dimension(150, 150));
        if (isNameLabel)
            label.setLocation(Constants.namePlace(actorIndex, PLAYER_NAME).getX(), Constants.namePlace(actorIndex, PLAYER_NAME).getY());
        else {
            label.setLocation(Constants.PercentagePlace(actorIndex, txt).getX(), Constants.PercentagePlace(actorIndex, txt).getY());
        }
        this.add(label);
        return label;
    }

    private boolean yesNoDialog(String title, String txt) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int result = JOptionPane.showOptionDialog(null, txt, title, dialogButton, JOptionPane.INFORMATION_MESSAGE
                , null, optionsForMessage, null);
        return result == JOptionPane.YES_OPTION;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && gameStarted)
            step();
    }

    /**
     * Updates everything in the gui (the player, the enemy, the HUD etc.) from the data that was given from the server
     */
    private void step() {
        if (nextGUIAction == GUIActions.SHOWED_DIALOG)
            return;
        if (nextGUIAction == GUIActions.YOU_WON || nextGUIAction == GUIActions.PLAYER_DISCONNECTED || nextGUIAction == GUIActions.ENEMY_WON) {
            String guiMsg = nextGUIAction == GUIActions.ENEMY_WON ? winner + nextGUIAction.msg : nextGUIAction.msg;
            String guiTitle = nextGUIAction.title;
            nextGUIAction = GUIActions.SHOWED_DIALOG;
            System.out.println("Game Ended");
            System.out.println("TITLE:" + guiTitle);
            System.out.println("MSG:" + guiMsg);
            boolean result = yesNoDialog(guiTitle, guiMsg);
            System.out.println(result);
            stop();
            return;
        }
        System.out.println("STEP");
        String msg = "";
        try {
            msg = udp.getMsg();
        } catch (IOException e) {
            System.out.println("somethings bad: " + e);
            msg = "";
        }
        if (msg == null) {
            System.out.println("something wrong!");
            System.exit(1);
        }
        if (!msg.equals("")) {
            System.out.println("msg: " + msg);
            String[] characterInfo = msg.split("&");
            if (player.isAlive)
                player.move(characterInfo[p_number]);
            else if (!player.hasFinishDeathAnim())
                player.move(winner + nextGUIAction.msg);

            int index = 0;
            for (Enemy enemy : enemyList) {
                if (index == p_number)
                    index++;
                if (enemy.isAlive)
                    enemy.move(characterInfo[index]);
                else if (!enemy.hasFinishDeathAnim())
                    enemy.move("");
                index++;
            }
        }
        if (player.isAlive || !player.clearedAfterDeathAnim) {
            repaint(player.getX() - 50, player.getY() - 150,
                    player.getWidth() + 100, player.getHeight() + 200);
            if (player.hasFinishDeathAnim())
                player.clearedAfterDeathAnim = true;
        }

        enemyList.forEach(enemy -> {
            if (enemy.isAlive || !enemy.clearedAfterDeathAnim) {
                repaint(enemy.getX() - 50, enemy.getY() - 150,
                        enemy.getWidth() + 100, enemy.getHeight() + 200);
            }
            if (enemy.hasFinishDeathAnim())
                enemy.clearedAfterDeathAnim = true;
        });
    }

    public void stop() {
        System.out.println("STOPPED");
        timer.stop();
        player.stop();
        pinger.stop();
        udp.closeAll();
    }

    public void playerWon(String data) {
        System.out.println("CALLED");
        if (data.equals("F")) {
            nextGUIAction = GUIActions.PLAYER_DISCONNECTED;
        } else {
            data = data.replace("W", "");
            int newData = Integer.parseInt(data);

            if (newData == p_number)
                nextGUIAction = GUIActions.YOU_WON;
            else {
                if (newData > p_number)
                    newData--;
                nextGUIAction = GUIActions.ENEMY_WON;
                winner = enemyList.get(newData).name;
                System.out.println("WINNER IS " + winner);
                System.out.println("WINNER NUMBER IS " + newData);
            }
        }
        System.out.println("nextGUIAction:" + nextGUIAction);
        step();
    }
}
