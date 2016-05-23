public class Coordinate {
    private int row;
    private int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Coordinate) {
            Coordinate c = (Coordinate) o;
            return (row == c.getRow() && col == c.getCol());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return row * col + row * 41 + col * 37;
    }
    
    @Override
    public Coordinate clone() {
        return new Coordinate(row, col);
    }
    
    /**
     * Shift this coordinate in a direction
     * @param d - Direction to shift
     */
    public void shift(Direction d) {
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
     * Get the Manhattan distance from another Coordinate
     * Manhattan distance = |x1 - x2| + |y1 - y2|
     * @param c - Coordinate to compare to
     * @return Manhattan distance between two Coordinates
     */
    public int distanceFrom(Coordinate c) {
        return Math.abs(row - c.getRow()) + Math.abs(col - c.getCol());
    }
}
