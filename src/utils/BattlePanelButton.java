package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nathan on 12/13/2015.
 */
public class BattlePanelButton extends JButton implements MouseListener {
    static private float textXStartUnpressed = 0.4F;
    static private float textYStartUnpressed = 0.6F;
    static private float textXStartPressed = 0.405F;
    static private float textYStartPressed = 0.675F;
    static private int fontSize = 20;

    private ArrayList<ActionListener> listeners = new ArrayList<>();
    private String text;
    private BufferedImage image;
    private float textXStart;
    private float textYStart;

    public BattlePanelButton(String text) {
        this.text = text;
        try {
            image = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Default.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.addMouseListener(this);
        this.textXStart = textXStartUnpressed;
        this.textYStart = textYStartUnpressed;
        System.out.println("Creating button");
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        System.out.println("Painting button");
        g.drawImage(image,
                    0, 0, this.getWidth(), this.getHeight(),
                    0, 0, image.getWidth(), image.getHeight(),
                    null);
        g.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
        g.drawString(text, (int)(textXStart*this.getWidth()), (int)(textYStart*this.getHeight()));
    }

    @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        try {
            image = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Pressed.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.textXStart = textXStartPressed;
        this.textYStart = textYStartPressed;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            image = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Hover.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.textXStart = textXStartUnpressed;
        this.textYStart = textYStartUnpressed;
        notifyListeners(e);
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("mouse entered");
        try {
            image = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Hover.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.textXStart = textXStartUnpressed;
        this.textYStart = textYStartUnpressed;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        try {
            image = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Default.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.textXStart = textXStartUnpressed;
        this.textYStart = textYStartUnpressed;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public void addActionListener(ActionListener listener)
    {
        listeners.add(listener);
    }

    private void notifyListeners(MouseEvent e)
    {
        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, new String(text), e.getWhen(), e.getModifiers());
        synchronized(listeners)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                ActionListener tmp = listeners.get(i);
                tmp.actionPerformed(evt);
            }
        }
    }
}
