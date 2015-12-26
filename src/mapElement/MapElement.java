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
    protected BufferedImage image;

    public BufferedImage getImage(int tick) {
        // Only read image once
        if(image == null) {
            try {
                image = ImageIO.read(new File(imageFilename));
            } catch (IOException e) {
                System.err.println("MapElement.getImage(): Error reading file: " + imageFilename);
            }
        }

        return modifyImage(image, tick);
    }

    // Can be overriden if image is adjusted based on tick (animation)
    protected BufferedImage modifyImage(BufferedImage img, int tick) { return img; }
    public abstract boolean isElementMovementBlocking(Character character);
    public abstract boolean isElementProjectileBlocking();
}
