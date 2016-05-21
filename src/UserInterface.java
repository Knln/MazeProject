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
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 880;
    private static final int RIGHT_PANEL_WIDTH = 320;
    private static final String GAME_NAME = "Dungeon Escape";
    
    public static final int EASY = 10;
    public static final int MEDIUM = 18; 
    public static final int HARD = 25;
    
    // set difficulty to easy to begin with
    private int difficulty;

    // Swing globals
    private Font baseFont;
    private JPanel parent;
    private JLabel movesLabel;
    private JLabel scoreLabel;
    private JPanel grid;
    private JButton resetButton;
    private JButton hintButton;
    private long startTime;
    
    // Game fields and attributes
    private int score;
    private int moves;
    private int resets;
    private int timeElapsed;
    private boolean isGameActive;
    private Maze maze;
    private Player player;
    private JLabel highScoresLabel;
    private JPanel inventoryPanel;

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
    
    private void initStartScreen() {
        ImagePanel holder = new ImagePanel("res/splash.jpg", null, 0, 0, false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
        
        holder.add(Box.createVerticalStrut(200));
        
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
                        "exit. All dungeons will have a single exit which is protected by a " +
                        "locked door - the key must be picked up before the player can make a " +
                        "clean escape. Along the way, items may also be picked up to increase " +
                        "final score.<br><br>" +
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
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(ROWS, COLS));

        populateGrid();
        holder.add(grid);

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

        currentGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 230));
        currentGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 230));
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

        currentGamePanel.add(infoPanel);
        currentGamePanel.add(Box.createVerticalStrut(6));

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
                + "Hint: -50 points<br>"
                + "Reset: restart with -50 points per reset"
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
        highScoresPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 200));
        highScoresPanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 200));
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

    private void showHint(List<Direction> path) {
    	// split our hint into a char array
    	// ie RRDDDR -> {'R','R','D','D','D','R'}
    	//char[] path = hint.toCharArray();
    	
    	int currRow = player.getRow();
    	int currCol = player.getCol();
    	int hintPosition = 0;
    	
    	for (Direction d : path) {
    		switch (d) {
	            case UP:
	            	currRow--;
	                break;
	            case DOWN:
	            	currRow++;
	                break;
	            case LEFT:
	            	currCol--;
	                break;
	            case RIGHT:
	            	currCol++;
	                break;
	            default:
	            	// ssshh go to sleep, no tears only dreams
    		}
    		hintPosition = ROWS * currRow + currCol;
    		
    		// get current component
        	ImagePanel withHint = (ImagePanel)grid.getComponent(hintPosition);
        	
        	// give it a beautiful border
        	// white border master race
        	withHint.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        	
        	grid.remove(hintPosition);
        	grid.add(withHint, hintPosition);
    	}
    }
    
    private void selectStartScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.first(parent);
    }
    
    private void selectMazeScreen() {
        CardLayout layout = (CardLayout) parent.getLayout();
        layout.last(parent);
    }

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
        StringBuilder output = new StringBuilder();
        output.append("<html>");
        // only get the top n scores
        int count = 0;
        for (Score sc : scores) {
            output.append("<b>" + sc.getScore() + "</b> " + sc.getName() + "<br>");
            count++;
            if (count == num) {
                break;
            }
        }
        output.append("</html>");
        return output.toString();
    }
    
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
                    if (maze.isLegalMove(player.getRow(), player.getCol(), d)) {
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
        maze = new Maze(ROWS, COLS);
        
        // TODO this part can be deleted once maze generation is working
        // guarantees a solvable maze
        MazeSolver solver = new MazeSolver();
        boolean solvable = false;
        
        while (!solvable) {
            maze = new Maze(ROWS, COLS);
            player.setHasKey(false);
            // check if there's a path from start to key
            if (solver.getBestPath(maze, player) != null) {
                player.setHasKey(true);
                // check if there's a path from start to finish
                if (solver.getBestPath(maze, player) != null) {
                    solvable = true;
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

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                
                // get tile value, color label accordingly
                char tileValue = maze.getTileFrom(row, col).getValue();

                // get the correct image for a tile
                String icon = gridIcon(ROWS, COLS, row, col);

                // Scale the size based on number of rows and columns
                int scaledWidth = (WINDOW_WIDTH - RIGHT_PANEL_WIDTH) / COLS;
                int scaledHeight = WINDOW_HEIGHT / ROWS;
                
                //isPlayer is initialized to false
                boolean isForegroundVisible = false;

                //initialize foregroundIcon
                String foregroundIcon = null;
                
                //check the Tile and set visibility and foregroundIcon
                if (row == player.getRow() && col == player.getCol()) {
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
                			break;			// TODO: the key part of this is getting a key picture working
                							// 		 it will really open doors
                							//		 james pls
                		case Tile.ITEM:
                			isForegroundVisible = true;
                			foregroundIcon = "res/treasure.png";
                			//foregroundIcon = "res/rupee.png";
                            break;
                	}
                }
                
                //add imagePanel to grid
                ImagePanel label2 = new ImagePanel(icon, foregroundIcon, scaledHeight, scaledWidth, isForegroundVisible);
                label2.setOpaque(true);
                label2.repaint(); 
                										//TODO remove this TODO.
                grid.add(label2);                
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

        // must be called to refresh the whole JFrame
        revalidate();
    }


    private void refreshGrid(boolean isReset) {
        if (isGameActive) {
            int row = player.getRow();
            int col = player.getCol();
            
            // Scale the size based on number of rows and columns
            int scaledWidth = (WINDOW_WIDTH - RIGHT_PANEL_WIDTH) / ROWS;
            int scaledHeight = WINDOW_HEIGHT / COLS;
            
            // new player pos   
            boolean isPlayer = true;
            String icon = gridIcon(ROWS, COLS, row, col);
            String foregroundIcon = null;   
            
            // determine direction and choose which player icon to display
            if (player.getPrevRow() == row+1 && player.getPrevCol() == col) {
            	//UP
            	foregroundIcon = "res/link2.png";
            } else if (player.getPrevRow() == row-1 && player.getPrevCol() == col) {
            	//DOWN
            	foregroundIcon = "res/link1.png";
            } else if (player.getPrevRow() == row && player.getPrevCol() == col+1) {
            	//LEFT
            	foregroundIcon = "res/link3.png";
            } else if (player.getPrevRow() == row && player.getPrevCol() == col-1) {
            	//RIGHT
            	foregroundIcon = "res/link4.png";
            }
            if (isReset) {
                // reset - make Link face right by default
                foregroundIcon = "res/link4.png";
            }

          
            // create new imagePanel for player's grid position
            ImagePanel labelCurr = new ImagePanel(icon, foregroundIcon, scaledHeight, scaledWidth, isPlayer);
            labelCurr.setOpaque(true);
            labelCurr.repaint(); 

            grid.remove(row * COLS + col);   
            grid.add(labelCurr,row * COLS + col);
            
            // old player pos
            String prevIcon = gridIcon(ROWS, COLS, player.getPrevRow(), player.getPrevCol());
            int prevPosition = player.getPrevRow() * COLS + player.getPrevCol();
            
            // replace previous position on grid (i.e. remove player's sprite)
            isPlayer = false;
            ImagePanel labelPrev = new ImagePanel(prevIcon, foregroundIcon, scaledHeight, scaledWidth, isPlayer);
            labelPrev.setOpaque(true);
            labelPrev.repaint();

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
            
            // update score
            score += maze.getTileFrom(player.getRow(), player.getCol()).getScore();

            if (movesLabel != null) {
                movesLabel.setText("Moves: " + moves);
            }
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            // must be called to refresh the whole JFrame
            revalidate();

            // if the player collects an item, replace the tile with an empty one
            if (maze.getTileFrom(player.getRow(), player.getCol()).getValue() == Tile.ITEM) {
            	maze.setTileEmpty(player.getRow(), player.getCol());
            	
            	// add an item to their inventory
            	// TODO treasure_inven.png is a placeholder until an actual item sprite is added
                JLabel label = new JLabel(new ImageIcon(new ImageIcon("res/treasure_inven.png").getImage()
                        .getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
                //label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                inventoryPanel.add(label);
                inventoryPanel.add(Box.createHorizontalStrut(10));
            }
            
            if (maze.getTileFrom(player.getRow(), player.getCol()).getValue() == Tile.KEY) {
            	maze.setTileEmpty(player.getRow(), player.getCol());
            	player.setHasKey(true);
            	
            	// add the key to their inventory
            	// TODO: get key icon working
                JLabel label = new JLabel(new ImageIcon(new ImageIcon("res/treasure_inven.png").getImage()
                        .getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
                //label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                inventoryPanel.add(label);
                inventoryPanel.add(Box.createHorizontalStrut(10));
            }

            // refresh current score
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }

            // check if player has finished the maze
            if (player.getRow() == maze.getFinishRow() && player.getCol() == maze.getFinishCol()
                    && player.hasKey()) {
                // reached the finish tile - they are finished
                isGameActive = false;

                resetButton.setEnabled(false);
                hintButton.setEnabled(false);
                
                // post-finish score calculation
                // note - ROWS conveniently holds the current difficulty
                switch (ROWS) {
                    case EASY:
                        // limit - 15 seconds, 40 moves
                        // scaling factor - 1.1
                        score += (40 - moves) * 100;
                        score += (15000 - timeElapsed) / 3;
                        score *= 1.1;
                        break;
                    case MEDIUM:
                        // limit - 30 seconds, 100 moves
                        // scaling factor - 0.7
                        score += (100 - moves) * 100;
                        score += (30000 - timeElapsed) / 3;
                        score *= 0.7;
                        break;
                    case HARD:
                        // limit - 45 seconds, 160 moves
                        // scaling factor - 0.55
                        score += (160 - moves) * 100;
                        score += (45000 - timeElapsed) / 3;
                        score *= 0.55;
                        break;
                }
                
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
            
            // reset items if we are resetting
            if (isReset) {
            	//variable for icon
                boolean isForegroundVisible = false;
                //initialize foregroundIcon
                foregroundIcon = null;
                //check the Tile and set visibility and foregroundIcon
    			isForegroundVisible = true;
            	
            	int[] itemRows = maze.getItemRows();
            	int[] itemCols = maze.getItemCols();
            	
            	for (int i = 0; i < itemRows.length; i++) {
            		if (itemRows[i] >= 0) {            			
            			//set tile as an item again, score purposes etc
            			maze.setTileItem(itemRows[i], itemCols[i]);
            			
            			//get correct icon for the item
                    	icon = gridIcon(ROWS, COLS, itemRows[i], itemCols[i]);

            			foregroundIcon = "res/treasure.png";
                        
                        //add imagePanel to grid
                        ImagePanel square = new ImagePanel(icon, foregroundIcon, scaledHeight, scaledWidth, isForegroundVisible);
                        square.setOpaque(true);
                        square.repaint(); 
                        grid.remove(itemRows[i] * COLS + itemCols[i]);
                        grid.add(square, itemRows[i] * COLS + itemCols[i]); 
            		}
            	}
            	// reset key only if required
            	if (player.hasKey()) {
	            	int keyRow = maze.getKeyRow();
	            	int keyCol = maze.getKeyCol();
	            	int keyIndex = keyRow * COLS + keyCol;

	            	//set tile as a key again, score purposes etc
            		maze.setTileKey(keyRow, keyCol);
            		
            		//create Key component and re-add
	            	icon = gridIcon(ROWS, COLS, keyRow, keyCol);
	            	ImagePanel square = new ImagePanel(icon, foregroundIcon, scaledHeight, scaledWidth, false);
	            	square.setOpaque(true);
	            	square.repaint(); 
	            	
	            	grid.remove(keyIndex);
	            	grid.add(square, keyIndex);
	            	
	            	// lastly take the key away
	            	player.setHasKey(false);
            	}
            	
            }
        }
    }

    private String gridIcon(int rows, int cols, int row, int col) {
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
            	//draw empty wall tile
                icon = "blank_wall.png";
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
            case Tile.ITEM:
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
        //System.out.printf("%s\n",icon);
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