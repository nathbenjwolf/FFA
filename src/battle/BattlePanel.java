package battle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nathan on 11/14/2015.
 */
public class BattlePanel extends JPanel {
    static int panelHeight = 100;
    static private String backgroundFile = "Assets/Misc/BattlePanelBackground.png";
    static BufferedImage img;
    static BufferedImage subImg;

    public BattlePanel() {
        try {
            img = ImageIO.read(new File(backgroundFile));
        } catch (IOException e) {
            System.err.println("BattlePanel: Error reading file: " + backgroundFile);
        }
    }

    public void paintComponent(Graphics g) {
        if(subImg == null) {
            subImg = img.getSubimage(0, 0, this.getWidth(), this.getHeight());
        }
        g.drawImage(subImg, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    public void updateBattlePanel(List<JButton> buttons, JButton backButton) {
        this.removeAll();
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        // Normal buttons
        for(int i = 0; i < buttons.size(); i++) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.gridx = i/2;
            c.gridy = i%2;
            this.add(buttons.get(i), c);
        }

        // Back button
        if(backButton != null) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.gridx = 1;
            c.gridy = 1;
            this.add(backButton, c);
        }

        revalidate();
        repaint();
    }
}
