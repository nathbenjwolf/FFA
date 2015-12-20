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
 * Ground Types:
 * Green - (0, 255, 0 ) - grass
 * Blue - (0, 0, 255) - water
 * White - (0, 0, 0) - NONE
 *
 * Object Types:
 * Brown - (185, 122, 87) - tree
 * White - (0, 0, 0) - NONE
 */
public final class MapParser {
    // Ground types:
    private static final int grass = 0x00FF00;
    private static final int water = 0x0000FF;

    // Object types:
    private static final int tree = 0xB97A57;

    // Both:
    private static final int NONE = 0x000000;

    private MapParser() {}

    public static MapCell[][] decodeMap(String cellGroundFile, String cellObjectFile) {
        BufferedImage cellGroundImg = null;
        BufferedImage cellObjectImg = null;
        int imgWidth;
        int imgHeight;
        try {
            cellGroundImg = ImageIO.read(new File(cellGroundFile));
            cellObjectImg = ImageIO.read(new File(cellObjectFile));
        } catch(IOException e) {
            System.err.println("MapParser.DecodeMap(): Error reading file");
        }

        assert(cellGroundImg.getWidth() == cellObjectImg.getWidth());
        assert(cellGroundImg.getHeight() == cellObjectImg.getHeight());

        imgWidth = cellGroundImg.getWidth();
        imgHeight = cellGroundImg.getHeight();

        MapCell[][] map = new MapCell[imgWidth][imgHeight];

        for(int y=0; y<imgHeight; y++) {
            for(int x=0; x<imgWidth; x++) {
                // Remove Alpha Layer
                int pixelColor = cellGroundImg.getRGB(x, y) & 0xFFFFFF;
                GroundElement ground = getGroundElement(pixelColor);

                // Remove Alpha Layer
                pixelColor = cellObjectImg.getRGB(x, y) & 0xFFFFFF;
                ObjectElement object = getObjectElement(pixelColor);

                map[x][y] = new MapCell(ground, object);
            }
        }

        return map;
    }

    private static ObjectElement getObjectElement(int rgb) {
        switch(rgb) {
            case tree:  return new Tree();

            case NONE:
            default: return null;
        }
    }

    private static GroundElement getGroundElement(int rgb) {
        switch(rgb) {
            case grass:  return new Grass();
            case water:  return new Water();

            case NONE:
            default: return null;
        }
    }

}
