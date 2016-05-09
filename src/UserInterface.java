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

    private boolean isGameActive;
    private Maze maze;
    private Player player;
    private JPanel grid;

    public UserInterface() {
        init();
    }

    /**
     * Initialise this user interface (JFrame)
     */
    public void init() {
        isGameActive = true;
        gameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
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
                score = 0;
                scoreLabel.setText("Score: " + score);
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
            if (isGameActive && e.getID() == KeyEvent.KEY_PRESSED) {   // TODO: Debounce key press
                Direction d;
                boolean arrowKeyPress = false;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        filler.setText("you pressed up");
                        d = Direction.UP;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        filler.setText("you pressed down");
                        d = Direction.DOWN;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        filler.setText("you pressed left");
                        d = Direction.LEFT;
                        arrowKeyPress = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        filler.setText("you pressed right");
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
                        refreshGrid(grid, ROWS, COLS);

                        scoreLabel.setText("Score: " + ++score);
                    }
                }
                refreshGrid(grid, ROWS, COLS);
            }
            return true;
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

                    if (row == player.getRow() && col == player.getCol()) {
                        label.setBackground(Color.BLUE);
                    } else if (tileValue == 's') {
                        label.setBackground(Color.RED);
                    } else if (tileValue == 'f') {
                        label.setBackground(Color.GREEN);
                    } else if (tileValue == 'e') {
                        label.setBackground(Color.WHITE);
                    } else if (tileValue == 'w') {
                        label.setBackground(Color.DARK_GRAY);
                    }

                    // check for start, finish, etc
                    if (player.getRow() == ROWS - 1 && player.getCol() == COLS - 1) {
                        // TODO: popup yo
                        isGameActive = false;
                        filler.setText("you is winrar");
                    }
                    grid.add(label);
                }
            }

            // must be called to refresh the whole JFrame
            revalidate();
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