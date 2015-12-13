package character;

import ability.WarriorAttackAbility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Nathan on 11/15/2015.
 */
public class Warrior extends Character {

    public Warrior(int health, int moveRange) {
        super(health, moveRange);
        attack = new WarriorAttackAbility();
        imageFilename = "Assets/Characters/test";
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage img = null;
        try {
            switch(direction) {
                case UP:
                    img = ImageIO.read(new File(imageFilename + "_up.png"));
                    break;
                case DOWN:
                    img = ImageIO.read(new File(imageFilename + "_down.png"));
                    break;
                case RIGHT:
                    img = ImageIO.read(new File(imageFilename + "_right.png"));
                    break;
                case LEFT:
                    img = ImageIO.read(new File(imageFilename + "_left.png"));
                default:
                    break;
            }
        } catch (IOException e) {
            System.err.println("Character.getImage(): Error reading file: " + imageFilename);
        }

        return img;
    }
}
