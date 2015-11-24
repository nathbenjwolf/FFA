package character;

import battle.Cell;
import mapElement.MapElement;
import utils.Globals;
import utils.PathFinding;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Nathan on 11/15/2015.
 */

//TODO: Update character with other features (equipment, abilities... etc)
public abstract class Character {
    public int health;
    public int damage;
    public int attackRange;
    public int moveRange;
    public Color color;
    protected int characterId;
    protected String imageFilename;

    public Character(int health, int damage, int attackRange, int moveRange) {
        this.health = health;
        this.damage = damage;
        this.attackRange = attackRange;
        this.moveRange = moveRange;
        this.characterId = Globals.getCharacterId();
    }

    public BufferedImage getImage() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageFilename));
        } catch (IOException e) {
            System.err.println("Character.getImage(): Error reading file: " + imageFilename);
        }

        return img;
    }

    public Set<Cell> getMovementCells(MapElement[][] map, Set<Cell> teamLocations, Set<Cell> enemyLocations, Cell cell) {
        Set<Cell> movementCells = PathFinding.findPathableCells(map, enemyLocations, this, cell, this.moveRange);
        // Remove team locations as possible movement cells
        for(Cell teamLocation: teamLocations) {
            if(movementCells.contains(teamLocation)) {
                movementCells.remove(teamLocation);
            }
        }

        return movementCells;
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
