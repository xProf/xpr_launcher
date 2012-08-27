package net.minecraft;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
//import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;


public class LauncherFrame extends Frame
{
  private static final long serialVersionUID = 1L;
  public Map<String, String> customParameters = new HashMap<String, String>();
  public Launcher launcher;
  public LoginForm LoginForm = new LoginForm(this);
  public String clientId;
  public static String client;
  public static JPanel panelBg = new bg();
  private String userLogin;

  public LauncherFrame()
  {

    super("Minecraft Launcher");
    setResizable(false);

    
    Thread thr = new Thread(new InitSplash());
    thr.start();
    setBackground(Color.BLACK);

    
    panelBg.setLayout(new BorderLayout());
    
    panelBg.setPreferredSize(new Dimension(854, 490));
    
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
	userLogin=userName;  
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
                int beginString=result.indexOf("[[");  
                String resultObr=result.substring(beginString,beginString+15);
	        if (resultObr.trim().equals("[[Bad login  ]]")) {
	          showError("Неправильный логін чи пароль!");
	          return;
	        } else if (resultObr.trim().equals("[[Bad version]]")) {
	          showError("Необхідно оновити лаунчер");
                  updateLauncher();
	          return;
	        } else {
	          showError(resultObr);
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
	  setResizable(true);
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
     
  private String getHash(String str) 
  {
        MessageDigest md5 ;        
        StringBuffer  hexString = new StringBuffer();
        try {
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(str.getBytes()); 
            byte messageDigest[] = md5.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
        } 
        catch (NoSuchAlgorithmException e) {                        
            return e.toString();
        }
        return hexString.toString();
    }
  
  private void md5s(){
      String allHashes=calculateDirectoryHash(new File(setting.mineFolderAbsolute + File.separator+ "bin"));
      allHashes=allHashes+calculateDirectoryHash(new File(setting.mineFolderAbsolute + File.separator+ "mods"));
      String  f = setting.mineFolderAbsolute + File.separator+"updater.exe";
  try{
  MessageDigest md5  = MessageDigest.getInstance("md5");
  String p =allHashes + calculateHash(md5, f);
 
  String hashOfAllHashes=getHash(p);
  	try {	     	
					URL localURL2 = new URL(setting.hashEtalonLink +hashOfAllHashes+"&user="+userLogin);
					BufferedReader localBufferedReader2 = new BufferedReader(new InputStreamReader(localURL2.openStream()));

                                        URL localURL = new URL(setting.hashLink +hashOfAllHashes+"&user="+userLogin);
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

private String calculateDirectoryHash(File path)
{
    String hashMD5="";
    if (path.exists())
    {
        File[] files=path.listFiles();
        for (int i=0;i<files.length;i++)
        {
            if (files[i].isDirectory()) hashMD5=hashMD5+calculateDirectoryHash(files[i]);
                 else 
                    {
                try {
                    MessageDigest md5  = MessageDigest.getInstance("md5");
                    hashMD5=hashMD5+calculateHash(md5,files[i].getAbsolutePath());
                } catch (Exception e) {
                    Logger.getLogger(LauncherFrame.class.getName()).log(Level.SEVERE, null, e);
                }
                    }
        }    
    }
    return hashMD5;
}

private void updateLauncher() throws IOException
{
    if (new File(setting.mineFolderAbsolute+File.separator+setting.updaterFileName).exists() )
        {
            Runtime.getRuntime().exec(setting.mineFolderAbsolute+File.separator+setting.updaterFileName);
            System.exit(0);
        }
}
}