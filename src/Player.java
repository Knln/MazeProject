/**
 * Created by brad on 9/05/16.
 */
public class Player {
    
    private int col;
    private int row;
    
    public Player() {
        row = 0;
        col = 0;
    }
    
    public void move(Direction d) {
        switch (d) {
            case UP:
                row--;
                break;
            case DOWN:
                row++;
                break;
            case LEFT:
                col--;
                break;
            case RIGHT:
                col++;
                break;
        }
    }
    
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
