/**
 * Created by brad on 9/05/16.
 */
public class Tile
{
    public static final char WALL = 'w';
    public static final char EMPTY = 'e';
    public static final char START = 's';
    public static final char FINISH = 'f';
    
    char value;

    public Tile(char ch)
    {
        value = ch;
    }

    public char getValue()
    {
        return value;
    }

}
