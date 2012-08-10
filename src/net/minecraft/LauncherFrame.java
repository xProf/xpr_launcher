package net.minecraft;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;
//import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public class LauncherFrame extends Frame
{
  private static final long serialVersionUID = 1L;
  public Map<String, String> customParameters = new HashMap<String, String>();
  public Launcher launcher;
  public LoginForm LoginForm = new LoginForm(this);
  public String clientId;
  public static String client;
  public static JPanel panelBg = new bg();
  

  public LauncherFrame()
  {

    super("Minecraft Launcher");
    setResizable(false);

    
    Thread thr = new Thread(new InitSplash());
    thr.start();
    setBackground(Color.BLACK);

    
    panelBg.setLayout(new BorderLayout());
    
    panelBg.setPreferredSize(new Dimension(854, 482));
    
    JPanel LauncherFormAll = new LoginForm(this);
    
    panelBg.add(LauncherFormAll);
    

    

    
    setLayout(new BorderLayout());
    add(panelBg);

    pack();
    setLocationRelativeTo(null);
    try
    {
      setIconImage(ImageIO.read(LauncherFrame.class.getResource("favicon.png")));
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent arg0) {
        new Thread() {
          public void run() {
            try {
              Thread.sleep(30000L);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("FORCING EXIT!");
            System.exit(0);
          }
        }
        .start();
        if (launcher != null) {
          launcher.stop();
          launcher.destroy();
        }
        System.exit(0);
      } } );
  }
  
 
  public void playCached(String userName) {
    try {
      if ((userName == null) || (userName.length() <= 0)) {
        userName = "Player";
      }
      launcher = new Launcher();
      launcher.customParameters.putAll(customParameters);
      launcher.customParameters.put("userName", userName);
      launcher.init();
      removeAll();
      add(launcher, "Center");
      validate();
      launcher.start();
      LoginForm = null;
      setTitle("Minecraft");
    } catch (Exception e) {
      e.printStackTrace();
      showError(e.toString());
    }
  }

  public void login(String userName, String password) {
	  
	    try {	     

                String rez_reestr;
                String am_param;
                String am_rezult;                
              
                String param = "user=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + 16;
                String result = Util.excutePost(setting.authLink, param);

                Preferences node = Preferences.userRoot().node("verify");
                rez_reestr=node.get("enderdragon", null);
                
	      if (result == null) {
	        showError("Неможливо підключитися до серверу!");
	        return;
	      }
	      if (!result.contains(":")) {
	        if (result.trim().equals("Bad login")) {
	          showError("Неправильный логін чи пароль!");
	          return;
	        } else if (result.trim().equals("Bad version")) {
	          showError("Необхідно оновити лаунчер");
	          return;
	        } else {
	          showError(result);
	          return;
	        }
	      }

      if (rez_reestr==null)
         {
           am_param="user=" + URLEncoder.encode(userName, "UTF-8") + "&action=register";
           am_rezult = Util.excutePost(setting.antiTwinkUrl, am_param);
           node.put("enderdragon", am_rezult);
           Preferences.userRoot().flush();
           return;
         }else
            {
                       am_param="user=" + URLEncoder.encode(userName, "UTF-8") + "&action=testing&serial="+rez_reestr;
                       am_rezult = Util.excutePost(setting.antiTwinkUrl, am_param);
                         if (!am_rezult.contains("All ok")) 
                         {
                              showError(am_rezult);
                              return;
                         }
            }

      am_param="user=" + URLEncoder.encode(userName, "UTF-8") + "&action=start";
      am_rezult = Util.excutePost(setting.onStartUrl, am_param);


	     BuildProfilePanelForm(result);

	      return;
	    } catch (Exception e) {

	      e.printStackTrace();
	      showError(e.toString());
	    }
	  }
  
  public void BuildProfilePanelForm(String result) {
	  JPanel asdasd = LoginForm.profile(result);
	  panelBg.removeAll();
	asdasd.setBounds(0, 0, 854, 480);
	panelBg.add(asdasd);
    validate();
    repaint();
  }

  private void showError(String error) {
	  JPanel asdasd = LoginForm.buildOfflinePanel(error);
	  panelBg.removeAll();
	asdasd.setBounds(0, 0, 854, 480);
	panelBg.add(asdasd);
    validate();
    repaint();
  }

  public boolean canPlayOffline(String userName) {
    Launcher launcher = new Launcher();
    launcher.customParameters.putAll(customParameters);
    launcher.init(userName, null, null, null);
    return launcher.canPlayOffline();
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException) {
    }
    LauncherFrame launcherFrame = new LauncherFrame();
    launcherFrame.setVisible(true);
    launcherFrame.customParameters.put("stand-alone", "true");

    if (args.length >= 3) {
      String ip = args[2];
      String port = "25565";
      if (ip.contains(":")) {
        String[] parts = ip.split(":");
        ip = parts[0];
        port = parts[1];
      }

      launcherFrame.customParameters.put("server", ip);
      launcherFrame.customParameters.put("port", port);
    }

    if (args.length >= 1) {
      launcherFrame.LoginForm.userName.setText(args[0]);
      if (args.length >= 2) {
        launcherFrame.LoginForm.password.setText(args[1]);
        launcherFrame.LoginForm.doLogin();
      }
    }
  }
  
  public void Startminecraft (String result){
	  setResizableLol();
	  md5s();
      String[] values = result.split(":");

      launcher = new Launcher();
      launcher.customParameters.putAll(customParameters);
      launcher.customParameters.put("userName", values[2].trim());
      launcher.customParameters.put("latestVersion", values[0].trim());
      launcher.customParameters.put("downloadTicket", values[1].trim());
      launcher.customParameters.put("sessionId", values[3].trim());
      launcher.init();

      removeAll();
      add(launcher, "Center");
      validate();
      launcher.start();
      LoginForm.loginOk();
      LoginForm = null;
      setTitle("Minecraft");
      
      return;
  }
  private void md5s(){
	  String applicationData = System.getenv("APPDATA");
      String  f = applicationData + "/." + setting.mineFolder + "/bin/"+ client +".jar";
 

 try{
  MessageDigest md5  = MessageDigest.getInstance("MD5");
String p = calculateHash(md5, f);
	try {	     	
					URL localURL = new URL(setting.hashLink + p);
					BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURL.openStream()));
					String result = localBufferedReader.readLine();
					if (result.trim().equals("1")){
						GameUpdater.forceUpdate = true;
						return;
					}
					if (result.trim().equals("2")){
						GameUpdater.forceUpdate = false;
						return;
					}
				}catch (Exception e) {
					 return;
				 }

 }catch (Exception e) {
	 GameUpdater.forceUpdate = true;
	 return;
  }
  }
  
  public static String calculateHash(MessageDigest algorithm,String fileName) throws Exception{
      FileInputStream    fis = new FileInputStream(fileName);
      BufferedInputStream bis = new BufferedInputStream(fis);
      DigestInputStream  dis = new DigestInputStream(bis, algorithm);

      while (dis.read() != -1);
            byte[] hash = algorithm.digest();

      return byteArray2Hex(hash);
  }
private static String byteArray2Hex(byte[] hash) {
      Formatter formatter = new Formatter();
      for (byte b : hash) {
          formatter.format("%02x", b);
      }
      return formatter.toString();
  }

public static void saveSetting(String prop) throws IOException{

		Properties defaultProps = new Properties();
		FileInputStream in;
		in = new FileInputStream(Util.getWorkingDirectory() + "/launcher.properties");
		defaultProps.load(in);
		defaultProps.setProperty("client", prop);
        FileOutputStream output = new FileOutputStream(Util.getWorkingDirectory() + "/launcher.properties");
        defaultProps.store(output, "Saved settings");
        in.close();
        output.close();

}
public void setResizableLol(){
	setResizable(true);
}

}