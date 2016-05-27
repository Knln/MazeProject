/**
 * Represents a single tile in a Maze. This stores what type of
 * tile it is (e.g. wall, empty, item) and how many points are 
 * associated with it.
 */
public class Tile {
	/** Global variables */
    public static final char WALL = '#';
    public static final char EMPTY = '.';
    public static final char START = 's';
    public static final char FINISH = 'f';
    public static final char ITEM = 'o';
    public static final char KEY = 'k';
    public static final char DOOR = 'd';
    
    /** Class variables */
    private char value;
    private int score;

    /** Constructor */
    public Tile(char value, int score) {
        this.value = value;
        this.score = score;
    }
    
    /** Getters & Setters */

    public char getValue() {
        return value;
    }
    
    public void setValue(char value) {
        this.value = value;
    }
    
    public int getScore() {
        return score;
    }

}
