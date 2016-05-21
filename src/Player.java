/**
 * Represents a player of the game
 */
public class Player {
    
    private Coordinate currPos;
    private Coordinate prevPos;
    private Direction lastMove;
    private boolean hasKey;
    
    /**
     * Create a new player at the start (coords = [0, 0])
     */
    public Player() {
        currPos = new Coordinate(0, 0);
        prevPos = new Coordinate(0, 0);
        hasKey = false;
        lastMove = Direction.RIGHT;
        
        moveToStart();
    }
    
    /**
     * Move the player in a certain direction
     * @precondition The move is valid
     * @postcondition The player's position will be updated based on the move
     * @param d - Direction to move in
     */
    public void move(Direction d) {
        prevPos = currPos.clone();
        lastMove = d;
        currPos.shift(d);
    }
    
    /**
     * Move the player back to the start (coords = [0, 0])
     */
    public void moveToStart() {
        prevPos = currPos.clone();
        currPos.setRow(0);
        currPos.setCol(0);
    }
    
    public Coordinate getCurrPos() {
        return currPos.clone();
    }
    
    public Coordinate getPrevPos() {
        return prevPos.clone();
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
    
    public Direction getLastMove() {
        return lastMove;
    }

}
