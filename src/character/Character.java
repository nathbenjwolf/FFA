package character;

import utils.Globals;

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
    protected int characterId;

    public Character(int health, int damage, int attackRange, int moveRange) {
        this.health = health;
        this.damage = damage;
        this.attackRange = attackRange;
        this.moveRange = moveRange;
        this.characterId = Globals.getCharacterId();
    }

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof Character) {
            return this.characterId == ((Character) object).characterId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.characterId;
    }
}
