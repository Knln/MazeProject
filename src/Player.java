/**
 * Represents a player of the game
 */
public class Player {
    
    private int col;
    private int row;
    
    private int prevCol;
    private int prevRow;
    
    private boolean hasKey;
    
    /**
     * Create a new player at the start (coords = [0, 0])
     */
    public Player() {
        moveToStart();
        
        prevCol = 0;
        prevRow = 0;
        hasKey = false;
    }
    
    /**
     * Move the player in a certain direction
     * @precondition The move is valid
     * @postcondition The player's position will be updated based on the move
     * @param d - Direction to move in
     */
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
    
    /**
     * Move the player back to the start (coords = [0, 0])
     */
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
    
    /**
     * Check whether the player has collected the key yet
     * @return Whether the player has the key
     */
    public boolean hasKey() {
        return hasKey;
    }
    
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

}
