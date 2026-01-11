package pl.art.lach.mateusz.javaopenchess.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.utils.MD5;


















public class Server
  implements Runnable
{
  private static final Logger LOG = Logger.getLogger(Server.class);
  
  public static boolean isPrintEnable = true;
  public static Map<Integer, Table> tables;
  public static int port = 4449;
  private static ServerSocket ss;
  private static boolean isRunning = false;
  
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
      LOG.error("IOException: " + ex);
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
          output.writeInt(ConnectionInfo.ERR_WRONG_TABLE_ID.getValue());
          output.flush();
          continue;
        }
        Table table = (Table)tables.get(Integer.valueOf(tableID));
        
        if (!MD5.encrypt(password).equals(password))
        {
          print("bad password: " + MD5.encrypt(password) + " != " + password);
          output.writeInt(ConnectionInfo.ERR_INVALID_PASSWORD.getValue());
          output.flush();
          continue;
        }
        
        if (joinAsPlayer)
        {
          print("join as player");
          if (table.isAllPlayers())
          {
            print("error: was all players at this table");
            output.writeInt(ConnectionInfo.ERR_TABLE_IS_FULL.getValue());
            output.flush();
            continue;
          }
          

          print("wasn't all players at this table");
          
          output.writeInt(ConnectionInfo.EVERYTHING_IS_OK.getValue());
          output.flush();
          
          table.addPlayer(new SClient(s, input, output, nick, table));
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
        else
        {
          print("join as observer");
          if (!table.canObserversJoin())
          {
            print("Observers can't join");
            output.writeInt(ConnectionInfo.ERR_GAME_WITHOUT_OBSERVERS.getValue());
            output.flush();
            continue;
          }
          

          output.writeInt(ConnectionInfo.EVERYTHING_IS_OK.getValue());
          output.flush();
          
          table.addObserver(new SClient(s, input, output, nick, table));
          
          if (clientPlayer2 != null)
          {
            table.sendSettingsAndMovesToNewObserver();
          }
          
          table.sendMessageToAll("** Obserwator " + nick + " dołączył do gry **");
        }
        
      }
      catch (IOException ex)
      {
        LOG.error("IOException: " + ex);
      }
    }
  }
  

  public static void print(String str)
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
}
