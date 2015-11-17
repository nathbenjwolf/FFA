package mapElement;

/**
 * Created by Nathan on 11/16/2015.
 */
public class Water extends MapElement {
    public Water() {
        imageFilename = "Assets/MapElements/water.bmp";
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
