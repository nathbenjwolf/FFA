package character;

import java.awt.*;

/**
 * Created by Nathan on 11/15/2015.
 */

//TODO: Update character with other features (equipment, abilities... etc)
public abstract class Character {
    public int health;
    public int damage;
    public int attackRange;
    public int moveRange;
    public Color color;

    public Character(int health, int damage, int attackRange, int moveRange) {
        this.health = health;
        this.damage = damage;
        this.attackRange = attackRange;
        this.moveRange = moveRange;
    }
}
