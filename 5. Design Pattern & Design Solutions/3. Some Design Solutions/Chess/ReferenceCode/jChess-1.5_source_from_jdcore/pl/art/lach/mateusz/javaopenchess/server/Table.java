package pl.art.lach.mateusz.javaopenchess.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.NetworkPlayer;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;




















public class Table
{
  public SClient clientPlayer1;
  public SClient clientPlayer2;
  public ArrayList<SClient> clientObservers;
  public Settings player1Set;
  public Settings player2Set;
  public Settings observerSettings;
  public String password;
  private boolean canObserversJoin;
  private boolean enableChat;
  private ArrayList<Move> movesList;
  
  Table(String password, boolean canObserversJoin, boolean enableChat)
  {
    this.password = password;
    this.enableChat = enableChat;
    this.canObserversJoin = canObserversJoin;
    
    if (canObserversJoin)
    {
      clientObservers = new ArrayList();
    }
    
    movesList = new ArrayList();
  }
  

  public void generateSettings()
  {
    player1Set = new Settings();
    player2Set = new Settings();
    
    player1Set.setGameMode(GameModes.NEW_GAME);
    player1Set.setPlayerWhite(new HumanPlayer(clientPlayer1.nick, Colors.WHITE));
    player1Set.setPlayerBlack(new NetworkPlayer(clientPlayer2.nick, Colors.BLACK));
    player1Set.setGameType(GameTypes.NETWORK);
    player1Set.setUpsideDown(false);
    
    player2Set.setGameMode(GameModes.NEW_GAME);
    player2Set.setPlayerWhite(new NetworkPlayer(clientPlayer1.nick, Colors.WHITE));
    player2Set.setPlayerBlack(new HumanPlayer(clientPlayer2.nick, Colors.BLACK));
    player2Set.setGameType(GameTypes.NETWORK);
    player2Set.setUpsideDown(false);
    
    if (canObserversJoin())
    {
      observerSettings = new Settings();
      
      observerSettings.setGameMode(GameModes.NEW_GAME);
      observerSettings.setPlayerWhite(new NetworkPlayer(clientPlayer1.nick, Colors.WHITE));
      observerSettings.setPlayerBlack(new NetworkPlayer(clientPlayer2.nick, Colors.BLACK));
      observerSettings.setGameType(GameTypes.NETWORK);
      observerSettings.setUpsideDown(false);
    }
  }
  
  public void sendSettingsToAll()
    throws IOException
  {
    Server.print("running function: sendSettingsToAll()");
    
    clientPlayer1.output.writeUTF("#settings");
    clientPlayer1.output.writeObject(player1Set);
    clientPlayer1.output.flush();
    
    clientPlayer2.output.writeUTF("#settings");
    clientPlayer2.output.writeObject(player2Set);
    clientPlayer2.output.flush();
    
    if (canObserversJoin())
    {
      for (SClient observer : clientObservers)
      {
        output.writeUTF("#settings");
        output.writeObject(observerSettings);
        output.flush();
      }
    }
  }
  

  public void sendSettingsAndMovesToNewObserver()
    throws IOException
  {
    SClient observer = (SClient)clientObservers.get(clientObservers.size() - 1);
    
    output.writeUTF("#settings");
    output.writeObject(observerSettings);
    output.flush();
    
    for (Move m : movesList)
    {
      output.writeUTF("#move");
      output.writeInt(bX);
      output.writeInt(bY);
      output.writeInt(eX);
      output.writeInt(eY);
      output.writeUTF(promoted);
    }
    output.flush();
  }
  
  public void sendMoveToOther(SClient sender, int beginX, int beginY, int endX, int endY, String promoted)
    throws IOException
  {
    Server.print("running function: sendMoveToOther(" + nick + ", " + beginX + ", " + beginY + ", " + endX + ", " + endY + ")");
    
    if ((sender == clientPlayer1) || (sender == clientPlayer2))
    {
      SClient receiver = clientPlayer1 == sender ? clientPlayer2 : clientPlayer1;
      output.writeUTF("#move");
      output.writeInt(beginX);
      output.writeInt(beginY);
      output.writeInt(endX);
      output.writeInt(endY);
      output.writeUTF(promoted);
      output.flush();
      
      if (canObserversJoin())
      {
        for (SClient observer : clientObservers)
        {
          output.writeUTF("#move");
          output.writeInt(beginX);
          output.writeInt(beginY);
          output.writeInt(endX);
          output.writeInt(endY);
          output.writeUTF(promoted);
          output.flush();
        }
      }
      
      movesList.add(new Move(beginX, beginY, endX, endY, promoted));
    }
  }
  
  public void sendUndoToAll(SClient sender, String msg) throws IOException
  {
    if ((sender == clientPlayer1) || (sender == clientPlayer2))
    {
      sendToAll(sender, msg);
      try
      {
        movesList.remove(movesList.size() - 1);
      }
      catch (ArrayIndexOutOfBoundsException exc) {}
    }
  }
  



  public void sendToAll(SClient sender, String msg)
    throws IOException
  {
    if ((sender == clientPlayer1) || (sender == clientPlayer2))
    {
      SClient receiver = clientPlayer1 == sender ? clientPlayer2 : clientPlayer1;
      output.writeUTF(msg);
      output.flush();
      
      if (canObserversJoin())
      {
        for (SClient observer : clientObservers)
        {
          output.writeUTF(msg);
          output.flush();
        }
      }
    }
  }
  
  public void sendToOtherPlayer(SClient sender, String msg)
    throws IOException
  {
    if ((sender == clientPlayer1) || (sender == clientPlayer2))
    {
      SClient receiver = clientPlayer1 == sender ? clientPlayer2 : clientPlayer1;
      output.writeUTF(msg);
      output.flush();
    }
  }
  

  public void sendErrorConnectionToOther(SClient sender)
    throws IOException
  {
    Server.print("running function: sendErrorConnectionToOther(" + nick + ")");
    
    if ((sender == clientPlayer1) || (sender == clientPlayer2))
    {
      if (clientPlayer1 != sender)
      {
        clientPlayer1.output.writeUTF("#errorConnection");
        clientPlayer1.output.flush();
      }
      if (clientPlayer2 != sender)
      {
        clientPlayer2.output.writeUTF("#errorConnection");
        clientPlayer2.output.flush();
      }
      
      if (canObserversJoin())
      {
        for (SClient observer : clientObservers)
        {
          output.writeUTF("#errorConnection");
          output.flush();
        }
      }
    }
  }
  
  public void sendMessageToAll(String str) throws IOException
  {
    Server.print("running function: sendMessageToAll(" + str + ")");
    
    if (clientPlayer1 != null)
    {
      clientPlayer1.output.writeUTF("#message");
      clientPlayer1.output.writeUTF(str);
      clientPlayer1.output.flush();
    }
    
    if (clientPlayer2 != null)
    {
      clientPlayer2.output.writeUTF("#message");
      clientPlayer2.output.writeUTF(str);
      clientPlayer2.output.flush();
    }
    
    if (canObserversJoin())
    {
      for (SClient observer : clientObservers)
      {
        output.writeUTF("#message");
        output.writeUTF(str);
        output.flush();
      }
    }
  }
  

  public boolean isAllPlayers()
  {
    if ((clientPlayer1 == null) || (clientPlayer2 == null))
    {
      return false;
    }
    return true;
  }
  

  public boolean isObservers()
  {
    return !clientObservers.isEmpty();
  }
  

  public boolean canObserversJoin()
  {
    return canObserversJoin;
  }
  

  public void addPlayer(SClient client)
  {
    if (clientPlayer1 == null)
    {
      clientPlayer1 = client;
      Server.print("Player1 connected");
    }
    else if (clientPlayer2 == null)
    {
      clientPlayer2 = client;
      Server.print("Player2 connected");
    }
  }
  

  public void addObserver(SClient client)
  {
    clientObservers.add(client);
  }
  

  private class Move
  {
    int bX;
    
    int bY;
    
    int eX;
    
    int eY;
    String promoted;
    
    Move(int bX, int bY, int eX, int eY, String promoted)
    {
      this.bX = bX;
      this.bY = bY;
      this.eX = eX;
      this.eY = eY;
      this.promoted = promoted;
    }
  }
}
