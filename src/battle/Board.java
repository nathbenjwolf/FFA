package battle;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
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
public class Board extends JPanel implements ActionListener, MouseMotionListener {

    // Size constants
    public static int cellSize = 80;
    static int gridLineThickness = 2;
    public static int cellYThickness = cellSize / 3;
    public static int cellXThickness = cellSize / 5;
    public static int backgroundXPadding = cellSize / 3;
    public static int backgroundYPadding = cellSize / 5;
    static int realCellSize = cellSize - gridLineThickness;
    static int healthBarXPadding = (int) ((double)cellSize * 0.15);
    static int healthBarYPadding = (int) ((double)cellSize * 0.05);
    static int healthBarWidth = (int) ((double)cellSize * 0.7);
    static int healthBarHeight = (int) ((double)cellSize * 0.1);
    public int boardDesiredWidth;
    public int boardDesiredHeight;

    // Animation timer constants
    static int animationTimerDelay = 100;
    public static int animationTotalTicks = 1000;
    static int tilePulseFrames = 10;

    // Shading Constants
    static float tileThicknessAlpha = 0.5F;
    static float gridLineAlpha = 0.3F;

    // Move Cell Constants
    static Color moveCellColorFill = new Color(1F, 0.55F, 0F, 0.5F);
    static Color moveCellColorBorder = new Color(1F, 0.55F, 0F, 1F);

    // Ability Cell Constants
    static Color abilityRangeCellColorFill = new Color(1F, 0.55F, 0.0F, 0.3F);
    static Color abilityRangeCellColorBorder = new Color(1F, 0.55F, 0.0F, 1F);
    static Color abilityTargetCellColorFill = new Color(0.0F, 0.25F, 0.25F, 0.5F);
    static Color abilityTargetCellColorBorder = new Color(0.0F, 0.25F, 0.25F, 1F);

    // Orientation Cell Constants
    static Color orientationCellColorFill = new Color(0.0F, 0.0F, 0.1F, 0.5F);
    static Color orientationCellColorBorder = new Color(0.0F, 0.0F, 0.1F, 1F);

    int numXCells;
    int numYCells;
    MapCell[][] map;
    Set<Cell> moveCells = new HashSet<>();
    Set<Cell> abilityTargetCells = new HashSet<>();
    Set<Cell> abilityRangeCells = new HashSet<>();
    Set<Cell> orientationCells = new HashSet<>();
    Cell mouseHoverCell;

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
        this.boardDesiredWidth = cellSize*numXCells + backgroundXPadding*2;
        this.boardDesiredHeight = cellSize*numYCells + cellYThickness + backgroundYPadding*2;

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

        // Mouse detector
        addMouseMotionListener(this);
    }

    public void paintComponent(Graphics g) {
        drawBackground(g);
        drawGround(g);
        drawCellIndicators(g);
        drawObjects(g);
    }

    private void drawBackground(Graphics g) {
        // TEMOPRARY: need background image stuff
        g.setColor(new Color(255,0,0));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawGround(Graphics g) {
        for(int y=0; y<map[0].length; y++) {
            for(int x=map.length-1; x>=0; x--) {
                Cell cell = new Cell(x,y);
                drawGroundCell(g, cell);
            }
        }
    }

    private void drawCellIndicators(Graphics g) {
        drawMoveCells(g);
        drawAbilityCells(g);
        drawOrientationCells(g);
    }

    private void drawObjects(Graphics g) {
        for(int x=0; x<map.length; x++) {
            for(int y=0; y<map[0].length; y++) {
                Cell cell = new Cell(x,y);
                Cell TLPixel = cellToTLPixel(cell);
                // TEMPORARY
                TLPixel.y -= (cellSize/3);
                Cell BRPixel = cellToBRPixel(cell);
                // TEMPORARY
                BRPixel.y -= (cellSize/3);

                // Draw Cell Object
                drawObjectCell(g, cell);

                // Draw Character
                drawCellCharacter(g, cell);
            }
        }
    }

    private void drawGroundCell(Graphics g, Cell cell) {
        Cell TLPixel = cellToTLPixel(cell);
        Cell BRPixel = cellToBRPixel(cell);
        if(map[cell.x][cell.y].isPresent() && map[cell.x][cell.y].ground != null) {
            BufferedImage img = map[cell.x][cell.y].ground.getImage(animationTick);

            g.drawImage(img,
                    TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                    0, 0, img.getWidth(), img.getHeight(),
                    null);

            drawCellGrid(g, cell);

            drawGroundCellThickness(g, cell, img);
        }
    }

    private void drawGroundCellThickness(Graphics g, Cell cell, BufferedImage img) {
        // Y-axis thickness
        Cell TLPixel = cellToTLYThicknessPixel(cell);
        Cell BRPixel = cellToBRYThicknessPixel(cell);
        Shape s = getYCellThicknessShape(cell);
        drawCellThickness(g, img, s, TLPixel, BRPixel);

        // X-axis thickness
        TLPixel = cellToTLXThicknessPixel(cell);
        BRPixel = cellToBRXThicknessPixel(cell);
        s = getXCellThicknessShape(cell);
        drawCellThickness(g, img, s, TLPixel, BRPixel);
    }

    private void drawCellThickness(Graphics g, BufferedImage img, Shape s, Cell TLPixel, Cell BRPixel) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setClip(s);
        g2.drawImage(img,
                TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1,
                0, 0, img.getWidth(), img.getHeight(),
                null);
        g2.setColor(new Color(0, 0, 0, tileThicknessAlpha));
        g2.fill(s);
        g2.setColor(new Color(0, 0, 0, gridLineAlpha));
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(gridLineThickness*2));
        g2.draw(s);
        g2.setClip(null);
        g2.setStroke(oldStroke);
    }

    private Shape getXCellThicknessShape(Cell cell) {
        Cell TLPixel = cellToTLPixel(cell);
        GeneralPath path = new GeneralPath();
        path.moveTo(TLPixel.x,TLPixel.y);

        TLPixel.x -= cellXThickness;
        TLPixel.y += cellYThickness;
        path.lineTo(TLPixel.x,TLPixel.y);

        TLPixel.y += cellSize;
        path.lineTo(TLPixel.x,TLPixel.y);

        TLPixel.x += cellXThickness;
        TLPixel.y -= cellYThickness;
        path.lineTo(TLPixel.x,TLPixel.y);

        path.closePath();

        return path;
    }

    private Shape getYCellThicknessShape(Cell cell) {
        // Grab 1 cell lower top left pixel
        Cell adjustedCell = new Cell(cell);
        adjustedCell.y += 1;

        Cell TLPixel = cellToTLPixel(adjustedCell);
        GeneralPath path = new GeneralPath();
        path.moveTo(TLPixel.x,TLPixel.y);

        TLPixel.x -= cellXThickness;
        TLPixel.y += cellYThickness;
        path.lineTo(TLPixel.x,TLPixel.y);

        TLPixel.x += cellSize;
        path.lineTo(TLPixel.x,TLPixel.y);

        TLPixel.x += cellXThickness;
        TLPixel.y -= cellYThickness;
        path.lineTo(TLPixel.x,TLPixel.y);

        path.closePath();

        return path;
    }

    private void drawMoveCells(Graphics g) {
        if(moveCells.size() == 0) {
            return;
        }

        Color fillColor = getPulseColor(moveCellColorFill);
        Color borderColor = getPulseColor(moveCellColorBorder);
        for(Cell cell: moveCells) {
            drawCell(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private void drawAbilityCells(Graphics g) {
        for(Cell cell: abilityRangeCells) {
            drawCell(g, cell, abilityRangeCellColorFill);
            drawCellBorder(g, cell, abilityRangeCellColorBorder);
        }

        Color fillColor = getPulseColor(abilityTargetCellColorFill);
        Color borderColor = getPulseColor(abilityTargetCellColorBorder);
        for(Cell cell: abilityTargetCells) {
            drawCell(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private void drawOrientationCells(Graphics g) {
        Color fillColor = getPulseColor(orientationCellColorFill);
        Color borderColor = getPulseColor(orientationCellColorBorder);
        for(Cell cell: orientationCells) {
            drawCell(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private Color getPulseColor(Color color) {
        return new Color((float)color.getRed()/255F,
                         (float)color.getGreen()/255F,
                         (float)color.getBlue()/255F,
                         ((float)color.getAlpha()/255F)*getPulseFrame());
    }

    private void drawObjectCell(Graphics g, Cell cell) {
        if(map[cell.x][cell.y].isPresent()) {
            Cell TLPixel = cellToTLPixel(cell);
            // TEMPORARY
            TLPixel.y -= (cellSize/2);
            Cell BRPixel = cellToBRPixel(cell);
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
        Cell TLPixel = cellToTLPixel(cell);
        // TEMPORARY
        TLPixel.y -= (cellSize/3);
        Cell BRPixel = cellToBRPixel(cell);
        // TEMPORARY
        BRPixel.y -= (cellSize/3);

        for(Character character: allCharacters) {
            if(character.cell.equals(cell)) {
                BufferedImage img = character.getImage();

                g.drawImage(img,
                        TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1, // Extra +1 because drawImage -1
                        0, 0, img.getWidth(), img.getHeight(),
                        null);

                if(mouseHoverCell != null && mouseHoverCell.equals(character.cell)) {
                    drawHealthBar(g, character);
                }
            }
        }


    }

    private void drawCharacters(Graphics g, List<Character> characters) {
        for(Character character : characters) {
            Cell TLPixel = cellToTLPixel(character.cell);
            // TEMPORARY
            TLPixel.y -= (cellSize/3);
            Cell BRPixel = cellToBRPixel(character.cell);
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
        Cell TLPixel = cellToTLPixel(character.cell);
        TLPixel.x += healthBarXPadding;
        TLPixel.y -= (cellSize/3);
        TLPixel.y += healthBarYPadding;

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
//        Old way of drawing (should remove once decided)
//        g.fillRect(topLeftPixel.x, topLeftPixel.y, cellSize, gridLineThickness);
//        g.fillRect(topLeftPixel.x, topLeftPixel.y, gridLineThickness, cellSize);
//        g.fillRect(topLeftPixel.x, topLeftPixel.y+cellSize-gridLineThickness, cellSize, gridLineThickness);
//        g.fillRect(topLeftPixel.x+cellSize-gridLineThickness, topLeftPixel.y, gridLineThickness, cellSize);

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(gridLineThickness));
        g2.drawRect(topLeftPixel.x+gridLineThickness, topLeftPixel.y+gridLineThickness, cellSize-(gridLineThickness*2), cellSize-(gridLineThickness*2));
        g2.setStroke(oldStroke);
    }

    private void drawCellGrid(Graphics g, Cell cell) {
        g.setColor(new Color(0, 0, 0, gridLineAlpha));
        Cell topLeftPixel = cellToTLPixel(cell);

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(gridLineThickness));
        g2.drawRect(topLeftPixel.x+1, topLeftPixel.y+1, cellSize-gridLineThickness, cellSize-gridLineThickness);
        g2.setStroke(oldStroke);
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
        return new Cell(cell.x*cellSize + backgroundXPadding, cell.y*cellSize + backgroundYPadding);
    }

    public Cell cellToBRPixel(Cell cell) {
        return new Cell((cell.x+1)*cellSize-1 + backgroundXPadding, (cell.y+1)*cellSize-1 + backgroundYPadding);
    }

    public Cell cellToTLYThicknessPixel(Cell cell) {
        cell = new Cell(cell.x, cell.y+1);
        Cell TLPixel = cellToTLPixel(cell);
        TLPixel.x -= cellXThickness;
        return TLPixel;
    }

    public Cell cellToBRYThicknessPixel(Cell cell) {
        Cell BRPixel = cellToBRPixel(cell);
        BRPixel.y += cellYThickness + 1; // cellToBRPixel removes an extra pixel for inclusive conditions
        return BRPixel;
    }

    public Cell cellToTLXThicknessPixel(Cell cell) {
        Cell TLPixel = cellToTLPixel(cell);
        TLPixel.x -= cellXThickness;
        return TLPixel;
    }

    public Cell cellToBRXThicknessPixel(Cell cell) {
        cell = new Cell(cell.x, cell.y+1);
        Cell BRPixel = cellToTLPixel(cell);
        BRPixel.y += cellYThickness;
        return BRPixel;
    }

    public boolean isBoardPixel(int x, int y) {
        return  x >= backgroundXPadding &&
                x < numXCells*cellSize + backgroundXPadding &&
                y >= backgroundYPadding &&
                y < numYCells*cellSize + backgroundYPadding;
    }

    public Cell pixelToCell(int x, int y) {
        if(isBoardPixel(x,y)) {
            return new Cell((x - backgroundXPadding) / cellSize, (y - backgroundYPadding) / cellSize);
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("Dragged");
        System.out.println("x: " + e.getPoint().x + " y: " + e.getPoint().y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("Moved");
        System.out.println("x: " + e.getPoint().x + " y: " + e.getPoint().y);
        mouseHoverCell = pixelToCell(e.getPoint().x, e.getPoint().y);
        repaint();
    }
}
