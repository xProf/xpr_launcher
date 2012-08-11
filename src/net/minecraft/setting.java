package net.minecraft;

public class setting {
	 public static String siteLink = "http://minecraft-tyachiv.org.ua/";
	 public static String forumLink = "http://minecraft-tyachiv.org.ua/_launcher16/forum.php";
	 public static String uslugiLink = "http://minecraft-tyachiv.org.ua/_launcher16/donat.php";
	 public static String monitorLink = "http://minecraft-tyachiv.org.ua/_launcher16/monitor.php";
	 public static String hashLink = "http://minecraft-tyachiv.org.ua/_launcher16/hash.php?hash=";
	 public static String authLink = "http://minecraft-tyachiv.org.ua/_launcher16/auth.php";
	 public static String loadLink = "http://minecraft-tyachiv.org.ua/download/";
	 public static String skinsLink = "http://minecraft-tyachiv.org.ua/_launcher16/skin2d.php?skinpath=";
	 public static String registerLink = "http://minecraft-tyachiv.org.ua/_launcher16/registerServer.php?user=";
	 public static String newsUrl = "http://minecraft-tyachiv.org.ua/news/index.htm";
	 public static String catalogSkinsUrl = "http://www.minecraftskins.com/";
	 public static String mineFolder = "minecraft";//Папка в которой находится майнкрафт (.minecraft)
         public static String mineFolderAbsolute = getMineDirectory();//Папка в которой находится майнкрафт (.minecraft)
	 public static String downLaunchLink = "http://minecraft-tyachiv.org.ua/_launcher16/download.jsp";
	 public static String client1 = "minecraft";
	 public static String client2 = "minecraft2";
	 public static String loadLinkUrlSkins = "http://minecraft-tyachiv.org.ua/_launcher16/skinLoad.php";
         public static String versionUrl= "http://minecraft-tyachiv.org.ua/download/version";
         public static String onStartUrl="http://minecraft-tyachiv.org.ua/_launcher16/start.php";
         public static String antiTwinkUrl="http://minecraft-tyachiv.org.ua/_launcher16/amlt.php";
         
         private static String getMineDirectory()
            {
                String OS = System.getProperty("os.name").toUpperCase();
                if (OS.contains("WIN"))
                    return System.getenv("APPDATA")+"\\."+mineFolder;
                else if (OS.contains("MAC"))
                       return System.getProperty("user.home") + "/Library/Application " + "Support";
                else if (OS.contains("NUX"))
                        return System.getProperty("user.home")+"/."+mineFolder;
                return System.getProperty("user.dir");
            }
}