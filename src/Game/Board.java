package Game;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

import static Game.Constants.*;
import static Game.Utils.constraints;

/**
 * a class that represents the game and everything in it
 */
public class Board extends JPanel implements ActionListener {

    private static Board instance = null;
    private final int DELAY = 1;
    private final String PLAYER_NAME;
    private Timer timer;
    private final Ping pinger;
    private Player player;
    private final List<Enemy> enemyList = new ArrayList<>();
    private final Networks udp;
    private final Networks tcp;
    private int p_number;
    private final Font font;
    private final List<JLabel> percentagesLabels = new ArrayList<>();
    private boolean initDrawing = false;
    private boolean gameStarted = false;
    private boolean showedStart = false;
    private boolean showedWait = false;
    public boolean wantLogin = false;
    public boolean loggedIn = false;
    private static GUIActions nextGUIAction = GUIActions.NOTHING;
    private static String winner;
    private boolean running = true;
    private final String[] optionsForMessage;

    /**
     * Creates a new Game.Board (aka Panel) with everything in it: e.g. the players, the HUD, and managing everything
     *
     * @param pName the player's name
     */
    public Board(String pName) throws IOException {
        font = createFont();
        PLAYER_NAME = pName;
        optionsForMessage = new String[]{"Quit"};
        tcp = Networks.getInstance(SocketType.TCP);
        int serverUdpPort = Integer.parseInt(tcp.getMsg());
        Networks.setServerUdpPort(serverUdpPort);
        udp = Networks.getInstance(SocketType.UDP);
        tcp.sendMsg(tcp.getIP() + "," + udp.getPort());
        System.out.println("setup pinger");
        pinger = new Ping(tcp);
        System.out.println("started pinger");
        initBoard();
    }

    public static Board getInstance(String pName) {
        if (instance == null) {
            try {
                instance = new Board(pName);
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
        addKeyListener(KeysAdapter.getInstance());
//        setBackground(Color.black);
        setFocusable(true);

        timer = new Timer(DELAY, this);
        timer.start();
        System.out.println("Init");
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        System.out.println("LLLL");
        super.paintComponent(g);
        if (gameStarted)
            doDrawing(g2d);
        else if (!showedStart) {
            g2d.drawImage(ScreensAndMaps.Start.image, 0, 0, this);
            showedStart = true;
        } else if (loggedIn && !showedWait) {
            g2d.drawImage(ScreensAndMaps.Wait.image, 0, 0, this);
            showedWait = true;
        } else if (!loggedIn)
            g2d.drawImage(ScreensAndMaps.Start.image, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * the real {@link #paintComponent(Graphics)} method. <br>
     * Draws the player and the enemy in the GUI
     *
     * @param g2d the Graphics Object
     */
    private void doDrawing(Graphics2D g2d) {
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
        createActorJLabel(player.name, 0, true);
        percentagesLabels.add(createActorJLabel(player.getPercentage(), 0, false));

        for (int i = 1; i <= enemyList.size(); i++) {
            percentagesLabels.add(createActorJLabel(enemyList.get(i - 1).getPercentage(), i, false));
            createActorJLabel(enemyList.get(i - 1).name, i, true);
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
    private JLabel createActorJLabel(String txt, int actorIndex, boolean isNameLabel) {
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

    private void yesNoDialog(String title, String txt) {
        int dialogButton = JOptionPane.DEFAULT_OPTION;
        JOptionPane.showOptionDialog(null, txt, title, dialogButton, JOptionPane.INFORMATION_MESSAGE
                , null, optionsForMessage, null);
    }


    private String[] loginDialog(String msg) {
        spriteIndex = 0;
        System.out.println("right here");
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label1 = new JLabel("Username");
        JTextField field1 = new JTextField("");
        field1.setPreferredSize(Constants.DEFAULT_FIELD_DIMENSION);
        JLabel label2 = new JLabel("Password");
        JPasswordField field2 = new JPasswordField("");
        field2.setPreferredSize(Constants.DEFAULT_FIELD_DIMENSION);

        separator.setPreferredSize(new Dimension(50, 10));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 5, 5);

        panel.add(label1, constraints(0, 0));
        panel.add(field1, constraints(2, 0));
        panel.add(label2, constraints(0, 1));
        panel.add(field2, constraints(2, 1));
        panel.add(separator, constraints(0, 2));

        gbc.gridwidth = 3;
        panel.add(new JLabel("<html> <b><font color='red'>" + msg + "</font></b></html>"), constraints(0, 3));

        createCharacterWheel(panel);

        String[] options = new String[]{"SignUp", "Submit"};
        int action = JOptionPane.showOptionDialog(null, panel, "hello", JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, null);

        System.out.println(field1.getText());
        System.out.println(field2.getPassword());
        System.out.println(SPRITES[spriteIndex].getName());

        String actionMsg = action == JOptionPane.YES_OPTION ? options[0] : options[1];
        return new String[]{field1.getText(), Arrays.toString(field2.getPassword()), actionMsg};
    }

    private static int spriteIndex = 0;

    private static void createCharacterWheel(JPanel panel) {
        System.out.println("OVER HERE");
        separator.setPreferredSize(new Dimension(1, 25));
        panel.add(separator, constraints(2, 4));

        String spriteName = SPRITES[spriteIndex].getName();
        System.out.println(spriteName);
        JLabel charName = new JLabel(spriteName);
        panel.add(charName, constraints(0, 5));

        gbc.gridheight = 5;
        gbc.gridwidth = 3;
        JLabel picture = new JLabel();
        int height = 100;
        panel.add(picture, constraints(0, 6));

        ActionListener ac = e -> {
            if (e.getActionCommand().equals("1")) {
                if (spriteIndex + 1 >= SPRITES.length)
                    spriteIndex = 0;
                else
                    spriteIndex += 1;
            } else {
                if (spriteIndex - 1 < 0)
                    spriteIndex = SPRITES.length - 1;
                else
                    spriteIndex -= 1;
            }
            charName.setText(SPRITES[spriteIndex].getName());
            System.out.println(Constants.res + "\\" + SPRITES[spriteIndex].getName() + ".png");
            picture.setIcon(new ImageIcon(new ImageIcon(Constants.res + "\\" + SPRITES[spriteIndex].getName() + ".png").getImage().getScaledInstance(height / 31 * 24, height, Image.SCALE_DEFAULT)));
        };

        gbc.gridwidth = 1;
        JButton right = blankButton("           >");
        right.setActionCommand("1");
        right.addActionListener(ac);
        panel.add(right, constraints(2, 6));
        right.doClick();

        JButton left = blankButton("<");
        left.setActionCommand("-1");
        left.addActionListener(ac);
        panel.add(left, constraints(0, 6));
        left.doClick();
    }

    public static JButton blankButton(String txt) {
        JButton button = new JButton(txt);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Roboto", Font.BOLD, 20));
        return button;
    }

    private void logIn() {
        String msg = "";
        String username = "";
        while (!loggedIn) {
            String[] info = loginDialog(msg);
            username = info[0];
            String password = info[1];
            String action = info[2];
            if (username.equals("") || password.equals("[]"))
                continue;
            byte[] passHash = "".getBytes();
            try {
                passHash = MessageDigest.getInstance("SHA-256").digest(password.getBytes());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                loggedIn = tryLogin(action, username, passHash);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg = "WRONG PASSWORD/USERNAME";
        }

        player = new Player(SPRITES[spriteIndex], username);
        KeysAdapter.getInstance().setPlayer(player);
        try {
            System.out.println("sending the server the player's sprite name");
            Networks.getInstance(SocketType.TCP).sendMsg(SPRITES[spriteIndex].getName());
            System.out.println("Created the player " + username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tryLogin(String action, String username, byte[] passHash) throws IOException {
        System.out.println("trys to log in");
        Networks.getInstance(SocketType.TCP).sendMsg(action + username);
        Networks.getInstance(SocketType.TCP).sendMsg(passHash);
        boolean success = Networks.getInstance(SocketType.TCP).getMsg().equals("True");
        System.out.println(success);
        return success;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && gameStarted) {
            step();
        } else if (showedStart && wantLogin && !loggedIn) {
            logIn();
            repaint();
            Thread th = new Thread(pinger);
            th.start();
        }
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
            yesNoDialog(guiTitle, guiMsg);
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
        try {
            tcp.getMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
        udp.closeAll();
        setEnabled(false);
        instance = null;
        System.out.println("STOPPED ALL");
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
