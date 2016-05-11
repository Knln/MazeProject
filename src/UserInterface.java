import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;


public class UserInterface extends JFrame {

    /**
     * WTF is this
     */
    private static final long serialVersionUID = 1L;
    
    // Game and UI constants
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int RIGHT_PANEL_WIDTH = 320;
    private static final String GAME_NAME = "420 Maze It";

    // Swing globals
    private Font baseFont;
    private JLabel timerLabel;
    private JLabel scoreLabel;
		private Timer timer;
    private JPanel grid;
    private JButton resetButton;
		private long startTime;
    
    // Game fields and attributes
    private int score;
    private boolean isGameActive;
    private Maze maze;
    private Player player;

    public UserInterface() {
        init();
    }

    /**
     * Initialise this user interface (JFrame)
     */
    public void init() {
        isGameActive = true;
        baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
        score = 0;

        // parent panel that will hold everything
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));

        // first panel - a grid to hold the maze
        grid = new JPanel();
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(ROWS, COLS));

        populateGrid(grid, ROWS, COLS);
        parent.add(grid);

        // second panel on the right
        JPanel rhs = new JPanel();
        rhs.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 700));
        rhs.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 700));
        rhs.setLayout(new BoxLayout(rhs, BoxLayout.Y_AXIS));
        rhs.setBorder(BorderFactory.createEtchedBorder());
        
        // game name at the top
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
        rhs.add(Box.createVerticalStrut(20));

        // panel for current game stuff
        JPanel currentGamePanel = new JPanel();
        currentGamePanel.setLayout(new BoxLayout(currentGamePanel, BoxLayout.Y_AXIS));
        currentGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        currentGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 295));
        currentGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 295));
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

        timerLabel = new JLabel();
        timerLabel.setFont(baseFont);
        infoPanel.add(timerLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        TimeElapsed(timer, timerLabel);

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
        hintButton.setFont(baseFont);
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(UserInterface.this, "Do better");
            }
        });
        hintResetPanel.add(hintButton);

        resetButton = new JButton("Reset");
        resetButton.setFont(baseFont);
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.moveToStart();
                score = 0; // TODO maybe a penalty for resetting?
                refreshGrid(grid, ROWS, COLS);
        				TimeElapsed(timer, timerLabel);
            }
        });
        hintResetPanel.add(resetButton);

        currentGamePanel.add(hintResetPanel);

        rhs.add(currentGamePanel);
        rhs.add(Box.createVerticalStrut(10));

        // panel for new game stuff
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));
        newGamePanel.setAlignmentX(CENTER_ALIGNMENT);

        newGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 295));
        newGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 295));
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
        radioEasy.setSelected(true);
        JRadioButton radioMedium = new JRadioButton("Medium");
        radioMedium.setFont(baseFont);
        radioMedium.setFocusable(false);
        JRadioButton radioHard = new JRadioButton("Hard");
        radioHard.setFont(baseFont);
        radioHard.setFocusable(false);

        ButtonGroup difficultyRadioButtons = new ButtonGroup();
        difficultyRadioButtons.add(radioEasy);
        difficultyRadioButtons.add(radioMedium);
        difficultyRadioButtons.add(radioHard);

        difficultyBox.add(radioEasy);
        difficultyBox.add(radioMedium);
        difficultyBox.add(radioHard);

        newGamePanel.add(difficultyBox);

        // spacing after the difficulty section
        newGamePanel.add(Box.createVerticalStrut(60));

        // button that reloads the grid
        JButton button = new JButton("New Maze");
        button.setFont(baseFont);
        button.setToolTipText("Generate a new maze. All progress on the current maze will be lost.");
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.removeAll();
                score = 0;
                populateGrid(grid, ROWS, COLS);
        				TimeElapsed(timer, timerLabel);
            }
        });

        newGamePanel.add(button);
        rhs.add(newGamePanel);

        parent.add(rhs);

        this.add(parent);
        this.setTitle(GAME_NAME);
        this.setSize(1000, 750);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // set the arrow key dispatcher
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new ArrowKeyDispatcher());
    }

    private void TimeElapsed(Timer timer, final JLabel timerLabel){
        timer = new Timer(10, new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	int milliseconds = ((int) System.currentTimeMillis() - (int) startTime);
            	int seconds = milliseconds/ 1000;
            	int minutes = seconds / 60;
            	timerLabel.setText("Time Elapsed: "+ Integer.toString(minutes) +" m "+ Integer.toString(seconds%60) + " s " + Integer.toString(milliseconds%1000) + " ms");
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
                        refreshGrid(grid, ROWS, COLS);
                    }
                }
            }
            return false;
        }
    }

    /**
     * Populate the grid with a specified maze
     * @param grid - JPanel that has a GridLayout to add stuff to
     * @param rows - Number of rows in the grid
     * @param cols - Number of columns in the grid
     */
    public void populateGrid(JPanel grid, int rows, int cols) {
        grid.setLayout(new GridLayout(rows, cols));
        isGameActive = true;
        maze = new Maze(ROWS,COLS);
        player = new Player();
        refreshGrid(grid, rows, cols);
    }


    public void refreshGrid(JPanel grid, int rows, int cols) {
        if (isGameActive) {
            // first remove all existing tiles
            grid.removeAll();

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    JLabel label = new JLabel();
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                    // get tile value, color label accordingly
                    char tileValue = maze.getTileFrom(row, col).getValue();
                    //get the correct image for a tile
                    String icon = gridIcon(grid, rows, cols, tileValue, row, col);
                    
                    //Use these scales for when maze is is 10x10 (maybe for easy maze?)
                    int scaledWidth = 65;
                    int scaledHeight = 69;
                    
                    //Use these scales for when maze is 18x18 (maybe for medium maze?)
                    	//int scaledWidth = 35;
                    	//int scaledHeight = 39;

                    if (row == player.getRow() && col == player.getCol()) {
                        label.setBackground(Color.BLUE);
                    } else {
                        switch (tileValue) {
                            case 's':
                                label.setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
                                //label.setBackground(Color.RED);
                                break;
                            case 'f':
                                label.setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
                                //label.setBackground(Color.GREEN);
                                break;
                            case 'e':
                                label.setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT)));
                                //label.setBackground(Color.WHITE);
                                break;
                            case 'w':
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
            
            if (player.getRow() == ROWS - 1 && player.getCol() == COLS - 1) {
                // reached the finish tile - they are finished
                isGameActive = false;
                resetButton.setEnabled(false);
                
                // Show a popup telling the user they've finished the maze
                JOptionPane optionPane = new JOptionPane("You is winrar!\n\nScore: " + score,
                        JOptionPane.PLAIN_MESSAGE);
                JDialog finishDialog = optionPane.createDialog(this, "Congratulations!");
                finishDialog.setVisible(true);
            }
        }
    }
    
    public String gridIcon(JPanel grid, int rows, int cols, char tileValue, int row, int col) {
    	//load the proper image for a tile
    	
    	//Can we go in these directions from current tile?
    	boolean left = true;
    	boolean right = true;
    	boolean up = true;
    	boolean down = true;
    	
    	if (tileValue == 'w'){
    		//return "blank.png";
    		//draw empty wall tile
    		return null;
    	}
    	if (tileValue == 's'){
    		//Test if tiles beside the start are walls
    		if (maze.getTileFrom(row+1,col).getValue()=='w'){
    			down = false;
    		}
    		if (maze.getTileFrom(row,col+1).getValue()=='w'){
    			right = false;
    		}
    		
    		//Give appropriate Tile
    		if (down==true && right==true){
    			return "tile6_2.png";
    		}
    		if (down==true && right==false){
    			return "tile6_3.png";
    		}
    		if (down==false && right==true){
    			return "tile6.png";
    		}
    		
    	}
    	if (tileValue == 'f'){
    		//Test if tiles beside the start are walls
    		if (maze.getTileFrom(row-1,col).getValue()=='w'){
    			up = false;
    		}
    		if (maze.getTileFrom(row,col-1).getValue()=='w'){
    			left = false;
    		}
    		
    		//Give appropriate Tile
    		if (up==true && left==true){
    			return "tile7_3.png";
    		}
    		if (up==true && left==false){
    			return "tile7_2.png";
    		}
    		if (up==false && left==true){
    			return "tile7.png";
    		}
    	}
    	if (tileValue == 'e'){
    		//Test tiles not on edge of maze
    		if (row!=0 && maze.getTileFrom(row-1,col).getValue()=='w'){
    			up = false;
    		}   		
    		if (row!=rows-1 && maze.getTileFrom(row+1,col).getValue()=='w'){
    			down = false;
    		}
    		if (col!=0 && maze.getTileFrom(row,col-1).getValue()=='w'){
    			left = false;
    		}
    		if (col!=cols-1 && maze.getTileFrom(row,col+1).getValue()=='w'){
    			right = false;
    		}
    		
    		//Test for tiles on edge of maze
    		if (row==0) {
    			up = false;
    		}
    		if (col==0) {
    			left = false;
    		}
    		if (row==rows-1) {
    			down = false;
    		}
    		if (col==cols-1) {
    			right = false;
    		}
    		
    		//Test which tile we place
    		if (left==false && right==false && up==false && down==false){
    			return "blank_wall.png";
    		}
    		if (left==true && right==false && up==false && down==false){
    			return "tile4.png";
    		}
    		if (left==true && right==true && up==false && down==false){
    			return "tile5.png";
    		}
    		if (left==true && right==true && up==true && down==false){
    			return "tile2_2.png";
    		}   		
    		if (left==true && right==true && up==true && down==true){
    			return "tile1.png";
    		}
    		if (left==false && right==true && up==false && down==false){
    			return "tile4_3.png";
    		}
    		if (left==false && right==true && up==true && down==false){
    			return "tile3_3.png";
    		}
    		if (left==false && right==true && up==true && down==true){
    			return "tile2.png";
    		}
    		if (left==false && right==false && up==true && down==false){
    			return "tile4_4.png";
    		}
    		if (left==false && right==false && up==true && down==true){
    			return "tile5_2.png";
    		}
    		if (left==true && right==false && up==true && down==false){
    			return "tile3_4.png";
    		}
    		if (left==false && right==false && up==false && down==true){
    			return "tile4_2.png";
    		}   		
    		if (left==true && right==true && up==false && down==true){
    			return "tile2_4.png";
    		}
    		if (left==true && right==false && up==true && down==true){
    			return "tile2_3.png";
    		}
    		if (left==false && right==true && up==false && down==true){
    			return "tile3_2.png";
    		}
    		if (left==true && right==false && up==false && down==true){
    			return "tile3.png";
    		}
    	}   	
		return null;
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
