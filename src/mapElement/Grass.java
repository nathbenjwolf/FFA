package mapElement;

import character.Character;

/**
 * Created by Nathan on 11/16/2015.
 */
public class Grass extends MapElement {
    public Grass(){
        imageFilename = "Assets/MapElements/grass.png";
    }

    @Override
    public boolean isElementMovementBlocking(Character character) {
        return false;
    }

    @Override
    public boolean isElementProjectileBlocking() {
        return false;
    }
}
