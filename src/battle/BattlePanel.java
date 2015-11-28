package battle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Nathan on 11/14/2015.
 */
public class BattlePanel extends JPanel {
    static int panelHeight = 100;

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, this.getWidth(),this.getHeight());
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
