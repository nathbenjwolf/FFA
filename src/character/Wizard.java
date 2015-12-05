package character;

import ability.WizardAttackAbility;
import battle.Cell;
import mapElement.MapElement;
import utils.PathFinding;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Wizard extends Character {

    public Wizard(int health, int moveRange) {
        super(health, moveRange);
        attack = new WizardAttackAbility();
        imageFilename = "Assets/Characters/wizard.png";
    }

    @Override
    public Set<Cell> getMovementCells(MapElement[][] map, List<Character> team, List<Character> enemyTeam) {
        Set<Cell> movementCells = PathFinding.findPathableRadialCells(map, this, cell, this.moveRange);
        // Remove team locations as possible movement cells
        for(Character character: team) {
            if(movementCells.contains(character.cell)) {
                movementCells.remove(character.cell);
            }
        }

        // Remove enemy team locations as possible movement cells
        for(Character character: enemyTeam) {
            if(movementCells.contains(character.cell)) {
                movementCells.remove(character.cell);
            }
        }

        return movementCells;
    }
}
