package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameClock;
import pl.art.lach.mateusz.javaopenchess.core.ai.AIFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.network.Chat;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

public class DrawLocalSettings
  extends JPanel implements ActionListener, TextListener
{
  private static final Logger LOG = Logger.getLogger(DrawLocalSettings.class);
  
  JDialog parent;
  JComboBox color;
  JRadioButton oponentComp;
  JRadioButton oponentHuman;
  ButtonGroup oponentChoos;
  JFrame localPanel;
  JLabel compLevLab;
  JSlider computerLevel;
  JTextField firstName;
  JTextField secondName;
  JLabel firstNameLab;
  JLabel secondNameLab;
  JCheckBox upsideDown;
  GridBagLayout gbl;
  GridBagConstraints gbc;
  Container cont;
  JSeparator sep;
  JButton okButton;
  JCheckBox timeGame;
  JComboBox time4Game;
  String[] colors = {
  
    Settings.lang("white"), Settings.lang("black") };
  

  String[] times = { "1", "3", "5", "8", "10", "15", "20", "25", "30", "60", "120" };
  








  public void textValueChanged(TextEvent e)
  {
    Object target = e.getSource();
    if ((target == firstName) || (target == secondName))
    {
      JTextField temp = new JTextField();
      if (target == firstName)
      {
        temp = firstName;
      }
      else if (target == secondName)
      {
        temp = secondName;
      }
      
      int len = temp.getText().length();
      if (len > 8)
      {
        try
        {
          temp.setText(temp.getText(0, 7));
        }
        catch (BadLocationException exc)
        {
          LOG.error("BadLocationException: Something wrong in editables, msg: " + exc.getMessage() + " object: ", exc);
        }
      }
    }
  }
  





  public void actionPerformed(ActionEvent e)
  {
    Object target = e.getSource();
    if (target == oponentComp)
    {
      computerLevel.setEnabled(true);
      secondName.setEnabled(false);
    }
    else if (target == oponentHuman)
    {

      secondName.setEnabled(true);
    }
    else if (target == okButton)
    {
      if (firstName.getText().length() > 9)
      {
        firstName.setText(trimString(firstName, 9));
      }
      if (secondName.getText().length() > 9)
      {
        secondName.setText(trimString(secondName, 9));
      }
      if ((!oponentComp.isSelected()) && (
        (firstName.getText().length() == 0) || (secondName.getText().length() == 0)))
      {
        JOptionPane.showMessageDialog(this, Settings.lang("fill_names"));
        return;
      }
      if ((oponentComp.isSelected()) && (firstName.getText().length() == 0))
      {
        JOptionPane.showMessageDialog(this, Settings.lang("fill_name"));
        return;
      }
      String playerFirstName = firstName.getText();
      String playerSecondName = secondName.getText();
      PlayerType blackType;
      String blackName;
      String whiteName;
      PlayerType blackType;
      PlayerType whiteType;
      if (0 == color.getSelectedIndex())
      {
        String whiteName = playerFirstName;
        String blackName = playerSecondName;
        PlayerType whiteType = PlayerType.LOCAL_USER;
        blackType = oponentComp.isSelected() ? PlayerType.COMPUTER : PlayerType.LOCAL_USER;
      }
      else
      {
        blackName = playerFirstName;
        whiteName = playerSecondName;
        blackType = PlayerType.LOCAL_USER;
        whiteType = oponentComp.isSelected() ? PlayerType.COMPUTER : PlayerType.LOCAL_USER;
      }
      Player playerWhite = PlayerFactory.getInstance(whiteName, Colors.WHITE, whiteType);
      Player playerBlack = PlayerFactory.getInstance(blackName, Colors.BLACK, blackType);
      Game newGUI = JChessApp.getJavaChessView().addNewTab(playerWhite.getName() + " vs " + playerBlack.getName());
      newGUI.getChat().setEnabled(false);
      Settings sett = newGUI.getSettings();
      sett.setPlayerWhite(playerWhite);
      sett.setPlayerBlack(playerBlack);
      sett.setGameMode(GameModes.NEW_GAME);
      sett.setGameType(GameTypes.LOCAL);
      sett.setUpsideDown(upsideDown.isSelected());
      newGUI.setActivePlayer(playerWhite);
      if (timeGame.isSelected())
      {
        String value = times[time4Game.getSelectedIndex()];
        Integer val = new Integer(value);
        sett.setTimeForGame(val.intValue() * 60);
        newGUI.getGameClock().setTimes(sett.getTimeForGame(), sett.getTimeForGame());
        newGUI.getGameClock().start();
      }
      LOG.debug("this.time4Game.getActionCommand(): " + time4Game.getActionCommand());
      
      LOG.debug("****************\nStarting new game: " + playerWhite.getName() + " vs. " + playerBlack.getName() + "\ntime 4 game: " + sett
        .getTimeForGame() + "\ntime limit set: " + sett.isTimeLimitSet() + "\nwhite on top?: " + sett
        .isUpsideDown() + "\n****************");
      
      newGUI.newGame();
      parent.setVisible(false);
      
      JChessApp.getJavaChessView().getActiveTabGame().repaint();
      JChessApp.getJavaChessView().setActiveTabGame(JChessApp.getJavaChessView().getNumberOfOpenedTabs() - 1);
      if (oponentComp.isSelected())
      {
        Game activeGame = JChessApp.getJavaChessView().getActiveTabGame();
        activeGame.setAi(AIFactory.getAI(computerLevel.getValue()));
        if ((activeGame.getSettings().isGameAgainstComputer()) && 
          (activeGame.getSettings().getPlayerWhite().getPlayerType() == PlayerType.COMPUTER))
        {
          activeGame.doComputerMove();
        }
      }
    }
  }
  


  public DrawLocalSettings(JDialog parent)
  {
    this.parent = parent;
    color = new JComboBox(colors);
    gbl = new GridBagLayout();
    gbc = new GridBagConstraints();
    sep = new JSeparator();
    okButton = new JButton(Settings.lang("ok"));
    compLevLab = new JLabel(Settings.lang("computer_level"));
    
    firstName = new JTextField("", 10);
    firstName.setSize(new Dimension(200, 50));
    secondName = new JTextField("", 10);
    secondName.setSize(new Dimension(200, 50));
    firstNameLab = new JLabel(Settings.lang("first_player_name") + ": ");
    secondNameLab = new JLabel(Settings.lang("second_player_name") + ": ");
    oponentChoos = new ButtonGroup();
    computerLevel = new JSlider();
    upsideDown = new JCheckBox(Settings.lang("upside_down"));
    timeGame = new JCheckBox(Settings.lang("time_game_min"));
    time4Game = new JComboBox(times);
    
    oponentComp = new JRadioButton(Settings.lang("against_computer"), false);
    oponentHuman = new JRadioButton(Settings.lang("against_other_human"), true);
    
    setLayout(gbl);
    oponentComp.addActionListener(this);
    oponentHuman.addActionListener(this);
    okButton.addActionListener(this);
    
    secondName.addActionListener(this);
    
    oponentChoos.add(oponentComp);
    oponentChoos.add(oponentHuman);
    computerLevel.setEnabled(false);
    computerLevel.setValue(1);
    computerLevel.setMaximum(2);
    computerLevel.setMinimum(1);
    computerLevel.setPaintTicks(true);
    computerLevel.setPaintLabels(true);
    computerLevel.setMajorTickSpacing(1);
    computerLevel.setMinorTickSpacing(1);
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(3, 3, 3, 3);
    gbl.setConstraints(oponentComp, gbc);
    add(oponentComp);
    gbc.gridx = 1;
    gbl.setConstraints(oponentHuman, gbc);
    add(oponentHuman);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbl.setConstraints(firstNameLab, gbc);
    add(firstNameLab);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbl.setConstraints(firstName, gbc);
    add(firstName);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbl.setConstraints(color, gbc);
    add(color);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbl.setConstraints(secondNameLab, gbc);
    add(secondNameLab);
    gbc.gridy = 4;
    gbl.setConstraints(secondName, gbc);
    add(secondName);
    gbc.gridy = 5;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbl.setConstraints(compLevLab, gbc);
    add(compLevLab);
    gbc.gridy = 6;
    gbl.setConstraints(computerLevel, gbc);
    add(computerLevel);
    gbc.gridy = 7;
    gbl.setConstraints(upsideDown, gbc);
    add(upsideDown);
    gbc.gridy = 8;
    gbc.gridwidth = 1;
    gbl.setConstraints(timeGame, gbc);
    add(timeGame);
    gbc.gridx = 1;
    gbc.gridy = 8;
    gbc.gridwidth = 1;
    gbl.setConstraints(time4Game, gbc);
    add(time4Game);
    gbc.gridx = 1;
    gbc.gridy = 9;
    gbc.gridwidth = 0;
    gbl.setConstraints(okButton, gbc);
    add(okButton);
  }
  








  public String trimString(JTextField txt, int length)
  {
    String result = new String();
    try
    {
      result = txt.getText(0, length);
    }
    catch (BadLocationException exc)
    {
      LOG.error("BadLocationException: Something wrong in trimString: \n", exc);
    }
    return result;
  }
}
