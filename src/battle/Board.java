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

    // Isometric size constants
    public static int cellYDelta = 40;
    public static int cellXDelta = (int)(1.6 * (double)cellYDelta);
    public static int cellThickness = 30;
    public static double cellSideSize = Math.sqrt(Math.pow(cellYDelta,2) + Math.pow(cellXDelta,2));

    // Board Border Line Values
    public static float yAxisSlope = -(float)cellYDelta/(float)cellXDelta; // negative slope because (0,0) is in top left corner of screen
    public static float xAxisSlope = -yAxisSlope;
    public float yAxisIntercept;
    public float xAxisIntercept;
    public Cell yAxisStartPoint;
    public Cell yAxisEndPoint;
    public Cell xAxisStartPoint;
    public Cell xAxisEndPoint;

    // Animation timer constants
    static int animationTimerDelay = 100;
    public static int animationTotalTicks = 1000;
    static int tilePulseFrames = 10;

    // Shading Constants
    static float tileLeftThicknessAlpha = 0.5F;
    static float tileRightThicknessAlpha = 0.0F;
    static float tileSurfaceAlpha = 0.05F;
    static float gridLineAlpha = 1F;

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

    // Active Character Cell Constants
    static Color activeCharacterCellColorFill = new Color(0.855F, 0.647F, 0.125F);
    static Color activeCharacterCellColorBorder = new Color(0.824F, 0.412F, 0.118F);

    int numXCells;
    int numYCells;
    MapCell[][] map;
    Set<Cell> moveCells = new HashSet<>();
    Set<Cell> abilityTargetCells = new HashSet<>();
    Set<Cell> abilityRangeCells = new HashSet<>();
    Set<Cell> orientationCells = new HashSet<>();
    Cell mouseHoverCell;

    private Battle battle;

    private Timer animationTimer;
    private int animationTick;

    public Board(Battle battle, String groundMap, String objectMap) {
        this.battle = battle;
        this.map = MapParser.decodeMap(groundMap, objectMap);
        this.numXCells = this.map.length;
        this.numYCells = this.map[0].length;
//        this.boardDesiredWidth = cellSize*numXCells + backgroundXPadding*2;
//        this.boardDesiredHeight = cellSize*numYCells + cellYThickness + backgroundYPadding*2;
        this.boardDesiredWidth = cellXDelta*(numYCells + numXCells) + backgroundXPadding*2;
        this.boardDesiredHeight = cellYDelta*(numYCells + numXCells) + cellYThickness + backgroundYPadding*2;

        //TODO: Temporary code till character placement code exists
        //Team 1
        for(int i = 0; i < battle.team1.size(); i++) {
            battle.team1.get(i).cell = new Cell(0,i+3);
            battle.team1.get(i).direction = Direction.RIGHT;
        }

        //Team 1
        for(int i = 0; i < battle.team2.size(); i++) {
            battle.team2.get(i).cell = new Cell(numXCells-1,i+3);
            battle.team2.get(i).direction = Direction.LEFT;
        }

        // calculate constant pixel values (need board size and shape before we can make these calculations)
        initConstPixelValues();

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
            for(int x=0; x<map.length; x++) {
                Cell cell = new Cell(x,y);
                drawGroundCell(g, cell);
            }
        }
    }

    private void drawCellIndicators(Graphics g) {
        drawMoveCells(g);
        drawAbilityCells(g);
        drawOrientationCells(g);
        drawActiveCharacterCell(g);
    }

    private void drawObjects(Graphics g) {
        for(int y=0; y<map[0].length; y++) {
            for(int x=0; x<map.length; x++) {
                Cell cell = new Cell(x,y);

                // Draw Cell Object
                drawObjectCell(g, cell);

                // Draw Character
                drawCellCharacter(g, cell);
            }
        }
    }

    private void drawGroundCell(Graphics g, Cell cell) {

        if(map[cell.x][cell.y].isPresent() && map[cell.x][cell.y].ground != null) {
            BufferedImage img = map[cell.x][cell.y].ground.getImage(animationTick);

            // Cell Surface
            drawGroundCellSurface(g, cell, img);

            // Cell Thickness
            drawGroundCellThickness(g, cell, img);
        }
    }

    private void drawGroundCellSurface(Graphics g, Cell cell, BufferedImage img) {
        drawCellImg(g, img, getCellShape(cell), cellToTLDrawPixel(cell), cellToBRDrawPixel(cell), tileSurfaceAlpha);
    }

    private void drawGroundCellThickness(Graphics g, Cell cell, BufferedImage img) {
        // Left thickness (modified y-axis)
        Cell TLPixel = cellToTLLeftThicknessPixel(cell);
        Cell BRPixel = cellToBRLeftThicknessPixel(cell);
        Shape s = getLeftCellThicknessShape(cell);
        drawCellImg(g, img, s, TLPixel, BRPixel, tileLeftThicknessAlpha);

        // Right thickness (modified x-axis)
        TLPixel = cellToTLRightThicknessPixel(cell);
        BRPixel = cellToBRRightThicknessPixel(cell);
        s = getRightCellThicknessShape(cell);
        drawCellImg(g, img, s, TLPixel, BRPixel, tileRightThicknessAlpha);
    }

    // Left thickness (modified y-axis)
    private Shape getLeftCellThicknessShape(Cell cell) {
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
    private Shape getRightCellThicknessShape(Cell cell) {
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

    private void drawCellImg(Graphics g, BufferedImage img, Shape s, Cell TLPixel, Cell BRPixel, float alpha) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(s);
        g2.drawImage(img,
                TLPixel.x, TLPixel.y, BRPixel.x + 1, BRPixel.y + 1,
                0, 0, img.getWidth(), img.getHeight(),
                null);
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fill(s);
        g2.setColor(new Color(0, 0, 0, gridLineAlpha));
        g2.setClip(null);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(gridLineThickness));
        g2.draw(s);
        g2.setStroke(oldStroke);
    }

    private void drawMoveCells(Graphics g) {
        Color fillColor = getPulseColor(moveCellColorFill);
        Color borderColor = getPulseColor(moveCellColorBorder);
        for(Cell cell: moveCells) {
            drawCellFill(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private void drawAbilityCells(Graphics g) {
        for(Cell cell: abilityRangeCells) {
            drawCellFill(g, cell, abilityRangeCellColorFill);
            drawCellBorder(g, cell, abilityRangeCellColorBorder);
        }

        Color fillColor = getPulseColor(abilityTargetCellColorFill);
        Color borderColor = getPulseColor(abilityTargetCellColorBorder);
        for(Cell cell: abilityTargetCells) {
            drawCellFill(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private void drawOrientationCells(Graphics g) {
        Color fillColor = getPulseColor(orientationCellColorFill);
        Color borderColor = getPulseColor(orientationCellColorBorder);
        for(Cell cell: orientationCells) {
            drawCellFill(g, cell, fillColor);
            drawCellBorder(g, cell, borderColor);
        }
    }

    private void drawActiveCharacterCell(Graphics g) {
        Color fillColor = getPulseColor(activeCharacterCellColorFill);
        Color borderColor = getPulseColor(activeCharacterCellColorBorder);
        drawCellFill(g, battle.activeCharacter.cell, fillColor);
        drawCellBorder(g, battle.activeCharacter.cell, borderColor);
    }

    private Color getPulseColor(Color color) {
        return new Color((float)color.getRed()/255F,
                         (float)color.getGreen()/255F,
                         (float)color.getBlue()/255F,
                         ((float)color.getAlpha()/255F)*getPulseFrame());
    }

    private void drawObjectCell(Graphics g, Cell cell) {
        if(map[cell.x][cell.y].isPresent()) {
            Cell TLPixel = cellToTLDrawObjectPixel(cell);
            // TEMPORARY
//            TLPixel.y -= (cellSize/2);
            Cell BRPixel = cellToBRDrawObjectPixel(cell);
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
        Cell TLPixel = cellToTLDrawObjectPixel(cell);
        // TEMPORARY
//        TLPixel.y -= (cellSize/3);
        Cell BRPixel = cellToBRDrawObjectPixel(cell);
        // TEMPORARY
        BRPixel.y -= (cellSize/3);

        for(Character character: battle.characterOrder) {
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
        Cell TLPixel = cellToTLDrawObjectPixel(character.cell);
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

    private void drawCellFill(Graphics g, Cell cell, Color color) {
        Shape s = getCellShape(cell);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(s);
//        g.fillRect(topLeftPixel.x+gridLineThickness, topLeftPixel.y+gridLineThickness, cellSize-(gridLineThickness*2), cellSize-(gridLineThickness*2));
    }

    private void drawCellBorder(Graphics g, Cell cell, Color color) {
        g.setColor(color);
        Cell topLeftPixel = cellToTLPixel(cell);
//        Old way of drawing (should remove once decided)
//        g.fillRect(topLeftPixel.x, topLeftPixel.y, cellSize, gridLineThickness);
//        g.fillRect(topLeftPixel.x, topLeftPixel.y, gridLineThickness, cellSize);
//        g.fillRect(topLeftPixel.x, topLeftPixel.y+cellSize-gridLineThickness, cellSize, gridLineThickness);
//        g.fillRect(topLeftPixel.x+cellSize-gridLineThickness, topLeftPixel.y, gridLineThickness, cellSize);

        Shape s = getCellShape(cell);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(gridLineThickness));
        g2.draw(s);
        g2.setStroke(oldStroke);

//        Graphics2D g2 = (Graphics2D) g;
//        Stroke oldStroke = g2.getStroke();
//        g2.setStroke(new BasicStroke(gridLineThickness));
//        g2.drawRect(topLeftPixel.x+gridLineThickness, topLeftPixel.y+gridLineThickness, cellSize-(gridLineThickness*2), cellSize-(gridLineThickness*2));
//        g2.setStroke(oldStroke);
    }

    private Shape getCellShape(Cell cell) {
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
            battle.updateCharacterStatus();
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

    private Character getCharacterOnCell(Cell cell) {
        for(Character character : battle.characterOrder) {
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
        return battle.team1.contains(character) ? battle.team1 : battle.team2;
    }

    private List<Character> getEnemyTeam(Character character) {
        return battle.team2.contains(character) ? battle.team1 : battle.team2;
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

    // Right top pixel (there are two top pixels)
    public Cell cellToTopPixel(Cell cell) {
        // (0,0) top cell
        int xPixel = cellXDelta*numYCells + backgroundXPadding;
        int yPixel = backgroundYPadding;

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



    public Cell cellToTLDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= cellXDelta;

        return topCell;
    }

    public Cell cellToBRDrawPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta-1;
        topCell.y += cellYDelta*2-1;

        return topCell;
    }

    public Cell cellToTLDrawObjectPixel(Cell cell) {
        Cell adjustedCell = new Cell(cell);
        // Use cell directly above the current cell
        adjustedCell.x--;
        adjustedCell.y--;
        return cellToTLDrawPixel(adjustedCell);
    }

    public Cell cellToBRDrawObjectPixel(Cell cell) {
        return cellToBRDrawPixel(cell);
    }

    public Cell cellToStartLeftThicknessPixel(Cell cell) {
        return cellToTLLeftThicknessPixel(cell); // happens to be the same as the top left pixel
    }

    public Cell cellToTLLeftThicknessPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= cellXDelta;
        topCell.y += cellYDelta+1; // 1 pixel below the edge of the cell surface

        return topCell;
    }

    public Cell cellToBRLeftThicknessPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x -= 1; // Left more of the top two pixels
        topCell.y += cellYDelta*2 + cellThickness;

        return topCell;
    }

    public Cell cellToStartRightThicknessPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta-1; // -1 so it stays in the same horizontal bounds as the surface
        topCell.y += cellYDelta+1; // +1 so it is below the cell surface

        return topCell;
    }

    public Cell cellToTLRightThicknessPixel(Cell cell){
        Cell topCell = cellToTopPixel(cell);
        topCell.y += cellYDelta+1; // +1 so it is below the cell surface

        return topCell;
    }

    public Cell cellToBRRightThicknessPixel(Cell cell) {
        Cell topCell = cellToTopPixel(cell);
        topCell.x += cellXDelta - 1;
        topCell.y += cellYDelta * 2 + cellThickness;

        return topCell;
    }

    // Dead code (delete)
//    public boolean isBoardPixel(int x, int y) {
//        return  x >= backgroundXPadding &&
//                x < (numYCells*cellXDelta + numXCells*cellXDelta + backgroundXPadding) &&
//                y >= backgroundYPadding &&
//                y < (numYCells*cellYDelta + numXCells*cellYDelta + backgroundYPadding);
//    }

    public Cell pixelToCell(int x, int y) {
        Cell cell = new Cell(pixelToCellX(x,y),pixelToCellY(x,y));
        if(cell.x == -1 || cell.y == -1) {
            return null; // Cell out of bounds
        }
        return cell;
    }

    public double getPixelDistance(Cell start, Cell end) {
        return Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
    }

    public int pixelToCellX(int x, int y) {
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

    public int pixelToCellY(int x, int y) {
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

    public void initConstPixelValues() {
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

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHoverCell = pixelToCell(e.getPoint().x, e.getPoint().y);
        repaint();
    }
}
