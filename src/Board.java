import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a class that represents the game and everything in it
 */
public class Board extends JPanel implements ActionListener {

    private final int DELAY = 1;
    private final String PLAYER_NAME;
    private Timer timer;
    private final Player player;
    private final List<Enemy> enemyList = new ArrayList<>();
    private final Networks udp;
    private final Networks tcp;
    private final int p_number;
    private final Font font;
    private List<JLabel> percentagesLabels = new ArrayList<>();
    private boolean initDrawing = false;

    /**
     * Creates a new Game Board (aka Panel) with everything in it: e.g. the players, the HUD, and managing everything
     *
     * @param pName the player's name
     * @param wasd  whether the user wants to play with wasd or the arrows
     */
    public Board(String pName, boolean wasd) throws Exception {
        font = createFont();
        PLAYER_NAME = pName;
        tcp = Networks.getInstance(SocketType.TCP);
        int serverUdpPort = Integer.parseInt(tcp.getMsg());
        Networks.setServerUdpPort(serverUdpPort);
        udp = Networks.getInstance(SocketType.UDP);
        player = new Player(Constants.SPI, PLAYER_NAME, wasd);
        tcp.sendMsg(player.getSprite().getName() + "," + PLAYER_NAME + "," + tcp.getIP() + "," + udp.getPort());
        String[] info = tcp.getMsg().split(","); //[p_number,max_index]
        p_number = Integer.parseInt(info[0]);
        String[] enemyInfo = tcp.getMsg().split(",,,"); //[e1_character&e1_name , e2_character&e2_name]

        System.out.println("enemyInfo 0: " + enemyInfo[0]);
        System.out.println("info 0: " + info[1]);

        for (int i = 0; i < Integer.parseInt(info[1]) - 1; i++) {
            String sprite = enemyInfo[i].split("&&&")[0];
            String name = enemyInfo[i].split("&&&")[1];
            enemyList.add(new Enemy(Utils.SpriteNameToSprite(sprite), name));
        }
        initBoard();
        Ping ping = new Ping(tcp);
        Thread playerThread = new Thread(player);
        Thread pingThread = new Thread(ping);
        playerThread.start();
        pingThread.start();
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
        doDrawing(g);
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
            initPlayerData();
            initDrawing = true;
        }

        g2d.drawImage(Maps.BattleField.image, 0, 0, this);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
    }

    /**
     * Updates everything in the gui (the player, the enemy, the HUD etc.) from the data that was given from the server
     */
    private void step() {
        String msg = udp.getMsg();
        if (msg == null) {
            System.exit(1);
        }
        String[] characterInfo = msg.split("&");
        if (player.isAlive)
            player.move(characterInfo[p_number]);
        else if (!player.hasFinishDeathAnim())
            player.move("");

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
}
