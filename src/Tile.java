/**
 * Represents a single tile in a Maze. This stores what type of
 * tile it is (e.g. wall, empty, item) and how many points are 
 * associated with it.
 */
public class Tile {
    public static final char WALL = '#';
    public static final char EMPTY = '.';
    public static final char START = 's';
    public static final char FINISH = 'f';
    public static final char ITEM = 'o';
    public static final char KEY = 'k';
    public static final char DOOR = 'd';
    
    private char value;
    private int score;

    public Tile(char value, int score) {
        this.value = value;
        this.score = score;
    }

    public char getValue() {
        return value;
    }
    
    public int getScore() {
        return score;
    }

}
