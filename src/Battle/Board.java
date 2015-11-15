package Battle;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Set;
import character.*;
import character.Character;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Board extends JPanel {
    static int cellSize = 50;
    static int numXCells = 15;
    static int numYCells = 10;
    static int gridLineThickness = 4;
    static int realCellSize = cellSize - (gridLineThickness/2);


    Set<Cell> moveCells = new HashSet<Cell>();

    private Map<Character, Cell> characterLocations = new HashMap<Character, Cell>();
    private List<Character> team1;
    private List<Character> team2;

    public Board(List<Character> team1, List<Character> team2) {
        this.team1 = team1;
        this.team2 = team2;

        //TODO: Temporary code till character placement code exists
        //Team 1
        for(int i = 0; i < team1.size(); i++) {
            characterLocations.put(team1.get(i), new Cell(0,i+3));
        }

        //Team 1
        for(int i = 0; i < team2.size(); i++) {
            characterLocations.put(team2.get(i), new Cell(numXCells-1,i+3));
        }
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, this.getWidth(),this.getHeight());

        drawMoveCells(g);
        drawCharacters(g);
        drawGrid(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        Cell topLeftPixel;

        // Vertical lines
        g.fillRect(0, 0, gridLineThickness/2, getHeight());
        for(int i=1; i<numXCells; i++) {
            topLeftPixel = cellToPixel(new Cell(i,0));
            g.fillRect(topLeftPixel.x-gridLineThickness/2, 0, gridLineThickness, getHeight());
        }
        topLeftPixel = cellToPixel(new Cell(numXCells,0));
        g.fillRect(topLeftPixel.x-gridLineThickness/2, 0, gridLineThickness/2, getHeight());

        // Horizontal lines
        g.fillRect(0, 0, getWidth(), gridLineThickness/2);
        for(int i=1; i<numYCells; i++) {
            topLeftPixel = cellToPixel(new Cell(0,i));
            g.fillRect(0, topLeftPixel.y-gridLineThickness/2, getWidth(), gridLineThickness);
        }
        topLeftPixel = cellToPixel(new Cell(0,numYCells));
        g.fillRect(0, topLeftPixel.y-gridLineThickness/2, getWidth(), gridLineThickness/2);
    }

    private void drawMoveCells(Graphics g) {
        if(moveCells.size() == 0) {
            return;
        }

        for(Cell cell: moveCells) {
            drawCell(g, cell, Color.GREEN);
        }
    }

    private void drawCharacters(Graphics g) {
        for(Map.Entry<Character, Cell> entry : characterLocations.entrySet()) {
            drawCell(g, entry.getValue(), entry.getKey().color);
        }
    }

    private void drawCell(Graphics g, Cell cell, Color color) {
        g.setColor(color);
        Cell topLeftPixel = cellToPixel(cell);
        g.fillRect(topLeftPixel.x, topLeftPixel.y, cellSize, cellSize);
    }

    private Set<Cell> findPathableCells(Cell cell, int range) {
        Set<Cell> cells = new HashSet<Cell>(); // Need to implement
        return cells;
    }

    private Set<Cell> findRadialCells(Cell cell, int radius) {
        Set<Cell> cells = new HashSet<Cell>();
        cells.add(cell);

        if(radius == 0) {
            return cells;
        }

        if(cell.y-1 >= 0) {
            //UP
            cells.addAll(findRadialCells(new Cell(cell.x, cell.y-1), radius-1));
        }
        if(cell.y+1 < numYCells) {
            //DOWN
            cells.addAll(findRadialCells(new Cell(cell.x, cell.y+1), radius-1));
        }
        if(cell.x-1 >= 0) {
            //LEFT
            cells.addAll(findRadialCells(new Cell(cell.x-1, cell.y), radius-1));
        }
        if(cell.x+1 < numXCells) {
            //RIGHT
            cells.addAll(findRadialCells(new Cell(cell.x+1, cell.y), radius-1));
        }

        return cells;
    }

    private Set<Cell> findProjectileCells(Cell cell, int range) {
        return new HashSet<Cell>(); // Need to implement
    }

    public Set<Cell> findMoveCells(Cell cell, int range) {
        return findRadialCells(cell, range);
    }

    public void showMoveCells(Character character) { //TODO: Change to pass player
        moveCells.clear();
        moveCells = findMoveCells(characterLocations.get(character), character.moveRange);
        repaint();
    }

    private boolean isMoveableCell(Cell cell) {
        return moveCells.contains(cell);
    }

    public boolean moveCharacter(Character character, Cell cell) {
        if(isMoveableCell(cell)) {
            characterLocations.put(character, cell);
            repaint();
            return true;
        } else {
            return false;
        }
    }

    public Cell cellToPixel(Cell cell) {
        return new Cell(cell.x*cellSize, cell.y*cellSize);
    }

    public Cell pixelToCell(int x, int y) {
        return new Cell(x/cellSize, y/cellSize);
    }
}
