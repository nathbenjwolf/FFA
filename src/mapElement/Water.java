package mapElement;

import battle.Board;
import battle.CellPixelAbstraction;
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
    protected BufferedImage modifyImage(BufferedImage img, int tick) {
        BufferedImage finalImg;
        int numFrames = img.getWidth();
        int xPixel = (int) (tick % numFrames);
        int cellWidth = CellPixelAbstraction.cellXDelta*2;
        int cellHeight = CellPixelAbstraction.cellYDelta*2;
        if(xPixel + cellWidth > numFrames) {
            // Merge the images from either end to make the animation seem infinite
            int remainingXPixels = numFrames-xPixel;
            BufferedImage img1 = img.getSubimage(xPixel, 0, remainingXPixels, cellHeight);
            BufferedImage img2 = img.getSubimage(0, 0, cellWidth-remainingXPixels, cellHeight);
            finalImg = new BufferedImage(cellWidth, cellHeight, img.getType());
            finalImg.createGraphics().drawImage(img1, 0, 0, null);
            finalImg.createGraphics().drawImage(img2, remainingXPixels, 0, null);
        } else {
            finalImg = img.getSubimage(xPixel, 0, cellWidth, cellHeight);
        }

        return finalImg;
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
