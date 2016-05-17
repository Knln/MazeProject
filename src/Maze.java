import java.util.Random;

/**
 * Created by brad on 9/05/16.
 */
public class Maze {
    private Tile[][] tiles;
    private int ROWS;
    private int COLS;

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
        
        // make one position for the item
        int itemRow = rand.nextInt(ROWS);
        int itemCol = rand.nextInt(COLS);
        
        // TODO: place 2 items for medium, 3 items for hard.
        // 		 store item positions in some neat way
        
        // TODO maybe border the maze with walls?
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {

                if (i == 0 && j == 0) {
                    // start
                    tiles[i][j] = new Tile(Tile.START);
                } else if (i == itemRow && j == itemCol) { 
                	// item spawn
                	tiles[i][j] = new Tile(Tile.ITEM);	
            	} else if (i == ROWS-1 && j == COLS - 1) {
                    // finish
                    tiles[i][j] = new Tile(Tile.FINISH);
                } else if (rand.nextFloat() > 0.79) {
                    // want 30% as walls
                    tiles[i][j] = new Tile(Tile.WALL);
                } else {
                    // and the rest as empty spaces
                    tiles[i][j] = new Tile(Tile.EMPTY);
                }
            }
        }
    }

    /**
     * Whether a move from a given position in a given direction is legal
     * @param row - row of square to move from
     * @param col - column of square to move from
     * @param direction - direction to move to
     * @return Whether the move is legal
     */
    public boolean isLegalMove(int row, int col, Direction direction) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return false;
        }

        switch (direction) {
            case UP:
                return !isWall(row - 1, col);
            case DOWN:
                return !isWall(row + 1, col);
            case LEFT:
                return !isWall(row, col - 1);
            case RIGHT:
                return !isWall(row, col + 1);
            default:
                return false;
        }
    }

    /**
     * Whether a specified tile is a wall
     * @param row - Row of tile to check
     * @param col - Column of tile to check
     * @return Whether it's a wall
     */
    public boolean isWall(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true;
        }

        // TODO could make .isWall() or isSolid() part of the tile class
        Tile t = getTileFrom(row, col);
        return (t.getValue() == Tile.WALL);
    }

    /**
     * Whether a specified tile is a wall
     * @param row - Row of tile to check
     * @param col - Column of tile to check
     * @return Whether it's a wall
     */
    public Tile getTileFrom(int row, int col) {
        return tiles[row][col];
    }
    
    public void setTileEmpty(int row, int col) {
        tiles[row][col] = new Tile(Tile.EMPTY);
    }

}
