package pl.art.lach.mateusz.javaopenchess.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.NetworkPlayer;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;























public class Server
  implements Runnable
{
  private static final Logger LOG = Logger.getLogger(Server.class);
  
  public static boolean isPrintEnable = true;
  
  private static Map<Integer, Table> tables;
  public static int port = 4449;
  private static ServerSocket ss;
  private static boolean isRunning = false;
  
  public static enum connection_info
  {
    all_is_ok(0), 
    err_bad_table_ID(1), 
    err_table_is_full(2), 
    err_game_without_observer(3), 
    err_bad_password(4);
    
    private int value;
    
    private connection_info(int value)
    {
      this.value = value;
    }
    
    public static connection_info get(int id)
    {
      switch (id)
      {
      case 0: 
        return all_is_ok;
      case 1: 
        return err_bad_table_ID;
      case 2: 
        return err_table_is_full;
      case 3: 
        return err_game_without_observer;
      case 4: 
        return err_bad_password;
      }
      return null;
    }
    

    public int getValue()
    {
      return value;
    }
  }
  
  public Server()
  {
    if (!isRunning)
    {
      runServer();
      
      Thread thread = new Thread(this);
      thread.start();
      
      isRunning = true;
    }
  }
  




  public static boolean isRunning()
  {
    return isRunning;
  }
  





  private static void runServer()
  {
    try
    {
      ss = new ServerSocket(port);
      print("running");
    }
    catch (IOException ex)
    {
      LOG.error("runServer/IOException: " + ex);
    }
    
    tables = new HashMap();
  }
  

  public void run()
  {
    print("listening port: " + port);
    



    for (;;)
    {
      try
      {
        Socket s = ss.accept();
        ObjectInputStream input = new ObjectInputStream(s.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
        
        print("new connection");
        

        int tableID = input.readInt();
        print("readed table ID: " + tableID);
        boolean joinAsPlayer = input.readBoolean();
        print("readed joinAsPlayer: " + joinAsPlayer);
        String nick = input.readUTF();
        print("readed nick: " + nick);
        String password = input.readUTF();
        print("readed password: " + password);
        

        if (!tables.containsKey(Integer.valueOf(tableID)))
        {
          print("bad table ID");
          output.writeInt(connection_info.err_bad_table_ID.getValue());
          output.flush();
        }
        else {
          Table table = (Table)tables.get(Integer.valueOf(tableID));
          
          if (!password.equals(password))
          {
            print("bad password");
            output.writeInt(connection_info.err_bad_password.getValue());
            output.flush();


          }
          else if (joinAsPlayer)
          {
            print("join as player");
            if (table.isAllPlayers())
            {
              print("error: was all players at this table");
              output.writeInt(connection_info.err_table_is_full.getValue());
              output.flush();
            }
            else
            {
              print("wasn't all players at this table");
              
              output.writeInt(connection_info.all_is_ok.getValue());
              output.flush();
              
              table.addPlayer(new Client(s, input, output, nick, table));
              table.sendMessageToAll("** Gracz " + nick + " dołączył do gry **");
              
              if (table.isAllPlayers())
              {
                table.generateSettings();
                
                print("Send settings to all");
                table.sendSettingsToAll();
                
                table.sendMessageToAll("** Nowa gra, zaczna: " + clientPlayer1.nick + "**");
              }
              else
              {
                table.sendMessageToAll("** Oczekiwanie na drugiego gracza **");
              }
            }
          }
          else
          {
            print("join as observer");
            if (!table.canObserversJoin())
            {
              print("Observers can't join");
              output.writeInt(connection_info.err_game_without_observer.getValue());
              output.flush();
            }
            else
            {
              output.writeInt(connection_info.all_is_ok.getValue());
              output.flush();
              
              table.addObserver(new Client(s, input, output, nick, table));
              
              if (clientPlayer2 != null)
              {
                table.sendSettingsAndMovesToNewObserver();
              }
              
              table.sendMessageToAll("** Obserwator " + nick + " dołączył do gry **");
            }
          }
        }
      }
      catch (IOException ex) {
        LOG.error("runServer/IOException: " + ex);
      }
    }
  }
  




  private static void print(String str)
  {
    if (isPrintEnable)
    {
      LOG.debug("Server: " + str);
    }
  }
  







  public void newTable(int idTable, String password, boolean withObserver, boolean enableChat)
  {
    print("create new table - id: " + idTable);
    tables.put(Integer.valueOf(idTable), new Table(password, withObserver, enableChat));
  }
  

  private class Table
  {
    public Server.Client clientPlayer1;
    
    public Server.Client clientPlayer2;
    
    public ArrayList<Server.Client> clientObservers;
    
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
        observerSettings.setPlayerWhite(new NetworkPlayer(clientPlayer1.nick, Colors.BLACK));
        observerSettings.setPlayerBlack(new NetworkPlayer(clientPlayer2.nick, Colors.BLACK));
        observerSettings.setGameType(GameTypes.NETWORK);
        observerSettings.setUpsideDown(false);
      }
    }
    
    public void sendSettingsToAll() throws IOException
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
        for (Server.Client observer : clientObservers)
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
      Server.Client observer = (Server.Client)clientObservers.get(clientObservers.size() - 1);
      
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
        output.writeUTF(promoted != null ? promoted : "");
      }
      output.flush();
    }
    
    public void sendMoveToOther(Server.Client sender, int beginX, int beginY, int endX, int endY, String promoted)
      throws IOException
    {
      Server.print("running function: sendMoveToOther(" + nick + ", " + beginX + ", " + beginY + ", " + endX + ", " + endY + ")");
      
      if ((sender == clientPlayer1) || (sender == clientPlayer2))
      {

        if (clientPlayer1 != sender)
        {
          clientPlayer1.output.writeUTF("#move");
          clientPlayer1.output.writeInt(beginX);
          clientPlayer1.output.writeInt(beginY);
          clientPlayer1.output.writeInt(endX);
          clientPlayer1.output.writeInt(endY);
          clientPlayer1.output.writeUTF(promoted != null ? promoted : "");
          clientPlayer1.output.flush();
        }
        if (clientPlayer2 != sender)
        {
          clientPlayer2.output.writeUTF("#move");
          clientPlayer2.output.writeInt(beginX);
          clientPlayer2.output.writeInt(beginY);
          clientPlayer2.output.writeInt(endX);
          clientPlayer2.output.writeInt(endY);
          clientPlayer2.output.writeUTF(promoted != null ? promoted : "");
          clientPlayer2.output.flush();
        }
        
        if (canObserversJoin())
        {
          for (Server.Client observer : clientObservers)
          {
            output.writeUTF("#move");
            output.writeInt(beginX);
            output.writeInt(beginY);
            output.writeInt(endX);
            output.writeInt(endY);
            output.writeUTF(promoted != null ? promoted : "");
            output.flush();
          }
        }
        
        movesList.add(new Move(beginX, beginY, endX, endY, promoted));
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
        for (Server.Client observer : clientObservers)
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
    
    public void addPlayer(Server.Client client)
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
    
    public void addObserver(Server.Client client)
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
  
  private class Client implements Runnable
  {
    private Socket s;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public String nick;
    private Server.Table table;
    
    Client(Socket s, ObjectInputStream input, ObjectOutputStream output, String nick, Server.Table table)
    {
      this.s = s;
      this.input = input;
      this.output = output;
      this.nick = nick;
      this.table = table;
      
      Thread thread = new Thread(this);
      thread.start();
    }
    

    public void run()
    {
      Server.print("running function: run()");
      try
      {
        for (;;)
        {
          String in = input.readUTF();
          
          if (in.equals("#move"))
          {
            int bX = input.readInt();
            int bY = input.readInt();
            int eX = input.readInt();
            int eY = input.readInt();
            String promoted = input.readUTF();
            table.sendMoveToOther(this, bX, bY, eX, eY, promoted);
          }
          if (in.equals("#message"))
          {
            String str = input.readUTF();
            
            table.sendMessageToAll(nick + ": " + str);
          }
        }
      }
      catch (IOException ex) {
        Server.LOG.error("private Client/IOException: " + ex);
      }
    }
  }
}
