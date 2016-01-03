package mapElement;

import character.Character;

/**
 * Created by Nathan on 1/2/2016.
 */
public class Boulder extends ObjectElement {
    public Boulder(){
        imageFilename = "Assets/MapElements/boulder.png";
    }

    @Override
    public boolean isElementMovementBlocking(Character character) {
        return true;
    }

    @Override
    public boolean isElementProjectileBlocking() {
        return false;
    }
}
