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
    static int cellSize = 75;
    static int gridLineThickness = 4;
    static int realCellSize = cellSize - (gridLineThickness);

    int numXCells;
    int numYCells;
    MapElement[][] map;
    Set<Cell> moveCells = new HashSet<Cell>();

    private List<Character> team1;
    private List<Character> team2;
    private Map<Character, Cell> characterLocations = new HashMap<Character, Cell>();

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
            Cell TLPixel = cellToTLRealPixel(entry.getValue());
            Cell BRPixel = cellToBRRealPixel(entry.getValue());
            BufferedImage img = entry.getKey().getImage();

            g.drawImage(img,
                    TLPixel.x, TLPixel.y, BRPixel.x+1, BRPixel.y+1, // Extra +1 because drawImage -1
                    0, 0, img.getWidth(), img.getHeight(),
                    null);
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

    public void showMoveCells(Character character) {
        moveCells.clear();
        moveCells = character.getMovementCells(map, getTeamLocations(character), getEnemyLocations(character), characterLocations.get(character));
        repaint();
    }

    private boolean isMoveableCell(Cell cell) {
        return moveCells.contains(cell);
    }

    public boolean moveCharacter(Character character, Cell cell) {
        if(isMoveableCell(cell)) {
            characterLocations.put(character, cell);
            moveCells.clear();
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
        return team2.contains(character) ? team1 : team2;
    }

    private Set<Cell> getTeamLocations(Character character) {
        List<Character> team = getTeam(character);
        Set<Cell> teamLocations = new HashSet<>();
        for(Character teammate : team) {
            teamLocations.add(characterLocations.get(teammate));
        }

        return teamLocations;
    }

    private Set<Cell> getEnemyLocations(Character character) {
        List<Character> team = getEnemyTeam(character);
        Set<Cell> teamLocations = new HashSet<>();
        for(Character teammate : team) {
            teamLocations.add(characterLocations.get(teammate));
        }

        return teamLocations;
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
