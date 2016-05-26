import java.util.List;

public class SolverTest {

    public void test() {
        int size = 10;
        Maze maze = new Maze(size, size);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(maze.getTileFrom(new Coordinate(i, j)).getValue());
            }
            System.out.print("\n");
        }
        
        Player p = new Player();
        p.setHasKey(true);
        
        MazeSolver solver = new MazeSolver();
        List<Direction> solution = solver.getBestPath(maze, p);
        if (solution == null) {
            System.out.println("Unsolvable");
        } else {
            System.out.println(solution.size());
            for (Direction d : solution) {
                switch (d) {
                    case UP:
                        System.out.print("U");
                        break;
                    case DOWN:
                        System.out.print("D");
                        break;
                    case LEFT:
                        System.out.print("L");
                        break;
                    case RIGHT:
                        System.out.print("R");
                        break;
                }
            }
        }
    }

}
