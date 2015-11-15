package Battle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Board extends JPanel {
    static int cellSize = 50;
    static int numXCells = 15;
    static int numYCells = 10;
    static int gridLineThickness = 4;
    static int realCellSize = cellSize - (gridLineThickness/2);

    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, this.getWidth(),this.getHeight());

        drawGrid(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);

        // Vertical lines
        g.fillRect(0, 0, gridLineThickness/2, getHeight());
        for(int i=1; i<numXCells; i++) {
            g.fillRect((i-1)*cellSize+realCellSize, 0, gridLineThickness, getHeight());
        }
        g.fillRect((numXCells-1)*cellSize+realCellSize, 0, gridLineThickness/2, getHeight());

        // Horizontal lines
        g.fillRect(0, 0, getWidth(), gridLineThickness/2);
        for(int i=1; i<numYCells; i++) {
            g.fillRect(0, (i-1)*cellSize+realCellSize, getWidth(), gridLineThickness);
        }
        g.fillRect(0, (numYCells-1)*cellSize+realCellSize, getWidth(), gridLineThickness/2);
    }

    public Cell pixelToCell(int x, int y) {
        return new Cell(x/cellSize, y/cellSize);
    }
}
