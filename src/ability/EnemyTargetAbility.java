package ability;

import battle.Cell;
import character.Character;
import mapElement.MapCell;
import mapElement.MapElement;
import utils.PathFinding;

import java.util.*;

/**
 * Created by Nathan on 11/28/2015.
 */
public abstract class EnemyTargetAbility extends Ability {
    public EnemyTargetAbility(int damage, int range) {
        super(damage, range);
    }

    @Override
    public void useAbility(MapCell[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter, Cell targetCell) {
        for(Character enemy : enemyTeam) {
            if(enemy.cell.equals(targetCell)) {
                applyAbility(sourceCharacter, enemy);
            }
        }
    }


    @Override
    public List<Set<Cell>> getAttackCells(MapCell[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter) {
        List<Set<Cell>> abilityCells = new ArrayList<>();
        Set<Cell> rangeCells = PathFinding.findRadialCells(map, sourceCharacter.cell, range);
        Set<Cell> attackCells = new HashSet<>();
        for(Character enemy : enemyTeam) {
            if(rangeCells.contains(enemy.cell)) {
                attackCells.add(enemy.cell);
            }
        }
        abilityCells.add(attackCells);
        abilityCells.add(rangeCells);

        return abilityCells;
    }
}
