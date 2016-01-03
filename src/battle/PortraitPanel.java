package battle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 1/2/2016.
 */
public class PortraitPanel extends JPanel {
    public static int panelWidth = BattlePanel.panelHeight;
    public static int panelHeight = BattlePanel.panelHeight;

    public PortraitPanel() {

    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.cyan);
        g.fillRect(0,0,getWidth(),getHeight());
    }
}
