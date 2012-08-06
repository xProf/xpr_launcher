package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RegisterPanel extends JDialog
{
  private static final long serialVersionUID = 1L;
  private TransparentLabel errorReasonLabel = new TransparentLabel("", 0);
  private TransparentLabel doneLabel = new TransparentLabel("Регистрация успешно завершена", 0);
  
  private JPanel BackgroundBg = new BackgroundRegisterPanel();
  static final Color LABELCOLORREG = new Color(255, 255, 255);
  static final Color LABELCOLORDONE = new Color(45, 118, 214);
  static final Color LABELCOLORERROR = new Color(181, 30, 30);
  private final Font LabelReason = new Font("Tahoma", Font.BOLD, 16);
  

  
  public RegisterPanel(Frame parent)
  {
    super(parent);
    setTitle("Регистрация");
    setSize(400, 380);
    setResizable(false);
    setModal(true);
    setLocationRelativeTo(parent);
    
    BackgroundBg.setLayout(new BorderLayout());
    
    


    add(BackgroundBg);
    BackgroundBg.setBorder(new EmptyBorder(24, 24, 24, 24));
    RegisterPanelForm ();

  }
  
  public void RegisterPanelForm (){
	  BackgroundBg.removeAll();
	  BackgroundBg.add(new RegisterLogo(), "North");
	    final Font fontLabelRegister = new Font("Tahoma", Font.BOLD, 13);
	    final TextField userField = new TextField();
	    JLabel userLabel = new JLabel("Ник: ", 2);
	    userLabel.setForeground(LABELCOLORREG);
	    userLabel.setFont(fontLabelRegister);
	    final TextField PasswordField = new TextField();
	    JLabel PasswordLabel = new JLabel("Пароль: ", 2);
	    PasswordLabel.setForeground(LABELCOLORREG);
	    PasswordLabel.setFont(fontLabelRegister);
	    final TextField Password2Field = new TextField();
	    JLabel Password2Label = new JLabel("Повторите пароль: ", 2);
	    Password2Label.setForeground(LABELCOLORREG);
	    Password2Label.setFont(fontLabelRegister);
	    final TextField MailField = new TextField();
	    MailField.setColumns(30);
	    final JLabel MailLabel = new JLabel("E-mail: ", 2);
	    MailLabel.setForeground(LABELCOLORREG);
	    MailLabel.setFont(fontLabelRegister);
	    

	    GridLayout FieldText = new GridLayout(0, 1);
	    FieldText.setVgap(10);
	    FieldText.setHgap(10);
	    GridLayout FieldText2 = new GridLayout(0, 1);
	    FieldText2.setVgap(10);
	    FieldText2.setHgap(10);
	    GridLayout EnterButton = new GridLayout(0, 1);

	    
	    TransparentPanel titles = new TransparentPanel(FieldText);
	    TransparentPanel values = new TransparentPanel(FieldText2);
	    TransparentPanel ButtonOK = new TransparentPanel(EnterButton);
	    
	    
	    values.add(userField);
	    titles.add(userLabel);
	    values.add(PasswordField);
	    titles.add(PasswordLabel);
	    values.add(Password2Field);
	    titles.add(Password2Label);
	    values.add(MailField);
	    titles.add(MailLabel);
	    
	    BackgroundBg.add(values, "East");
	    BackgroundBg.add(titles, "West");
	    
	    
	    JButton doneButton = new TransparentButton("Зарегестрироваться");
	    doneButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
		  	    String user = userField.getText();
			    String Pass = PasswordField.getText();
			    String Pass2 = Password2Field.getText();
			    String eMail = MailField.getText();
	    	  
	    	  register(user, Pass, Pass2, eMail);
	          
	          
	      
	      }
	    });

	    
	    doneButton.setBorder(new EmptyBorder(16, 16, 16, 16));
	    ButtonOK.setBorder(new EmptyBorder(16, 0, 0, 0));
	    ButtonOK.add(doneButton);
	    BackgroundBg.add(ButtonOK, "South");
	    
	    BackgroundBg.repaint();
	    BackgroundBg.revalidate();
  }

public void register(String name, String pass, String pass2, String mail) {
  	  
	  try {
	    URL localURL = new URL(setting.registerLink + URLEncoder.encode(name, "UTF-8") + "&password=" + URLEncoder.encode(pass, "UTF-8") + "&password2=" + URLEncoder.encode(pass2, "UTF-8") + "&email=" + URLEncoder.encode(mail, "UTF-8"));
		BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURL.openStream()));
  	    String resultSite = localBufferedReader.readLine(); 
  	    System.out.println(resultSite);
  	    if (resultSite.equals("done")){
  	    	doneRegister();
  	    }else{
  	    	if (resultSite.equals("errorMail")){
  	  	    	failRegister ("eMail адрес введен некорректно");
  	  	    } else if (resultSite.equals("errorMail2")){
  	  	    	failRegister ("eMail адрес содержит запрещенные символы");
  	  	    }else if (resultSite.equals("passErrorSymbol")){
  	  	    	failRegister ("Пароль содержит запрещенные символы");
  	  	    }else if (resultSite.equals("errorLoginSymbol")){
  	  	    	failRegister ("Логин содержит запрещенные символы");
  	  	    }else if (resultSite.equals("errorSmallLogin")){
  	  	    	failRegister ("Логин должен содержать 2-20 символов");
  	  	    }else if (resultSite.equals("errorPassSmall")){
  	  	    	failRegister ("Пароль должен содержать 6-20 символов");
  	  	    }else if (resultSite.equals("emailErrorPovtor")){
  	  	    	failRegister ("eMail уже зарегестрирован");
  	  	    }else if (resultSite.equals("loginErrorPovtor")){
  	  	    	failRegister ("Пользователем с таким логином уже зарегистрирован");
  	  	    }else if (resultSite.equals("errorMail")){
  	  	    	failRegister ("Неправильный адрес eMail");
  	  	    }else if (resultSite.equals("errorField")){
  	  	    	failRegister ("Заполнены не все поля");
  	  	    }else {
	  	    	failRegister ("Неизвестная ошибка");
	  	    }
  	    }
  	    
        
        
  	    
  	    
  	    return;

		} catch (Exception e) {
			e.printStackTrace();
		}
}
  
  public void failRegister (String errorReason){

	  BackgroundBg.removeAll();
	  BackgroundBg.add(new RegisterLogo(), "North");
	  errorReasonLabel.setText(errorReason);
	  errorReasonLabel.setForeground(LABELCOLORERROR);
	  errorReasonLabel.setFont(LabelReason);
	  BackgroundBg.add(errorReasonLabel,"Center");
	  
	  
    GridLayout EnterButton = new GridLayout(0, 1);
    EnterButton.setVgap(2);
    
	TransparentPanel ButtonPanel = new TransparentPanel(EnterButton);
	  
	    JButton doneButton = new TransparentButton("Попробовать еще раз");
	    doneButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  RegisterPanelForm ();
	      }
	    });
	    JButton closeButton = new TransparentButton("Закрыть");
	    closeButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  setVisible(false);
	      }
	    });
	    doneButton.setBorder(new EmptyBorder(6, 0, 6, 0));
	    closeButton.setBorder(new EmptyBorder(6, 0, 6, 0));
	    ButtonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
	    ButtonPanel.add(closeButton);
	    ButtonPanel.add(doneButton);
	  BackgroundBg.add(ButtonPanel, "South"); 
	    
	    BackgroundBg.repaint();
	    BackgroundBg.revalidate();
	    
	    
  }
  
  public void doneRegister (){

	  BackgroundBg.removeAll();
	  BackgroundBg.add(new RegisterLogo(), "North");
	  doneLabel.setForeground(LABELCOLORDONE);
	  doneLabel.setFont(LabelReason);
	  BackgroundBg.add(doneLabel,"Center");
	  
	  
    GridLayout EnterButton = new GridLayout(0, 1);
    EnterButton.setVgap(2);
    
	TransparentPanel ButtonPanel = new TransparentPanel(EnterButton);
	 
	    JButton closeButton = new TransparentButton("Закрыть");
	    closeButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  setVisible(false);
	      }
	    });
	    closeButton.setBorder(new EmptyBorder(16, 0, 16, 0));
	    ButtonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
	    ButtonPanel.add(closeButton);
	  BackgroundBg.add(ButtonPanel, "South"); 
	    
	    BackgroundBg.repaint();
	    BackgroundBg.revalidate();
	    
	    
  }
  

}