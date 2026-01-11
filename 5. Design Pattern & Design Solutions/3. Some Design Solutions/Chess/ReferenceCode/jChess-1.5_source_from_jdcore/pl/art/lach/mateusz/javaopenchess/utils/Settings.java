package pl.art.lach.mateusz.javaopenchess.utils;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;



















public class Settings
  implements Serializable
{
  private static final Logger LOG = Logger.getLogger(Settings.class);
  
  private static final String RESOURCES_I18N_MAIN = ".resources.i18n.main";
  
  private static ResourceBundle loc = null;
  

  protected int timeForGame;
  

  protected boolean runningChat;
  

  protected boolean runningGameClock;
  
  protected boolean timeLimitSet = false;
  
  protected boolean upsideDown;
  
  protected boolean displayLegalMovesEnabled = true;
  
  protected GameModes gameMode;
  
  protected Player playerWhite;
  
  protected Player playerBlack;
  
  protected GameTypes gameType;
  
  protected boolean renderLabels = true;
  
  public Settings()
  {
    this(new HumanPlayer("", Colors.WHITE
      .getColorName()), new HumanPlayer("", Colors.BLACK
      .getColorName()));
  }
  

  public Settings(Player playerWhite, Player playerBlack)
  {
    this.playerWhite = playerWhite;
    this.playerBlack = playerBlack;
    timeLimitSet = false;
    
    gameMode = GameModes.NEW_GAME;
  }
  



  public boolean isRunningChat()
  {
    return runningChat;
  }
  



  public boolean isRunningGameClock()
  {
    return runningGameClock;
  }
  



  public boolean isTimeLimitSet()
  {
    return timeLimitSet;
  }
  
  public boolean isUpsideDown()
  {
    return upsideDown;
  }
  



  public GameModes getGameMode()
  {
    return gameMode;
  }
  



  public Player getPlayerWhite()
  {
    return playerWhite;
  }
  



  public Player getPlayerBlack()
  {
    return playerBlack;
  }
  
  public void setPlayerWhite(Player player)
  {
    playerWhite = player;
  }
  
  public void setPlayerBlack(Player player)
  {
    playerBlack = player;
  }
  



  public GameTypes getGameType()
  {
    return gameType;
  }
  



  public boolean isRenderLabels()
  {
    return renderLabels;
  }
  
  public void setRenderLabels(boolean renderLabels)
  {
    this.renderLabels = renderLabels;
  }
  



  public void setUpsideDown(boolean upsideDown)
  {
    this.upsideDown = upsideDown;
  }
  



  public void setGameMode(GameModes gameMode)
  {
    this.gameMode = gameMode;
  }
  



  public void setGameType(GameTypes gameType)
  {
    this.gameType = gameType;
  }
  



  public void setTimeForGame(int timeForGame)
  {
    timeLimitSet = true;
    this.timeForGame = timeForGame;
  }
  



  public boolean isDisplayLegalMovesEnabled()
  {
    return displayLegalMovesEnabled;
  }
  



  public void setDisplayLegalMovesEnabled(boolean displayLegalMovesEnabled)
  {
    this.displayLegalMovesEnabled = displayLegalMovesEnabled;
  }
  




  public int getTimeForGame()
  {
    return timeForGame;
  }
  

  public boolean isGameAgainstComputer()
  {
    return (playerBlack.getPlayerType() == PlayerType.COMPUTER) || (playerWhite.getPlayerType() == PlayerType.COMPUTER);
  }
  
  public static String lang(String key)
  {
    if (loc == null)
    {
      loc = PropertyResourceBundle.getBundle(JChessApp.MAIN_PACKAGE_NAME + ".resources.i18n.main");
      Locale.setDefault(Locale.ENGLISH);
    }
    String result = "";
    try
    {
      result = loc.getString(key);
    }
    catch (MissingResourceException exc)
    {
      result = key;
    }
    LOG.debug("Locale: " + loc.getLocale().toString());
    return result;
  }
}
