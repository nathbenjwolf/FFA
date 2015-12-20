package character;

import ability.Ability;
import battle.Cell;
import mapElement.MapCell;
import mapElement.MapElement;
import utils.Globals;
import utils.Orientation;
import utils.PathFinding;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import utils.Orientation.Direction;

/**
 * Created by Nathan on 11/15/2015.
 */

//TODO: Update character with other features (equipment, abilities... etc)
public abstract class Character {
    public int totalHealth;
    public int currentHealth;
    public int moveRange;
    public Ability attack;
    protected int characterId;
    protected String imageFilename;
    public Cell cell;
    public Direction direction;

    public Character(int totalHealth, int moveRange) {
        this.totalHealth = totalHealth;
        this.currentHealth = this.totalHealth;
        this.moveRange = moveRange;
        this.characterId = Globals.getCharacterId();
    }

    public BufferedImage getImage() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageFilename));
            switch(direction) {
                case UP:
                    img = rotateImage(img, Math.PI / 2);
                    break;
                case DOWN:
                    img = rotateImage(img, -Math.PI / 2);
                    break;
                case RIGHT:
                    img = flipImageHorizontal(img);
                    break;
                case LEFT:
                default:
                    break;
            }
        } catch (IOException e) {
            System.err.println("Character.getImage(): Error reading file: " + imageFilename);
        }

        return img;
    }


    // TEMPORARY STUFF TO INDICATE DIRECITON
    // Later we will have images for each direction facing
    private BufferedImage rotateImage(BufferedImage img, double theta) {
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5*img.getWidth(), 0.5*img.getHeight());
        tx.rotate(theta);
        tx.translate(-0.5*img.getWidth(), -0.5*img.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

    // TEMPORARY STUFF TO INDICATE DIRECITON
    // Later we will have images for each direction facing
    private BufferedImage flipImageHorizontal(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

    public void takeDamage(Character source, int damage) {
        this.currentHealth -= damage;
    }
    public boolean isDead() { return this.currentHealth <= 0; }

    public Set<Cell> getMovementCells(MapCell[][] map, List<Character> team, List<Character> enemyTeam) {
        // Enemy team is movement blocking
        Set<Cell> blockingCells = new HashSet<>();
        for(Character character : enemyTeam) {
            blockingCells.add(character.cell);
        }
        Set<Cell> movementCells = PathFinding.findPathableCells(map, blockingCells, this, cell, this.moveRange);
        // Remove team locations as possible movement cells
        for(Character character: team) {
            if(movementCells.contains(character.cell)) {
                movementCells.remove(character.cell);
            }
        }

        return movementCells;
    }

    public void moveCharacter(Cell destination) {
        direction = Orientation.getDirection(cell, destination);
        cell = destination;
    }

    public void setOrientation(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof Character) {
            return this.characterId == ((Character) object).characterId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.characterId;
    }
}
