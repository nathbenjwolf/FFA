package character;

import java.awt.*;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Ranger extends Character {

    public Ranger(int health, int damage, int attackRange, int moveRange) {
        super(health, damage, attackRange, moveRange);
        color = Color.MAGENTA;
    }
}
