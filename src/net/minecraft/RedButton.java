package net.minecraft;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ButtonModel;
import javax.swing.JButton;
public class RedButton extends JButton {
    public RedButton(String text) {
   setText(text);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.white);
        setPreferredSize(new Dimension(27, 27));
	    setCursor(Cursor.getPredefinedCursor(12));

    } 
    @Override
    protected void paintComponent(Graphics g) {
        ButtonModel buttonModel = getModel();
        Graphics2D gd = (Graphics2D) g.create();
        


                gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gd.setPaint(new   GradientPaint(0,   0, Color.decode("#ec4040"),   0, getHeight(), Color.decode("#bb2e38")));
       
         if (buttonModel.isRollover()) {
            gd.setPaint(new GradientPaint(0, 0, Color.decode("#f04d4d"), 0, getHeight(), Color.decode("#c93743")));
            if (buttonModel.isPressed()) {
                gd.setPaint(new GradientPaint(0, 0, Color.decode("#bb2e38"), 0, getHeight(), Color.decode("#ec4040")));
            } else {
                setForeground(Color.white);
            }
        }
         

                
        gd.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        gd.dispose();
        super.paintComponent(g);
    }
}