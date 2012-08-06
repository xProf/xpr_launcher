package net.minecraft;

import java.awt.Color;
import javax.swing.JLabel;

public class TransparentLabel extends JLabel
{
  private static final long serialVersionUID = 1L;
  static final Color LIGHT_BLUE_COLOR = new Color(45, 118, 214);

  public TransparentLabel(String string, int center)
  {
    super(string, center);
    
    setForeground(LIGHT_BLUE_COLOR);
  }

  public TransparentLabel(String string) {
    super(string);
    setForeground(Color.DARK_GRAY);
  }

  public boolean isOpaque() {
    return false;
  }
}