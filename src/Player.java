/**
 * Created by brad on 9/05/16.
 */
public class Player {
    
    private int col;
    private int row;
    
    private int prevCol;
    private int prevRow;
    
    private boolean hasKey;
    
    public Player() {
        moveToStart();
        
        prevCol = 0;
        prevRow = 0;
        hasKey = false;
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
    
    public boolean hasKey() {
        return hasKey;
    }
    
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

}
