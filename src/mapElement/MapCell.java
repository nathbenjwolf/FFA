package mapElement;

import character.Character;

/**
 * Created by Nathan on 12/19/2015.
 */
public class MapCell {
    public GroundElement ground;
    public ObjectElement object;

    public MapCell(GroundElement ground, ObjectElement object) {
        this.ground = ground;
        this.object = object;
    }

    public boolean isPresent() {
        return ground != null || object != null;
    }

    public boolean isElementMovementBlocking(Character character) {
        if(ground != null) {
            if(ground.isElementMovementBlocking(character)) {
                return true;
            }
        }
        if(object != null) {
            if(object.isElementMovementBlocking(character)) {
                return true;
            }
        }

        if(!isPresent()) {
            return true;
        }

        return false;
    }
    public boolean isElementProjectileBlocking() {
        if(ground != null) {
            if(ground.isElementProjectileBlocking()) {
                return true;
            }
        }
        if(object != null) {
            if(object.isElementProjectileBlocking()) {
                return true;
            }
        }
        if(!isPresent()) {
            return false;
        }

        return false;
    }
}
