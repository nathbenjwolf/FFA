package battle;

/**
 * Created by Nathan on 11/16/2015.
 */

import mapElement.*;
import mapElement.MapElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads a map from an image using valid colors. Valid colors are as follows:
 * Green - (0, 255, 0 ) - grass
 * Blue - (0, 0, 255) - water
 * Brown - (185, 122, 87) - tree
 */
public final class MapParser {
    private static final int grass = 0x00FF00;
    private static final int water = 0x0000FF;
    private static final int tree = 0xB97A57;

    private MapParser() {}

    public static MapElement[][] decodeMap(String filename) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch(IOException e) {
            System.err.println("MapParser.DecodeMap(): Error reading file: " + filename);
        }

        MapElement[][] map = new MapElement[img.getWidth()][img.getHeight()];

        for(int y=0; y<img.getHeight(); y++) {
            for(int x=0; x<img.getWidth(); x++) {
                int pixelColor = img.getRGB(x, y);
                // remove alpha layer
                pixelColor = pixelColor & 0xFFFFFF;
                map[x][y] = getMapElement(pixelColor);
            }
        }

        return map;
    }

    private static MapElement getMapElement(int rgb) {
        switch(rgb) {
            case grass:  return new Grass();
            case water:  return new Water();
            case tree:  return new Tree();
            default: return null;
        }
    }

}
