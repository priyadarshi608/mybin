package org.apache.log4j.varia;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.helpers.LogLog;



































































































class HUP
  extends Thread
{
  int port;
  ExternallyRolledFileAppender er;
  
  HUP(ExternallyRolledFileAppender er, int port)
  {
    this.er = er;
    this.port = port;
  }
  
  public void run()
  {
    while (!isInterrupted()) {
      try {
        ServerSocket serverSocket = new ServerSocket(port);
        for (;;) {
          Socket socket = serverSocket.accept();
          LogLog.debug("Connected to client at " + socket.getInetAddress());
          new Thread(new HUPNode(socket, er), "ExternallyRolledFileAppender-HUP").start();
        }
      } catch (InterruptedIOException e) {
        Thread.currentThread().interrupt();
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (RuntimeException e) {
        e.printStackTrace();
      }
    }
  }
}
