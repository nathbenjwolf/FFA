package battle;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Set;

import ability.Ability;
import character.Character;
import mapElement.MapCell;
import mapElement.MapElement;
import utils.Orientation;
import utils.Orientation.Direction;
import utils.PathFinding;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Board extends JPanel implements ActionListener {
    public static int cellSize = 80;
    static int gridLineThickness = 2;
    public static int cellThickness = cellSize / 3;
    static int realCellSize = cellSize - gridLineThickness;
    static int healthBarPadding = (int) ((double)Board.realCellSize * 0.05);
    static int healthBarWidth = (int) ((double)Board.realCellSize * 0.7);
    static int healthBarHeight = (int) ((double)Board.realCellSize * 0.1);
    static int animationTimerDelay = 100;
    public static int animationTotalTicks = 1000;
    static int tilePulseFrames = 10;

    int numXCells;
    int numYCells;
    MapCell[][] map;
    Set<Cell> moveCells = new HashSet<>();
    Set<Cell> abilityTargetCells = new HashSet<>();
    Set<Cell> abilityRangeCells = new HashSet<>();
    Set<Cell> orientationCells = new HashSet<>();

    private List<Character> team1;
    private List<Character> team2;
    private List<Character> allCharacters;
    private Map<Character, Cell> characterLocations = new HashMap<>();

    private Timer animationTimer;
    private int animationTick;

    public Board(List<Character> team1, List<Character> team2, String groundMap, String objectMap) {
        this.team1 = team1;
        this.team2 = team2;
        this.allCharacters = new ArrayList<>();
        this.allCharacters.addAll(team1);
        this.allCharacters.addAll(team2);
        this.map = MapParser.decodeMap(groundMap, objectMap);
        this.numXCells = this.map.length;
        this.numYCells = this.map[0].length;

        //TODO: Temporary code till character placement code exists
        //Team 1
        for(int i = 0; i < team1.size(); i++) {
            team1.get(i).cell = new Cell(0,i+3);
            team1.get(i).direction = Direction.RIGHT;
        }

        //Team 1
        for(int i = 0; i < team2.size(); i++) {
            team2.get(i).cell = new Cell(numXCells-1,i+3);
            team2.get(i).direction = Direction.LEFT;
        }

        // Setup animation timer
        animationTick = 0;
        animationTimer = new Timer(animationTimerDelay, this);
        animationTimer.setInitialDelay(animationTimerDelay);
        animationTimer.start();
    }

    public void paintComponent(Graphics g) {
        drawGround(g);
        drawGrid(g);
        drawCellIndicators(g);
        drawObjects(g);
    }

    private void drawGround(Graphics g) {
        for(int x=0; x<map.length; x++) {
            for(int y=0; y<map[0].length; y++) {
                Cell cell = new Cell(x,y);
                drawGroundCell(g, cell);
            }
        }
    }

    private void drawGroundCell(Graphics g, Cell cell) {
        Cell TLPixel = cellToTLRealPixel(cell);
        Cell BRPixel = cellToBRRealPixel(cell);
        if(map[cell.x][cell.y].isPresent() && map[cell.x][cell.y].ground != null) {
            BufferedImage img = map[cell.x][cell.y].ground.getImage(animationTick);

            g.drawImage(img,
                    TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                    0, 0, img.getWidth(), img.getHeight(),
                    null);

            drawGroundCellThickness(g, cell, img);
        }
    }

    private void drawGroundCellThickness(Graphics g, Cell cell, BufferedImage img) {
        Cell TLPixel = cellToTLThicknessPixel(cell);
        Cell BRPixel = cellToBRThicknessPixel(cell);

        g.drawImage(img,
                TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                0, 0, img.getWidth(), img.getHeight(),
                null);

        // Make thickness darker to appear as a shadow
        g.setColor(new Color(0, 0, 0, 0.5F));

        g.fillRect(TLPixel.x, TLPixel.y, cellSize, cellThickness);
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(0, 0, 0, 0.3F));
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

    private void drawCellIndicators(Graphics g) {
        drawMoveCells(g);
        drawAbilityCells(g);
        drawOrientationCells(g);
    }

    private void drawMoveCells(Graphics g) {
        if(moveCells.size() == 0) {
            return;
        }

        for(Cell cell: moveCells) {
            drawCell(g, cell, new Color(1F, 0.55F, 0F, getPulseFrame()*0.5F));
            drawCellBorder(g, cell, new Color(1F, 0.55F, 0F, getPulseFrame()*1F));
        }
    }

    private void drawAbilityCells(Graphics g) {
        for(Cell cell: abilityRangeCells) {
            drawCell(g, cell, new Color(1F, 0.55F, 0.0F, 0.3F));
            drawCellBorder(g, cell, new Color(1F, 0.55F, 0.0F, 1F));
        }

        for(Cell cell: abilityTargetCells) {
            drawCell(g, cell, new Color(0.0F, 0.25F, 0.25F, getPulseFrame()*0.5F));
            drawCellBorder(g, cell, new Color(0.0F, 0.25F, 0.25F, getPulseFrame()*1F));
        }
    }

    private void drawOrientationCells(Graphics g) {
        for(Cell cell: orientationCells) {
            drawCell(g, cell, new Color(0.0F, 0.0F, 0.1F, getPulseFrame()*0.5F));
            drawCellBorder(g, cell, new Color(0.0F, 0.0F, 0.1F, getPulseFrame()*1F));
        }
    }

    private void drawObjects(Graphics g) {
        for(int x=0; x<map.length; x++) {
            for(int y=0; y<map[0].length; y++) {
                Cell cell = new Cell(x,y);
                Cell TLPixel = cellToTLRealPixel(cell);
                // TEMPORARY
                TLPixel.y -= (cellSize/3);
                Cell BRPixel = cellToBRRealPixel(cell);
                // TEMPORARY
                BRPixel.y -= (cellSize/3);

                // Draw Cell Object
                drawObjectCell(g, cell);

                // Draw Character
                drawCellCharacter(g, cell);
            }
        }
    }

    private void drawObjectCell(Graphics g, Cell cell) {
        if(map[cell.x][cell.y].isPresent()) {
            Cell TLPixel = cellToTLRealPixel(cell);
            // TEMPORARY
            TLPixel.y -= (cellSize/2);
            Cell BRPixel = cellToBRRealPixel(cell);
            // TEMPORARY
            BRPixel.y -= (cellSize/4);
            if(map[cell.x][cell.y].isPresent() && map[cell.x][cell.y].object != null) {
                BufferedImage img = map[cell.x][cell.y].object.getImage(animationTick);

                g.drawImage(img,
                        TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                        0, 0, img.getWidth(), img.getHeight(),
                        null);
            }
        }
    }

    private void drawCellCharacter(Graphics g, Cell cell) {
        Cell TLPixel = cellToTLRealPixel(cell);
        // TEMPORARY
        TLPixel.y -= (cellSize/3);
        Cell BRPixel = cellToBRRealPixel(cell);
        // TEMPORARY
        BRPixel.y -= (cellSize/3);

        for(Character character: allCharacters) {
            if(character.cell.equals(cell)) {
                BufferedImage img = character.getImage();

                g.drawImage(img,
                        TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                        0, 0, img.getWidth(), img.getHeight(),
                        null);
            }
        }
    }

    private void drawCharacters(Graphics g, List<Character> characters) {
        for(Character character : characters) {
            Cell TLPixel = cellToTLRealPixel(character.cell);
            // TEMPORARY
            TLPixel.y -= (cellSize/3);
            Cell BRPixel = cellToBRRealPixel(character.cell);
            // TEMPORARY
            BRPixel.y -= (cellSize/3);
            // Character image
            BufferedImage img = character.getImage();

            g.drawImage(img,
                    TLPixel.x, TLPixel.y, BRPixel.x+1, BRPixel.y+1, // Extra +1 because drawImage -1
                    0, 0, img.getWidth(), img.getHeight(),
                    null);

            // Health bar
            //drawHealthBar(g, character);
        }
    }

    private void drawHealthBar(Graphics g, Character character) {
        Cell TLPixel = cellToTLRealPixel(character.cell);
        TLPixel.x += healthBarPadding;
        TLPixel.y -= (cellSize/3);
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

    private float getPulseFrame() {
        // Pulse between 50% and 100%
        return (((float)animationTick%tilePulseFrames)+tilePulseFrames)/(tilePulseFrames*2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(animationTick+1 >= animationTotalTicks) {
            animationTick = 0;
        } else {
            animationTick++;
        }

        repaint();
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
            character.moveCharacter(new Cell(cell));
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

    public void showOrientationCells(Character character) {
        orientationCells.clear();
        orientationCells = PathFinding.findRadialCells(map, character.cell, 1);
        orientationCells.remove(character.cell);
        repaint();
    }

    public boolean isOrientaionCell(Cell cell) { return orientationCells.contains(cell); }

    public boolean orientCharacter(Character character, Cell cell) {
        if(isOrientaionCell(cell)) {
            character.setOrientation(Orientation.getDirection(character.cell, cell));
            clearOrientationCells();
            return true;
        } else {
            return false;
        }
    }

    public void clearOrientationCells() {
        orientationCells.clear();
        repaint();
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
        return new Cell(cell.x*cellSize, cell.y*cellSize);
    }

    public Cell cellToBRRealPixel(Cell cell) {
        return new Cell((cell.x+1)*cellSize-1, (cell.y+1)*cellSize-1);
    }

    public Cell cellToTLThicknessPixel(Cell cell) {
        cell = new Cell(cell.x, cell.y+1);
        return cellToTLRealPixel(cell);
    }

    public Cell cellToBRThicknessPixel(Cell cell) {
        Cell pixel = cellToBRRealPixel(cell);
        pixel.y += cellThickness;
        return pixel;
    }

    public Cell pixelToCell(int x, int y) {
        return new Cell(x/cellSize, y/cellSize);
    }

}
