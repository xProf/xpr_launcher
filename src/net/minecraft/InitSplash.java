         package net.minecraft;
         import java.awt.Graphics;
         import java.awt.Image;
 import java.awt.Toolkit;
 import javax.swing.ImageIcon;
 import javax.swing.JWindow;
 
 public class InitSplash extends JWindow
   implements Runnable
 {
   private static final long serialVersionUID = 4901025307215330193L;
   static Image bi = Toolkit.getDefaultToolkit().getImage(InitSplash.class.getResource("logo.png"));
 
   ImageIcon ii = new ImageIcon(bi);
 
   public static void main(String[] args)
   {
    InitSplash tss = new InitSplash();
    tss.showsplash();
   }
 
   public void paint(Graphics g)
   {
     g.drawImage(bi, 0, 0, this);
   }
 
   public void run()
   {
     main(null);
   }
 
   public void showsplash()
   {
     try
     {
       setSize(this.ii.getIconWidth(), this.ii.getIconHeight());
       setLocationRelativeTo(null);
       setVisible(true);
       Thread.sleep(2000L);
       dispose();
     } catch (Exception exception) {
       exception.printStackTrace();
     }
   }
}
 