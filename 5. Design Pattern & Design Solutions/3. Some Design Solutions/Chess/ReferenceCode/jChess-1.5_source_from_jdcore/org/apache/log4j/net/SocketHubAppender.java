package org.apache.log4j.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;































































































public class SocketHubAppender
  extends AppenderSkeleton
{
  static final int DEFAULT_PORT = 4560;
  private int port = 4560;
  private Vector oosList = new Vector();
  private ServerMonitor serverMonitor = null;
  private boolean locationInfo = false;
  private CyclicBuffer buffer = null;
  
  private String application;
  
  private boolean advertiseViaMulticastDNS;
  
  private ZeroConfSupport zeroConf;
  
  public static final String ZONE = "_log4j_obj_tcpaccept_appender.local.";
  
  private ServerSocket serverSocket;
  

  public SocketHubAppender() {}
  

  public SocketHubAppender(int _port)
  {
    port = _port;
    startServer();
  }
  


  public void activateOptions()
  {
    if (advertiseViaMulticastDNS) {
      zeroConf = new ZeroConfSupport("_log4j_obj_tcpaccept_appender.local.", port, getName());
      zeroConf.advertise();
    }
    startServer();
  }
  





  public synchronized void close()
  {
    if (closed) {
      return;
    }
    LogLog.debug("closing SocketHubAppender " + getName());
    closed = true;
    if (advertiseViaMulticastDNS) {
      zeroConf.unadvertise();
    }
    cleanUp();
    
    LogLog.debug("SocketHubAppender " + getName() + " closed");
  }
  




  public void cleanUp()
  {
    LogLog.debug("stopping ServerSocket");
    serverMonitor.stopMonitor();
    serverMonitor = null;
    

    LogLog.debug("closing client connections");
    while (oosList.size() != 0) {
      ObjectOutputStream oos = (ObjectOutputStream)oosList.elementAt(0);
      if (oos != null) {
        try {
          oos.close();
        } catch (InterruptedIOException e) {
          Thread.currentThread().interrupt();
          LogLog.error("could not close oos.", e);
        } catch (IOException e) {
          LogLog.error("could not close oos.", e);
        }
        
        oosList.removeElementAt(0);
      }
    }
  }
  


  public void append(LoggingEvent event)
  {
    if (event != null)
    {
      if (locationInfo) {
        event.getLocationInformation();
      }
      if (application != null) {
        event.setProperty("application", application);
      }
      event.getNDC();
      event.getThreadName();
      event.getMDCCopy();
      event.getRenderedMessage();
      event.getThrowableStrRep();
      
      if (buffer != null) {
        buffer.add(event);
      }
    }
    

    if ((event == null) || (oosList.size() == 0)) {
      return;
    }
    

    for (int streamCount = 0; streamCount < oosList.size(); streamCount++)
    {
      ObjectOutputStream oos = null;
      try {
        oos = (ObjectOutputStream)oosList.elementAt(streamCount);
      }
      catch (ArrayIndexOutOfBoundsException e) {}
      





      if (oos == null) {
        break;
      }
      try {
        oos.writeObject(event);
        oos.flush();
        


        oos.reset();
      }
      catch (IOException e) {
        if ((e instanceof InterruptedIOException)) {
          Thread.currentThread().interrupt();
        }
        
        oosList.removeElementAt(streamCount);
        LogLog.debug("dropped connection");
        

        streamCount--;
      }
    }
  }
  



  public boolean requiresLayout()
  {
    return false;
  }
  



  public void setPort(int _port)
  {
    port = _port;
  }
  




  public void setApplication(String lapp)
  {
    application = lapp;
  }
  



  public String getApplication()
  {
    return application;
  }
  


  public int getPort()
  {
    return port;
  }
  




  public void setBufferSize(int _bufferSize)
  {
    buffer = new CyclicBuffer(_bufferSize);
  }
  



  public int getBufferSize()
  {
    if (buffer == null) {
      return 0;
    }
    return buffer.getMaxSize();
  }
  





  public void setLocationInfo(boolean _locationInfo)
  {
    locationInfo = _locationInfo;
  }
  


  public boolean getLocationInfo()
  {
    return locationInfo;
  }
  
  public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS) {
    this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
  }
  
  public boolean isAdvertiseViaMulticastDNS() {
    return advertiseViaMulticastDNS;
  }
  


  private void startServer()
  {
    serverMonitor = new ServerMonitor(port, oosList);
  }
  




  protected ServerSocket createServerSocket(int socketPort)
    throws IOException
  {
    return new ServerSocket(socketPort);
  }
  

  private class ServerMonitor
    implements Runnable
  {
    private int port;
    
    private Vector oosList;
    
    private boolean keepRunning;
    
    private Thread monitorThread;
    
    public ServerMonitor(int _port, Vector _oosList)
    {
      port = _port;
      oosList = _oosList;
      keepRunning = true;
      monitorThread = new Thread(this);
      monitorThread.setDaemon(true);
      monitorThread.setName("SocketHubAppender-Monitor-" + port);
      monitorThread.start();
    }
    


    public synchronized void stopMonitor()
    {
      if (keepRunning) {
        LogLog.debug("server monitor thread shutting down");
        keepRunning = false;
        try {
          if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
          }
        }
        catch (IOException ioe) {}
        try {
          monitorThread.join();
        }
        catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        


        monitorThread = null;
        LogLog.debug("server monitor thread shut down");
      }
    }
    
    private void sendCachedEvents(ObjectOutputStream stream) throws IOException
    {
      if (buffer != null) {
        for (int i = 0; i < buffer.length(); i++) {
          stream.writeObject(buffer.get(i));
        }
        stream.flush();
        stream.reset();
      }
    }
    



    public void run()
    {
      serverSocket = null;
      try {
        serverSocket = createServerSocket(port);
        serverSocket.setSoTimeout(1000);
      }
      catch (Exception e) {
        if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
          Thread.currentThread().interrupt();
        }
        LogLog.error("exception setting timeout, shutting down server socket.", e);
        keepRunning = false;
        return;
      }
      try
      {
        try {
          serverSocket.setSoTimeout(1000);
        }
        catch (SocketException e) {
          LogLog.error("exception setting timeout, shutting down server socket.", e); return;
        }
        

        while (keepRunning) {
          Socket socket = null;
          try {
            socket = serverSocket.accept();

          }
          catch (InterruptedIOException e) {}catch (SocketException e)
          {

            LogLog.error("exception accepting socket, shutting down server socket.", e);
            keepRunning = false;
          }
          catch (IOException e) {
            LogLog.error("exception accepting socket.", e);
          }
          

          if (socket != null) {
            try {
              InetAddress remoteAddress = socket.getInetAddress();
              LogLog.debug("accepting connection from " + remoteAddress.getHostName() + " (" + remoteAddress.getHostAddress() + ")");
              


              ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
              if ((buffer != null) && (buffer.length() > 0)) {
                sendCachedEvents(oos);
              }
              

              oosList.addElement(oos);
            } catch (IOException e) {
              if ((e instanceof InterruptedIOException)) {
                Thread.currentThread().interrupt();
              }
              LogLog.error("exception creating output stream on socket.", e);
            }
          }
        }
        return;
      }
      finally {
        try {
          serverSocket.close();
        } catch (InterruptedIOException e) {
          Thread.currentThread().interrupt();
        }
        catch (IOException e) {}
      }
    }
  }
}
