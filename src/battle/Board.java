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
    private CellPixelAbstraction cpa;

    private Timer animationTimer;
    private int animationTick;

    public Board(Battle battle, String groundMap, String objectMap) {
        this.battle = battle;
        this.map = MapParser.decodeMap(groundMap, objectMap);
        this.numXCells = this.map.length;
        this.numYCells = this.map[0].length;
        this.cpa = new CellPixelAbstraction(this.numXCells, this.numYCells);

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

        // Setup animation timer
        animationTick = 0;
        animationTimer = new Timer(animationTimerDelay, this);
        animationTimer.setInitialDelay(animationTimerDelay);
        animationTimer.start();

        // Mouse detector
        addMouseMotionListener(this);
    }

    public int getBoardDesiredWidth() {
        return cpa.getBoardDesiredWidth();
    }

    public int getBoardDesiredHeight() {
        return cpa.getBoardDesiredHeight();
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
        drawCellImg(g, img, cpa.getCellShape(cell), cpa.cellToTLGroundDrawPixel(cell), cpa.cellToBRGroundDrawPixel(cell), tileSurfaceAlpha);
    }

    private void drawGroundCellThickness(Graphics g, Cell cell, BufferedImage img) {
        // Left thickness (modified y-axis)
        Cell TLPixel = cpa.cellToTLLeftThicknessDrawPixel(cell);
        Cell BRPixel = cpa.cellToBRLeftThicknessDrawPixel(cell);
        Shape s = cpa.getLeftCellThicknessShape(cell);
        drawCellImg(g, img, s, TLPixel, BRPixel, tileLeftThicknessAlpha);

        // Right thickness (modified x-axis)
        TLPixel = cpa.cellToTLRightThicknessDrawPixel(cell);
        BRPixel = cpa.cellToBRRightThicknessDrawPixel(cell);
        s = cpa.getRightCellThicknessShape(cell);
        drawCellImg(g, img, s, TLPixel, BRPixel, tileRightThicknessAlpha);
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
        g2.setStroke(new BasicStroke(cpa.gridLineThickness));
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
            Cell TLPixel = cpa.cellToTLObjectDrawPixel(cell);
            Cell BRPixel = cpa.cellToBRObjectDrawPixel(cell);
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
        Cell TLPixel = cpa.cellToTLObjectDrawPixel(cell);
        Cell BRPixel = cpa.cellToBRObjectDrawPixel(cell);

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

    private void drawHealthBar(Graphics g, Character character) {
        Cell TLPixel = cpa.cellToTLHealthBarPixel(character.cell);

        // Health bar border
        g.setColor(Color.WHITE);
        g.drawRect(TLPixel.x, TLPixel.y, cpa.healthBarWidth, cpa.healthBarHeight);

        // Health bar contents
        g.setColor(Color.RED);
        int healthWidth = (int)((double)(cpa.healthBarWidth-1) * ((double)character.currentHealth/(double)character.totalHealth));
        g.fillRect(TLPixel.x+1, TLPixel.y+1, healthWidth, cpa.healthBarHeight-1);
    }

    private void drawCellFill(Graphics g, Cell cell, Color color) {
        Shape s = cpa.getCellShape(cell);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(s);
    }

    private void drawCellBorder(Graphics g, Cell cell, Color color) {
        g.setColor(color);

        Shape s = cpa.getCellShape(cell);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(cpa.gridLineThickness));
        g2.draw(s);
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

    public Cell pixelToCell(int x, int y) {
        return cpa.pixelToCell(x, y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHoverCell = cpa.pixelToCell(e.getPoint().x, e.getPoint().y);
        repaint();
    }
}
