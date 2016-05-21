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
    
    public boolean is(int row, int col) {
        return row == this.row && col == this.col;
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
}
