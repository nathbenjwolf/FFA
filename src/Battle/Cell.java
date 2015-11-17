package battle;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Cell {
    public int x; // index of horizontal cells (column)
    public int y; // index of vertical cells (row)

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof Cell) {
            return this.x == ((Cell) object).x && this.y == ((Cell) object).y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.x + this.y*Board.numXCells;
    }
}
