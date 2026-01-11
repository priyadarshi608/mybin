package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;










public class ThemeChooseWindow
  extends JDialog
  implements ActionListener, ListSelectionListener
{
  private static final Logger LOG = Logger.getLogger(ThemeChooseWindow.class);
  JList themesList;
  ImageIcon themePreview;
  GridBagLayout gbl;
  public String result;
  GridBagConstraints gbc;
  JButton themePreviewButton;
  JButton okButton;
  
  public ThemeChooseWindow(Frame parent)
    throws Exception
  {
    super(parent);
    
    File dir = new File(GUI.getJarPath() + File.separator + "theme" + File.separator);
    
    LOG.debug("Theme path: " + dir.getPath());
    
    File[] files = dir.listFiles();
    if ((files != null) && (dir.exists()))
    {
      setTitle(Settings.lang("choose_theme_window_title"));
      Dimension winDim = new Dimension(550, 230);
      setMinimumSize(winDim);
      setMaximumSize(winDim);
      setSize(winDim);
      setResizable(false);
      setLayout(null);
      setDefaultCloseOperation(2);
      String[] dirNames = new String[files.length];
      for (int i = 0; i < files.length; i++)
      {
        dirNames[i] = files[i].getName();
      }
      themesList = new JList(dirNames);
      themesList.setLocation(new Point(10, 10));
      themesList.setSize(new Dimension(100, 120));
      add(themesList);
      themesList.setSelectionMode(0);
      themesList.addListSelectionListener(this);
      Properties prp = GUI.getConfigFile();
      
      gbl = new GridBagLayout();
      gbc = new GridBagConstraints();
      try
      {
        themePreview = new ImageIcon(GUI.loadImage("Preview.png"));
      }
      catch (NullPointerException exc)
      {
        LOG.error("NullPointerException: Cannot find preview image: ", exc);
        themePreview = new ImageIcon(JChessApp.class.getResource("theme/noPreview.png"));
        return;
      }
      result = "";
      themePreviewButton = new JButton(themePreview);
      themePreviewButton.setLocation(new Point(110, 10));
      themePreviewButton.setSize(new Dimension(420, 120));
      add(themePreviewButton);
      okButton = new JButton("OK");
      okButton.setLocation(new Point(175, 140));
      okButton.setSize(new Dimension(200, 50));
      add(okButton);
      okButton.addActionListener(this);
      setModal(true);
    }
    else
    {
      throw new Exception(Settings.lang("error_when_creating_theme_config_window"));
    }
  }
  


  public void valueChanged(ListSelectionEvent event)
  {
    String element = themesList.getModel().getElementAt(themesList.getSelectedIndex()).toString();
    String path = GUI.getJarPath() + File.separator + "theme/";
    

    LOG.debug(path + element + "/images/Preview.png");
    
    themePreview = new ImageIcon(path + element + "/images/Preview.png");
    themePreviewButton.setIcon(themePreview);
  }
  



  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == okButton)
    {
      Properties prp = GUI.getConfigFile();
      int element = themesList.getSelectedIndex();
      String name = themesList.getModel().getElementAt(element).toString();
      if (GUI.themeIsValid(name))
      {
        prp.setProperty("THEME", name);
        try
        {
          FileOutputStream fOutStr = new FileOutputStream("config.txt");
          prp.store(fOutStr, null);
          fOutStr.flush();
          fOutStr.close();
        }
        catch (IOException exc)
        {
          LOG.error("actionPerformed/IOException: ", exc);
        }
        JOptionPane.showMessageDialog(this, Settings.lang("changes_visible_after_restart"));
        setVisible(false);
      }
      
      LOG.debug("property theme: " + prp.getProperty("THEME"));
    }
  }
}
