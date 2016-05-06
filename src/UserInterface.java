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
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class UserInterface extends JFrame {

    /**
     * WTF is this
     */
    private static final long serialVersionUID = 1L;
    
    private static final int ROWS = 20;
    private static final int COLS = 16;
    
    private static final int RIGHT_PANEL_WIDTH = 360;
    
    private Font gameFont;
    private JLabel filler;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private int score;

    public UserInterface() {
        init();
    }

    /**
     * Initialise this user interface (JFrame)
     */
    public void init() {
        
        gameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
        score = 0;
        
        // parent panel that will hold everything
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));

        // first panel - a grid to hold the maze
        final JPanel grid = new JPanel();
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
        
        // panel for current game stuff
        JPanel currentGamePanel = new JPanel();
        currentGamePanel.setLayout(new BoxLayout(currentGamePanel, BoxLayout.Y_AXIS));
        currentGamePanel.setAlignmentX(CENTER_ALIGNMENT);
        
        currentGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 400));
        currentGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 400));
        TitledBorder currentBorder = BorderFactory.createTitledBorder("Current Maze");
        currentBorder.setTitleFont(gameFont);
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
        
        timerLabel = new JLabel("Time elapsed: 00:00.0");
        timerLabel.setFont(gameFont);
        infoPanel.add(timerLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(gameFont);
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        
        filler = new JLabel("Some label");
        infoPanel.add(filler);
        
        currentGamePanel.add(infoPanel);
        currentGamePanel.add(Box.createVerticalStrut(20));
        
        // hint and reset buttons
        JPanel hintResetPanel = new JPanel();
        hintResetPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        
        JButton hintButton = new JButton("Hint");
        hintButton.setFont(gameFont);
        hintResetPanel.add(hintButton);
        
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(gameFont);
        resetButton.setEnabled(false);
        hintResetPanel.add(resetButton);
        
        currentGamePanel.add(hintResetPanel);
        
        rhs.add(currentGamePanel);
        
        // panel for new game stuff
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));
        newGamePanel.setAlignmentX(CENTER_ALIGNMENT);
        
        newGamePanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, 300));
        newGamePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 300));
        TitledBorder newBorder = BorderFactory.createTitledBorder("New Maze");
        newBorder.setTitleFont(gameFont);
        newBorder.setTitleJustification(TitledBorder.CENTER);
        newGamePanel.setBorder(BorderFactory.createCompoundBorder(
                newBorder, new EmptyBorder(10, 10, 10, 10)));
        
        // difficulty label and toggles - wrap in their own box
        JPanel difficultyBox = new JPanel();
        difficultyBox.setLayout(new BoxLayout(difficultyBox, BoxLayout.Y_AXIS));
        difficultyBox.setAlignmentX(CENTER_ALIGNMENT);
        difficultyBox.add(Box.createHorizontalGlue());
        
        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(gameFont);
        difficultyBox.add(difficultyLabel);
        difficultyBox.add(Box.createVerticalStrut(10));
        
        JRadioButton radioEasy = new JRadioButton("Easy");
        radioEasy.setFont(gameFont);
        radioEasy.setSelected(true);
        JRadioButton radioMedium = new JRadioButton("Medium");
        radioMedium.setFont(gameFont);
        JRadioButton radioHard = new JRadioButton("Hard");
        radioHard.setFont(gameFont);
        
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
        //newGamePanel.add(Box.createVerticalGlue());
        
        // button that reloads the grid
        JButton button = new JButton("New Maze");
        button.setFont(gameFont);
        button.setToolTipText("Generate a new maze. All progress on the current maze will be lost.");
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.removeAll();
                populateGrid(grid, ROWS, COLS);
                revalidate();
            }
        });
        
        newGamePanel.add(button);
        rhs.add(newGamePanel);
        
        parent.add(rhs);

        this.add(parent);
        this.setTitle("Maze");
        this.setSize(1000, 750);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        // set the arrow key dispatcher
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new ArrowKeyDispatcher());
    }
    
    /**
     * A KeyEventDispatcher that catches arrow key presses and performs the appropriate
     * action
     *
     */
    private class ArrowKeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        filler.setText("you pressed up");
                        break;
                    case KeyEvent.VK_DOWN:
                        filler.setText("you pressed down");
                        break;
                    case KeyEvent.VK_LEFT:
                        filler.setText("you pressed left");
                        break;
                    case KeyEvent.VK_RIGHT:
                        filler.setText("you pressed right");
                        break;
                }
            }
            return true;
        }
    }
    
    /**
     * Populate the grid with random tiles
     * TODO placeholder until we actually start generating mazes
     * @param grid - JPanel that has a GridLayout to add stuff to
     * @param rows - Number of rows in the grid
     * @param cols - Number of columns in the grid
     */
    public void populateGrid(JPanel grid, int rows, int cols) {
        grid.setLayout(new GridLayout(rows, cols));
        Random rand = new Random();
        
        // placeholder that populates the grid with random tiles
        // (black for wall, white for empty)
        for (int i = 0; i < rows * cols; i++) {
            int row = i / cols;
            int col = i % cols;
            
            //JLabel label = new JLabel(row + ", " + col);
            JLabel label = new JLabel();
            
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            
            if (row == 0 && col == 0) {
                label.setText("S");
                label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                label.setBackground(Color.WHITE);
                grid.add(label);
                continue;
            } else if (row == rows - 1 && col == cols - 1) {
                label.setText("F");
                label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                label.setBackground(Color.WHITE);
                grid.add(label);
                continue;
            }
            
            if (rand.nextFloat() > 0.70) {
                label.setBackground(Color.DARK_GRAY);
            } else {
                label.setBackground(Color.WHITE);
            }
            grid.add(label);
        }
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