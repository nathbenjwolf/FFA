package character;

import ability.RangerAttackAbility;
import battle.Cell;
import mapElement.MapElement;
import utils.PathFinding;

import java.awt.*;
import java.util.Set;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Ranger extends Character {

    public Ranger(int health, int moveRange) {
        super(health, moveRange);
        attack = new RangerAttackAbility();
        imageFilename = "Assets/Characters/Archer.png";
    }

}
