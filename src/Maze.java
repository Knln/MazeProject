import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * A randomly generated maze populated with an assortment of tiles
 * Has a key placed in the middle of the maze with the start and end tile at 0,0 and MAX(Length),MAX(Width)
 * Has chests placed randomly at a traversable square in the maze
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
            case UserInterface.HARD: itemCount = 3; break;
        }
        
        //Needs to be implemented and changed properly if time permits. Doing this tomorrow
        for (int i = 0; i < itemCount; i++) {
        	//ArrayList<Coordinate> Visited = new ArrayList<Coordinate>();
            int itemRow = rand.nextInt(ROWS - 1);
            int itemCol = rand.nextInt(COLS - 1);
            int DeadEndCount = 0;
            while (!checkDeadEnd(tiles, itemRow, itemCol)) {
            	DeadEndCount++;
            	if(DeadEndCount > 1000000){
                    if(tiles[itemRow][itemCol].getValue() != Tile.EMPTY){
                    	break;
                    }
            	}
                itemRow = rand.nextInt(ROWS - 1);
                itemCol = rand.nextInt(COLS - 1);
            }
            //Finding the chest gives 3000 points
            tiles[itemRow][itemCol] = new Tile(Tile.ITEM, 3000); 
            items[i] = new Coordinate(itemRow, itemCol);
        }
        
        for (int i = itemCount; i < 4; i++) {
            items[i] = new Coordinate(-1, -1);
        }
    }
    
    //Some horrendous code incoming
    //Checks each case for dead ends
    private boolean checkDeadEnd (Tile[][] tiles, int itemRow, int itemCol) {
    	
        if(tiles[itemRow][itemCol].getValue() != Tile.EMPTY){
        	return false;
        }
        
        //Checks case for the 0th row
    	if (itemRow == 0) {
    		if (itemCol == COLS-1) {
    			if(tiles[itemRow+1][itemCol].getValue() == Tile.WALL || tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
    				return true;
    			}
    		}
    		if (tiles[itemRow][itemCol-1].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		return false;
    		
    	//Checks case for the last row
    	} else if (itemRow == ROWS-1) {
    		if (itemCol == 0) {
    			if (tiles[itemRow-1][itemCol].getValue() == Tile.WALL || tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    				return true;
    			}
    		}
    		if (tiles[itemRow][itemCol-1].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		if (tiles[itemRow-1][itemCol].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		if (tiles[itemRow-1][itemCol].getValue() == Tile.WALL 
    	    	    	&& tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
    	    			return true;
    	    }
    		return false;
    	
    	//Checks case for the 0th column
    	} else if (itemCol == 0) {
    		if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
    	    	&& tiles[itemRow-1][itemCol].getValue() == Tile.WALL){
    			return true;
    		}
    		if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
        	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
        			return true;
        	}
    		if (tiles[itemRow-1][itemCol].getValue() == Tile.WALL 
        	    	&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
        			return true;
        	}
    		return false;
    	
    	//Checks case for the last column
    	} else if (itemCol == COLS-1){
       		if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
        	    	&& tiles[itemRow-1][itemCol].getValue() == Tile.WALL){
        			return true;
        	}
        	if (tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
            	    && tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
            		return true;
            }
        	if (tiles[itemRow-1][itemCol].getValue() == Tile.WALL 
            	    && tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
            		return true;
            }
        	return false;
    	}
    	
    	//Checks cases for which there is a dead end in the middle of the maze
    	if( tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
    		&& tiles[itemRow-1][itemCol].getValue() == Tile.WALL
    		&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL){
    		return true;
    	}  	
    	if(tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
        		&& tiles[itemRow-1][itemCol].getValue() == Tile.WALL
        		&& tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
        		return true;
        }      	
    	if(tiles[itemRow+1][itemCol].getValue() == Tile.WALL 
        		&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL
        		&& tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
        		return true;
        }
    	if(tiles[itemRow-1][itemCol].getValue() == Tile.WALL 
        		&& tiles[itemRow][itemCol+1].getValue() == Tile.WALL
        		&& tiles[itemRow][itemCol-1].getValue() == Tile.WALL){
        		return true;
        }
    	return false;
    }
    
    //It's a stack based excavation (Random DFS) to generate the maze
    public void recursiveExcavation(int x, int y){
        Stack<Coordinate> visited = new Stack<Coordinate>();
        List<Direction> randomList = null;
        
        visited.push(new Coordinate(x, y));
        
        while (!visited.isEmpty()){
            randomList = getRandomArray();
            int x_init = x;
            int y_init = y;
            
            for (Direction d: randomList){
                switch (d) {
                    case UP:
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
                        break;
                    
                    case DOWN:
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
                        break;
                
                    case LEFT:
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
                        break;

                    case RIGHT:
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
                        break;
                }
            }
            
            // After for loop
            if(x_init == x && y_init == y){
                Coordinate temp = visited.pop();
                x = temp.getRow();
                y = temp.getCol();
            }
            
            randomList.clear();
        }
    }
    
    //Chooses a random direction to construct the maze in
    private List<Direction> getRandomArray(){
        List<Direction> list = new ArrayList<Direction>();
        
        list.add(Direction.UP);
        list.add(Direction.DOWN);
        list.add(Direction.LEFT);
        list.add(Direction.RIGHT);
        
        Collections.shuffle(list);
        return list;
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
