package mapElement;

import character.Character;

/**
 * Created by Nathan on 11/16/2015.
 */
public class Tree extends ObjectElement {
    public Tree() {
        imageFilename = "Assets/MapElements/tree.png";
    }

    @Override
    public boolean isElementMovementBlocking(Character character) {
        return true;
    }

    @Override
    public boolean isElementProjectileBlocking() {
        return true;
    }
}
