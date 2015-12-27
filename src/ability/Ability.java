package ability;

import battle.Cell;
import character.Character;
import mapElement.MapCell;
import mapElement.MapElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Created by Nathan on 11/28/2015.
 */
public abstract class Ability {
    protected int damage;
    protected int range;
    protected String iconFilename = "Assets/ButtonIcons/defaultIcon.png";
    protected BufferedImage iconImg;

    public Ability(int damage, int range) {
        this.damage = damage;
        this.range = range;
    }

    public BufferedImage getIcon() {
        if(iconImg == null) {
            try {
                iconImg = ImageIO.read(new File(iconFilename));
            } catch (IOException e) {
                System.err.println("Ability.getIcon(): Error reading file: " + iconFilename );
            }
        }

        return iconImg;
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
