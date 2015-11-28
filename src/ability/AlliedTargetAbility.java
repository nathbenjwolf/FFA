package ability;

import battle.Cell;
import character.Character;
import mapElement.MapElement;
import utils.PathFinding;

import java.util.*;

/**
 * Created by Nathan on 11/28/2015.
 */
public abstract class AlliedTargetAbility extends Ability {
    public AlliedTargetAbility(int damage, int range) {
        super(damage, range);
    }

    @Override
    public void useAbility(MapElement[][] map, Map<Character, Cell> characterLocations, Character sourceCharacter, Cell cell) {
        for(Character character : characterLocations.keySet()) {
            if(characterLocations.get(character).equals(cell)) {
                applyAbility(sourceCharacter, character);
            }
        }
    }

    @Override
    public List<Set<Cell>> getAttackCells(MapElement[][] map, Set<Cell> teamLocations, Set<Cell> enemyLocations, Cell cell) {
        List<Set<Cell>> abilityCells = new ArrayList<>();
        Set<Cell> rangeCells = PathFinding.findRadialCells(map, cell, range);
        Set<Cell> attackCells = new HashSet<>();
        for(Cell teamLocation : teamLocations) {
            if(rangeCells.contains(teamLocation)) {
                attackCells.add(teamLocation);
            }
        }
        abilityCells.add(attackCells);
        abilityCells.add(rangeCells);

        return abilityCells;
    }
}
