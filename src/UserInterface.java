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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class UserInterface extends JFrame {

    /**
     * WTF is this
     */
    private static final long serialVersionUID = 1L;

    // Game and UI constants
    private int ROWS = 10;
    private int COLS = 10;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 750;
    private static final int RIGHT_PANEL_WIDTH = 320;
    private static final int ITEM_PICKUP = 20;
    private static final String GAME_NAME = "Dungeon Escape";
    
    public static final int EASY = 10;
    public static final int MEDIUM = 18; 
    public static final int HARD = 25;
    
    // set difficulty to easy to begin with
    private int difficulty = EASY;

    // Swing globals
    private Font baseFont;
    private JPanel parent;
    private JLabel scoreLabel;
    private JPanel grid;
    private JButton resetButton;
    private long startTime;
    
    // Game fields and attributes
    private int score;
    private boolean isGameActive;
    private Maze maze;
    private Player player;
    private JLabel highScoresLabel;

    public UserInterface() {
        // set properties of the frame
        this.setTitle(GAME_NAME);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
        
        // set the arrow key dispatcher
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new ArrowKeyDispatcher());
        
        // initialise both screens
        initStartScreen();
        selectStartScreen();
    }
    
    public void initStartScreen() {
        BackgroundPanel holder = new BackgroundPanel();
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
        holder.setBackgroundImage("res/splash.jpg");
        
        holder.add(Box.createVerticalStrut(180));
        
        Font menuFont = baseFont.deriveFont(Font.BOLD, 20);
        
        JLabel nameLabel = new JLabel(GAME_NAME, SwingConstants.CENTER);
        nameLabel.setPreferredSize(new Dimension(400, 80));
        nameLabel.setMaximumSize(new Dimension(400, 80));
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        nameLabel.setFont(menuFont.deriveFont((float) 30));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBackground(Color.GRAY);
        nameLabel.setOpaque(true);
        nameLabel.setBorder(BorderFactory.createEtchedBorder());
        
        holder.add(nameLabel);
        
        holder.add(Box.createVerticalStrut(75));
        
        // start button
        JButton start = new JButton("New Game");
        start.setFont(menuFont);
        start.setForeground(Color.WHITE);
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setFocusable(false);
        start.setAlignmentX(CENTER_ALIGNMENT);
        start.setPreferredSize(new Dimension(150, 50));
        start.setMaximumSize(new Dimension(150, 50));
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String[] values = {"Easy", "Medium", "Hard"};
                        String selected = (String) JOptionPane.showInputDialog(null,
                                "Choose a difficulty:",
                                "Difficulty Selection",
                                JOptionPane.DEFAULT_OPTION, null,
                                values, "Easy");
                        
                        if (selected != null) {
                            if (selected.equals("Easy")) {
                                difficulty = EASY;
                            } else if (selected.equals("Medium")) {
                                difficulty = MEDIUM;
                            } else if (selected.equals("Hard")) {
                                difficulty = HARD;
                            }
                            ROWS = difficulty;
                            COLS = difficulty;
                            
                            // generate and show the maze screen
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
        highScores.setPreferredSize(new Dimension(150, 50));
        highScores.setMaximumSize(new Dimension(150, 50));
        highScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*JOptionPane optionPane = new JOptionPane(readHighScores(), JOptionPane.PLAIN_MESSAGE);
                JDialog dialog = optionPane.createDialog(null, "High Scores");
                dialog.setVisible(true);*/
                JOptionPane.showMessageDialog(null, readHighScores(15), "High Scores", JOptionPane.PLAIN_MESSAGE);
            }
        });
        holder.add(highScores);
        
        holder.add(Box.createVerticalStrut(50));
        
        // exit button
        JButton exit = new JButton("Exit");
        exit.setFont(menuFont);
        exit.setForeground(Color.WHITE);
        exit.setOpaque(false);
        exit.setContentAreaFilled(false);
        exit.setFocusable(false);
        exit.setAlignmentX(CENTER_ALIGNMENT);
        exit.setPreferredSize(new Dimension(150, 50));
        exit.setMaximumSize(new Dimension(150, 50));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                UserInterface.this.setVisible(false);
            }
        });
        holder.add(exit);
        
        parent.add(holder);
    }

    /**
     * Initialise this user interface (JFrame)
     */
    public void initMazeScreen() {
        JPanel holder = new JPanel();
        holder.setLayout(new BoxLayout(holder, BoxLayout.X_AXIS));
        holder.setBackground(null);
        
        isGameActive = true;
        score = 0;

        // 1) on the left - a grid to hold the maze
        grid = new JPanel();
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(ROWS, COLS));

        populateGrid();
        holder.add(grid);

        // 2) on the right - control panel
        JPanel rhs = new JPanel();
        rhs.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 700));
        rhs.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 700));
        rhs.setLayout(new BoxLayout(rhs, BoxLayout.Y_AXIS));
        rhs.setBorder(BorderFactory.createEtchedBorder());

        // ====== GAME NAME ======
        JLabel nameLabel = new JLabel(GAME_NAME, SwingConstants.CENTER);
        nameLabel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 80));
        nameLabel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 80));
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBackground(Color.GRAY);
        nameLabel.setOpaque(true);
        nameLabel.setBorder(BorderFactory.createEtchedBorder());
        
        rhs.add(nameLabel);
        rhs.add(Box.createVerticalStrut(10));

        // ====== CURRENT GAME ======
        JPanel currentGamePanel = new JPanel();
        currentGamePanel.setLayout(new BoxLayout(currentGamePanel, BoxLayout.Y_AXIS));
        currentGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        currentGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 170));
        currentGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 170));
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
        infoPanel.add(Box.createVerticalStrut(10));
        resetTimer(timerLabel);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(baseFont);
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        currentGamePanel.add(infoPanel);
        currentGamePanel.add(Box.createVerticalStrut(20));

        // hint and reset buttons
        JPanel hintResetPanel = new JPanel();
        hintResetPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));

        JButton hintButton = new JButton("Hint");
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(UserInterface.this, "Do better");
            }
        });
        hintResetPanel.add(hintButton);

        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.moveToStart();
                score = 0; // TODO maybe a penalty for resetting?
                refreshGrid();
                resetTimer(timerLabel);
            }
        });
        hintResetPanel.add(resetButton);

        currentGamePanel.add(hintResetPanel);

        rhs.add(currentGamePanel);
        rhs.add(Box.createVerticalStrut(10));

        // ====== NEW GAME STUFF ======
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));
        newGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        newGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 230));
        newGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 230));
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

        // spacing after the difficulty section
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
        highScoresPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 170));
        highScoresPanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 170));
        TitledBorder highScoresBorder = BorderFactory.createTitledBorder("High Scores");
        highScoresBorder.setTitleFont(baseFont.deriveFont(Font.BOLD));
        highScoresBorder.setTitleJustification(TitledBorder.CENTER);
        highScoresPanel.setBorder(BorderFactory.createCompoundBorder(
                highScoresBorder, new EmptyBorder(10, 10, 10, 10)));
        highScoresLabel = new JLabel();
        highScoresLabel.setFont(baseFont);
        highScoresLabel.setText(readHighScores(4));
        highScoresPanel.add(highScoresLabel);
        
        rhs.add(highScoresPanel);
        
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

    private void selectStartScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.first(parent);
    }
    
    private void selectMazeScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.last(parent);
    }

    private void writeHighScore(String name, int score) {
        try {
            Files.write(Paths.get("highscores.txt"),
                    (score + " " + name + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling
        }
    }

    private String readHighScores(int num) {
        String fileName = "highscores.txt";
        StringBuilder scores = new StringBuilder();
        String line;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            scores.append("<html>");
            int count = 0;
            while((line = bufferedReader.readLine()) != null) {
                scores.append(line + "<br>" );
                count++;
                if (count == num) {
                    break;
                }
            }
            scores.append("</html>");

            bufferedReader.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return scores.toString();
    }
    
    private void resetTimer(final JLabel timerLabel){
        final Timer timer = new Timer(40, null);
        timer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	final int elapsed = (int)(System.currentTimeMillis() - startTime);
            	
            	// update in the UI thread
            	SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int ms = elapsed % 1000;
                        int seconds = (elapsed / 1000) % 60;
                        int minutes = elapsed / 1000 / 60;
                        timerLabel.setText("Time: " + String.format("%d:%02d.%03d", minutes, seconds, ms));
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
            if (isGameActive && e.getID() == KeyEvent.KEY_PRESSED) {   // TODO: Debounce key press
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
                    if (maze.isLegalMove(player.getRow(), player.getCol(), d)) {
                        player.move(d);
                        score++;
                        refreshGrid();
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
    public void populateGrid() {
        grid.setLayout(new GridLayout(ROWS, COLS));
        grid.removeAll();
        isGameActive = true;
        maze = new Maze(ROWS, COLS);
        player = new Player();
        score = 0;

        // old refreshGrid()

        grid.removeAll();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);

                // get tile value, color label accordingly
                char tileValue = maze.getTileFrom(row, col).getValue();

                //get the correct image for a tile
                String icon = gridIcon(ROWS, COLS, row, col);

                // Scale the size based on number of rows and columns
                int scaledWidth = (WINDOW_WIDTH - RIGHT_PANEL_WIDTH) / COLS;
                int scaledHeight = WINDOW_HEIGHT / ROWS;

                if (row == player.getRow() && col == player.getCol()) {
                    label.setBackground(Color.BLUE);
                } else {
                    switch (tileValue) {
                        case Tile.START:
                        case Tile.FINISH:
                        case Tile.EMPTY:
                            label.setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
                            break;
                        case Tile.WALL:
                            //label.setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
                            label.setBackground(Color.DARK_GRAY);
                            break;
                    }
                }
                grid.add(label);
            }
        }

        // at start - disable reset
        if (resetButton != null) {
            if (player.getRow() == 0 && player.getCol() == 0) {
                resetButton.setEnabled(false);
            } else {
                resetButton.setEnabled(true);
            }
        }

        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }

        // must be called to refresh the whole JFrame
        revalidate();

        /*if (player.getRow() == ROWS - 1 && player.getCol() == COLS - 1) {
            // reached the finish tile - they are finished
            isGameActive = false;

            resetButton.setEnabled(false);

            // TODO: if high score, show this:
            // Allow user to write new high score
            String name = JOptionPane.showInputDialog("You win! \nScore: " + score + "\n \nNew high score! Enter your name:");
            writeHighScore(name, score);
            highScoresLabel.setText(readHighScores());

            // TODO: else, show this:
            // Show a popup telling the user they've finished the maze
                JOptionPane optionPane = new JOptionPane("You is winrar!\n\nScore: " + score,
                        JOptionPane.PLAIN_MESSAGE);
                JDialog finishDialog = optionPane.createDialog(this, "Congratulations!");
                finishDialog.setFont(baseFont);
                finishDialog.setVisible(true);
        }*/
    }


    public void refreshGrid() {
        if (isGameActive) {
            int row = player.getRow();
            int col = player.getCol();
            
            // new player pos
            JLabel labelCurr = new JLabel();
            labelCurr.setHorizontalAlignment(SwingConstants.CENTER);
            labelCurr.setVerticalAlignment(SwingConstants.CENTER);
            labelCurr.setOpaque(true);

            grid.remove(row * COLS + col);
            labelCurr.setBackground(Color.BLUE);
            grid.add(labelCurr,row * COLS + col);

            // Scale the size based on number of rows and columns
            int scaledWidth = (WINDOW_WIDTH - RIGHT_PANEL_WIDTH) / ROWS;
            int scaledHeight = WINDOW_HEIGHT / COLS;

            // old player pos
            JLabel labelPrev = new JLabel();
            labelPrev.setHorizontalAlignment(SwingConstants.CENTER);
            labelPrev.setVerticalAlignment(SwingConstants.CENTER);
            labelPrev.setOpaque(true);

            String prevIcon = gridIcon(ROWS, COLS, player.getPrevRow(), player.getPrevCol());
            int prevPosition = player.getPrevRow() * COLS + player.getPrevCol();

            labelPrev.setIcon(new ImageIcon(new ImageIcon(prevIcon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
            grid.remove(prevPosition);
            grid.add(labelPrev,prevPosition);

            // at start - disable reset
            if (resetButton != null) {
                if (player.getRow() == 0 && player.getCol() == 0) {
                    resetButton.setEnabled(false);
                } else {
                    resetButton.setEnabled(true);
                }
            }


            // must be called to refresh the whole JFrame
            revalidate();

            // check if player has reached an item
            if (maze.getTileFrom(player.getRow(), player.getCol()).getValue() == Tile.ITEM) {
            	score += ITEM_PICKUP;
            	maze.setTileEmpty(player.getRow(), player.getCol());
            }

            // refresh current score
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            // check if player has finished the maze
            if (player.getRow() == ROWS - 1 && player.getCol() == COLS - 1) {
                // reached the finish tile - they are finished
                isGameActive = false;

                resetButton.setEnabled(false);

                // TODO: if high score, show this:
                // Allow user to write new high score
                String name = JOptionPane.showInputDialog("You've cleared the dungeon!\n"
                        + "Score: " + score + "\n\n"
                        + "New high score! Enter your name:");
                writeHighScore(name, score);
                highScoresLabel.setText(readHighScores(4));

                // TODO: else, show this:
                // Show a popup telling the user they've finished the maze
//                JOptionPane optionPane = new JOptionPane("You win!\n\nScore: " + score,
//                        JOptionPane.PLAIN_MESSAGE);
//                JDialog finishDialog = optionPane.createDialog(this, "Congratulations!");
//
//                finishDialog.setVisible(true);
            }
        }
    }

    public String gridIcon(int rows, int cols, int row, int col) {
        //load the proper image for a tile
        String icon = null;
        char tileValue = maze.getTileFrom(row, col).getValue();

        //Can we go in these directions from current tile?
        boolean left = true;
        boolean right = true;
        boolean up = true;
        boolean down = true;

        switch (tileValue) {
            case Tile.WALL:
                //return "blank.png";
                //draw empty wall tile
                break;
            case Tile.START:
                //Test if tiles beside the start are walls
                if (maze.isWall(row + 1, col)){
                    down = false;
                }
                if (maze.isWall(row, col + 1)){
                    right = false;
                }
    
                //Give appropriate Tile
                if (down && right){
                    icon = "tile6_2.png";
                }
                if (down && !right){
                    icon = "tile6_3.png";
                }
                if (!down && right){
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
                if (maze.isWall(row - 1,col)){
                    up = false;
                }
                if (maze.isWall(row, col - 1)){
                    left = false;
                }
    
                //Give appropriate Tile
                if (up && left){
                    icon = "tile7_3.png";
                }
                if (up && !left){
                    icon = "tile7_2.png";
                }
                if (!up && left){
                    icon = "tile7.png";
                }
                if (!up && !left) {
                    // this should never happen - end is unreachable
                    // filler tile
                    icon = "tile7.png";
                }
                break;
            case Tile.EMPTY:
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
