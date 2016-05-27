import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class UserInterface extends JFrame {

	/** Some serial ID */
    private static final long serialVersionUID = 49875093875647L;

    /** Game and UI constants */
    private int ROWS = 10;
    private int COLS = 10;
    private static final int WINDOW_WIDTH = 1220;
    private static final int WINDOW_HEIGHT = 901;
    private static final int RIGHT_PANEL_WIDTH = 319;
    private static final String GAME_NAME = "Dungeon Escape";

    public static final int EASY = 9;
    public static final int MEDIUM = 17;
    public static final int HARD = 25;

    /** Set difficulty to easy to begin with */
    private int difficulty;

    /** Swing globals */
    private Font baseFont;
    private JPanel parent;
    private JLabel movesLabel;
    private JLabel scoreLabel;
    private JLabel goalLabel;
    private JPanel grid;
    private JButton resetButton;
    private JButton hintButton;
    private long startTime;

    /** Game fields and attributes */
    private int score;
    private int moves;
    private int resets;
    private int timeElapsed;
    private boolean isDarknessMode;
    private boolean isGameActive;
    private Maze maze;
    private List<Coordinate> hintPath;
    private Player player;
    private JLabel highScoresLabel;
    private JPanel inventoryPanel;

    /** Constructor */
    public UserInterface() {
    	// keep track of hint visual so it can be removed
        hintPath = new ArrayList<>();

        // set properties of the frame
        this.setTitle(GAME_NAME);
        this.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.pack();
        //this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // parent panel that holds everything in the frame
        parent = new JPanel(new CardLayout());
        this.add(parent);

        baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
        UIManager.put("OptionPane.messageFont", baseFont);
        UIManager.put("OptionPane.buttonFont", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("ToolTip.font", baseFont.deriveFont((float) 14));

        // set the arrow key dispatcher
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new ArrowKeyDispatcher());

        // initialise both screens
        initStartScreen();
        selectStartScreen();
    }

    /** 
     * Initialize game 
     * */
    private void initStartScreen() {
        //Pick a splash screen
        ImagePanel holder = new ImagePanel("res/splash_ver1.png", null, 0, 0, false);
        //ImagePanel holder = new ImagePanel("res/splash_ver2.png", null, 0, 0, false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));

        // was 200, now 360 to create space
        holder.add(Box.createVerticalStrut(360));

        Font menuFont = baseFont.deriveFont(Font.BOLD, 20);
        Dimension buttonSize = new Dimension(200, 50);

        // start button
        JButton start = new JButton("New Game");
        start.setFont(menuFont);
        start.setForeground(Color.WHITE);
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setFocusable(false);
        start.setAlignmentX(CENTER_ALIGNMENT);
        start.setPreferredSize(buttonSize);
        start.setMaximumSize(buttonSize);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JPanel dialog = new JPanel();
                        dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));

                        JCheckBox checkDark = new JCheckBox("Darkness mode");
                        checkDark.setFont(baseFont);
                        checkDark.setSelected(isDarknessMode);
                        checkDark.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                AbstractButton b = (AbstractButton) arg0.getSource();
                                isDarknessMode = b.getModel().isSelected();
                            }
                        });
                        checkDark.setToolTipText("The dungeon will be enshrouded in darkness! You'll only"
                                + " be able to see in a 3x3 square around you");
                        dialog.add(checkDark);
                        dialog.add(Box.createVerticalStrut(5));

                        String[] values = {"Easy", "Medium", "Hard"};
                        JComboBox<String> selector = new JComboBox<String>(values);
                        dialog.add(selector);

                        int selection = JOptionPane.showConfirmDialog(null, dialog, "Start a new game",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

                        if (selection == JOptionPane.OK_OPTION) {
                            switch (selector.getSelectedIndex()) {
                                case 0:
                                    difficulty = EASY; break;
                                case 1:
                                    difficulty = MEDIUM; break;
                                case 2:
                                    difficulty = HARD; break;
                            }
                            ROWS = difficulty;
                            COLS = difficulty;

                            initMazeScreen();
                            selectMazeScreen();
                        }
                    }
                });
            }
        });
        holder.add(start);

        holder.add(Box.createVerticalStrut(50));

        // high scores button
        JButton highScores = new JButton("High Scores");
        highScores.setFont(menuFont);
        highScores.setForeground(Color.WHITE);
        highScores.setOpaque(false);
        highScores.setContentAreaFilled(false);
        highScores.setFocusable(false);
        highScores.setAlignmentX(CENTER_ALIGNMENT);
        highScores.setPreferredSize(buttonSize);
        highScores.setMaximumSize(buttonSize);
        highScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, readHighScores(15),
                        "High Scores", JOptionPane.PLAIN_MESSAGE);
            }
        });
        holder.add(highScores);

        holder.add(Box.createVerticalStrut(50));

        // about button
        JButton about = new JButton("About");
        about.setFont(menuFont);
        about.setForeground(Color.WHITE);
        about.setOpaque(false);
        about.setContentAreaFilled(false);
        about.setFocusable(false);
        about.setAlignmentX(CENTER_ALIGNMENT);
        about.setPreferredSize(buttonSize);
        about.setMaximumSize(buttonSize);
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                    "<html><body><p style='width: 400px;'>" +
                    GAME_NAME + " is a Zelda-inspired maze game designed by UNSW computing students.<br><br>" +
                    "<b>How to play:</b><br>" +
                    "The aim of the game is to escape the dungeon by navigating to the " +
                    "exit.<br>"
                    + "All dungeons have a single exit (bottom right) which is protected by a " +
                    "locked door - the key (centre) must be picked up before the player can make a " +
                    "clean escape.<br>"
                    + "Along the way, items may also be picked up to increase score.<br><br>" +
                    "<b>Controls:</b><br>" +
                    "Use the arrow keys to move around the maze. Pretty simple right?<br>" + 
                    "</p></body></html>",
                    "About " + GAME_NAME, JOptionPane.PLAIN_MESSAGE);
            }
        });
        holder.add(about);

        holder.add(Box.createVerticalStrut(50));

        // exit button
        JButton exit = new JButton("Exit");
        exit.setFont(menuFont);
        exit.setForeground(Color.WHITE);
        exit.setOpaque(false);
        exit.setContentAreaFilled(false);
        exit.setFocusable(false);
        exit.setAlignmentX(CENTER_ALIGNMENT);
        exit.setPreferredSize(buttonSize);
        exit.setMaximumSize(buttonSize);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                UserInterface.this.setVisible(false);
                UserInterface.this.dispose();
            }
        });
        holder.add(exit);

        parent.add(holder);
    }

    /**
     * Initialise this user interface (JFrame)
     */
    private void initMazeScreen() {
        JPanel holder = new JPanel();
        holder.setLayout(new BoxLayout(holder, BoxLayout.X_AXIS));
        holder.setBackground(null);

        // 1) on the left - a grid to hold the maze
        grid = new JPanel();
        //grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(ROWS, COLS));

        populateGrid();
        holder.add(grid);

        grid.setForeground(Color.GRAY);

        // 2) on the right - control panel
        JPanel rhs = new JPanel();
        rhs.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, WINDOW_HEIGHT));
        rhs.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, WINDOW_HEIGHT));
        rhs.setLayout(new BoxLayout(rhs, BoxLayout.Y_AXIS));
        rhs.setBorder(BorderFactory.createEtchedBorder());

        // ====== INVENTORY ======
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.X_AXIS));
        inventoryPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 80));
        inventoryPanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 80));
        inventoryPanel.setAlignmentX(CENTER_ALIGNMENT);
        TitledBorder invBorder = BorderFactory.createTitledBorder("Inventory");
        invBorder.setTitleFont(baseFont.deriveFont(Font.BOLD));
        invBorder.setTitleJustification(TitledBorder.CENTER);
        inventoryPanel.setBorder(BorderFactory.createCompoundBorder(
                invBorder, new EmptyBorder(5, 10, 5, 10)));

        rhs.add(inventoryPanel);
        rhs.add(Box.createVerticalStrut(10));

        // ====== CURRENT GAME ======
        JPanel currentGamePanel = new JPanel();
        currentGamePanel.setLayout(new BoxLayout(currentGamePanel, BoxLayout.Y_AXIS));
        currentGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        currentGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 270));
        currentGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 270));
        TitledBorder currentBorder = BorderFactory.createTitledBorder("Current Maze");
        currentBorder.setTitleFont(baseFont.deriveFont(Font.BOLD));
        currentBorder.setTitleJustification(TitledBorder.CENTER);
        currentGamePanel.setBorder(BorderFactory.createCompoundBorder(
                currentBorder, new EmptyBorder(10, 10, 10, 10)));

        // Information panel - timer, score
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        // the following two lines will left-align this panel
        // I have no idea how it works
        infoPanel.setAlignmentX(CENTER_ALIGNMENT);
        infoPanel.add(Box.createHorizontalGlue());

        final JLabel timerLabel = new JLabel();
        timerLabel.setFont(baseFont);
        infoPanel.add(timerLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        resetTimer(timerLabel);

        movesLabel = new JLabel("Steps: " + moves);
        movesLabel.setFont(baseFont);
        infoPanel.add(movesLabel);
        infoPanel.add(Box.createVerticalStrut(6));

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(baseFont);
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        
        goalLabel = new JLabel("Current goal: Find the key");
        goalLabel.setFont(baseFont);
        infoPanel.add(goalLabel);

        currentGamePanel.add(infoPanel);
        currentGamePanel.add(Box.createVerticalStrut(10));

        // hint and reset buttons
        JPanel hintResetPanel = new JPanel();
        hintResetPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));

        hintButton = new JButton("Hint");
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // each hint costs 50 points
                score -= 50;
                scoreLabel.setText("Score: " + score);

                MazeSolver solver = new MazeSolver();
                List<Direction> path = solver.getBestPath(maze, player);

                // remove last step of hint so we don't cover up the key
                if (!player.hasKey()) {
                    path.remove(path.size()-1);
                }

                // show the hint path - should only show the next 8 moves
                showHint(path.subList(0, Math.min(path.size(), 8)));
            }
        });
        hintResetPanel.add(hintButton);

        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.moveToStart();
                resets++;
                score = -50 * resets; // reset penalty
                moves = 0;

                refreshGrid(true);
                resetTimer(timerLabel);

                // clear their inventory too
                inventoryPanel.removeAll();
                inventoryPanel.revalidate();
                inventoryPanel.repaint();
            }
        });
        hintResetPanel.add(resetButton);

        currentGamePanel.add(hintResetPanel);
        
        JLabel warningLabel = new JLabel("<html><body><p style='width: " + (RIGHT_PANEL_WIDTH - 100) + "px'>"
                + "Hints cost 50 points<br>"
                + "Resets cost 50 points"
                + "</p></body></html>");
        warningLabel.setAlignmentX(CENTER_ALIGNMENT);
        warningLabel.setFont(baseFont);
        currentGamePanel.add(warningLabel);

        rhs.add(currentGamePanel);
        rhs.add(Box.createVerticalStrut(10));

        // ====== NEW GAME STUFF ======
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));
        newGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        newGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 270));
        newGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 270));
        TitledBorder newBorder = BorderFactory.createTitledBorder("New Maze");
        newBorder.setTitleFont(baseFont.deriveFont(Font.BOLD));
        newBorder.setTitleJustification(TitledBorder.CENTER);
        newGamePanel.setBorder(BorderFactory.createCompoundBorder(
                newBorder, new EmptyBorder(10, 10, 10, 10)));

        // difficulty label and toggles - wrap in their own box
        JPanel difficultyBox = new JPanel();
        difficultyBox.setLayout(new BoxLayout(difficultyBox, BoxLayout.Y_AXIS));
        difficultyBox.setAlignmentX(CENTER_ALIGNMENT);
        difficultyBox.add(Box.createHorizontalGlue());

        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(baseFont);
        difficultyBox.add(difficultyLabel);
        difficultyBox.add(Box.createVerticalStrut(10));

        JRadioButton radioEasy = new JRadioButton("Easy");
        radioEasy.setFont(baseFont);
        radioEasy.setFocusable(false);
        radioEasy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                difficulty = EASY;
            }
        });
        JRadioButton radioMedium = new JRadioButton("Medium");
        radioMedium.setFont(baseFont);
        radioMedium.setFocusable(false);
        radioMedium.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                difficulty = MEDIUM;
            }
        });
        JRadioButton radioHard = new JRadioButton("Hard");
        radioHard.setFont(baseFont);
        radioHard.setFocusable(false);
        radioHard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                difficulty = HARD;
            }
        });

        // set current selection appropriately
        switch (difficulty) {
            case EASY: radioEasy.setSelected(true); break;
            case MEDIUM: radioMedium.setSelected(true); break;
            case HARD: radioHard.setSelected(true); break;
        }

        ButtonGroup difficultyRadioButtons = new ButtonGroup();
        difficultyRadioButtons.add(radioEasy);
        difficultyRadioButtons.add(radioMedium);
        difficultyRadioButtons.add(radioHard);

        difficultyBox.add(radioEasy);
        difficultyBox.add(radioMedium);
        difficultyBox.add(radioHard);

        newGamePanel.add(difficultyBox);
        newGamePanel.add(Box.createVerticalStrut(10));

        final JCheckBox checkDark = new JCheckBox("Darkness mode");
        checkDark.setFont(baseFont);
        checkDark.setSelected(isDarknessMode);
        checkDark.setAlignmentX(CENTER_ALIGNMENT);
        checkDark.setToolTipText("The dungeon will be enshrouded in darkness! You'll only"
                + " be able to see in a 3x3 square around you");
        newGamePanel.add(checkDark);
        newGamePanel.add(Box.createVerticalGlue());

        // button that reloads the grid
        JButton button = new JButton("New Maze");
        button.setToolTipText("Generate a new maze. All progress on the current maze will be lost.");
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ROWS = difficulty;
                COLS = difficulty;
                isDarknessMode = checkDark.isSelected();
                populateGrid();
                resetTimer(timerLabel);
            }
        });
        newGamePanel.add(button);

        rhs.add(newGamePanel);
        rhs.add(Box.createVerticalStrut(10));

        // ====== HIGH SCORES ======
        JPanel highScoresPanel = new JPanel();
        highScoresPanel.setLayout(new BoxLayout(highScoresPanel, BoxLayout.Y_AXIS));
        highScoresPanel.setAlignmentX(CENTER_ALIGNMENT);
        highScoresPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 180));
        highScoresPanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 180));
        TitledBorder highScoresBorder = BorderFactory.createTitledBorder("High Scores");
        highScoresBorder.setTitleFont(baseFont.deriveFont(Font.BOLD));
        highScoresBorder.setTitleJustification(TitledBorder.CENTER);
        highScoresPanel.setBorder(BorderFactory.createCompoundBorder(
                highScoresBorder, new EmptyBorder(10, 10, 10, 10)));
        highScoresLabel = new JLabel();
        highScoresLabel.setFont(baseFont);
        highScoresLabel.setText(readHighScores(5));
        highScoresPanel.add(highScoresLabel);

        rhs.add(highScoresPanel);

        rhs.add(Box.createVerticalStrut(10));

        // quick main menu button
        JButton menuButton = new JButton("Main Menu");
        menuButton.setAlignmentX(CENTER_ALIGNMENT);
        menuButton.setPreferredSize(new Dimension(200, 50));
        menuButton.setMaximumSize(new Dimension(200, 50));
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                isGameActive = false;
                // return to the start menu
                selectStartScreen();
                // remove child 1 of parent (the maze screen) - this is because
                // we need to reload a new game each time
                parent.remove(1);
            }
        });
        rhs.add(menuButton);

        holder.add(rhs);
        parent.add(holder);
    }

    /**
     * Visually displays the hint path on the maze.
     * 
     * @param path		The list of Directions for the suggested path.
     */
    private void showHint(List<Direction> path) {
        // current position of path as we create it
        Coordinate currPos = player.getCurrPos();

        // current index of path in grid
        int hintPosition = 0;

        for (Direction d : path) {
            switch (d) {
                case UP:
                    currPos.shift(Direction.UP);
                    break;
                case DOWN:
                    currPos.shift(Direction.DOWN);
                    break;
                case LEFT:
                    currPos.shift(Direction.LEFT);
                    break;
                case RIGHT:
                    currPos.shift(Direction.RIGHT);
                    break;
                default:
                    // ssshh go to sleep, no tears only dreams. lel
            }
            hintPosition = ROWS * currPos.getRow() + currPos.getCol();

            // get current component
            ImagePanel withHint = (ImagePanel) grid.getComponent(hintPosition);

            // set some sparkles :3 ^.^
            withHint.setForegroundIcon("res/sparkle.png");
            withHint.repaint();

            grid.remove(hintPosition);
            grid.add(withHint, hintPosition);

            // add coordinate to hintPath to keep track of path
            hintPath.add(currPos.clone());
        }
    }

    /**
     * Removes the hint path from the maze.
     */
    private void removeHint() {

        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();

        for (Coordinate hintCoordinate : hintPath) {
            if (!isDarknessMode || hintCoordinate.isVisibleToPlayer(player)) {
                // get tile value, color label accordingly
                char tileValue = maze.getTileFrom(hintCoordinate).getValue();

                // ImagePanel setup
                String prevIcon = gridIcon(ROWS, COLS, hintCoordinate);
                int hintGridIndex = hintCoordinate.getRow() * COLS + hintCoordinate.getCol();
                String foregroundIcon = null;
                boolean isForegroundVisible = false;

                // Setup if the tile was an item
                if (tileValue == Tile.ITEM) {
                    isForegroundVisible = true;
                    foregroundIcon = "res/treasure.png";
                }	

                ImagePanel normalImage = new ImagePanel(prevIcon,
                        foregroundIcon, scaledHeight, scaledWidth, isForegroundVisible);
                normalImage.setOpaque(true);
                normalImage.repaint();

                grid.remove(hintGridIndex);
                grid.add(normalImage, hintGridIndex);
            } else {

                // ImagePanel setup
                int hintGridIndex = hintCoordinate.getRow() * COLS + hintCoordinate.getCol();
                ImagePanel label3 = new ImagePanel(null, null,
                        scaledHeight, scaledWidth, false);
                label3.setOpaque(true);
                label3.setBackground(Color.BLACK);
                label3.repaint();
                grid.remove(hintGridIndex);
                grid.add(label3, hintGridIndex);
            }
        }

        hintPath.clear();
    }

    private void selectStartScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.first(parent);
    }

    private void selectMazeScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.last(parent);
    }

    /**
     * Writes a name and high score to the high scores text file.
     * Name = "Unknown" if it is null.
     * 
     * @param name		The name to write to the file. May be null.
     * @param score		The score to write to the file.
     */
    private void writeHighScore(String name, int score)  {
        String nameToPrint;

        // if file does not exist, create file 
        try {
            Files.createFile(Paths.get("highscores.txt"));
        } catch(FileAlreadyExistsException ignored) {
            // ignore
        } catch (Exception e) {
            // ignore
        }

        // if name not provided, print "Unknown" to file instead
        if (name == null || name.isEmpty()) {
            nameToPrint = "Unknown";
        } else {
            nameToPrint = name;
        }

        try {
            String line = score + " " + nameToPrint + System.lineSeparator();
            Files.write(Paths.get("highscores.txt"),line.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling
        }
    }

    /**
     * Wrapper class for high scores. Implements Comparable so that scores
     * can be sorted.
     *
     */
    private class Score implements Comparable<Score> {
        private int score;
        private String name;
        public Score(int score, String name) {
            this.score = score;
            this.name = name;
        }
        public int getScore() { return score; }
        public String getName() { return name; }

        @Override
        public int compareTo(Score arg0) {
            return arg0.getScore() - score;
        }
    }

    /**
     * Reads high scores from the high scores text file and returns them as a String.
     * 
     * @param num		The number of high scores to read.
     * @return			The high scores as a String.
     */
    private String readHighScores(int num) {
        String fileName = "highscores.txt";
        List<Score> scores = new ArrayList<Score>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(" ", 2);
                int score = Integer.parseInt(args[0]);
                String name = args[1];
                scores.add(new Score(score, name));
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }

        // sort the scores
        Collections.sort(scores);

        // format scores into html so they can be displayed in a JLabel
        // shitty HTML table to display scores
        StringBuilder output = new StringBuilder();
        output.append("<html><table cellpadding='1' cellspacing='0'>");
        
        // only get the top n scores
        int count = 0;
        for (Score sc : scores) {
            output.append(String.format("<tr><td><b>%5d&nbsp;&nbsp;</b></td><td>%s</td></tr>",
                    sc.getScore(), sc.getName()));
            count++;
            if (count == num) {
                break;
            }
        }
        output.append("</table></html>");
        return output.toString();
    }

    /**
     * Resets the timer to 0, and starts it again.
     * 
     * @param timerLabel	The label that displays the timer.
     */
    private void resetTimer(final JLabel timerLabel){
        final Timer timer = new Timer(40, null);
        timer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timeElapsed = (int)(System.currentTimeMillis() - startTime);

                // update in the UI thread
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int ms = timeElapsed % 1000;
                        int seconds = (timeElapsed / 1000) % 60;
                        int minutes = timeElapsed / 1000 / 60;
                        timerLabel.setText("Time: "
                                + String.format("%d:%02d.%03d", minutes, seconds, ms));
                    }
                });

                if (!isGameActive) {
                    timer.stop();
                }
            }
        });
        timer.setInitialDelay(0);
        startTime = System.currentTimeMillis();
        timer.start();
    }

    /**
     * A KeyEventDispatcher that catches arrow key presses and performs the appropriate
     * action
     *
     */
    private class ArrowKeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (isGameActive && e.getID() == KeyEvent.KEY_PRESSED) {
                Direction d;
                boolean arrowKeyPress = false;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        d = Direction.UP;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        d = Direction.DOWN;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        d = Direction.LEFT;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        d = Direction.RIGHT;
                        arrowKeyPress = true;
                        break;
                    default:
                        d = Direction.UP;
                        arrowKeyPress = false;
                }

                if (arrowKeyPress) {
                    if (maze.isLegalMove(player.getCurrPos(), d)) {
                        player.move(d);
                        moves++;
                        refreshGrid(false);
                    }
                }
            }
            return false;
        }
    }

    /**
     * Populate the grid with tiles for a new maze
     * @param rows - Number of rows in the grid
     * @param cols - Number of columns in the grid
     * @param newMaze - Whether a new maze should be generated
     */
    private void populateGrid() {
        grid.setLayout(new GridLayout(ROWS, COLS));
        grid.removeAll();

        // create a new maze with a new player
        player = new Player();

        int appropriateLength = 200;
        if (isDarknessMode) {
            switch (ROWS) {
                case EASY: appropriateLength = 30; break;
                case MEDIUM: appropriateLength = 60; break;
                case HARD: appropriateLength = 100; break;
            }
        } else {
            switch (ROWS) {
                case EASY: appropriateLength = 45; break;
                case MEDIUM: appropriateLength = 90; break;
                case HARD: appropriateLength = 150; break;
            }
        }

        // guarantees a maze of appropriate length
        MazeSolver solver = new MazeSolver();
        boolean appropriate = false;

        while (!appropriate) {
            maze = new Maze(ROWS, COLS);

            // check minimum finish path
            List<Direction> toKey = solver.navigate(maze, new Coordinate(0, 0), maze.getKeyPos());
            List<Direction> toFinish = solver.navigate(maze, maze.getKeyPos(), maze.getFinishPos());

            if (toKey != null && toFinish != null) {
                if (toKey.size() + toFinish.size() < appropriateLength) {
                    appropriate = true;
                }
            }
        }

        player.setHasKey(false);
        // set game parameters to default values
        isGameActive = true;
        score = 0;
        moves = 0;
        resets = 0;
        timeElapsed = 0;

        // clear the inventory
        if (inventoryPanel != null) {
            inventoryPanel.removeAll();
            inventoryPanel.revalidate();
            inventoryPanel.repaint();
        }

        grid.removeAll();

        if (isDarknessMode) {
            // Scale the size based on number of rows and columns
            int scaledWidth = getScaledWidth();
            int scaledHeight = getScaledHeight();

            // darken the maze
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    ImagePanel label3 = new ImagePanel(null, null,
                            scaledHeight, scaledWidth, false);
                    label3.setOpaque(true);
                    label3.setBackground(Color.BLACK);
                    label3.repaint();
                    grid.add(label3); 
                }
            }

            // light up start area
            lightUpStartArea();
        } else {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    Coordinate pos = new Coordinate(row, col);

                    // get tile value, color label accordingly
                    char tileValue = maze.getTileFrom(pos).getValue();

                    // get the correct image for a tile
                    String icon = gridIcon(ROWS, COLS, pos);

                    // Scale the size based on number of rows and columns
                    int scaledWidth = getScaledWidth();
                    int scaledHeight = getScaledHeight();

                    //isPlayer is initialized to false
                    boolean isForegroundVisible = false;

                    //initialize foregroundIcon
                    String foregroundIcon = null;

                    // check the Tile and set visibility and foregroundIcon
                    if (row == 0 && col == 0) {
                        // start tile
                        isForegroundVisible = true;
                        foregroundIcon = "res/link4.png";
                    } else {
                        switch (tileValue) {
                            case Tile.START:
                            case Tile.FINISH:
                            case Tile.EMPTY:
                            case Tile.WALL:
                                break;
                            case Tile.KEY:
                                isForegroundVisible = true;
                                foregroundIcon = "res/key.png";
                                break;
                            case Tile.ITEM:
                                isForegroundVisible = true;
                                foregroundIcon = "res/treasure.png";
                                break;
                        }
                    }

                    //add imagePanel to grid
                    ImagePanel label2 = new ImagePanel(icon, foregroundIcon,
                            scaledHeight, scaledWidth, isForegroundVisible);
                    label2.setOpaque(true);
                    label2.repaint(); 

                    grid.add(label2);                
                }
            }
        }



        // at start - disable reset
        if (resetButton != null) {
            resetButton.setEnabled(false);
        }
        // enable hints
        if (hintButton != null) {
            hintButton.setEnabled(true);
        }

        if (movesLabel != null) {
            movesLabel.setText("Moves: " + moves);
        }
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
        if (goalLabel != null) {
            goalLabel.setText("Current goal: Find the key");
        }

        // must be called to refresh the whole JFrame
        revalidate();
    }

    /**
     * Lights up the start area of the maze.
     */
    private void lightUpStartArea() {
        // light up the start position
        Coordinate[] coordinates = new Coordinate[4];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 0);
        coordinates[3] = new Coordinate(1, 1);
        // scale the size based on number of rows and columns
        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();


        for (Coordinate coordinate : coordinates) {
            int row = coordinate.getRow();
            int col = coordinate.getCol();

            // get tile value, color label accordingly
            char tileValue = maze.getTileFrom(coordinate).getValue();

            // get the correct image for a tile
            String icon = gridIcon(ROWS, COLS, coordinate);


            //isPlayer is initialized to false
            boolean isForegroundVisible = false;

            //initialize foregroundIcon
            String foregroundIcon = null;

            // check the Tile and set visibility and foregroundIcon
            if (row == 0 && col == 0) {
                // start tile
                isForegroundVisible = true;
                foregroundIcon = "res/link4.png";
            } else {
                switch (tileValue) {
                    case Tile.START:
                    case Tile.FINISH:
                    case Tile.EMPTY:
                    case Tile.WALL:
                        break;
                    case Tile.KEY:
                        isForegroundVisible = true;
                        foregroundIcon = "res/key.png";
                        break;
                    case Tile.ITEM:
                        isForegroundVisible = true;
                        foregroundIcon = "res/treasure.png";
                        break;
                }
            }

            //add imagePanel to grid
            ImagePanel label2 = new ImagePanel(icon, foregroundIcon,
                    scaledHeight, scaledWidth, isForegroundVisible);
            label2.setOpaque(true);
            label2.repaint(); 
            grid.remove(row * COLS + col);
            grid.add(label2, row * COLS + col);
        }
    }

	/**
	 * Refreshes the grid so that the newest player action can be seen on the screen.
	 * 
	 * @param isReset		True if the refresh is due to the reset button being pressed.
	 */
    private void refreshGrid(boolean isReset) {
        if (isGameActive) {
            // Scale the size based on number of rows and columns
            int scaledWidth = getScaledWidth();
            int scaledHeight = getScaledHeight();

            // new player pos   
            boolean isPlayer = true;
            boolean isForegroundVisible;
            Coordinate currPos = player.getCurrPos();
            String icon = gridIcon(ROWS, COLS, currPos);
            String foregroundIcon = null;

            // remove hint path when the player moves
            if (!hintPath.isEmpty()) {
                removeHint();
            }

            switch (player.getLastMove()) {
                case UP:
                    foregroundIcon = "res/link2.png";
                    break;
                case DOWN:
                    foregroundIcon = "res/link1.png";
                    break;
                case LEFT:
                    foregroundIcon = "res/link3.png";
                    break;
                case RIGHT:
                    foregroundIcon = "res/link4.png";
                    break;
            }

            if (isReset) {
                // reset - make Link face right by default
                foregroundIcon = "res/link4.png";
            }

            // create new imagePanel for player's grid position
            ImagePanel labelCurr = new ImagePanel(icon,
                    foregroundIcon, scaledHeight, scaledWidth, isPlayer);
            labelCurr.setOpaque(true);
            labelCurr.repaint(); 

            int positionIndex = currPos.getRow() * COLS + currPos.getCol();
            grid.remove(positionIndex);   
            grid.add(labelCurr, positionIndex);


            if (isDarknessMode) {
                //
                // lighting
                //

                // create new lit up Tile locations
                Direction latsMove = player.getLastMove();
                Coordinate[] toLight = new Coordinate[3];
                int currRow = currPos.getRow();
                int currCol = currPos.getCol();

                if (latsMove == Direction.RIGHT) {
                    toLight[0] = new Coordinate(currRow-1, currCol+1);
                    toLight[1] = new Coordinate(currRow, currCol+1);
                    toLight[2] = new Coordinate(currRow+1, currCol+1);
                } else if (latsMove == Direction.LEFT) {
                    toLight[0] = new Coordinate(currRow-1, currCol-1);
                    toLight[1] = new Coordinate(currRow, currCol-1);
                    toLight[2] = new Coordinate(currRow+1, currCol-1);
                } else if (latsMove == Direction.UP) {
                    toLight[0] = new Coordinate(currRow-1, currCol-1);
                    toLight[1] = new Coordinate(currRow-1, currCol);
                    toLight[2] = new Coordinate(currRow-1, currCol+1);
                } else if (latsMove == Direction.DOWN) {
                    toLight[0] = new Coordinate(currRow+1, currCol-1);
                    toLight[1] = new Coordinate(currRow+1, currCol);
                    toLight[2] = new Coordinate(currRow+1, currCol+1);
                }

                for (int i = 0; i < toLight.length; i++) {
                    if (toLight[i].getRow() >= 0 && toLight[i].getCol() >= 0 
                            && toLight[i].getRow() < ROWS && toLight[i].getCol() < COLS) {

                        char tileValue = maze.getTileFrom(toLight[i]).getValue();
                        // Setup if the tile was an item
                        if (tileValue == Tile.ITEM) {
                            isForegroundVisible = true;
                            foregroundIcon = "res/treasure.png";
                        } else if (tileValue == Tile.KEY) {
                            isForegroundVisible = true;
                            foregroundIcon = "res/key.png";
                        } else {
                            foregroundIcon = null;
                        }

                        icon = gridIcon(ROWS, COLS, toLight[i]);
                        labelCurr = new ImagePanel(icon,
                                foregroundIcon, scaledHeight, scaledWidth, isPlayer);
                        labelCurr.setOpaque(true);
                        labelCurr.repaint(); 

                        positionIndex = toLight[i].getRow() * COLS + toLight[i].getCol();
                        grid.remove(positionIndex);   
                        grid.add(labelCurr, positionIndex);
                    }
                }

                // create new dark Tile locations
                Coordinate[] toDark = new Coordinate[3];

                if (latsMove == Direction.RIGHT) {
                    toDark[0] = new Coordinate(currRow-1, currCol-2);
                    toDark[1] = new Coordinate(currRow, currCol-2);
                    toDark[2] = new Coordinate(currRow+1, currCol-2);
                } else if (latsMove == Direction.LEFT) {
                    toDark[0] = new Coordinate(currRow-1, currCol+2);
                    toDark[1] = new Coordinate(currRow, currCol+2);
                    toDark[2] = new Coordinate(currRow+1, currCol+2);
                } else if (latsMove == Direction.UP) {
                    toDark[0] = new Coordinate(currRow+2, currCol-1);
                    toDark[1] = new Coordinate(currRow+2, currCol);
                    toDark[2] = new Coordinate(currRow+2, currCol+1);
                } else if (latsMove == Direction.DOWN) {
                    toDark[0] = new Coordinate(currRow-2, currCol-1);
                    toDark[1] = new Coordinate(currRow-2, currCol);
                    toDark[2] = new Coordinate(currRow-2, currCol+1);
                }

                for (int i = 0; i < toDark.length; i++) {
                    if (toDark[i].getRow() >= 0 && toDark[i].getCol() >= 0
                            && toDark[i].getRow() < ROWS && toDark[i].getCol() < COLS) {
                        labelCurr = new ImagePanel(null, foregroundIcon,
                                scaledHeight, scaledWidth, false);
                        labelCurr.setOpaque(true);
                        labelCurr.setBackground(Color.BLACK);
                        labelCurr.repaint();

                        positionIndex = toDark[i].getRow() * COLS + toDark[i].getCol();
                        grid.remove(positionIndex);   
                        grid.add(labelCurr, positionIndex);
                    }
                }
            }

            // get previous position on grid 
            Coordinate prevPos = player.getPrevPos();
            String prevIcon = gridIcon(ROWS, COLS, prevPos);
            int prevPosition = prevPos.getRow() * COLS + prevPos.getCol();

            // replace previous position on grid (i.e. remove player's sprite)
            isPlayer = false;
            ImagePanel labelPrev = new ImagePanel(prevIcon,
                    foregroundIcon, scaledHeight, scaledWidth, isPlayer);
            labelPrev.setOpaque(true);
            labelPrev.repaint();

            grid.remove(prevPosition);
            grid.add(labelPrev,prevPosition);

            // at start - disable reset
            if (resetButton != null) {
                if (isReset) {
                    resetButton.setEnabled(false);
                } else {
                    resetButton.setEnabled(true);
                }
            }

            // update score
            score += maze.getTileFrom(currPos).getScore();

            if (movesLabel != null) {
                movesLabel.setText("Moves: " + moves);
            }
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            // must be called to refresh the whole JFrame
            revalidate();

            // if the player collects an item, replace the tile with an empty one
            if (maze.getTileFrom(currPos).getValue() == Tile.ITEM) {
                maze.setTileEmpty(currPos);

                // add an item to their inventory
                JLabel label = new JLabel(new ImageIcon(new ImageIcon("res/treasure_inven.png").getImage()
                        .getScaledInstance(28, 28, Image.SCALE_DEFAULT)));

                inventoryPanel.add(label);
                inventoryPanel.add(Box.createHorizontalStrut(10));
            }

            if (maze.getTileFrom(currPos).getValue() == Tile.KEY) {
                maze.setTileEmpty(currPos);
                player.setHasKey(true);

                // add the key to their inventory
                JLabel label = new JLabel(new ImageIcon(new ImageIcon("res/key_inven.png").getImage()
                        .getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
                
                // update goal
                goalLabel.setText("Current goal: Go to the finish!");

                inventoryPanel.add(label);
                inventoryPanel.add(Box.createHorizontalStrut(10));
            }

            // refresh current score
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            // check if player has finished the maze
            if (currPos.equals(maze.getFinishPos()) && player.hasKey()) {
                finishGame();
            }

            // reset items if we are resetting
            if (isReset) {
                // variables for icon
                foregroundIcon = null;
                isForegroundVisible = true;

                for (Coordinate itemCoord : maze.getItemCoords()) {
                    int row = itemCoord.getRow();
                    int col = itemCoord.getCol();

                    // sanity check, as only 1/4 items will be valid on easy mode
                    if (row > 0) {
                        if (!isDarknessMode || itemCoord.isInStartArea()) {
                            // set to lit-up

                            //get correct icon for the item
                            icon = gridIcon(ROWS, COLS, itemCoord);

                            foregroundIcon = "res/treasure.png";

                            //add imagePanel to grid
                            ImagePanel newPanel = new ImagePanel(icon, foregroundIcon,
                                    scaledHeight, scaledWidth, isForegroundVisible);
                            newPanel.setOpaque(true);
                            newPanel.repaint();

                            int itemIndex = row * COLS + col;
                            grid.remove(itemIndex);
                            grid.add(newPanel, itemIndex); 
                        } else {
                            // set to darkness

                            ImagePanel newPanel = new ImagePanel(null, null,
                                    scaledHeight, scaledWidth, false);
                            newPanel.setOpaque(true);
                            newPanel.setBackground(Color.BLACK);
                            newPanel.repaint();

                            int itemIndex = row * COLS + col;
                            grid.remove(itemIndex);   
                            grid.add(newPanel, itemIndex);
                        }

                        //set tile as an item again, score purposes etc
                        maze.setTileItem(itemCoord);
                    }
                }

                // reset key only if required
                if (player.hasKey()) {
                    Coordinate keyPos = maze.getKeyPos();
                    int row = keyPos.getRow();
                    int col = keyPos.getCol();

                    if (!isDarknessMode || keyPos.isInStartArea()) {
                        // set to lit-up

                        int keyIndex = keyPos.getRow() * COLS + keyPos.getCol();


                        //create Key component and re-add
                        icon = gridIcon(ROWS, COLS, keyPos);

                        foregroundIcon = "res/key.png";

                        ImagePanel square = new ImagePanel(icon, foregroundIcon, 
                                scaledHeight, scaledWidth, isForegroundVisible);
                        square.setOpaque(true);
                        square.repaint(); 

                        grid.remove(keyIndex);
                        grid.add(square, keyIndex);
                        
                        goalLabel.setText("Current goal: Find the key");

                        // lastly take the key away
                        player.setHasKey(false);
                    } else {
                        // set to darkness

                        ImagePanel newPanel = new ImagePanel(null, null,
                                scaledHeight, scaledWidth, false);
                        newPanel.setOpaque(true);
                        newPanel.setBackground(Color.BLACK);
                        newPanel.repaint();

                        int itemIndex = row * COLS + col;
                        grid.remove(itemIndex);   
                        grid.add(newPanel, itemIndex);
                    }

                    //set tile as a key again, score purposes etc
                    maze.setTileKey(maze.getKeyPos());
                }
                if (isDarknessMode) {
                    // darken previous area surrounding the player
                    darkenPreviousArea();

                    // light up start area
                    lightUpStartArea();
                }
            }
        }
    }

    /**
     * Resets relevant variables and calculates a final score.
     */
    private void finishGame() {
        // reached the finish tile - they are finished
        isGameActive = false;

        resetButton.setEnabled(false);
        hintButton.setEnabled(false);

        int moveLimit = 0;      // moves
        int timeLimit = 0;      // milliseconds
        double scaling = 0;     // scaling factor

        // post-finish score calculation
        // note - ROWS conveniently holds the current difficulty
        if (isDarknessMode) {
            // TODO test and adjust
            switch (ROWS) {
                case EASY:
                    moveLimit = 60;
                    timeLimit = 20000;
                    scaling = 2;
                    break;
                case MEDIUM:
                    moveLimit = 180;
                    timeLimit = 60000;
                    scaling = 1.75;
                    break;
                case HARD:
                    moveLimit = 300;
                    timeLimit = 100000;
                    scaling = 1.6;
                    break;
            }
        } else {
            switch (ROWS) {
                case EASY:
                    moveLimit = 40;
                    timeLimit = 15000;
                    scaling = 1.1;
                    break;
                case MEDIUM:
                    moveLimit = 100;
                    timeLimit = 30000;
                    scaling = 0.7;
                    break;
                case HARD:
                    moveLimit = 160;
                    timeLimit = 45000;
                    scaling = 0.55;
                    break;
            }
        }

        score += (moveLimit - moves) * 100;
        score += (timeLimit - timeElapsed) / 3;
        score *= scaling;

        if (score < 0) score = 0;

        scoreLabel.setText("Final score: " + score);

        // Allow user to write new high score
        String name = JOptionPane.showInputDialog(null,
                "You've cleared the dungeon!\n"
                        + "Score: " + score + "\n\n"
                        + "Enter your name:",
                        "Congratulations!", JOptionPane.PLAIN_MESSAGE);
        writeHighScore(name, score);
        highScoresLabel.setText(readHighScores(5));
    }

    /**
     * Darken the area the player was in prior to pressing reset.
     */
    private void darkenPreviousArea() {
        Coordinate prevPos = player.getPrevPos();
        int row = prevPos.getRow();
        int col = prevPos.getCol();

        Coordinate[] toDarken = new Coordinate[9];
        toDarken[0] = new Coordinate(row-1, col-1);
        toDarken[1] = new Coordinate(row-1, col);
        toDarken[2] = new Coordinate(row-1, col+1);
        toDarken[3] = new Coordinate(row, col-1);
        toDarken[4] = new Coordinate(row, col);
        toDarken[5] = new Coordinate(row, col+1);
        toDarken[6] = new Coordinate(row+1, col-1);
        toDarken[7] = new Coordinate(row+1, col);
        toDarken[8] = new Coordinate(row+1, col+1);

        // scale the size based on number of rows and columns
        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();

        for (Coordinate coordinate : toDarken) {
            row = coordinate.getRow();
            col = coordinate.getCol();
            if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                ImagePanel labelCurr = new ImagePanel(null, null,
                        scaledHeight, scaledWidth, false);
                labelCurr.setOpaque(true);
                labelCurr.setBackground(Color.BLACK);
                labelCurr.repaint();

                int positionIndex = row * COLS + col;
                grid.remove(positionIndex);   
                grid.add(labelCurr, positionIndex);
            }
        }


    }

    /**
     * Obtain the relevant grid icon image for this coordinate in the maze.
     * 
     * @param rows		Maze rows.
     * @param cols		Maze columns.	
     * @param pos		Current position we are finding the grid icon for.
     * @return			The name of the icon to use.
     */
    private String gridIcon(int rows, int cols, Coordinate pos) {
        //load the proper image for a tile
        String icon = null;
        char tileValue = maze.getTileFrom(pos).getValue();
        int row = pos.getRow();
        int col = pos.getCol();

        //Can we go in these directions from current tile?
        boolean left = true;
        boolean right = true;
        boolean up = true;
        boolean down = true;

        switch (tileValue) {
            case Tile.WALL:
                //draw empty wall tile
                icon = "blank_wall.png";
                break;
            case Tile.START:
                //Test if tiles beside the start are walls
                if (maze.isWall(row + 1, col)) {
                    down = false;
                }
                if (maze.isWall(row, col + 1)) {
                    right = false;
                }

                //Give appropriate Tile
                if (down && right) {
                    icon = "tile6_2.png";
                }
                if (down && !right) {
                    icon = "tile6_3.png";
                }
                if (!down && right) {
                    icon = "tile6.png";
                }
                if (!down && !right) {
                    // this should never happen - stuck at start
                    // filler tile
                    icon = "tile6.png";
                }
                break;
            case Tile.FINISH:
                // Test if tiles beside the finish are walls
                if (maze.isWall(row - 1,col)) {
                    up = false;
                }
                if (maze.isWall(row, col - 1)) {
                    left = false;
                }




                //Give appropriate Tile
                if (up && left){
                    //Test if Player has Key
                    if ((player.getCurrPos().equals(maze.getFinishPos()) && player.hasKey())) {
                        //NOTE: Uncomment if you want door to open on tile before finish tile
                        //|| (player.getCurrPos().getRow()==ROWS-1 && player.getCurrPos().getCol()==COLS-2 && player.hasKey())) {
                        icon = "door_open_3.png";
                    } else {
                        icon = "door_closed_3.png";
                    }
                }
                if (up && !left){
                    //Test if Player has Key
                    if ((player.getCurrPos().equals(maze.getFinishPos()) && player.hasKey())) { 
                        //NOTE: Uncomment if you want door to open on tile before finish tile
                        //|| (player.getCurrPos().getRow()==ROWS-2 && player.getCurrPos().getCol()==COLS-1 && player.hasKey())) {
                        icon = "door_open_2.png";
                    } else {
                        icon = "door_closed_2.png";
                    }
                }
                if (!up && left){
                    //Test if Player has Key
                    if ((player.getCurrPos().equals(maze.getFinishPos()) && player.hasKey())) { 
                        //NOTE: Uncomment if you want door to open on tile before finish tile
                        //|| (player.getCurrPos().getRow()==ROWS-1 && player.getCurrPos().getCol()==COLS-2 && player.hasKey()) 
                        //|| (player.getCurrPos().getRow()==ROWS-2 && player.getCurrPos().getCol()==COLS-1 && player.hasKey())) {
                        icon = "door_open.png";
                    } else {
                        icon = "door_closed.png";
                    }
                }
                if (!up && !left) {
                    // this should never happen - end is unreachable
                    // filler tile
                    icon = "tile7.png";
                }


                break;
            case Tile.EMPTY:
            case Tile.ITEM:
            case Tile.KEY:
                // Test tiles not on edge of maze
                if (row != 0 && maze.isWall(row - 1, col)){
                    up = false;
                }
                if (row != rows - 1 && maze.isWall(row + 1, col)){
                    down = false;
                }
                if (col != 0 && maze.isWall(row, col - 1)){
                    left = false;
                }
                if (col != cols - 1 && maze.isWall(row, col + 1)){
                    right = false;
                }

                //Test for tiles on edge of maze
                if (row == 0) {
                    up = false;
                }
                if (col == 0) {
                    left = false;
                }
                if (row == rows-1) {
                    down = false;
                }
                if (col == cols-1) {
                    right = false;
                }

                // Test which tile we place
                if (!left && !right && !up && !down) {
                    icon = "blank_wall.png";
                }
                if (left && !right && !up && !down) {
                    icon = "tile4.png";
                }
                if (left && right && !up && !down) {
                    icon = "tile5.png";
                }
                if (left && right && up && !down) {
                    icon = "tile2_2.png";
                }
                if (left && right && up && down) {
                    icon = "tile1.png";
                }
                if (!left && right && !up && !down) {
                    icon = "tile4_3.png";
                }
                if (!left && right && up && !down) {
                    icon = "tile3_3.png";
                }
                if (!left && right && up && down) {
                    icon = "tile2.png";
                }
                if (!left && !right && up && !down) {
                    icon = "tile4_4.png";
                }
                if (!left && !right && up && down) {
                    icon = "tile5_2.png";
                }
                if (left && !right && up && !down){
                    icon = "tile3_4.png";
                }
                if (!left && !right && !up && down){
                    icon = "tile4_2.png";
                }
                if (left && right && !up && down){
                    icon = "tile2_4.png";
                }
                if (left && !right && up && down){
                    icon = "tile2_3.png";
                }
                if (!left && right && !up && down){
                    icon = "tile3_2.png";
                }
                if (left && !right && !up && down){
                    icon = "tile3.png";
                }
                break;
            default:
                break;
        }
        // if icon exists, direct it to the res/ folder
        if (icon != null) {
            icon = "res/".concat(icon);
        }

        return icon;
    }

    /** Getters & Setters */
    
    private int getScaledHeight() {
        return WINDOW_HEIGHT / ROWS + 1;
    }

    private int getScaledWidth() {
        return (WINDOW_WIDTH - RIGHT_PANEL_WIDTH) / COLS + 1;
    }
    
    /** Entry point */
    public static void main(String[] args) {
        // create JFrame and make it visible
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserInterface ui = new UserInterface();
                ui.setVisible(true);
            }
        });
    }
}
