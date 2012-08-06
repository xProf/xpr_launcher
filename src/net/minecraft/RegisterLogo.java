package net.minecraft;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class RegisterLogo extends JPanel
{
  private static final long serialVersionUID = 1L;
  private Image bgImage;

  public RegisterLogo()
  {
    setOpaque(true);
    try
    {
      BufferedImage src = ImageIO.read(LoginForm.class.getResource("registerLogo.png"));
      int w = src.getWidth();
      int h = src.getHeight();
      bgImage = src.getScaledInstance(w, h, 16);
      setPreferredSize(new Dimension(w + 70, h + 16));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paintComponent(Graphics g2) {
    g2.drawImage(bgImage, 70, 0, null);
  }
}