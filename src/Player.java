/**
 * Created by brad on 9/05/16.
 */
public class Player {
    
    private int col;
    private int row;
    
    private int prevCol;
    private int prevRow;
    
    public Player() {
        moveToStart();
        
        prevCol = 0;
        prevRow = 0;
    }
    
    public void move(Direction d) {
        prevCol = col;
        prevRow = row;
        
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
    
    public void moveToStart() {
        prevCol = col;
        prevRow = row;
        
        row = 0;
        col = 0;
    }
    
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPrevCol() {
        return prevCol;
    }

    public int getPrevRow() {
        return prevRow;
    }

}
