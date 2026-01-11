package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.network.Client;
import pl.art.lach.mateusz.javaopenchess.server.Server;
import pl.art.lach.mateusz.javaopenchess.utils.MD5;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
















public class DrawNetworkSettings
  extends JPanel
  implements ActionListener
{
  private static final Logger LOG = Logger.getLogger(DrawNetworkSettings.class);
  
  private JDialog parent;
  
  private GridBagLayout gbl;
  
  private GridBagConstraints gbc;
  
  private ButtonGroup serverORclient;
  private JRadioButton radioServer;
  private JRadioButton radioClient;
  private JLabel labelNick;
  private JLabel labelPassword;
  private JLabel labelGameID;
  private JLabel labelOptions;
  private JPanel panelOptions;
  private JTextField textNick;
  private JPasswordField textPassword;
  private JTextField textGameID;
  private JButton buttonStart;
  private ServOptionsPanel servOptions;
  private ClientOptionsPanel clientOptions;
  
  public DrawNetworkSettings(JDialog parent)
  {
    this.parent = parent;
    
    radioServer = new JRadioButton(Settings.lang("create_server"), true);
    radioClient = new JRadioButton(Settings.lang("connect_2_server"), false);
    serverORclient = new ButtonGroup();
    serverORclient.add(radioServer);
    serverORclient.add(radioClient);
    radioServer.addActionListener(this);
    radioClient.addActionListener(this);
    
    labelNick = new JLabel(Settings.lang("nickname"));
    labelPassword = new JLabel(Settings.lang("password"));
    labelGameID = new JLabel(Settings.lang("game_id"));
    labelOptions = new JLabel(Settings.lang("server_options"));
    
    textNick = new JTextField();
    textPassword = new JPasswordField();
    textGameID = new JTextField();
    
    panelOptions = new JPanel();
    clientOptions = new ClientOptionsPanel();
    servOptions = new ServOptionsPanel();
    
    buttonStart = new JButton(Settings.lang("start"));
    buttonStart.addActionListener(this);
    

    gbl = new GridBagLayout();
    gbc = new GridBagConstraints();
    gbc.fill = 1;
    setLayout(gbl);
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbl.setConstraints(radioServer, gbc);
    add(radioServer);
    
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbl.setConstraints(radioClient, gbc);
    add(radioClient);
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbl.setConstraints(labelGameID, gbc);
    add(labelGameID);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbl.setConstraints(textGameID, gbc);
    add(textGameID);
    
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbl.setConstraints(labelNick, gbc);
    add(labelNick);
    
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbl.setConstraints(textNick, gbc);
    add(textNick);
    
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbl.setConstraints(labelPassword, gbc);
    add(labelPassword);
    
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;
    gbl.setConstraints(textPassword, gbc);
    add(textPassword);
    
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbl.setConstraints(labelOptions, gbc);
    add(labelOptions);
    
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbl.setConstraints(panelOptions, gbc);
    add(panelOptions);
    
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    gbl.setConstraints(buttonStart, gbc);
    add(buttonStart);
    
    panelOptions.add(servOptions);
  }
  


  public void actionPerformed(ActionEvent arg0)
  {
    if (arg0.getSource() == radioServer)
    {
      panelOptions.removeAll();
      panelOptions.add(servOptions);
      panelOptions.revalidate();
      panelOptions.requestFocus();
      panelOptions.repaint();
    }
    else if (arg0.getSource() == radioClient)
    {
      panelOptions.removeAll();
      panelOptions.add(clientOptions);
      panelOptions.revalidate();
      panelOptions.requestFocus();
      panelOptions.repaint();
    }
    else if (arg0.getSource() == buttonStart)
    {
      String error = "";
      if (textGameID.getText().isEmpty())
      {
        error = Settings.lang("fill_game_id") + "\n";
      }
      if (textNick.getText().length() == 0)
      {
        error = error + Settings.lang("fill_name") + "\n";
      }
      if (textPassword.getText().length() <= 4)
      {
        error = error + Settings.lang("fill_pass_with_more_than_4_signs") + "\n";
      }
      if ((radioClient.isSelected()) && (clientOptions.textServIP.getText().length() == 0))
      {
        error = error + Settings.lang("please_fill_field") + " IP \n";
      }
      else if (radioClient.isSelected())
      {
        Pattern ipPattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
        if (!ipPattern.matcher(clientOptions.textServIP.getText()).matches())
        {
          error = error + Settings.lang("bad_ip_format") + "\n";
        }
      }
      if (error.length() > 0)
      {
        JOptionPane.showMessageDialog(this, error);
        return;
      }
      String pass = textPassword.getText().toString();
      if (radioServer.isSelected())
      {
        Server server = new Server();
        server.newTable(Integer.parseInt(textGameID.getText()), pass, !servOptions.checkWitchoutObserver.isSelected(), !servOptions.checkDisableChat.isSelected());
        
        clientOptions.textServIP.setText("127.0.0.1");
        
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException ex)
        {
          LOG.error("InterruptedException: " + ex);
        }
      }
      
      try
      {
        Client client = new Client(clientOptions.textServIP.getText(), Server.port);
        boolean isJoining = client.join(Integer.parseInt(textGameID.getText()), !clientOptions.checkOnlyWatch.isSelected(), textNick.getText(), MD5.encrypt(textPassword.getText()));
        
        if (isJoining)
        {
          LOG.debug("Client connection: succesful");
          
          Game newGUI = JChessApp.getJavaChessView().addNewTab("Network game, table: " + textGameID.getText());
          client.setGame(newGUI);
          newGUI.getChessboard().repaint();
          Settings sett = newGUI.getSettings();
          String whiteName = sett.getPlayerWhite().getName();
          String blackName = sett.getPlayerBlack().getName();
          Player whitePlayer = null;
          Player blackPlayer = null;
          if (radioServer.isSelected())
          {
            whitePlayer = PlayerFactory.getInstance(whiteName, Colors.WHITE, PlayerType.LOCAL_USER);
            blackPlayer = PlayerFactory.getInstance(blackName, Colors.BLACK, PlayerType.NETWORK_USER);
          }
          else
          {
            blackPlayer = PlayerFactory.getInstance(blackName, Colors.BLACK, PlayerType.LOCAL_USER);
            whitePlayer = PlayerFactory.getInstance(whiteName, Colors.WHITE, PlayerType.NETWORK_USER);
          }
          sett.setUpsideDown(false);
          sett.setPlayerBlack(blackPlayer);
          sett.setPlayerWhite(whitePlayer);
          newGUI.setSettings(sett);
          Thread thread = new Thread(client);
          thread.start();
          
          parent.setVisible(false);
        }
        else
        {
          JOptionPane.showMessageDialog(this, Settings.lang("error_connecting_to_server"));
        }
        
      }
      catch (Error err)
      {
        LOG.error("Client connection: failure: " + err);
        JOptionPane.showMessageDialog(this, err);
      }
      JChessApp.getJavaChessView().setLastTabAsActive();
    }
  }
  

  private class ServOptionsPanel
    extends JPanel
  {
    private GridBagLayout gbl;
    
    private GridBagConstraints gbc;
    
    private JLabel labelGameTime;
    
    public JTextField textGameTime;
    public JCheckBox checkWitchoutObserver;
    public JCheckBox checkDisableChat;
    
    ServOptionsPanel()
    {
      labelGameTime = new JLabel(Settings.lang("time_game_min"));
      textGameTime = new JTextField();
      checkWitchoutObserver = new JCheckBox(Settings.lang("without_observers"));
      checkDisableChat = new JCheckBox(Settings.lang("without_chat"));
      

      labelGameTime.setEnabled(false);
      textGameTime.setEnabled(false);
      checkDisableChat.setEnabled(false);
      

      gbl = new GridBagLayout();
      gbc = new GridBagConstraints();
      gbc.fill = 1;
      setLayout(gbl);
      
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridwidth = 2;
      gbl.setConstraints(labelGameTime, gbc);
      add(labelGameTime);
      
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.gridwidth = 2;
      gbl.setConstraints(textGameTime, gbc);
      add(textGameTime);
      
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.gridwidth = 1;
      gbl.setConstraints(checkWitchoutObserver, gbc);
      add(checkWitchoutObserver);
      
      gbc.gridx = 1;
      gbc.gridy = 2;
      gbc.gridwidth = 1;
      gbl.setConstraints(checkDisableChat, gbc);
      add(checkDisableChat);
    }
  }
  

  private class ClientOptionsPanel
    extends JPanel
  {
    private GridBagLayout gbl;
    
    private GridBagConstraints gbc;
    
    private JLabel labelServIP;
    
    public JTextField textServIP;
    public JCheckBox checkOnlyWatch;
    
    ClientOptionsPanel()
    {
      labelServIP = new JLabel(Settings.lang("server_ip"));
      textServIP = new JTextField();
      checkOnlyWatch = new JCheckBox(Settings.lang("only_observe"));
      
      gbl = new GridBagLayout();
      gbc = new GridBagConstraints();
      gbc.fill = 1;
      setLayout(gbl);
      
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbl.setConstraints(labelServIP, gbc);
      add(labelServIP);
      
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbl.setConstraints(textServIP, gbc);
      add(textServIP);
      
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbl.setConstraints(checkOnlyWatch, gbc);
      add(checkOnlyWatch);
    }
  }
}
