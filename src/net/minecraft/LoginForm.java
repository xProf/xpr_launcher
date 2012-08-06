package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class LoginForm extends TransparentPanel
{
  //private static final int PANEL_SIZE = 100;
  private static final long serialVersionUID = 1L;

  public static JTextField userName = new JTextField(20);
  public static JPasswordField password = new JPasswordField(20);
  private TransparentCheckbox rememberBox = new TransparentCheckbox("Запам'ятати пароль");
  private TransparentButton launchButton = new TransparentButton("Вхід");
  private TransparentButton optionsButton = new TransparentButton("Налаштування");
  private TransparentButton registerButton = new TransparentButton("Реєстрація");
  private TransparentButton retryButton = new TransparentButton("Спробувати ще раз");
  private TransparentButton offlineButton = new TransparentButton("Грати оффлайн");
  private TransparentButton forgetPass = new TransparentButton ("Забули пароль");
  private TransparentLabel errorLabel = new TransparentLabel("", 0);
  private LauncherFrame launcherFrame;
  private boolean outdated = false; 
  private JScrollPane scrollPane;
  private JLabel userNameL = new TransparentLabel("Логін:");
  private JLabel passwordL = new TransparentLabel("Пароль:");
  private Color LIGHTBLUEAUTH = Color.decode("#3ba4d1");
  final Font font = new Font("Arial", Font.PLAIN, 11);
  private Color LIGHTREDERROR = Color.decode("#dd504f");
  final Font fontError = new Font("Arial", Font.BOLD, 12);
  private String item;

  public LoginForm(final LauncherFrame launcherFrame)
  { 
    this.launcherFrame = launcherFrame;
    setLayout(null);

    JPanel buildMLP = buildMainLoginPanel();
    buildMLP.setBounds(0, 0, 854, 480);
    add(buildMLP);

    readUsername();

    ActionListener al = new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        doLogin();
      }
    };
    userName.addActionListener(al);
    password.addActionListener(al);

    retryButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        errorLabel.setText("");
        launcherFrame.panelBg.removeAll();
        launcherFrame.panelBg.add(LoginForm.this.buildMainLoginPanel());
        launcherFrame.panelBg.validate();
        launcherFrame.panelBg.repaint();
      }
    });
    offlineButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        launcherFrame.playCached(userName.getText());
      }
    });
    launchButton.addActionListener(al);


    
    registerButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          new RegisterPanel(launcherFrame).setVisible(true);
        }
      });
  }

  public void doLogin() {
	  launcherFrame.panelBg.removeAll();
	  launcherFrame.panelBg.add(buildMainAuth2Panel());
	  launcherFrame.panelBg.validate();
	  launcherFrame.panelBg.repaint();

	    new Thread() {
	      public void run() {
	        try {
	          launcherFrame.login(userName.getText(), new String(password.getPassword()));
	        } catch (Exception e) {
	          setError(e.toString());
	        }
	      }
	    }
	    .start();

	  }


  private void readUsername() {
    try {
      File lastLogin = new File(Util.getWorkingDirectory(), "lastlogin");

      Cipher cipher = getCipher(2, "passwordfile");
      DataInputStream dis;
      if (cipher != null)
        dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
      else {
        dis = new DataInputStream(new FileInputStream(lastLogin));
      }
      userName.setText(dis.readUTF());
      password.setText(dis.readUTF());
      rememberBox.setSelected(password.getPassword().length > 0);
      dis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void writeUsername() {
    try {
      File lastLogin = new File(Util.getWorkingDirectory(), "lastlogin");

      Cipher cipher = getCipher(1, "passwordfile");
      DataOutputStream dos;
      if (cipher != null)
        dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
      else {
        dos = new DataOutputStream(new FileOutputStream(lastLogin));
      }
      dos.writeUTF(userName.getText());
      dos.writeUTF(rememberBox.isSelected() ? new String(password.getPassword()) : "");
      dos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Cipher getCipher(int mode, String password) throws Exception {
    Random random = new Random(43287234L);
    byte[] salt = new byte[8];
    random.nextBytes(salt);
    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

    SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
    Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
    cipher.init(mode, pbeKey, pbeParamSpec);
    return cipher;
  }

  private JScrollPane getUpdateNews()
  {
    if (scrollPane != null) return scrollPane;
    try
    {
      final JTextPane editorPane = new JTextPane()
      {
        private static final long serialVersionUID = 1L;
      };
      editorPane.setContentType ( "text/html" );
      editorPane.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>Loading update news..</center></font></body></html>");
      editorPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent he) {
          if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            try {
              Util.openLink(he.getURL().toURI());
            } catch (Exception e) {
              e.printStackTrace();
            }
        }
      });
      new Thread() {
        public void run() {
          try {
        	  editorPane.setPage(new URL(setting.newsUrl));
          } catch (Exception e) {
            e.printStackTrace();
            editorPane.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>Failed to update news<br></center></font></body></html>");
          }
        }
      }
      .start();
      editorPane.setOpaque(false);
      editorPane.setEditable(false);
      scrollPane = new JScrollPane(editorPane);
      scrollPane.setBorder(null);
      scrollPane.setOpaque(false);
      scrollPane.getViewport().setOpaque(false);
      editorPane.setMargin(null);
    } catch (Exception e2) {
      e2.printStackTrace();
    }

    return scrollPane;
  }

  private JPanel buildMainLoginPanel() {
	    JPanel p = new TransparentPanel(null);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(343,67,173,36);
	    p.add(logo);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	   

	    p.add(buildLoginPanel());
	    
	    jpAuth.setBounds(79,167,279,234);
	    p.add(jpAuth);

	    JPanel jpNews = new JPanel();
	    jpNews.setBackground(new Color(0, 0, 0, 129));
	    jpNews.setPreferredSize(new Dimension(377, 234));

	    JPanel jpNews2 = new TransparentPanel();
	    jpNews2.setLayout(new GridBagLayout());
	    jpNews2.setPreferredSize(new Dimension(377, 234));
	    jpNews2.add(new NewsLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));
	    getUpdateNews().setPreferredSize(new Dimension(353, 175));
	    jpNews2.add(getUpdateNews(), new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
	    
	    jpNews2.setBounds(398,167,377,234);
	    p.add(jpNews2);
	    
	    jpNews.setBounds(398,167,377,234);
	    p.add(jpNews);

	    JPanel monitoring = buildMonitor();
	    monitoring.setBounds(79, 430, 500, 30);
	    p.add(monitoring);
	    p.add(logo);
	    return p;
	  }
	  private JPanel buildLoginPanel() {
	    TransparentPanel panel = new TransparentPanel();
	    panel.setLayout(new GridBagLayout());
	    panel.setOpaque(false);
	    
	    

	    userNameL.setForeground(LIGHTBLUEAUTH);
	    passwordL.setForeground(LIGHTBLUEAUTH);

	    panel.add(new authLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
	    panel.add(userNameL, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.WEST , GridBagConstraints.BOTH, new Insets(0, 20, 6, 0), 0, 0));
	    panel.add(userName, new GridBagConstraints(1, 1, 1, 1, 1, 1,GridBagConstraints.EAST , GridBagConstraints.BOTH, new Insets(0, 0, 6, 20), 0, 0));
	    
	    panel.add(passwordL, new GridBagConstraints(0, 2, 1, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 20, 0, 0), 0, 0));
	    panel.add(password, new GridBagConstraints(1, 2, 1, 1, 1, 1,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 20), 0, 0));
	    
	    panel.add(rememberBox, new GridBagConstraints(1, 3, 2, 1, 1, 1,GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 20), 0, 0));
	    panel.add(launchButton, new GridBagConstraints(0, 4, 2, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 20, 6, 20), 0, 0));
	    
	    panel.add(registerButton, new GridBagConstraints(0, 5, 2, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 20, 35, 20), 0, 0));
	    
	    //panel.add(forgetPass, new GridBagConstraints(0, 5, 2, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 20, 45, 20), 0, 0));
	    
	    panel.setBounds(79,167,279,234);

	    return panel;
	  }
	  
	  private JPanel buildMainAuth2Panel() {
		    JPanel p = new TransparentPanel(null);

		    JPanel logo = new LogoPanel();
		    logo.setBounds(343,67,173,36);
		    p.add(logo);
		    
		    JPanel jpAuth = new JPanel();
		    jpAuth.setLayout(new GridBagLayout());
		    jpAuth.setBackground(new Color(0, 0, 0, 129));
		   

		    p.add(buildAuth2Panel());
		    
		    jpAuth.setBounds(79,167,279,234);
		    p.add(jpAuth);

		    JPanel jpNews = new JPanel();
		    jpNews.setBackground(new Color(0, 0, 0, 129));
		    jpNews.setPreferredSize(new Dimension(377, 234));

		    JPanel jpNews2 = new TransparentPanel();
		    jpNews2.setLayout(new GridBagLayout());
		    jpNews2.setPreferredSize(new Dimension(377, 234));
		    jpNews2.add(new NewsLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));
		    getUpdateNews().setPreferredSize(new Dimension(353, 175));
		    jpNews2.add(getUpdateNews(), new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		    
		    jpNews2.setBounds(398,167,377,234);
		    p.add(jpNews2);
		    
		    jpNews.setBounds(398,167,377,234);
		    p.add(jpNews);

		    JPanel monitoring = buildMonitor();
		    monitoring.setBounds(79, 430, 500, 30);
		    p.add(monitoring);
		    p.add(logo);
		    return p;
		  }
		  private JPanel buildAuth2Panel() {
		    TransparentPanel panel = new TransparentPanel();
		    panel.setLayout(new GridBagLayout());
		    panel.setOpaque(false);
		    
		 
		    
		    JLabel auth = new TransparentLabel("АВТОРИЗАЦІЯ...");
		    auth.setForeground(LIGHTBLUEAUTH);
		    auth.setFont(new Font("Arial", Font.BOLD, 13));

		    panel.add(new authLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
		    panel.add(auth, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 50, 0), 0, 0));

		    panel.setBounds(79,167,279,234);

		    return panel;
		  }

  


		  private TransparentLabel getUpdateLink() {
			    TransparentLabel accountLink = new TransparentLabel("Завантажити новий лаунчер") {
			      private static final long serialVersionUID = 0L;
			      public void paint(Graphics g) { super.paint(g);
			        int x = 0;
			        int y = 0;
			        FontMetrics fm = g.getFontMetrics();
			        int width = fm.stringWidth(getText());
			        int height = fm.getHeight();
			        if (getAlignmentX() == 2.0F) x = 0;
			        else if (getAlignmentX() == 0.0F) x = getBounds().width / 2 - width / 2;
			        else if (getAlignmentX() == 4.0F) x = getBounds().width - width;
			        y = getBounds().height / 2 + height / 2 - 1;
			        g.drawLine(x + 2, y, x + width - 2, y); }
			      public void update(Graphics g)
			      {
			        paint(g);
			      }
			    };
			    accountLink.setCursor(Cursor.getPredefinedCursor(12));
			    accountLink.addMouseListener(new MouseAdapter() {
			      public void mousePressed(MouseEvent arg0) {
			        try {
			          Util.openLink(new URL(setting.downLaunchLink).toURI());
			        } catch (Exception e) {
			          e.printStackTrace();
			        }
			      }
			    });
			    accountLink.setForeground(LIGHTBLUEAUTH);
			    return accountLink;
			  }

  private JPanel buildErrorPanel() {

	   JPanel p = new TransparentPanel(null);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(343,67,173,36);
	    p.add(logo);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	    //
	    
	    TransparentPanel errorPanels = new TransparentPanel();
	    errorPanels.setLayout(new GridBagLayout());
	    errorPanels.setOpaque(false);
	    
	    

	    userNameL.setForeground(LIGHTBLUEAUTH);
	    passwordL.setForeground(LIGHTBLUEAUTH);

	    JLabel errorLabel = new JLabel("Помилка");
	    
	    errorPanels.add(new authLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
	    errorPanels.add(errorLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.CENTER , GridBagConstraints.BOTH, new Insets(0, 20, 6, 0), 0, 0));

	    errorPanels.setBounds(79,167,279,234);
	   
	    p.add(errorPanels);
	    
	    jpAuth.setBounds(79,167,279,234);
	    p.add(jpAuth);

	    JPanel jpNews = new JPanel();
	    jpNews.setBackground(new Color(0, 0, 0, 129));
	    jpNews.setPreferredSize(new Dimension(377, 234));

	    JPanel jpNews2 = new TransparentPanel();
	    jpNews2.setLayout(new GridBagLayout());
	    jpNews2.setPreferredSize(new Dimension(377, 234));
	    jpNews2.add(new NewsLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));
	    getUpdateNews().setPreferredSize(new Dimension(353, 175));
	    jpNews2.add(getUpdateNews(), new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
	    
	    jpNews2.setBounds(398,167,377,234);
	    p.add(jpNews2);
	    
	    jpNews.setBounds(398,167,377,234);
	    p.add(jpNews);
	    
	    p.add(logo);
	  
    return p;
  }
  
  public void ShowErrorsBuild (String error) {
	  buildErrorPanel();
	  
  }

  private Component center(Component c) {
    TransparentPanel tp = new TransparentPanel(new GridBagLayout());
    tp.add(c);
    return tp;
  }


  
  public JPanel buildOfflinePanel(String error)
  {
	   JPanel p = new TransparentPanel(null);
	   
	   JLabel errorLabels = new JLabel(error);
	   errorLabels.setFont(fontError);
	   errorLabels.setForeground(LIGHTREDERROR);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(343,67,173,36);
	    p.add(logo);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	   
	    TransparentPanel errorPanels = new TransparentPanel(new GridBagLayout());
	    if (error.trim().equals("228")){	
	    	errorLabels.setText("Необхідно оновити лаунчер");
	    	errorPanels.add(new errorLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
		    errorPanels.add(errorLabels, new GridBagConstraints(0, 1, 2, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 0, 10), 0, 0));
		    errorPanels.add(getUpdateLink(), new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 70, 10), 0, 0));
	    }else{
	    	errorPanels.add(new errorLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
		    errorPanels.add(errorLabels, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
		    errorPanels.add(retryButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20,6, 20), 0, 20));
		    errorPanels.add(offlineButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 20));
        }
	    

	    errorPanels.setBounds(79,167,279,234);

	    p.add(errorPanels);
	    

	    
	    jpAuth.setBounds(79,167,279,234);
	    p.add(jpAuth);
	    
	    

	    JPanel jpNews = new JPanel();
	    jpNews.setBackground(new Color(0, 0, 0, 129));
	    jpNews.setPreferredSize(new Dimension(377, 234));

	    JPanel jpNews2 = new TransparentPanel();
	    jpNews2.setLayout(new GridBagLayout());
	    jpNews2.setPreferredSize(new Dimension(377, 234));
	    jpNews2.add(new NewsLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));
	    getUpdateNews().setPreferredSize(new Dimension(353, 175));
	    jpNews2.add(getUpdateNews(), new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
	    
	    jpNews2.setBounds(398,167,377,234);
	    p.add(jpNews2);
	    
	    jpNews.setBounds(398,167,377,234);
	    p.add(jpNews);
	    
	    p.add(logo);
	    return p;
  }

  public void setError(String errorMessage) {

	removeAll();
    JPanel buildOP = buildOfflinePanel(errorMessage);
    buildOP.setBounds(0, 0, 854, 480);
    add(buildOP);
    validate();
    repaint();
  }

  public void loginOk() {
    writeUsername();
  }

  public void checkAutologin() {
    if (password.getPassword().length > 0)
      launcherFrame.login(userName.getText(), new String(password.getPassword()));
  }
  

  public JPanel profile(final String info)
  {
	  JLabel forumLink = getForumLink();
	  JLabel uslugiLink = getUslugiLink();
	  
      String[] values = info.split(":");
      
      Font font22 = new Font("Arial", Font.BOLD, 15);
      forumLink.setFont(font22);
      uslugiLink.setFont(font22);

      final Font fontProfile = new Font("Arial", Font.BOLD, 12);
      
      JButton enterGame = new RedButton("Увійти в гру");
      
      JButton enterSkins = new TransparentButton("Система скінів");
      
      
String usernameProfile = values[2];
String balanceProfile = values[4];
JLabel usernameProfileLabel = new JLabel("Вітаю, " + usernameProfile);
JLabel balanceProfileLabel = new JLabel("Ваш баланс: " + balanceProfile + " мун");
usernameProfileLabel.setForeground(Color.WHITE);
balanceProfileLabel.setForeground(Color.WHITE);
usernameProfileLabel.setFont(fontProfile);
balanceProfileLabel.setFont(fontProfile);
	  
	   JPanel p = new TransparentPanel(null);
	   
	   forumLink.setBounds(648,40,50,36);
	    p.add(forumLink);
	    uslugiLink.setBounds(724,40,50,36);
		    p.add(uslugiLink);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(398,40,173,36);
	    p.add(logo);
	    
	    JPanel viewSkin = new ViewSkins(usernameProfile);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	   
	    TransparentPanel errorPanels = new TransparentPanel(new GridBagLayout());

	    
	    errorPanels.add(new Logotype("panel.png"), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(8, 0, 8, 0), 0, 0));
	    errorPanels.add(usernameProfileLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
	    
	    errorPanels.add(viewSkin, new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(8, 0, 8, 0), 0, 0));
	    
	    errorPanels.add(balanceProfileLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        
	    errorPanels.add(enterSkins, new GridBagConstraints(0, 4, 1, 1, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.BOTH, new Insets(0, 20, 6, 20), 0, 0));
	    errorPanels.add(optionsButton, new GridBagConstraints(0, 5, 1, 1, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.BOTH, new Insets(0, 20, 6, 20), 0, 0));
	    
	    optionsButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	      	  buildSettingSystem(info);
	        }
	      });
	    
	    errorPanels.add(enterGame, new GridBagConstraints(0, 6, 2, 1, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.BOTH, new Insets(0, 20, 20, 20), 0, 0));
	    errorPanels.setBounds(79,40,279,400);

	    p.add(errorPanels);
	    

	    
	    jpAuth.setBounds(79,40,279,400);
	    p.add(jpAuth);
	    
	    

	    JPanel jpNews = new JPanel();
	    jpNews.setBackground(new Color(0, 0, 0, 129));
	    jpNews.setPreferredSize(new Dimension(696, 234));

	    JPanel jpNews2 = new TransparentPanel();
	    jpNews2.setLayout(new GridBagLayout());
	    jpNews2.setPreferredSize(new Dimension(353, 234));
	    jpNews2.add(new NewsLogo(), new GridBagConstraints(0, 0, 1, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));
	    getUpdateNews().setPreferredSize(new Dimension(353, 175));
	    jpNews2.add(getUpdateNews(), new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.SOUTH , GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
	    
	    jpNews2.setBounds(398,206,377,234);
	    p.add(jpNews2);
	    
	    jpNews.setBounds(398,206,377,234);
	    p.add(jpNews);
	    
	    p.add(logo);
	    
	    JPanel monitoring = buildMonitor();
	    monitoring.setBounds(79, 447, 500, 30);
	    p.add(monitoring);
	    
	    final String results22 = info;
	    
	    ActionListener StartMc = new ActionListener() {
	        public void actionPerformed(ActionEvent arg0) {
	        	launcherFrame.Startminecraft(results22);
	        }
	      };
	      enterGame.addActionListener(StartMc);
	      
		    ActionListener buildSkin = new ActionListener() {
		        public void actionPerformed(ActionEvent arg0) {
		        	
		        	buildSkinSystem(info);
		        }
		      };
		      
		      enterSkins.addActionListener(buildSkin);
	    
	    return p;
  }
  
  private JPanel buildMonitor(){

	 int onofoff;
	  
	 JPanel p = new JPanel(new GridBagLayout());
	 p.setOpaque(false);
	 
	 
	 Color onColor = Color.decode("#6ad449");
	 Color offColor = Color.decode("#d64040");
	 Font fontMonitor = new Font("Arial", Font.BOLD, 12);
	 
	 JLabel text = new JLabel("");
 	URL localURL;
	try {
		localURL = new URL(setting.monitorLink);
                String result=Util.excutePost(setting.monitorLink, "");
		 text.setForeground(onColor);
		 text.setFont(fontMonitor);
		      text.setText(result);		 
	      String[] values = result.split(":");
	      String onlineUser = values[1];
	      String allUser = values[2];
		 
		 if (result.contains(":")){
 			 	text.setText(setting.mineFolderAbsolute+" Сервер ввімкнено, грають " + onlineUser + " з " + allUser + " можливих");
			 	onofoff = 1;
		 } else if (result.equals("OFF")){
			 text.setText("Сервер вимкнено");
			 text.setForeground(offColor);
			 onofoff = 2;
		 }else{
			 text.setText("Невідома помилка");
			 text.setForeground(offColor);
			 onofoff = 2;
		 }

		 
		} catch (Exception e) {
		      e.printStackTrace();
                      text.setText("Помилка при підключенні до серверу");
		      text.setForeground(offColor);
		      onofoff = 2;
		}


	JPanel iconon = new Logotype("/image/on.png");
	JPanel iconoff = new Logotype("/image/off.png");
	
	if (onofoff == 1){
		p.add(iconon, new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.WEST , GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}else{
		p.add(iconoff, new GridBagConstraints(0, 1, 0, 0, 1, 1,GridBagConstraints.WEST , GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
	}
	 
p.add(text, new GridBagConstraints(0, 2, 0, 0, 1, 1,GridBagConstraints.WEST , GridBagConstraints.NONE, new Insets(0, 20, 3, 0), 0, 0));
	    

	return p;
  }
  
 private void buildSkinSystem(String info){
	 
	 
		LauncherFrame.panelBg.removeAll();
	    JPanel p = panelSkinSystem(userName.getText(), new String(password.getPassword()),info);
	    p.setBounds(0, 0, 854, 480);
	    LauncherFrame.panelBg.add(p);
	    LauncherFrame.panelBg.validate();
	    LauncherFrame.panelBg.repaint();
  }
  
  private JPanel panelSkinSystem(final String login, final String pass, final String info){
	  
	  
      final JButton againButton = new RedButton("Попробовать еще раз");
	    ActionListener buildSkin = new ActionListener() {
	        public void actionPerformed(ActionEvent arg0) {
	        	
	        	buildSkinSystem(info);
	        }
	      };
	      
	      againButton.addActionListener(buildSkin);
	  
	  Color DoneColor = Color.decode("#6ad449");
	  Font font = new Font("Arial", Font.BOLD, 13);
	  final JLabel doneLabel = new TransparentLabel("Скин успешно загружен!");
	  doneLabel.setForeground(DoneColor);
	  doneLabel.setFont(font);

	  Font errorFont = new Font("Arial", Font.BOLD, 12);
	  final JLabel sizeErrorLabel = new TransparentLabel("Изображение должны быть размером 64x32");
	  final JLabel chiterErrorLabel = new TransparentLabel("неизвестная ошибка(если вы понимаете о чем я ;)");
	  sizeErrorLabel.setForeground(LIGHTREDERROR);
	  sizeErrorLabel.setFont(errorFont);
	  chiterErrorLabel.setForeground(LIGHTREDERROR);
	  
	  final JButton loadSkin = new TransparentButton("Загрузить скин");
	  final JButton exitButton = new TransparentButton("Перейти в профиль");

	  exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	launcherFrame.BuildProfilePanelForm(info);
            }
        });
	  
	  JButton openFile = new TransparentButton("Открыть файл");
	  JButton openSkinUrl = new RedButton("Каталог скинов");
	  openSkinUrl.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
      	    setCursor(Cursor.getPredefinedCursor(12));
    	          try {
					Util.openLink(new URL(setting.catalogSkinsUrl).toURI());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
          }
      });

	    JPanel p = new TransparentPanel(null);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(343,67,173,36);
	    p.add(logo);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	   
	    final TransparentPanel errorPanels = new TransparentPanel(new GridBagLayout());
	    errorPanels.add(new Logotype("/image/loadSkinLogo.png"), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 6, 0), 0, 0));
	    errorPanels.add(openSkinUrl, new GridBagConstraints(0, 1, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 6, 20), 0, 9));
	    errorPanels.add(openFile, new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 6, 20), 0, 9));
	    errorPanels.add(loadSkin, new GridBagConstraints(0, 3, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 6, 20), 0, 9));
	    errorPanels.add(exitButton, new GridBagConstraints(0, 4, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 9));
	    
	    errorPanels.setBounds(277,167,300,234);

		  openFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileopen = new JFileChooser("png");
	fileopen.setFileFilter(new ExtFileFilter("png", "png Изображения"));
					int ret = fileopen.showDialog(null, "Открыть файл");				
					if (ret == JFileChooser.APPROVE_OPTION) {
						final File file23 = fileopen.getSelectedFile();
						  loadSkin.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String result = setSkin(file23, login, pass);
									if (result.trim().equals("done")){
										errorPanels.removeAll();
									    errorPanels.add(new Logotype("/image/loadSkinLogo.png"), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
									    errorPanels.add(doneLabel, new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(14, 20, 15, 20), 0, 0));
									    errorPanels.add(exitButton, new GridBagConstraints(0, 3, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(14, 20, 20, 20), 0, 30));
									    errorPanels.repaint();
									    errorPanels.validate();
									    
									} else if (result.trim().equals("sizeError")){
										errorPanels.removeAll();
									    errorPanels.add(new errorLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 6, 0), 0, 0));
									    errorPanels.add(sizeErrorLabel, new GridBagConstraints(0, 1, 2, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 6, 10), 0, 0));
									    errorPanels.add(againButton, new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 6, 20), 0, 25));
									    errorPanels.add(exitButton, new GridBagConstraints(0, 3, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 25));
									    errorPanels.repaint();
									    errorPanels.validate();
									}else if (result.trim().equals("badLogin")){
										errorPanels.removeAll();
									    errorPanels.add(new errorLogo(), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 6, 0), 0, 0));
									    errorPanels.add(chiterErrorLabel, new GridBagConstraints(0, 1, 2, 1, 1, 1,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 6, 10), 0, 0));
									    errorPanels.add(againButton, new GridBagConstraints(0, 2, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 6, 20), 0, 25));
									    errorPanels.add(exitButton, new GridBagConstraints(0, 3, 2, 1, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 20), 0, 25));
									    errorPanels.repaint();
									    errorPanels.validate();
									}
									
								}
							});
					}
				}
			});
	    
	    p.add(errorPanels);

	    jpAuth.setBounds(277,167,300,234);
	    p.add(jpAuth);

	    p.add(logo);
	    return p;
  }

  private void buildSettingSystem(String info){
		LauncherFrame.panelBg.removeAll();
	    JPanel p = panelSettingSystem(info);
	    p.setBounds(0, 0, 854, 480);
	    LauncherFrame.panelBg.add(p);
	    LauncherFrame.panelBg.validate();
	    LauncherFrame.panelBg.repaint();
  }
  
  private JPanel panelSettingSystem(final String info){

	    final TransparentCheckbox dlFl = new TransparentCheckbox ("Обновить клиент!");

	    dlFl.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	            boolean forceUpdateInfo;
	            forceUpdateInfo = dlFl.isSelected();
	        	if (forceUpdateInfo == true)
	            	GameUpdater.forceUpdate = true;
	            else
	            	GameUpdater.forceUpdate = false;
	        }
	      });
	    
	    


		String[] items = {
					"Полную версию","Легкую версию"
				};
		final JComboBox selectGame = new JComboBox(items);
		selectGame.setEditable(false);
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                item = (String)selectGame.getSelectedItem();
                selectGame.setSelectedItem(item);
			}
		};

		
		selectGame.addActionListener(actionListener);
		
		  if (launcherFrame.clientId.trim().equals("2")){
			  selectGame.setSelectedItem("Легкую версию");
		  }else{
			  selectGame.setSelectedItem("Полную версию");
		  }

			
		JButton save = new RedButton("Сохранить и выйти");
		

	    JPanel p = new TransparentPanel(null);

	    JPanel logo = new LogoPanel();
	    logo.setBounds(343,67,173,36);
	    p.add(logo);
	    final JLabel memorySet = new JLabel("Обновление:");
	    memorySet.setForeground(LIGHTBLUEAUTH);
	    final JLabel selectGameLabel = new JLabel("Скачивать:");
	    selectGameLabel.setForeground(LIGHTBLUEAUTH);
	    
	    JPanel jpAuth = new JPanel();
	    jpAuth.setLayout(new GridBagLayout());
	    jpAuth.setBackground(new Color(0, 0, 0, 129));
	   
	    TransparentPanel errorPanels = new TransparentPanel(new GridBagLayout());
	    errorPanels.add(new Logotype("/image/setting.png"), new GridBagConstraints(0, 0, 2, 1, 1, 1,GridBagConstraints.NORTH , GridBagConstraints.NONE, new Insets(14, 0, 15, 0), 0, 0));
	    errorPanels.add(memorySet, new GridBagConstraints(0, 1, 1, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(6, 20, 6, 20), 0, 0));
	    errorPanels.add(dlFl, new GridBagConstraints(1, 1, 1, 1, 1, 1,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(6, 20, 6, 20), 0, 0));
	    errorPanels.add(selectGameLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(6, 20, 6, 20), 0, 0));
	    errorPanels.add(selectGame, new GridBagConstraints(1, 2, 1, 1, 1, 1,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(6, 20, 6, 20), 0, 0));
	    errorPanels.add(save, new GridBagConstraints(0, 3, 2, 1, 1, 1,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(6, 20, 20, 20), 0, 0));
        errorPanels.setBounds(277,167,300,234);

	    
	    save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                	if (item.trim().equals("Полную версию")){
                		launcherFrame.clientId = "1";
                		launcherFrame.client=setting.client1;
                		
                		try {
							LauncherFrame.saveSetting("1");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
 
                	}else if(item.trim().equals("Легкую версию")){
                		launcherFrame.clientId = "2";
                		launcherFrame.client=setting.client2;
                		
                		
                		try {
							LauncherFrame.saveSetting("2");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
                	}
           launcherFrame.BuildProfilePanelForm(info);
            }
        });

	    		       
	    p.add(errorPanels);

	    jpAuth.setBounds(277,167,300,234);
	    p.add(jpAuth);

	    p.add(logo);
	    return p;
  }
  
  private TransparentLabel getForumLink() {
	    TransparentLabel accountLink = new TransparentLabel("Форум") {
	      private static final long serialVersionUID = 0L;
	      public void paint(Graphics g) { super.paint(g);
	        int x = 0;
	        int y = 0;
	        FontMetrics fm = g.getFontMetrics();
	        int width = fm.stringWidth(getText());
	        int height = fm.getHeight();
	        if (getAlignmentX() == 2.0F) x = 0;
	        else if (getAlignmentX() == 0.0F) x = getBounds().width / 2 - width / 2;
	        else if (getAlignmentX() == 4.0F) x = getBounds().width - width;
	        y = getBounds().height / 2 + height / 2 - 1;
	        }
	      public void update(Graphics g)
	      {
	        paint(g);
	      }
	    };
	    accountLink.setCursor(Cursor.getPredefinedCursor(12));
	    accountLink.addMouseListener(new MouseAdapter() {
	      public void mousePressed(MouseEvent arg0) {
	        try {
	          Util.openLink(new URL(setting.forumLink).toURI());
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    });
	    accountLink.setForeground(Color.decode("#36535c"));
	    return accountLink;
	  }
  
  private TransparentLabel getUslugiLink() {
	  
	    TransparentLabel accountLink = new TransparentLabel("Услуги") {
	    	
	      private static final long serialVersionUID = 0L;
	      public void paint(Graphics g) { super.paint(g);
	        int x = 0;
	        int y = 0;
	        FontMetrics fm = g.getFontMetrics();
	        int width = fm.stringWidth(getText());
	        int height = fm.getHeight();
	        if (getAlignmentX() == 2.0F) x = 0;
	        else if (getAlignmentX() == 0.0F) x = getBounds().width / 2 - width / 2;
	        else if (getAlignmentX() == 4.0F) x = getBounds().width - width;
	        y = getBounds().height / 2 + height / 2 - 1;
	         }
	      public void update(Graphics g)
	      {
	        paint(g);
	      }
	    };
	    
	    accountLink.setCursor(Cursor.getPredefinedCursor(12));
	    accountLink.addMouseListener(new MouseAdapter() {
	      public void mousePressed(MouseEvent arg0) {
	        try {
	          Util.openLink(new URL(setting.uslugiLink).toURI());
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    });
	    
	    accountLink.setForeground(Color.decode("#36535c"));
	    return accountLink;
	  }
 
  
  public static byte[] getBytesFromFile(File file) throws IOException {
      InputStream is = new FileInputStream(file);
  
      // Get the size of the file
      long length = file.length();
  
      if (length > Integer.MAX_VALUE) {
          // File is too large
      }
  
      // Create the byte array to hold the data
      byte[] bytes = new byte[(int)length];
  
      // Read in the bytes
      int offset = 0;
      int numRead = 0;
      while (offset < bytes.length
             && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
          offset += numRead;
      }
  
      // Ensure all the bytes have been read in
      if (offset < bytes.length) {
          throw new IOException("Could not completely read file "+file.getName());
      }
  
      // Close the input stream and return bytes
      is.close();
      return bytes;
  }
  
  public String copyfile(File file) {
	  try {
		  return (Base64.encodeBase64String(getBytesFromFile(file)));
	} catch (IOException e) {
		e.printStackTrace();
	}
	  return "error";
  }
  
  public String setSkin(File file, String login, String pass){
		String parameters = "user=" +login + "&pass=" + pass + "&code=" + copyfile(file);
		return Util.excutePost(setting.loadLinkUrlSkins, parameters);
  }




}
