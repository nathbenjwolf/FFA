package ability;

import battle.Cell;
import character.Character;
import mapElement.MapElement;
import utils.PathFinding;

import java.util.*;
import java.util.List;

/**
 * Created by Nathan on 11/28/2015.
 */
public abstract class AlliedTargetAbility extends Ability {
    public AlliedTargetAbility(int damage, int range) {
        super(damage, range);
    }

    @Override
    public void useAbility(MapElement[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter, Cell targetCell) {
        for(Character teammate : team) {
            if (teammate.cell.equals(targetCell)) {
                applyAbility(sourceCharacter, teammate);
            }
        }
    }

    @Override
    public List<Set<Cell>> getAttackCells(MapElement[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter) {
        List<Set<Cell>> abilityCells = new ArrayList<>();
        Set<Cell> rangeCells = PathFinding.findRadialCells(map, sourceCharacter.cell, range);
        Set<Cell> attackCells = new HashSet<>();
        for(Character teammate : team) {
            if(rangeCells.contains(teammate.cell)) {
                attackCells.add(teammate.cell);
            }
        }
        abilityCells.add(attackCells);
        abilityCells.add(rangeCells);

        return abilityCells;
    }
}
