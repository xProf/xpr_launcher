package net.minecraft;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Transparentbg extends JPanel
{
  private static final long serialVersionUID = 1L;
  private Image img;
  private Image bgImage;

  public Transparentbg()
  {
    setOpaque(true);
    try
    {
      bgImage = ImageIO.read(LoginForm.class.getResource("transparrentbg.png")).getScaledInstance(1, 1, 1);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paintComponent(Graphics g2) {
    int w = getWidth();
    int h = getHeight();
    if ((img == null) || (img.getWidth(null) != w) || (img.getHeight(null) != h)) {
      img = createImage(w, h);

      Graphics g = img.getGraphics();
      for (int x = 0; x <= w / 1; x++) {
        for (int y = 0; y <= h / 1; y++)
          g.drawImage(bgImage, x, y, null);
      }
      if ((g instanceof Graphics2D)) {
        Graphics2D gg = (Graphics2D)g;

      }
      g.dispose();
    }
    g2.drawImage(img, 0, 0, w, h, null);
  }
}