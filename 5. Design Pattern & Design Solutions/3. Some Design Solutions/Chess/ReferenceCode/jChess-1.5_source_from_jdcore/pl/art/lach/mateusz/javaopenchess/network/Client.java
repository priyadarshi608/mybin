package pl.art.lach.mateusz.javaopenchess.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.display.windows.JChessTabbedPane;
import pl.art.lach.mateusz.javaopenchess.server.ConnectionInfo;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;




















public class Client
  implements Runnable
{
  private static final Logger LOG = Logger.getLogger(Client.class);
  
  public static boolean isPrintEnable = true;
  
  protected Socket socket;
  
  protected ObjectOutputStream output;
  
  protected ObjectInputStream input;
  
  protected String ip;
  
  protected int port;
  
  protected Game game;
  
  protected Settings settings;
  
  protected boolean wait4undoAnswer = false;
  
  protected boolean isObserver = false;
  
  public Client(String ip, int port)
  {
    print("running");
    
    this.ip = ip;
    this.port = port;
  }
  


  public boolean join(int tableID, boolean asPlayer, String nick, String password)
    throws Error
  {
    print("running function: join(" + tableID + ", " + asPlayer + ", " + nick + ")");
    try
    {
      print("join to server: ip:" + getIp() + " port:" + getPort());
      setIsObserver(!asPlayer);
      try
      {
        setSocket(new Socket(getIp(), getPort()));
        setOutput(new ObjectOutputStream(getSocket().getOutputStream()));
        setInput(new ObjectInputStream(getSocket().getInputStream()));
        
        print("send to server: table ID");
        getOutput().writeInt(tableID);
        print("send to server: player or observer");
        getOutput().writeBoolean(asPlayer);
        print("send to server: player nick");
        getOutput().writeUTF(nick);
        print("send to server: password");
        getOutput().writeUTF(password);
        getOutput().flush();
        
        int servCode = getInput().readInt();
        print("connection info: " + ConnectionInfo.get(servCode).name());
        if (ConnectionInfo.get(servCode).name().startsWith("err_"))
        {
          throw new Error(ConnectionInfo.get(servCode).name());
        }
        if (servCode == ConnectionInfo.EVERYTHING_IS_OK.getValue())
        {
          return true;
        }
        

        return false;

      }
      catch (Error err)
      {
        LOG.error("Error exception, message: " + err.getMessage());
        return false;
      }
      catch (ConnectException ex)
      {
        LOG.error("ConnectException, message: " + ex.getMessage() + " object: " + ex);
        return false;
      }
      









      return false;
    }
    catch (UnknownHostException ex)
    {
      LOG.error("UnknownHostException, message: " + ex.getMessage() + " object: " + ex);
      return false;
    }
    catch (IOException ex)
    {
      LOG.error("UIOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  



  public void run()
  {
    print("running function: run()");
    boolean isOK = true;
    while (isOK)
    {
      try
      {
        String in = getInput().readUTF();
        print("input code: " + in);
        
        if (in.equals("#move"))
        {
          int beginX = getInput().readInt();
          int beginY = getInput().readInt();
          int endX = getInput().readInt();
          int endY = getInput().readInt();
          String promoted = getInput().readUTF();
          getGame().simulateMove(beginX, beginY, endX, endY, promoted);
          int tabNumber = JChessApp.getJavaChessView().getTabNumber(getGame());
          JTabbedPane gamesPane = JChessApp.getJavaChessView().getGamesPane();
          gamesPane.setForegroundAt(tabNumber, JChessTabbedPane.EVENT_COLOR);
          gamesPane.repaint();
        }
        else if (in.equals("#message"))
        {
          String str = getInput().readUTF();
          getGame().getChat().addMessage(str);
        }
        else if (in.equals("#settings"))
        {
          try
          {
            setSettings((Settings)getInput().readObject());
          }
          catch (ClassNotFoundException ex)
          {
            LOG.error("ClassNotFoundException, message: " + ex.getMessage() + " object: " + ex);
          }
          
          getGame().setSettings(getSettings());
          getGame().setClient(this);
          getGame().getChat().setClient(this);
          getGame().newGame();
          getGame().getChessboard().repaint();
        }
        else if (in.equals("#errorConnection"))
        {
          getGame().getChat().addMessage("** " + Settings.lang("error_connecting_one_of_player") + " **");
        }
        else if ((in.equals("#undoAsk")) && (!isIsObserver()))
        {
          int result = JOptionPane.showConfirmDialog(null, 
          
            Settings.lang("your_oponent_plase_to_undo_move_do_you_agree"), 
            Settings.lang("confirm_undo_move"), 0);
          


          if (result == 0)
          {
            getGame().getChessboard().undo();
            getGame().switchActive();
            sendUndoAnswerPositive();
          }
          else
          {
            sendUndoAnswerNegative();
          }
        }
        else if ((in.equals("#undoAnswerPositive")) && ((isWait4undoAnswer()) || (isIsObserver())))
        {
          setWait4undoAnswer(false);
          String lastMove = (String)getGame().getMoves().getMoves().get(getGame().getMoves().getMoves().size() - 1);
          getGame().getChat().addMessage("** " + Settings.lang("permision_ok_4_undo_move") + ": " + lastMove + "**");
          getGame().getChessboard().undo();
        }
        else if ((in.equals("#undoAnswerNegative")) && (isWait4undoAnswer()))
        {
          getGame().getChat().addMessage(Settings.lang("no_permision_4_undo_move"));
        }
      }
      catch (IOException ex)
      {
        isOK = false;
        getGame().getChat().addMessage("** " + Settings.lang("error_connecting_to_server") + " **");
        LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
      }
    }
  }
  


  public static void print(String str)
  {
    if (isPrintEnable)
    {
      LOG.debug("Client: " + str);
    }
  }
  


  public void sendMove(int beginX, int beginY, int endX, int endY, String promotedPiece)
  {
    print("running function: sendMove(" + beginX + ", " + beginY + ", " + endX + ", " + endY + ")");
    try
    {
      getOutput().writeUTF("#move");
      getOutput().writeInt(beginX);
      getOutput().writeInt(beginY);
      getOutput().writeInt(endX);
      getOutput().writeInt(endY);
      getOutput().writeUTF(promotedPiece != null ? promotedPiece : "");
      getOutput().flush();
    }
    catch (IOException ex)
    {
      LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  
  public void sendUndoAsk()
  {
    print("sendUndoAsk");
    try
    {
      setWait4undoAnswer(true);
      getOutput().writeUTF("#undoAsk");
      getOutput().flush();
    }
    catch (IOException ex)
    {
      LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  
  public void sendUndoAnswerPositive()
  {
    try
    {
      getOutput().writeUTF("#undoAnswerPositive");
      getOutput().flush();
    }
    catch (IOException ex)
    {
      LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  
  public void sendUndoAnswerNegative()
  {
    try
    {
      getOutput().writeUTF("#undoAnswerNegative");
      getOutput().flush();
    }
    catch (IOException ex)
    {
      LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  



  public void sendMassage(String str)
  {
    print("running function: sendMessage(" + str + ")");
    try
    {
      getOutput().writeUTF("#message");
      getOutput().writeUTF(str);
      getOutput().flush();
    }
    catch (IOException ex)
    {
      LOG.error("IOException, message: " + ex.getMessage() + " object: " + ex);
    }
  }
  



  public Game getGame()
  {
    return game;
  }
  



  public void setGame(Game game)
  {
    this.game = game;
  }
  



  public Settings getSettings()
  {
    return settings;
  }
  



  public void setSettings(Settings settings)
  {
    this.settings = settings;
  }
  



  public Socket getSocket()
  {
    return socket;
  }
  



  public void setSocket(Socket socket)
  {
    this.socket = socket;
  }
  



  public ObjectOutputStream getOutput()
  {
    return output;
  }
  



  public void setOutput(ObjectOutputStream output)
  {
    this.output = output;
  }
  



  public ObjectInputStream getInput()
  {
    return input;
  }
  



  public void setInput(ObjectInputStream input)
  {
    this.input = input;
  }
  



  public String getIp()
  {
    return ip;
  }
  



  public void setIp(String ip)
  {
    this.ip = ip;
  }
  



  public int getPort()
  {
    return port;
  }
  



  public void setPort(int port)
  {
    this.port = port;
  }
  



  public boolean isWait4undoAnswer()
  {
    return wait4undoAnswer;
  }
  



  public void setWait4undoAnswer(boolean wait4undoAnswer)
  {
    this.wait4undoAnswer = wait4undoAnswer;
  }
  



  public boolean isIsObserver()
  {
    return isObserver;
  }
  



  public void setIsObserver(boolean isObserver)
  {
    this.isObserver = isObserver;
  }
}
