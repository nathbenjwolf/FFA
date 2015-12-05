package battle;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Set;

import ability.Ability;
import character.Character;
import mapElement.MapElement;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Board extends JPanel {
    static int cellSize = 75;
    static int gridLineThickness = 4;
    static int realCellSize = cellSize - (gridLineThickness);
    static int healthBarPadding = (int) ((double)Board.realCellSize * 0.05);
    static int healthBarWidth = (int) ((double)Board.realCellSize * 0.7);
    static int healthBarHeight = (int) ((double)Board.realCellSize * 0.1);

    int numXCells;
    int numYCells;
    MapElement[][] map;
    Set<Cell> moveCells = new HashSet<>();
    Set<Cell> abilityTargetCells = new HashSet<>();
    Set<Cell> abilityRangeCells = new HashSet<>();

    private List<Character> team1;
    private List<Character> team2;
    private List<Character> allCharacters;
    private Map<Character, Cell> characterLocations = new HashMap<>();

    public Board(List<Character> team1, List<Character> team2, String map) {
        this.team1 = team1;
        this.team2 = team2;
        this.allCharacters = new ArrayList<>();
        this.allCharacters.addAll(team1);
        this.allCharacters.addAll(team2);
        this.map = MapParser.decodeMap(map);
        this.numXCells = this.map.length;
        this.numYCells = this.map[0].length;

        //TODO: Temporary code till character placement code exists
        //Team 1
        for(int i = 0; i < team1.size(); i++) {
            team1.get(i).cell = new Cell(0,i+3);
        }

        //Team 1
        for(int i = 0; i < team2.size(); i++) {
            team2.get(i).cell = new Cell(numXCells-1,i+3);
        }
    }

    public void paintComponent(Graphics g) {
        drawMap(g);
        drawCharacters(g, allCharacters);
        drawGrid(g);
        drawMoveCells(g);
        drawAbilityCells(g);
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

    private void drawAbilityCells(Graphics g) {
        for(Cell cell: abilityRangeCells) {
            drawCellBorder(g, cell, Color.YELLOW);
        }

        for(Cell cell: abilityTargetCells) {
            drawCellBorder(g, cell, Color.MAGENTA);
        }
    }

    private void drawCharacters(Graphics g, List<Character> characters) {
        for(Character character : characters) {
            Cell TLPixel = cellToTLRealPixel(character.cell);
            Cell BRPixel = cellToBRRealPixel(character.cell);
            // Character image
            BufferedImage img = character.getImage();

            g.drawImage(img,
                    TLPixel.x, TLPixel.y, BRPixel.x+1, BRPixel.y+1, // Extra +1 because drawImage -1
                    0, 0, img.getWidth(), img.getHeight(),
                    null);

            // Health bar
            drawHealthBar(g, character);
        }
    }

    private void drawHealthBar(Graphics g, Character character) {
        Cell TLPixel = cellToTLRealPixel(character.cell);
        TLPixel.x += healthBarPadding;
        TLPixel.y += healthBarPadding;

        // Health bar border
        g.setColor(Color.WHITE);
        g.drawRect(TLPixel.x, TLPixel.y, healthBarWidth, healthBarHeight);

        // Health bar contents
        g.setColor(Color.RED);
        int healthWidth = (int)((double)(healthBarWidth-1) * ((double)character.currentHealth/(double)character.totalHealth));
        g.fillRect(TLPixel.x+1, TLPixel.y+1, healthWidth, healthBarHeight-1);
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
        moveCells = character.getMovementCells(map, getTeam(character), getEnemyTeam(character));
        repaint();
    }

    public void clearMoveCells() {
        moveCells.clear();
        repaint();
    }

    private boolean isMoveableCell(Cell cell) {
        return moveCells.contains(cell);
    }

    public boolean moveCharacter(Character character, Cell cell) {
        if(isMoveableCell(cell)) {
            character.cell = new Cell(cell);
            clearMoveCells();
            return true;
        } else {
            return false;
        }
    }

    public void showAbilityCells(Character character, Ability ability) {
        abilityTargetCells.clear();
        abilityRangeCells.clear();
        List<Set<Cell>> abilityCells = ability.getAttackCells(map, getTeam(character), getEnemyTeam(character), character);
        abilityTargetCells = abilityCells.get(0);
        abilityRangeCells = abilityCells.get(1);
        repaint();
    }

    public void clearAbilityCells() {
        abilityTargetCells.clear();
        abilityRangeCells.clear();
        repaint();
    }

    private boolean isAbilityCell(Cell cell) {
        return abilityTargetCells.contains(cell);
    }

    public boolean useAbility(Character character, Ability ability, Cell cell) {
        if(isAbilityCell(cell)) {
            ability.useAbility(map, getTeam(character), getEnemyTeam(character), character, cell);
            updateCharacterStatus();
            clearAbilityCells();
            return true;
        } else {
            return false;
        }
    }

    private void updateCharacterStatus() {
        // Check for dead characters on both teams
        List<Character> deadCharacters = new ArrayList<>();
        for(Character character : allCharacters) {
            if(character.isDead()) {
                deadCharacters.add(character);
            }
        }

        for(Character deadCharacter : deadCharacters) {
            team1.remove(deadCharacter);
            team2.remove(deadCharacter);
            allCharacters.remove(deadCharacter);
        }
    }

    private Character getCharacterOnCell(Cell cell) {
        for(Character character : allCharacters) {
            if(character.cell.equals(cell)) {
                return character;
            }
        }
        return null;
    }

    private Character getTeamOnCell(List<Character> team, Cell cell) {
        for(Character character : team) {
            if(character.cell.equals(cell)) {
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
            teamLocations.add(teammate.cell);
        }

        return teamLocations;
    }

    private Set<Cell> getEnemyLocations(Character character) {
        List<Character> team = getEnemyTeam(character);
        Set<Cell> teamLocations = new HashSet<>();
        for(Character teammate : team) {
            teamLocations.add(teammate.cell);
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
