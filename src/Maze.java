import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * A randomly generated maze populated with an assortment of tiles
 */
public class Maze {
    private Tile[][] tiles;
    private int ROWS;
    private int COLS;
    
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
        
        finishPos = new Coordinate(ROWS - 1, COLS - 1);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // initialise as all walls
                tiles[i][j] = new Tile(Tile.WALL, 0);
            }
        }
        
        recursiveExcavation(ROWS/2, COLS/2);
        
        // set (0, 0) as the start
        tiles[0][0] = new Tile(Tile.START, 0);
        // and set the finish tile
        tiles[finishPos.getRow()][finishPos.getCol()] = new Tile(Tile.FINISH, 0);
        
        // the key will go in the middle
        int keyRow = ROWS / 2;
        int keyCol = COLS / 2;
        tiles[keyRow][keyCol] = new Tile(Tile.KEY, 100);
        keyPos = new Coordinate(keyRow, keyCol);
        
        // create positions for the items
        items = new Coordinate[4];
        
        // each difficulty has an associated amount of items
        int itemCount = 0;
        switch (ROWS) {
            case UserInterface.EASY: itemCount = 1; break;
            case UserInterface.MEDIUM: itemCount = 2; break;
            case UserInterface.HARD: itemCount = 4; break;
        }
        
        for (int i = 0; i < itemCount; i++) {
            int itemRow = rand.nextInt(ROWS - 1);
            int itemCol = rand.nextInt(COLS - 1);
            while (tiles[itemRow][itemCol].getValue() != Tile.EMPTY) {
                itemRow = rand.nextInt(ROWS - 1);
                itemCol = rand.nextInt(COLS - 1);
            }
            tiles[itemRow][itemCol] = new Tile(Tile.ITEM, 1000);
            items[i] = new Coordinate(itemRow, itemCol);
        }
        
        for (int i = itemCount; i < 4; i++) {
            items[i] = new Coordinate(-1, -1);
        }
    }
    
    public void recursiveExcavation(int x, int y){
        Stack<Coordinate> visited = new Stack<Coordinate>();
        ArrayList<Integer> randomArrayList = null;
        
        visited.push(new Coordinate(x, y));
        
        while (!visited.isEmpty()){
            randomArrayList = getRandomArray();
            int x_init = x;
            int y_init = y;
            
            for (Integer i: randomArrayList){
                //Up Direction
                if(i == 0){
                    if(x-2 < 0 || tiles[x-2][y].getValue() == Tile.EMPTY){
                        continue;
                    }
                    
                    if (tiles[x-2][y].getValue() != Tile.EMPTY){
                        tiles[x-2][y].setValue(Tile.EMPTY);
                        tiles[x-1][y].setValue(Tile.EMPTY);
                        visited.push(new Coordinate(x-2, y));
                        x = x-2;
                        break;
                    }
                }
                
                //Down Direction
                if(i == 1 ){
                    if(x+2 >= ROWS || tiles[x+2][y].getValue() == Tile.EMPTY){
                        continue;
                    } 
                    if(tiles[x+2][y].getValue() != Tile.EMPTY){
                        tiles[x+2][y].setValue(Tile.EMPTY);
                        tiles[x+1][y].setValue(Tile.EMPTY);
                        visited.push(new Coordinate(x+2, y));
                        x = x+2;
                        break;
                    }
                }
                
                //Left Direction
                if(i == 2){
                    if(y-2 < 0 || tiles[x][y-2].getValue() == Tile.EMPTY){
                        continue;
                    } 
                    if(tiles[x][y-2].getValue() != Tile.EMPTY){
                        tiles[x][y-2].setValue(Tile.EMPTY);
                        tiles[x][y-1].setValue(Tile.EMPTY);
                        visited.push(new Coordinate(x, y-2));
                        y=y-2;
                        break;
                    }
                }
                //Right Direction
                if(i == 3){
                    if(y+2 >= COLS || tiles[x][y+2].getValue() == Tile.EMPTY){
                        continue;
                    } 
                    if(tiles[x][y+2].getValue() != Tile.EMPTY){
                        tiles[x][y+2].setValue(Tile.EMPTY);
                        tiles[x][y+1].setValue(Tile.EMPTY);
                        visited.push(new Coordinate(x,y+2));
                        y=y+2;
                        break;
                    }
                }
            }
            
            // After for loop
            if(x_init == x && y_init == y){
                Coordinate temp = visited.pop();
                x = temp.getRow();
                y = temp.getCol();
            }
            
            randomArrayList.clear();
        }
    }
    
    public ArrayList<Integer> getRandomArray(){
        ArrayList<Integer> randomArrayList = new ArrayList<Integer>();
        //Random rand = new Random();
        
        for (int i = 0; i < 4; i++) {
            //randomArrayList.add(rand.nextInt(4));
            randomArrayList.add(i);
        }
        Collections.shuffle(randomArrayList);
        
        return randomArrayList;
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
        tiles[pos.getRow()][pos.getCol()] = new Tile(Tile.ITEM, 1000);
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