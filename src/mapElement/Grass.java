package mapElement;

/**
 * Created by Nathan on 11/16/2015.
 */
public class Grass extends MapElement {
    public Grass(){
        imageFilename = "Assets/MapElements/grass.bmp";
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
