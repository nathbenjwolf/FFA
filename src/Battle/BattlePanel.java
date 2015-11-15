package Battle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 11/14/2015.
 */
public class BattlePanel extends JPanel {
    static int panelHeight = 100;

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, this.getWidth(),this.getHeight());
    }
}
