package pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

















public class PGNNotation
  implements DataImporter, DataExporter
{
  private static final Logger LOG = Logger.getLogger(Game.class);
  
  private static final String BLACK_COLOR_INTRO = "[Black";
  
  private static final String WHITE_COLOR_INTRO = "[White";
  
  private static final String START_MOVES_LINE_INTRO = "1.";
  

  public PGNNotation() {}
  
  public Game importData(String data)
    throws ReadGameError
  {
    BufferedReader br = new BufferedReader(new StringReader(data));
    
    try
    {
      String tempStr = getLineWithVar(br, "[White");
      String whiteName = getValue(tempStr);
      tempStr = getLineWithVar(br, "[Black");
      String blackName = getValue(tempStr);
      tempStr = getLineWithVar(br, "1.");
    }
    catch (ReadGameError err)
    {
      LOG.error("Error reading file: " + err);
      return null; }
    String whiteName;
    String blackName; String tempStr; Game game = GameFactory.instance(GameModes.LOAD_GAME, GameTypes.LOCAL, whiteName, blackName, PlayerType.LOCAL_USER, PlayerType.LOCAL_USER, true, false);
    








    importData(tempStr, game);
    game.getChessboard().repaint();
    return game;
  }
  
  public void importData(String data, Game game)
    throws ReadGameError
  {
    game.setBlockedChessboard(true);
    importData(new BufferedReader(new StringReader(data)), game);
    game.setBlockedChessboard(false);
  }
  
  private void importData(BufferedReader br, Game game) throws ReadGameError
  {
    game.getMoves().setMoves(getLineWithVar(br, "1."));
  }
  

  public String exportData(Game game)
  {
    Calendar cal = Calendar.getInstance();
    Settings sett = game.getSettings();
    StringBuilder strBuilder = new StringBuilder();
    String header = String.format("[Event \"Game\"]\n[Date \"%s.%s.%s\"]\n[White \"%s\"]\n[Black \"%s\"]\n\n", new Object[] {
    
      Integer.valueOf(cal.get(1)), Integer.valueOf(cal.get(2) + 1), Integer.valueOf(cal.get(5)), sett
      .getPlayerWhite().getName(), sett.getPlayerBlack().getName() });
    
    strBuilder.append(header);
    strBuilder.append(game.getMoves().getMovesInString());
    return strBuilder.toString();
  }
  





  private static String getLineWithVar(BufferedReader br, String srcStr)
    throws ReadGameError
  {
    String str = new String();
    do
    {
      try
      {
        str = br.readLine();
      }
      catch (IOException exc)
      {
        LOG.error("Something wrong reading file: ", exc);
        throw new ReadGameError("Something wrong reading file: " + exc);
      }
      if (str == null)
      {
        LOG.error("Something wrong reading file, str == null.");
        throw new ReadGameError("Something wrong reading file, str == null.");
      }
    } while (!str.startsWith(srcStr));
    
    return str;
  }
  






  private static String getValue(String line)
    throws ReadGameError
  {
    int from = line.indexOf("\"");
    int to = line.lastIndexOf("\"");
    int size = line.length() - 1;
    String result = "";
    if ((to < from) || (from > size) || (to > size) || (to < 0) || (from < 0))
    {
      throw new ReadGameError("Error reading value from PGN header section.");
    }
    try
    {
      result = line.substring(from + 1, to);
    }
    catch (StringIndexOutOfBoundsException exc)
    {
      LOG.error("error getting value: ", exc);
      return "none";
    }
    return result;
  }
}
