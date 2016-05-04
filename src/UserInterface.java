import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;


public class UserInterface extends JFrame {

    /**
     * WTF is this
     */
    private static final long serialVersionUID = 1L;
    
    private static final int ROWS = 10;
    private static final int COLS = 8;

    public UserInterface() {
        init();
    }

    /**
     * Initialise this user interface (JFrame)
     */
    public void init() {
        
        // parent panel that will hold everything
        JPanel parent = new JPanel(new GridLayout());

        // first panel - a grid to hold the maze
        final JPanel grid = new JPanel();
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        grid.setLayout(new GridLayout(ROWS, COLS));

        populateGrid(grid, ROWS, COLS);

        parent.add(grid);
        
        // second panel on the right
        JPanel rhs = new JPanel();
        rhs.setLayout(new BoxLayout(rhs, BoxLayout.Y_AXIS));
        rhs.setBorder(new BevelBorder(BevelBorder.LOWERED));
        rhs.setBackground(new Color(0xF6F6F6));
        
        JLabel label = new JLabel("This is the right-hand side");
        label.setHorizontalAlignment(SwingConstants.CENTER);;
        rhs.add(label);
        
        // button that reloads the grid
        JButton button = new JButton("Reload grid");
        button.setSize(100, 50);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.removeAll();
                populateGrid(grid, ROWS, COLS);
                revalidate();
            }
        });
        rhs.add(button);
        parent.add(rhs);

        this.add(parent);
        this.setTitle("Maze");
        this.setSize(900, 600);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Populate the grid with random tiles
     * TODO placeholder until we actually start generating mazes
     * @param grid - JPanel that has a GridLayout to add stuff to
     * @param rows - Number of rows in the grid
     * @param cols - Number of columns in the grid
     */
    public void populateGrid(JPanel grid, int rows, int cols) {
        Random rand = new Random();
        
        // placeholder that populates the grid with random tiles
        // (black for wall, white for empty)
        for (int i = 0; i < rows * cols; i++) {
            JLabel label = new JLabel();
            
            if (rand.nextFloat() > 0.65) {
                label.setBackground(Color.DARK_GRAY);
            } else {
                label.setBackground(Color.WHITE);
            }
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
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