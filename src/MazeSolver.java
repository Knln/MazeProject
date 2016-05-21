import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A class that assists with finding the best paths through a maze
 *
 */
public class MazeSolver {
    
    // TODO replace row, col fields with coordinate objects
    /**
     * Find the best path for solving the maze. If the player has the key,
     * this will find the best path to the exit. If they don't have the key
     * yet, this will find the best path to the key.
     * @param maze - Maze to find the best path for
     * @param player - Player playing the maze
     * @return A list of directions to represent the best path through the maze
     */
    public List<Direction> getBestPath(Maze maze, Player player) {
        boolean hasKey = player.hasKey();
        int row = player.getRow();
        int col = player.getCol();
        
        if (hasKey) {
            // navigate to the finish
            return navigate(maze, row, col, maze.getFinishRow(), maze.getFinishCol());
        } else {
            // navigate to the key
            return navigate(maze, row, col, maze.getKeyRow(), maze.getKeyCol());
        }
    }
    
    private Direction[] directions = {Direction.DOWN, Direction.RIGHT,
            Direction.LEFT, Direction.UP};
    
    /**
     * A* navigation from one place to another through a maze
     * @param maze - Maze to navigate
     * @param startRow - starting row
     * @param startCol - starting column
     * @param endRow - destination row
     * @param endCol - destination column
     * @return A list of directions to get from the specified start to the destination.
     *  Returns null if there is no possible path
     */
    private List<Direction> navigate(Maze maze, int startRow, int startCol, int endRow, int endCol) {
        // indexed on row * cols + col
        Hashtable<Integer, Boolean> visited = new Hashtable<Integer, Boolean>();
        PriorityQueue<State> queue = new PriorityQueue<State>();
        
        State start = new State(new ArrayList<Direction>(), 0, startRow, startCol);
        queue.add(start);
        
        int count = 0;
        
        State s = null;
        while (!queue.isEmpty()) {
            count++;
            if (count > 10000) {
                // unsolvable fail-safe - the algorithm should be able to finish
                // in much less than 10,000 iterations, but just to be safe;
                return null;
            }
            
            s = queue.remove();
            if (s.isFinished(endRow, endCol)) {
                break;
            }
            visited.put(s.getRow() * maze.getCols() + s.getCol(), true);
            
            List<Direction> path = s.getPath();
            int currRow = s.getRow();
            int currCol = s.getCol();
            
            for (Direction d : directions) {
                if (maze.isLegalMove(currRow, currCol, d)) {
                    List<Direction> newPath = new ArrayList<Direction>(path);
                    newPath.add(d);
                    int newRow = currRow;
                    int newCol = currCol;
                    switch (d) {
                        case DOWN:
                            newRow++;
                            break;
                        case RIGHT:
                            newCol++;
                            break;
                        case LEFT:
                            newCol--;
                            break;
                        case UP:
                            newRow--;
                            break;
                    }
                    int heuristic = Math.abs(newCol - endCol) + Math.abs(newRow - endRow);
                    
                    if (!visited.containsKey(newRow * maze.getCols() + newCol)) { 
                        queue.add(new State(newPath, heuristic, newRow, newCol));
                    }
                }
            }
            
        }
        
        // note - this won't return a NullPointerException because the queue always has at least
        // one element - the starting state
        if (s.isFinished(endRow, endCol)) {
            return s.getPath();
        } else {
            // return null if there is no possible path
            return null;
        }
    }
    
    /**
     * Class representing a state in the A* search for finding the shortest path
     * through the maze.
     * Implements Comparable so it can be used in a priority queue for A*
     *
     */
    private class State implements Comparable<State> {
        private List<Direction> path;
        private int heuristic;
        private int row;
        private int col;
        
        /**
         * Constructor for a search state in A*
         * @param path - current list of directions representing this state
         * @param heuristic - estimated cost to the destination
         * @param row - current row of this state
         * @param col - current column of this state
         */
        public State(List<Direction> path, int heuristic, int row, int col) {
            this.path = path;
            this.heuristic = heuristic;
            this.row = row;
            this.col = col;
        }
        
        public List<Direction> getPath() { return path; }
        public int getRow() { return row; }
        public int getCol() { return col; }
        
        /**
         * Get total A* cost f(n) = g(n) + h(n) 
         * @return Total cost f(n)
         */
        public int getCost() {
            return path.size() + heuristic;
        }
        
        /**
         * Determines if this is a finished state
         * @param finishRow - destination row
         * @param finishCol - destination column
         * @return Whether this is a finished state
         */
        public boolean isFinished(int finishRow, int finishCol) {
            return (row == finishRow && col == finishCol);
        }

        @Override
        public int compareTo(State s) {
            return getCost() - s.getCost();
        }
    }
    
}
