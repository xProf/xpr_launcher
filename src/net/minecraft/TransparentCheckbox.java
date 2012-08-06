package net.minecraft;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

public class TransparentCheckbox extends JCheckBox
{
  private static final long serialVersionUID = 1L;

  public TransparentCheckbox(String string)
  {
    super(string);
    
			setIcon(new ImageIcon("src/image/CheckBox1.png"));	
			setRolloverIcon(new ImageIcon("src/image/CheckBox2.png"));
			setSelectedIcon(new ImageIcon("src/image/CheckBox3.png"));
			setRolloverSelectedIcon(new ImageIcon("src/image/CheckBox4.png"));
		



	
    
    setForeground(Color.decode("#3ba4d1"));
    setOpaque(false);
  }
  
  

}