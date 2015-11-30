package character;

import ability.WizardAttackAbility;
import battle.Cell;
import mapElement.MapElement;
import utils.PathFinding;

import java.awt.*;
import java.util.Set;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Wizard extends Character {

    public Wizard(int health, int moveRange) {
        super(health, moveRange);
        attack = new WizardAttackAbility();
        color = Color.MAGENTA;
        imageFilename = "Assets/Characters/wizard.png";
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
