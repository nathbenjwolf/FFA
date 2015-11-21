package battle;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Set;

import character.Character;
import mapElement.MapElement;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Board extends JPanel {
    static int cellSize = 50;
    static int gridLineThickness = 3;
    static int realCellSize = cellSize - (gridLineThickness);

    int numXCells;
    int numYCells;
    MapElement[][] map;
    Set<Cell> moveCells = new HashSet<Cell>();

    private Map<Character, Cell> characterLocations = new HashMap<Character, Cell>();
    private List<Character> team1;
    private List<Character> team2;

    public Board(List<Character> team1, List<Character> team2, String map) {
        this.team1 = team1;
        this.team2 = team2;
        this.map = MapParser.decodeMap(map);
        this.numXCells = this.map.length;
        this.numYCells = this.map[0].length;

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
        drawMap(g);
        drawCharacters(g);
        drawGrid(g);
        drawMoveCells(g);
    }

    private void drawMap(Graphics g) {
        for(int x=0; x<map.length; x++) {
            for(int y=0; y<map[0].length; y++) {
                Cell TLPixel = cellToTLRealPixel(new Cell(x,y));
                Cell BRPixel = cellToBRRealPixel(new Cell(x,y));
                BufferedImage img = map[x][y].getImage();

                g.drawImage(img,
                        TLPixel.x, TLPixel.y, BRPixel.x+1, BRPixel.y+1, // Extra +1 because drawImage -1
                        0, 0, img.getWidth(), img.getHeight(),
                        null);
            }
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        Cell topLeftPixel;

        // Vertical lines
        g.fillRect(0, 0, gridLineThickness, getHeight());
        for(int i=1; i<numXCells; i++) {
            topLeftPixel = cellToTLPixel(new Cell(i,0));
            g.fillRect(topLeftPixel.x-gridLineThickness, 0, gridLineThickness*2, getHeight());
        }
        topLeftPixel = cellToTLPixel(new Cell(numXCells,0));
        g.fillRect(topLeftPixel.x-gridLineThickness, 0, gridLineThickness, getHeight());

        // Horizontal lines
        g.fillRect(0, 0, getWidth(), gridLineThickness);
        for(int i=1; i<numYCells; i++) {
            topLeftPixel = cellToTLPixel(new Cell(0,i));
            g.fillRect(0, topLeftPixel.y-gridLineThickness, getWidth(), gridLineThickness*2);
        }
        topLeftPixel = cellToTLPixel(new Cell(0,numYCells));
        g.fillRect(0, topLeftPixel.y-gridLineThickness, getWidth(), gridLineThickness);
    }

    private void drawMoveCells(Graphics g) {
        if(moveCells.size() == 0) {
            return;
        }

        for(Cell cell: moveCells) {
            drawCellBorder(g, cell, Color.RED);
        }
    }

    private void drawCharacters(Graphics g) {
        for(Map.Entry<Character, Cell> entry : characterLocations.entrySet()) {
            drawCell(g, entry.getValue(), entry.getKey().color);
        }
    }

    private void drawCell(Graphics g, Cell cell, Color color) {
        g.setColor(color);
        Cell topLeftPixel = cellToTLPixel(cell);
        g.fillRect(topLeftPixel.x, topLeftPixel.y, cellSize, cellSize);
    }

    private void drawCellBorder(Graphics g, Cell cell, Color color) {
        g.setColor(color);
        Cell topLeftPixel = cellToTLPixel(cell);
        g.fillRect(topLeftPixel.x, topLeftPixel.y, cellSize, gridLineThickness);
        g.fillRect(topLeftPixel.x, topLeftPixel.y, gridLineThickness, cellSize);
        g.fillRect(topLeftPixel.x, topLeftPixel.y+cellSize-gridLineThickness, cellSize, gridLineThickness);
        g.fillRect(topLeftPixel.x+cellSize-gridLineThickness, topLeftPixel.y, gridLineThickness, cellSize);
    }

    private Set<Cell> findPathableCells(Character character, Cell cell, int range) {
        Set<Cell> cells = new HashSet<Cell>();
        cells.add(new Cell(cell.x, cell.y));

        if(range == 0) {
            return cells;
        }

        // Cannot pass movement blocking cells or enemy units

        // UP
        cell.y--;
        if(cell.y >= 0 && !map[cell.x][cell.y].isElementMovementBlocking(character) && getTeamOnCell(getEnemyTeam(character),cell) == null) {
            cells.addAll(findPathableCells(character, cell, range-1));
        }
        cell.y++;

        // DOWN
        cell.y++;
        if(cell.y < numYCells && !map[cell.x][cell.y].isElementMovementBlocking(character) && getTeamOnCell(getEnemyTeam(character),cell) == null) {
            cells.addAll(findPathableCells(character, cell, range-1));
        }
        cell.y--;

        // LEFT
        cell.x--;
        if(cell.x >= 0 && !map[cell.x][cell.y].isElementMovementBlocking(character) && getTeamOnCell(getEnemyTeam(character),cell) == null) {
            cells.addAll(findPathableCells(character, cell, range-1));
        }
        cell.x++;

        // RIGHT
        cell.x++;
        if(cell.x < numXCells && !map[cell.x][cell.y].isElementMovementBlocking(character) && getTeamOnCell(getEnemyTeam(character),cell) == null) {
            cells.addAll(findPathableCells(character, cell, range-1));
        }
        cell.x--;

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

    public Set<Cell> findMoveCells(Character character, Cell cell, int range) {
        Set<Cell> pathableCells = findPathableCells(character, cell, range);
        // Remove teammates (can't stand on the same cell but can path through them)
        for(Character teammate : getTeam(character)) {
            if(pathableCells.contains(characterLocations.get(teammate))) {
                pathableCells.remove(characterLocations.get(teammate));
            }
        }

        return pathableCells;
    }

    public void showMoveCells(Character character) {
        moveCells.clear();
        moveCells = findMoveCells(character, new Cell(characterLocations.get(character)), character.moveRange);
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

    private Character getTeamOnCell(List<Character> team, Cell cell) {
        for(Character character : team) {
            if(characterLocations.get(character).equals(cell)) {
                return character;
            }
        }
        return null;
    }

    private List<Character> getTeam(Character character) {
        return team1.contains(character) ? team1 : team2;
    }

    private List<Character> getEnemyTeam(Character character) {
        return team1.contains(character) ? team2 : team1;
    }

    public Cell cellToTLPixel(Cell cell) {
        return new Cell(cell.x*cellSize, cell.y*cellSize);
    }

    public Cell cellToBRPixel(Cell cell) {
        return new Cell((cell.x+1)*cellSize-1, (cell.y+1)*cellSize-1);
    }

    public Cell cellToTLRealPixel(Cell cell) {
        return new Cell(cell.x*cellSize+gridLineThickness, cell.y*cellSize+gridLineThickness);
    }

    public Cell cellToBRRealPixel(Cell cell) {
        return new Cell((cell.x+1)*cellSize-gridLineThickness-1, (cell.y+1)*cellSize-gridLineThickness-1);
    }

    public Cell pixelToCell(int x, int y) {
        return new Cell(x/cellSize, y/cellSize);
    }
}
