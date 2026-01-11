package pl.art.lach.mateusz.javaopenchess.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;














public class GameClock
  extends JPanel
  implements Runnable
{
  private static final Logger LOG = Logger.getLogger(GameClock.class);
  
  private Clock clock1;
  
  private Clock clock2;
  
  private Clock runningClock;
  
  private Settings settings;
  
  private Thread thread;
  
  private Game game;
  
  private Graphics g;
  
  private String white_clock;
  
  private String black_clock;
  
  private BufferedImage background;
  
  private Graphics bufferedGraphics;
  

  GameClock(Game game)
  {
    clock1 = new Clock();
    clock2 = new Clock();
    runningClock = clock1;
    this.game = game;
    settings = game.getSettings();
    background = new BufferedImage(600, 600, 2);
    
    int time = settings.getTimeForGame();
    
    setTimes(time, time);
    setPlayers(settings.getPlayerBlack(), settings.getPlayerWhite());
    
    thread = new Thread(this);
    if (settings.isTimeLimitSet())
    {
      thread.start();
    }
    drawBackground();
    setDoubleBuffered(true);
  }
  


  public void start()
  {
    thread.start();
  }
  


  public void stop()
  {
    runningClock = null;
    

    try
    {
      thread.wait();
    }
    catch (InterruptedException|IllegalMonitorStateException exc)
    {
      LOG.error("Error blocking thread: ", exc);
    }
  }
  


  void drawBackground()
  {
    Graphics gr = background.getGraphics();
    Graphics2D g2d = (Graphics2D)gr;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font font = new Font("Serif", 2, 20);
    
    g2d.setColor(Color.WHITE);
    g2d.fillRect(5, 30, 80, 30);
    g2d.setFont(font);
    
    g2d.setColor(Color.BLACK);
    g2d.fillRect(85, 30, 90, 30);
    g2d.drawRect(5, 30, 170, 30);
    g2d.drawRect(5, 60, 170, 30);
    g2d.drawLine(85, 30, 85, 90);
    font = new Font("Serif", 2, 16);
    g2d.drawString(settings.getPlayerWhite().getName(), 10, 50);
    g2d.setColor(Color.WHITE);
    g2d.drawString(settings.getPlayerBlack().getName(), 100, 50);
    bufferedGraphics = background.getGraphics();
  }
  





  public void paint(Graphics g)
  {
    super.paint(g);
    white_clock = clock1.prepareString();
    black_clock = clock2.prepareString();
    Graphics2D g2d = (Graphics2D)g;
    g2d.drawImage(background, 0, 0, this);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    


    Font font = new Font("Serif", 2, 20);
    g2d.drawImage(background, 0, 0, this);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(5, 30, 80, 30);
    g2d.setFont(font);
    
    g2d.setColor(Color.BLACK);
    g2d.fillRect(85, 30, 90, 30);
    g2d.drawRect(5, 30, 170, 30);
    g2d.drawRect(5, 60, 170, 30);
    g2d.drawLine(85, 30, 85, 90);
    font = new Font("Serif", 2, 14);
    g2d.drawImage(background, 0, 0, this);
    g2d.setFont(font);
    g.drawString(settings.getPlayerWhite().getName(), 10, 50);
    g.setColor(Color.WHITE);
    g.drawString(settings.getPlayerBlack().getName(), 100, 50);
    g2d.setFont(font);
    g.setColor(Color.BLACK);
    g2d.drawString(white_clock, 10, 80);
    g2d.drawString(black_clock, 90, 80);
  }
  





  public void update(Graphics g)
  {
    paint(g);
  }
  




  public void switchClocks()
  {
    if (runningClock == clock1)
    {
      runningClock = clock2;
    }
    else
    {
      runningClock = clock1;
    }
  }
  






  public void setTimes(int t1, int t2)
  {
    clock1.init(t1);
    clock2.init(t2);
  }
  




  private void setPlayers(Player p1, Player p2)
  {
    if (p1.getColor() == Colors.WHITE)
    {
      clock1.setPlayer(p1);
      clock2.setPlayer(p2);
    }
    else
    {
      clock1.setPlayer(p2);
      clock2.setPlayer(p1);
    }
  }
  




  public void run()
  {
    for (;;)
    {
      if (runningClock != null)
      {
        if (runningClock.decrement())
        {
          repaint();
          try
          {
            Thread.sleep(1000L);
          }
          catch (InterruptedException e)
          {
            LOG.error("Some error in gameClock thread: " + e);
          }
        }
        if ((runningClock != null) && (runningClock.getLeftTime() == 0))
        {
          timeOver();
        }
      }
    }
  }
  


  private void timeOver()
  {
    String color = new String();
    if (clock1.getLeftTime() == 0)
    {

      color = clock2.getPlayer().getColor().toString();
    }
    else if (clock2.getLeftTime() == 0)
    {
      color = clock1.getPlayer().getColor().toString();

    }
    else
    {
      LOG.debug("Time over called when player got time 2 play");
    }
    game.endGame("Time is over! " + color + " player win the game.");
    stop();
  }
}
