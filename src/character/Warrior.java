package character;

import java.awt.*;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Warrior extends Character {

    public Warrior(int health, int damage, int attackRange, int moveRange) {
        super(health, damage, attackRange, moveRange);
        color = Color.CYAN;
        imageFilename = "Assets/Characters/Knight.png";
    }
}
