import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A class that assists with finding the best paths through a maze
 *
 */
public class MazeSolver {
    
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
        Coordinate position = player.getCurrPos();
        
        if (hasKey) {
            // navigate to the finish
            return navigate(maze, position, maze.getFinishPos());
        } else {
            // navigate to the key
            return navigate(maze, position, maze.getKeyPos());
        }
    }
    
    private Direction[] directions = {Direction.DOWN, Direction.RIGHT,
            Direction.LEFT, Direction.UP};
    
    /**
     * A* navigation from one place to another through a maze
     * @param maze - Maze to navigate
     * @param position - Current position
     * @param destination - Destination position
     * @return A list of directions to get from the specified start to the destination.
     *  Returns null if there is no possible path
     */
    private List<Direction> navigate(Maze maze, Coordinate position, Coordinate destination) {
        Hashtable<Coordinate, Boolean> visited = new Hashtable<Coordinate, Boolean>();
        PriorityQueue<State> queue = new PriorityQueue<State>();
        
        State start = new State(new ArrayList<Direction>(), 0, position);
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
            if (s.isFinished(destination)) {
                break;
            }
            visited.put(s.position, true);
            
            List<Direction> path = s.getPath();
            Coordinate currPos = s.getPosition();
            
            for (Direction d : directions) {
                if (maze.isLegalMove(currPos, d)) {
                    List<Direction> newPath = new ArrayList<Direction>(path);
                    newPath.add(d);
                    
                    Coordinate newPos = currPos.clone();
                    newPos.shift(d);
                    int heuristic = newPos.distanceFrom(destination);
                    if (!visited.containsKey(newPos)) { 
                        queue.add(new State(newPath, heuristic, newPos));
                    }
                }
            }
            
        }
        
        // note - this won't return a NullPointerException because the queue always has at least
        // one element - the starting state
        if (s.isFinished(destination)) {
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
        private Coordinate position;
        
        /**
         * Constructor for a search state in A*
         * @param path - current list of directions representing this state
         * @param heuristic - estimated cost to the destination
         * @param position - current position of this state
         */
        public State(List<Direction> path, int heuristic, Coordinate position) {
            this.path = path;
            this.heuristic = heuristic;
            this.position = position;
        }
        
        public List<Direction> getPath() { return path; }
        public Coordinate getPosition() { return position.clone(); }
        
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
        public boolean isFinished(Coordinate finish) {
            return position.equals(finish);
        }

        @Override
        public int compareTo(State s) {
            return getCost() - s.getCost();
        }
    }
    
}
