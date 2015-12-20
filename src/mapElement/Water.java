package mapElement;

import battle.Board;
import character.Character;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 11/16/2015.
 */
public class Water extends GroundElement {
    public Water() {
        imageFilename = "Assets/MapElements/water.png";
    }

    @Override
    public BufferedImage getImage(int tick) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageFilename));
            int numFrames = img.getWidth();
            int xPixel = (int) (tick % numFrames);
            if(xPixel + Board.cellSize > numFrames) {
                // Merge the images from either end to make the animation seem infinite.
                BufferedImage img1 = img.getSubimage(xPixel, 0, numFrames-xPixel, Board.cellSize);
                BufferedImage img2 = img.getSubimage(0, 0, Board.cellSize-(numFrames-xPixel), Board.cellSize);
                img = new BufferedImage(Board.cellSize, Board.cellSize, img.getType());
                img.createGraphics().drawImage(img1, 0, 0, null);
                img.createGraphics().drawImage(img2, numFrames-xPixel, 0, null);
            } else {
                img = img.getSubimage(xPixel, 0, Board.cellSize, Board.cellSize);
            }

        } catch (IOException e) {
            System.err.println("MapElement.getImage(): Error reading file: " + imageFilename);
        }

        return img;
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
