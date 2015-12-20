package ability;

import battle.Cell;
import character.Character;
import mapElement.MapCell;
import mapElement.MapElement;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Nathan on 11/28/2015.
 */
public abstract class Ability {
    protected int damage;
    protected int range;

    public Ability(int damage, int range) {
        this.damage = damage;
        this.range = range;
    }

    public abstract void useAbility(MapCell[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter, Cell targetCell);

    protected void applyAbility(Character sourceCharacter, List<Character> targetCharacters) {
        for(Character targetCharacter : targetCharacters) {
            targetCharacter.takeDamage(sourceCharacter, damage);
        }
    }

    protected void applyAbility(Character sourceCharacter, Character targetCharacter) {
        targetCharacter.takeDamage(sourceCharacter, damage);
    }

    public abstract List<Set<Cell>> getAttackCells(MapCell[][] map, List<Character> team, List<Character> enemyTeam, Character sourceCharacter);
}
