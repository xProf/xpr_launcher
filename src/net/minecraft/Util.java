package net.minecraft;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
//import java.security.PublicKey;
//import java.security.cert.Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.net.ssl.HttpsURLConnection;

public class Util
{
  private static File workDir = null;

  public static File getWorkingDirectory() {
    if (workDir == null) workDir = getWorkingDirectory(setting.mineFolder);
    return workDir;
  }

  public static File getWorkingDirectory(String applicationName) {
    String userHome = System.getProperty("user.home", ".");
    File workingDirectory;
    switch (getPlatform().ordinal()) {
    case 0:
    case 1:
      workingDirectory = new File(userHome, '.' + applicationName + '/');
      break;
    case 2:
      String applicationData = System.getenv("APPDATA");
      if (applicationData != null)
    	  workingDirectory = new File(applicationData, "." + applicationName + '/');
      else
        workingDirectory = new File(userHome, '.' + applicationName + '/');
      break;
    case 3:
      workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
      break;
    default:
      workingDirectory = new File(userHome, applicationName + '/');
    }
    if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) throw new RuntimeException("The working directory could not be created: " + workingDirectory);
    return workingDirectory;
  }

  private static OS getPlatform() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) return OS.windows;
    if (osName.contains("mac")) return OS.macos;
    if (osName.contains("solaris")) return OS.solaris;
    if (osName.contains("sunos")) return OS.solaris;
    if (osName.contains("linux")) return OS.linux;
    if (osName.contains("unix")) return OS.linux;
    return OS.unknown;
  }

  public static String excutePost(String targetURL, String urlParameters)
  {
    HttpURLConnection connection = null;
    try
    {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();

      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      connection.connect();

      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();

      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null)
      {
        response.append(line);
      }
      rd.close();

      String str1 = response.toString();
      return str1;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      if (connection != null)
        connection.disconnect();
    }
  }
  
  public static boolean isEmpty(String str) {
    return (str == null) || (str.length() == 0);
  }

  public static void openLink(URI uri) {
    try {
      Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { uri });
    } catch (Throwable e) {
      System.out.println("Failed to open link " + uri.toString());
    }
  }

// -------------------------------------------------
  public static void resetVersion()
  {
    DataOutputStream dos = null;
    try {
      File dir = new File(getWorkingDirectory() + File.separator + "bin" + File.separator);
      File versionFile = new File(dir, "version");
      dos = new DataOutputStream(new FileOutputStream(versionFile));
      dos.writeUTF("0");
      dos.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        dos.close();
      } catch (IOException ex) {
        Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  
  private static enum OS
  {
    linux, solaris, windows, macos, unknown;
  }
}