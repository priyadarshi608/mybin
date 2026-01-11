package pl.art.lach.mateusz.javaopenchess.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.utils.MD5;


















public class Console
{
  private static final Logger LOG = Logger.getLogger(Console.class);
  
  public Console() {}
  
  public static void main(String[] args) { System.out.println("JChess Server Start!");
    
    Server server = new Server();
    Server.isPrintEnable = false;
    
    boolean isOK = true;
    while (isOK)
    {
      System.out.println("--------------------");
      System.out.println("[1] New table");
      System.out.println("[2] List of active tables");
      System.out.println("[3] Turn on/off server messages");
      System.out.println("[4] Turn off server");
      System.out.print("-> ");
      String str = readString();
      int gameID;
      if (str.equals("1"))
      {
        System.out.print("ID of game: ");
        gameID = Integer.parseInt(readString());
        
        System.out.print("Password: ");
        String pass = MD5.encrypt(readString());
        
        String observer;
        do
        {
          System.out.print("Game with observers?[t/n] (t=YES, n=NO): ");
          observer = readString();
        }
        while ((!observer.equalsIgnoreCase("t")) && (!observer.equalsIgnoreCase("n")));
        
        boolean canObserver = observer.equalsIgnoreCase("t");
        
        server.newTable(gameID, pass, canObserver, true);
      }
      else if (str.equals("2"))
      {
        for (Map.Entry<Integer, Table> entry : Server.tables.entrySet())
        {
          Integer id = (Integer)entry.getKey();
          Table table = (Table)entry.getValue();
          
          String p1;
          String p1;
          if ((clientPlayer1 == null) || (clientPlayer1.nick == null))
          {
            p1 = "empty";
          }
          else
          {
            p1 = clientPlayer1.nick; }
          String p2;
          String p2;
          if ((clientPlayer2 == null) || (clientPlayer2.nick == null))
          {
            p2 = "empty";
          }
          else
          {
            p2 = clientPlayer2.nick;
          }
          
          System.out.println("\t" + id + ": " + p1 + " vs " + p2);
        }
      }
      else if (str.equals("3"))
      {
        if (!Server.isPrintEnable)
        {
          Server.isPrintEnable = true;
          System.out.println("Messages of server has been turned on");
        }
        else
        {
          Server.isPrintEnable = false;
          System.out.println("Messages of server has been turned off");
        }
      }
      else if (str.equals("4"))
      {
        isOK = false;
      }
      else
      {
        System.out.println("Unrecognized command");
      }
    }
    System.exit(0);
  }
  

  public static String readString()
  {
    StringBuilder sb = new StringBuilder();
    try {
      int ch;
      while ((ch = System.in.read()) != 10)
      {
        sb.append((char)ch);
      }
    }
    catch (IOException ex)
    {
      LOG.error("readString()/IOException: " + ex);
    }
    
    return sb.toString();
  }
}
