package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameClock;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;











public class JChessTabbedPane
  extends JTabbedPane
  implements MouseListener, ImageObserver, ChangeListener
{
  private static final Logger LOG = Logger.getLogger(GameClock.class);
  
  private TabbedPaneIcon closeIcon;
  
  private Image addIcon = null;
  
  private Image unclickedAddIcon = null;
  
  private Rectangle addIconRect = null;
  
  public static final Color DEFAULT_COLOR = Color.BLACK;
  
  public static final Color EVENT_COLOR = Color.RED;
  

  public JChessTabbedPane()
  {
    closeIcon = new TabbedPaneIcon(closeIcon);
    unclickedAddIcon = GUI.loadImage("add-tab-icon.png");
    addIcon = unclickedAddIcon;
    setDoubleBuffered(true);
    initListeners();
  }
  
  protected final void initListeners()
  {
    addChangeListener(this);
    addMouseListener(this);
  }
  

  public void addTab(String title, Component component)
  {
    addTab(title, component, null);
  }
  
  public void addTab(String title, Component component, Icon closeIcon)
  {
    super.addTab(title, new TabbedPaneIcon(closeIcon), component);
    LOG.debug("Present number of tabs: " + getTabCount());
    updateAddIconRect();
  }
  

  public void mouseReleased(MouseEvent e) {}
  

  public void mousePressed(MouseEvent e) {}
  
  private void showNewGameWindow()
  {
    JChessView jcv = JChessApp.getJavaChessView();
    if (JChessApp.getJavaChessView().getNewGameFrame() == null)
    {
      jcv.setNewGameFrame(new NewGameWindow());
    }
    JChessApp.getApplication().show(JChessApp.getJavaChessView().getNewGameFrame());
  }
  


  public void mouseClicked(MouseEvent e)
  {
    int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
    if (tabNumber >= 0)
    {
      Rectangle rect = ((TabbedPaneIcon)getIconAt(tabNumber)).getBounds();
      if (rect.contains(e.getX(), e.getY()))
      {
        LOG.debug("Removing tab with " + tabNumber + " number!...");
        removeTabAt(tabNumber);
        updateAddIconRect();
        if (getTabCount() == 0)
        {
          showNewGameWindow();
        }
      }
      if ((0 < getTabCount()) && (null != getTabComponentAt(getSelectedIndex())))
      {
        getTabComponentAt(getSelectedIndex()).repaint();
      }
      else if ((0 < getTabCount()) && (null != getComponent(getSelectedIndex())))
      {
        Component activeTab = getComponent(getSelectedIndex());
        setForegroundAt(getSelectedIndex(), DEFAULT_COLOR);
        activeTab.repaint();
      }
    }
    else if ((addIconRect != null) && (addIconRect.contains(e.getX(), e.getY())))
    {
      LOG.debug("newGame by + button");
      showNewGameWindow();
    }
  }
  
  public void highlightTab(Game game)
  {
    int tabNumber = JChessApp.getJavaChessView().getTabNumber(game);
    highlightTab(tabNumber);
  }
  
  public void highlightTab(int number)
  {
    if (number < getTabCount())
    {
      setForegroundAt(number, EVENT_COLOR);
    }
  }
  

  public void mouseEntered(MouseEvent e) {}
  

  public void mouseExited(MouseEvent e) {}
  
  private void updateAddIconRect()
  {
    if (getTabCount() > 0)
    {
      Rectangle rect = getBoundsAt(getTabCount() - 1);
      



      addIconRect = new Rectangle(x + width + 5, y, addIcon.getWidth(this), addIcon.getHeight(this));

    }
    else
    {
      addIconRect = null;
    }
  }
  
  private Rectangle getAddIconRect()
  {
    return addIconRect;
  }
  

  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
  {
    super.imageUpdate(img, infoflags, x, y, width, height);
    updateAddIconRect();
    return true;
  }
  

  public void paint(Graphics g)
  {
    super.paint(g);
    Rectangle rect = getAddIconRect();
    if (rect != null)
    {
      g.drawImage(addIcon, x, y, null);
    }
  }
  

  public void update(Graphics g)
  {
    repaint();
  }
  

  public void stateChanged(ChangeEvent changeEvent)
  {
    if (1 != getTabCount())
    {
      JTabbedPane sourceTabbedPane = (JTabbedPane)changeEvent.getSource();
      Game game = (Game)sourceTabbedPane.getSelectedComponent();
      if (null != game)
      {
        game.resizeGame();
      }
    }
  }
}
