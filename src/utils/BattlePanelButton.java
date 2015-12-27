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
    static private float iconXStartUnpressed = 0.3F;
    static private float iconYStartUnpressed = 0.3F;
    static private float iconXStartPressed = 0.305F;
    static private float iconYStartPressed = 0.375F;
    static private int fontSize = 20;

    private BufferedImage btnDefault;
    private BufferedImage btnPressed;
    private BufferedImage btnHover;
    private BufferedImage btnDisabled;

    private ArrayList<ActionListener> listeners = new ArrayList<>();
    private String text;
    private BufferedImage iconImage;
    private BufferedImage image;
    private float textXStart;
    private float textYStart;
    private float iconXStart;
    private float iconYStart;

    public BattlePanelButton(String text, BufferedImage iconImage) {
        this.text = text;
        this.iconImage = iconImage;
        initButton();
    }

    public BattlePanelButton(String text) {
        this.text = text;
        initButton();
    }

    private void initButton() {
        try {
            btnDefault = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Default.png"));
            btnPressed = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Pressed.png"));
            btnHover = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Hover.png"));
            btnDisabled = ImageIO.read(new File("Assets/Buttons/BattlePanelButton_Disabled.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(isEnabled()) {
            this.image = btnDefault;
        } else {
            this.image = btnDisabled;
        }
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.addMouseListener(this);
        this.textXStart = textXStartUnpressed;
        this.textYStart = textYStartUnpressed;
        this.iconXStart = iconXStartUnpressed;
        this.iconYStart = iconYStartUnpressed;
        System.out.println("Creating button");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            this.image = btnDefault;
        } else {
            this.image = btnDisabled;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("Painting button: " + this.text);
        // Background
        g.drawImage(image,
                    0, 0, this.getWidth(), this.getHeight(),
                    0, 0, image.getWidth(), image.getHeight(),
                    null);
        // Icon
        if(iconImage != null) {
            g.drawImage(iconImage, (int)(iconXStart*this.getWidth()), (int)(iconYStart*this.getHeight()), null);
        }

        // Text
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
        if(isEnabled()) {
            this.textXStart = textXStartPressed;
            this.textYStart = textYStartPressed;
            this.iconXStart = iconXStartPressed;
            this.iconYStart = iconYStartPressed;
            image = btnPressed;
        } else {
            image = btnDisabled;
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(isEnabled()) {
            this.textXStart = textXStartUnpressed;
            this.textYStart = textYStartUnpressed;
            this.iconXStart = iconXStartUnpressed;
            this.iconYStart = iconYStartUnpressed;
            image = btnHover;
            notifyListeners(e);
        } else {
            image = btnDisabled;
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(isEnabled()) {
            this.textXStart = textXStartUnpressed;
            this.textYStart = textYStartUnpressed;
            this.iconXStart = iconXStartUnpressed;
            this.iconYStart = iconYStartUnpressed;
            image = btnHover;
        } else {
            image = btnDisabled;
        }

        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(isEnabled()) {
            this.textXStart = textXStartUnpressed;
            this.textYStart = textYStartUnpressed;
            this.iconXStart = iconXStartUnpressed;
            this.iconYStart = iconYStartUnpressed;
            image = btnDefault;
        } else {
            image = btnDisabled;
        }

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
