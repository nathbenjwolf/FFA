package character;

import battle.Cell;
import mapElement.MapElement;
import utils.PathFinding;

import java.awt.*;
import java.util.Set;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Ranger extends Character {

    public Ranger(int health, int damage, int attackRange, int moveRange) {
        super(health, damage, attackRange, moveRange);
        color = Color.MAGENTA;
        imageFilename = "Assets/Characters/Archer.png";
    }

    @Override
    public Set<Cell> getMovementCells(MapElement[][] map, Set<Cell> teamLocations, Set<Cell> enemyLocations, Cell cell) {
        Set<Cell> movementCells = PathFinding.findPathableRadialCells(map, this, cell, this.moveRange);
        // Remove team locations as possible movement cells
        for(Cell teamLocation: teamLocations) {
            if(movementCells.contains(teamLocation)) {
                movementCells.remove(teamLocation);
            }
        }

        // Remove enemy team locations as possible movement cells
        for(Cell enemyLocation: enemyLocations) {
            if(movementCells.contains(enemyLocation)) {
                movementCells.remove(enemyLocation);
            }
        }

        return movementCells;
    }
}
