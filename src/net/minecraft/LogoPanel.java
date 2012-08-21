package net.minecraft;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LogoPanel extends JPanel
{
  private static final long serialVersionUID = 1L;
  private Image bgImage;

  public LogoPanel()
  {
    setOpaque(true);
    try
    {
      BufferedImage src = ImageIO.read(LoginForm.class.getResource("logo.png"));
      int w = src.getWidth();
      int h = src.getHeight();
      bgImage = src.getScaledInstance(w, h, 32);
      setPreferredSize(new Dimension(w, h));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setCursor(Cursor.getPredefinedCursor(12));
    addMouseListener(new MouseAdapter() {
	      public void mousePressed(MouseEvent arg0) {
	        try {
	          Util.openLink(new URL(setting.siteLink).toURI());
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    });
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paintComponent(Graphics g2) {
    g2.drawImage(bgImage, 0, 0, null);
  }
}