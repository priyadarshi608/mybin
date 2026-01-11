package pl.art.lach.mateusz.javaopenchess.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

























class SClient
  implements Runnable
{
  private Socket s;
  public ObjectInputStream input;
  public ObjectOutputStream output;
  public String nick;
  private Table table;
  protected boolean wait4undoAnswer = false;
  
  SClient(Socket s, ObjectInputStream input, ObjectOutputStream output, String nick, Table table)
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
    boolean isOK = true;
    while (isOK)
    {
      try
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
        else if (in.equals("#message"))
        {
          String str = input.readUTF();
          
          table.sendMessageToAll(nick + ": " + str);
        }
        else if ((in.equals("#undoAsk")) || (in.equals("#undoAnswerNegative")))
        {
          table.sendToAll(this, in);
        }
        else if (in.equals("#undoAnswerPositive"))
        {
          table.sendUndoToAll(this, in);
        }
      }
      catch (IOException ex)
      {
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        isOK = false;
        try
        {
          table.sendErrorConnectionToOther(this);
        }
        catch (IOException ex1)
        {
          Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex1);
        }
      }
    }
  }
}
