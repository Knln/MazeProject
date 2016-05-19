/**
 * Created by brad on 9/05/16.
 */
public class Tile {
    public static final char WALL = '#';
    public static final char EMPTY = '.';
    public static final char START = 's';
    public static final char FINISH = 'f';
    public static final char ITEM = 'o';
    
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
