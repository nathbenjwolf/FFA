package battle;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Created by Nathan on 1/10/2016.
 */

// Class responsible for maintaining the cell to pixel abstraction for Board class
public class CellPixelAbstraction {
    // Isometric size constants
    public static int cellYDelta = 40;
    public static int cellXDelta = (int)(1.6 * (double)cellYDelta);
    public static int cellThickness = 30;
    public static int gridLineThickness = 2;
    static double cellSideSize = Math.sqrt(Math.pow(cellYDelta,2) + Math.pow(cellXDelta,2));
    static int backgroundXPadding = cellXDelta/2;
    static int backgroundTopYPadding = cellYDelta;
    static int backgroundBottomYPadding = cellYDelta/5;
    static int healthBarXPadding = (int) ((double)cellXDelta * 0.50);
    static int healthBarYPadding = (int) ((double)cellYDelta * 0.10);
    public static int healthBarWidth = (int) ((double)cellXDelta * 1.0);
    public static int healthBarHeight = (int) ((double)cellYDelta * 0.2);

    // Board Border Line Values
    static float yAxisSlope = -(float)cellYDelta/(float)cellXDelta; // negative slope because (0,0) is in top left corner of screen
    static float xAxisSlope = -yAxisSlope;
    float yAxisIntercept;
    float xAxisIntercept;
    Cell yAxisStartPoint;
    Cell yAxisEndPoint;
    Cell xAxisStartPoint;
    Cell xAxisEndPoint;

    // Board specific values
    int numXCells;
    int numYCells;

    public CellPixelAbstraction(int numXCells, int numYCells) {
        this.numXCells = numXCells;
        this.numYCells = numYCells;

        init();
    }

    private void init() {
        // Pre-calculations

        // X-axis calculations
        xAxisStartPoint = cellToTopPixel(new Cell(0,0));
        xAxisEndPoint = cellToRightPixel(new Cell(numXCells-1,0));
        xAxisIntercept = xAxisStartPoint.y - xAxisSlope*xAxisStartPoint.x;

        // Y-axis calculations
        yAxisStartPoint = cellToTopPixel(new Cell(0,0));
        yAxisStartPoint.x--; // Left more of the two top pixels
        yAxisEndPoint = cellToLeftPixel(new Cell(0,numYCells-1));
        yAxisIntercept = yAxisStartPoint.y - yAxisSlope*yAxisStartPoint.x;

        return;
    }

    public int getBoardDesiredWidth() {
        return cellXDelta*(numYCells + numXCells) + backgroundXPadding*2;
    }

    public int getBoardDesiredHeight() {
        return cellYDelta*(numYCells + numXCells) + cellThickness + backgroundTopYPadding + backgroundBottomYPadding;
    }

    public Shape getCellShape(Cell cell) {
        Cell topPixel = cellToTopPixel(cell);

        GeneralPath path = new GeneralPath();
        path.moveTo(topPixel.x,topPixel.y);

        topPixel.x += cellXDelta-1;
        topPixel.y += cellYDelta-1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.y += 1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.x -= cellXDelta-1;
        topPixel.y += cellYDelta-1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.x -= 1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.x -= cellXDelta-1;
        topPixel.y -= cellYDelta-1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.y -= 1;
        path.lineTo(topPixel.x,topPixel.y);

        topPixel.x += cellXDelta-1;
        topPixel.y -= cellYDelta-1;
        path.lineTo(topPixel.x,topPixel.y);

        path.closePath();

        return path;
    }

    // Left thickness (modified y-axis)
    public Shape getLeftCellThicknessShape(Cell cell) {
        Cell startPixel = cellToStartLeftThicknessPixel(cell);

        GeneralPath path = new GeneralPath();
        path.moveTo(startPixel.x,startPixel.y);

        // Down
        startPixel.y += cellThickness;
        path.lineTo(startPixel.x,startPixel.y);

        // Down, Right
        startPixel.x += cellXDelta-1; // -1 for left more of the two bottom pixels
        startPixel.y += cellYDelta;
        path.lineTo(startPixel.x,startPixel.y);

        // Up
        startPixel.y -= cellThickness;
        path.lineTo(startPixel.x,startPixel.y);

        path.closePath();

        return path;
    }

    // Right thickness (modified x-axis)
    public Shape getRightCellThicknessShape(Cell cell) {
        Cell startPixel = cellToStartRightThicknessPixel(cell);

        GeneralPath path = new GeneralPath();
        path.moveTo(startPixel.x,startPixel.y);

        // Down
        startPixel.y += cellThickness;
        path.lineTo(startPixel.x,startPixel.y);

        // Down, Left
        startPixel.x -= cellXDelta-1; // -1 for right more of the two bottom pixels
        startPixel.y += cellYDelta;
        path.lineTo(startPixel.x,startPixel.y);

        // Up
        startPixel.y -= cellThickness;
        path.lineTo(startPixel.x,startPixel.y);

        path.closePath();

        return path;
    }

    // Right top pixel (there are two top pixels)
    public Cell cellToTopPixel(Cell cell) {
        // (0,0) top cell
        int xPixel = cellXDelta*numYCells + backgroundXPadding;
        int yPixel = backgroundTopYPadding;

        // Y cell offsets
        xPixel -= cellXDelta*cell.y;
        yPixel += cellYDelta*cell.y;

        // X cell offsets
        xPixel += cellXDelta*cell.x;
        yPixel += cellYDelta*cell.x;

        return new Cell(xPixel, yPixel);
    }

    // Upper of the two right pixels
    public Cell cellToRightPixel(Cell cell) {
        Cell topPixel = cellToTopPixel(cell);

        topPixel.x += cellXDelta-1;
        topPixel.y += cellYDelta-1;

        return topPixel;
    }

    // Upper of the two left pixels
    public Cell cellToLeftPixel(Cell cell) {
        Cell topPixel = cellToTopPixel(cell);

        topPixel.x -= cellXDelta;
        topPixel.y += cellYDelta-1;

        return topPixel;
    }

    public Cell cellToTLGroundDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= cellXDelta;

        return topCell;
    }

    public Cell cellToBRGroundDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta-1;
        topCell.y += cellYDelta*2-1;

        return topCell;
    }

    public Cell cellToTLObjectDrawPixel(Cell cell) {
        Cell adjustedCell = new Cell(cell);
        // Use cell directly above the current cell
        adjustedCell.x--;
        adjustedCell.y--;
        return cellToTLGroundDrawPixel(adjustedCell);
    }

    public Cell cellToBRObjectDrawPixel(Cell cell) {
        return cellToBRGroundDrawPixel(cell);
    }

    private Cell cellToStartLeftThicknessPixel(Cell cell) {
        return cellToTLLeftThicknessDrawPixel(cell); // happens to be the same as the top left pixel
    }

    public Cell cellToTLLeftThicknessDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= cellXDelta;
        topCell.y += cellYDelta+1; // 1 pixel below the edge of the cell surface

        return topCell;
    }

    public Cell cellToBRLeftThicknessDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= 1; // Left more of the top two pixels
        topCell.y += cellYDelta*2 + cellThickness;

        return topCell;
    }

    private Cell cellToStartRightThicknessPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta-1; // -1 so it stays in the same horizontal bounds as the surface
        topCell.y += cellYDelta+1; // +1 so it is below the cell surface

        return topCell;
    }

    public Cell cellToTLRightThicknessDrawPixel(Cell cell){
        Cell topCell = cellToTopPixel(cell);
        topCell.y += cellYDelta+1; // +1 so it is below the cell surface

        return topCell;
    }

    public Cell cellToBRRightThicknessDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta - 1;
        topCell.y += cellYDelta * 2 + cellThickness;

        return topCell;
    }

    public Cell cellToTLHealthBarPixel(Cell cell) {
        Cell TLPixel = cellToTLObjectDrawPixel(cell);
        TLPixel.x += healthBarXPadding;
        TLPixel.y += healthBarYPadding;

        return TLPixel;
    }

    public Cell pixelToCell(int x, int y) {
        Cell cell = new Cell(pixelToCellX(x,y),pixelToCellY(x,y));
        if(cell.x == -1 || cell.y == -1) {
            return null; // Cell out of bounds
        }
        return cell;
    }

    private double getPixelDistance(Cell start, Cell end) {
        return Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
    }

    private int pixelToCellX(int x, int y) {
        // Calculate y-intercept for pixel line
        float yIntercept = y - yAxisSlope*x;

        // X value of xAxis Pixel
        float xPixel = (xAxisIntercept - yIntercept)/(yAxisSlope-xAxisSlope);

        // Y value of xAxis Pixel
        float yPixel = (xAxisSlope*yIntercept - yAxisSlope*xAxisIntercept) / (xAxisSlope - yAxisSlope);

        // Check if the pixels are "in-bounds"
        if(xPixel >= xAxisStartPoint.x && xPixel <= xAxisEndPoint.x &&
                yPixel >= xAxisStartPoint.y && yPixel <= xAxisEndPoint.y) {
            double distance = getPixelDistance(xAxisStartPoint, new Cell((int)xPixel, (int)yPixel));
            int xCell = (int)(distance/cellSideSize);
            return xCell;
        } else {
            return -1; // Pixel out of bounds
        }
    }

    private int pixelToCellY(int x, int y) {
        // Calculate y-intercept for pixel line
        float yIntercept = y - xAxisSlope*x;

        // X value of yAxis Pixel
        float xPixel = (yAxisIntercept - yIntercept)/(xAxisSlope-yAxisSlope);

        // Y value of xAxis Pixel
        float yPixel = (yAxisSlope*yIntercept - xAxisSlope*yAxisIntercept) / (yAxisSlope - xAxisSlope);

        // Check if the pixels are "in-bounds"
        if(xPixel <= yAxisStartPoint.x && xPixel >= yAxisEndPoint.x &&
                yPixel >= yAxisStartPoint.y && yPixel <= yAxisEndPoint.y) {
            double distance = getPixelDistance(yAxisStartPoint, new Cell((int)xPixel, (int)yPixel));
            int yCell = (int)(distance/cellSideSize);
            return yCell;
        } else {
            return -1; // Pixel out of bounds
        }
    }
}
