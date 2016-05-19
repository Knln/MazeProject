import java.util.Random;

/**
 * Created by brad on 9/05/16.
 */
public class Maze {
    private Tile[][] tiles;
    private int ROWS;
    private int COLS;
    
    // TODO replace this coordinate stuff with the coordinate object 
    private int finishRow;
    private int finishCol;
    
    private int keyRow = 0;
    private int keyCol = 0;

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
        
        // make 3 positions for the items
        int[] itemRows = new int[4];
        int[] itemCols = new int[4];
        
        // initialize item positions
        int items = 0;
        switch (ROWS) {
            case UserInterface.EASY: items = 1; break;
            case UserInterface.MEDIUM: items = 2; break;
            case UserInterface.HARD: items = 4; break;
        }
        
        for (int i = 0; i < items; i++) {
            itemRows[i] = rand.nextInt(ROWS - 1);
            itemCols[i] = rand.nextInt(COLS - 1);
        }
        for (int i = items; i < 4; i++) {
            itemRows[i] = -1;
            itemCols[i] = -1;
        }
        
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {

                if (i == 0 && j == 0) {
                    // start
                    tiles[i][j] = new Tile(Tile.START, 0);
                } else if ( (i == itemRows[0] && j == itemCols[0])
                		 || (i == itemRows[1] && j == itemCols[1])
                		 || (i == itemRows[2] && j == itemCols[2])
                		 || (i == itemRows[3] && j == itemCols[3])) { 
                	// item spawn
                	tiles[i][j] = new Tile(Tile.ITEM, 200);	
            	} else if (i == ROWS-1 && j == COLS - 1) {
                    // finish
                    tiles[i][j] = new Tile(Tile.FINISH, 0);
                    finishRow = ROWS - 1;
                    finishCol = COLS - 1;
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
        tiles[row][col] = new Tile(Tile.EMPTY, 0);
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getCols() {
        return COLS;
    }
    
    public int getFinishRow() {
        return finishRow;
    }
    
    public int getFinishCol() {
        return finishCol;
    }
    
    public int getKeyRow() {
        return keyRow;
    }
    
    public int getKeyCol() {
        return keyCol;
    }

}
