package mapElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import character.Character;

/**
 * Created by Nathan on 11/16/2015.
 */

public abstract class MapElement {
    protected String imageFilename;

    public BufferedImage getImage(int tick) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageFilename));
        } catch (IOException e) {
            System.err.println("MapElement.getImage(): Error reading file: " + imageFilename);
        }

        return img;
    }
    public abstract boolean isElementMovementBlocking(Character character);
    public abstract boolean isElementProjectileBlocking();
}
