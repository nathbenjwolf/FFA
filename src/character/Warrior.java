package character;

import ability.WarriorAttackAbility;

import java.awt.*;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Warrior extends Character {

    public Warrior(int health, int moveRange) {
        super(health, moveRange);
        attack = new WarriorAttackAbility();
        imageFilename = "Assets/Characters/knight.png";
    }
}
