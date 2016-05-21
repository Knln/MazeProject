import java.util.Random;

/**
 * A randomly generated maze populated with an assortment of tiles
 */
public class Maze {
    private Tile[][] tiles;
    private int ROWS;
    private int COLS;
    
    // TODO replace this coordinate stuff with the coordinate object 
    private Coordinate finishPos;
    private Coordinate keyPos;
    
    private Coordinate[] items;

    /**
     * Initialise the maze
     *
     * @param rows - Number of rows in the maze
     * @param cols - Number of columns in the maze
     */
    public Maze(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        tiles = new Tile[rows][cols];
        Random rand = new Random();
        
        // create a position for the key
        keyPos = new Coordinate(rand.nextInt(ROWS - 1), rand.nextInt(COLS - 1));
        finishPos = new Coordinate(ROWS - 1, COLS - 1);
        
        // create 3 positions for the items
        items = new Coordinate[4];
        
        // initialize item positions
        int itemCount = 0;
        switch (ROWS) {
            case UserInterface.EASY: itemCount = 1; break;
            case UserInterface.MEDIUM: itemCount = 2; break;
            case UserInterface.HARD: itemCount = 4; break;
        }
        
        for (int i = 0; i < itemCount; i++) {
            items[i] = new Coordinate(rand.nextInt(ROWS - 1), rand.nextInt(COLS - 1));
        }
        for (int i = itemCount; i < 4; i++) {
            items[i] = new Coordinate(-1, -1);
        }
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Coordinate thisPos = new Coordinate(i, j);

                if (i == 0 && j == 0) {
                    // start
                    tiles[i][j] = new Tile(Tile.START, 0);
                } else if (thisPos.equals(items[0]) || thisPos.equals(items[1])
                        || thisPos.equals(items[2]) || thisPos.equals(items[3])) { 
                	// item spawn
                	tiles[i][j] = new Tile(Tile.ITEM, 200);	
            	} else if (thisPos.equals(keyPos)) {
                    // key
                    tiles[i][j] = new Tile(Tile.KEY, 50);
                } else if (i == ROWS-2 && j == COLS - 1) {
                    // make a wall here, for when we put in the door (to be confirmed) TODO: confirm
                    tiles[i][j] = new Tile(Tile.WALL, 0);
                } else if (thisPos.equals(finishPos)) {
                    // finish
                    tiles[i][j] = new Tile(Tile.FINISH, 0);
                } else if (rand.nextFloat() > 0.7) {
                    // want 30% as walls
                    tiles[i][j] = new Tile(Tile.WALL, 0);
                } else {
                    // and the rest as empty spaces
                    tiles[i][j] = new Tile(Tile.EMPTY, 0);
                }
            }
        }
    }

    /**
     * Whether a move from a given position in a given direction is legal
     * @param pos - position to make the move from
     * @param direction - direction to move to
     * @return Whether the move is legal
     */
    public boolean isLegalMove(Coordinate pos, Direction direction) {
        Coordinate temp = pos.clone();
        temp.shift(direction);
        return !isWall(temp);
    }

    /**
     * Whether a specified tile is a wall
     * @param row - Row of tile to check
     * @param col - Column of tile to check
     * @return Whether it's a wall
     */
    public boolean isWall(Coordinate pos) {
        int row = pos.getRow();
        int col = pos.getCol();
        // prevent illegal array access
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true;
        }

        Tile t = getTileFrom(pos);
        return (t.getValue() == Tile.WALL);
    }
    
    public boolean isWall(int row, int col) {
        return isWall(new Coordinate(row, col));
    }

    /**
     * Whether a specified tile is a wall
     * @param row - Row of tile to check
     * @param col - Column of tile to check
     * @return Whether it's a wall
     */
    public Tile getTileFrom(Coordinate pos) {
        return tiles[pos.getRow()][pos.getCol()];
    }
    
    /**
     * Set a specific tile to be empty
     * @param row - Row of the tile to set
     * @param col - Column of the tile to set
     */
    public void setTileEmpty(Coordinate pos) {
        tiles[pos.getRow()][pos.getCol()] = new Tile(Tile.EMPTY, 0);
    }
    
    /**
     * Set a specific tile to be an item
     * @param row - Row of the tile to set
     * @param col - Column of the tile to set
     */
    public void setTileItem(Coordinate pos) {
        tiles[pos.getRow()][pos.getCol()] = new Tile(Tile.ITEM, 200);
    }
    
    /**
     * Set a specific tile to hold the maze's key
     * @param row - Row of the tile to set
     * @param col - Column of the tile to set
     */
    public void setTileKey(Coordinate pos) {
        tiles[pos.getRow()][pos.getCol()] = new Tile(Tile.KEY, 50);
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getCols() {
        return COLS;
    }
    
    public Coordinate getFinishPos() {
        return finishPos.clone();
    }
    
    public Coordinate getKeyPos() {
        return keyPos.clone();
    }
    
    public Coordinate[] getItemCoords() {
        return items.clone();
    }

}
