package org.apache.log4j.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;










































































































public class SocketAppender
  extends AppenderSkeleton
{
  public static final int DEFAULT_PORT = 4560;
  static final int DEFAULT_RECONNECTION_DELAY = 30000;
  String remoteHost;
  public static final String ZONE = "_log4j_obj_tcpconnect_appender.local.";
  InetAddress address;
  int port = 4560;
  ObjectOutputStream oos;
  int reconnectionDelay = 30000;
  boolean locationInfo = false;
  
  private String application;
  
  private Connector connector;
  int counter = 0;
  

  private static final int RESET_FREQUENCY = 1;
  
  private boolean advertiseViaMulticastDNS;
  
  private ZeroConfSupport zeroConf;
  

  public SocketAppender() {}
  

  public SocketAppender(InetAddress address, int port)
  {
    this.address = address;
    remoteHost = address.getHostName();
    this.port = port;
    connect(address, port);
  }
  


  public SocketAppender(String host, int port)
  {
    this.port = port;
    address = getAddressByName(host);
    remoteHost = host;
    connect(address, port);
  }
  


  public void activateOptions()
  {
    if (advertiseViaMulticastDNS) {
      zeroConf = new ZeroConfSupport("_log4j_obj_tcpconnect_appender.local.", port, getName());
      zeroConf.advertise();
    }
    connect(address, port);
  }
  





  public synchronized void close()
  {
    if (closed) {
      return;
    }
    closed = true;
    if (advertiseViaMulticastDNS) {
      zeroConf.unadvertise();
    }
    
    cleanUp();
  }
  



  public void cleanUp()
  {
    if (oos != null) {
      try {
        oos.close();
      } catch (IOException e) {
        if ((e instanceof InterruptedIOException)) {
          Thread.currentThread().interrupt();
        }
        LogLog.error("Could not close oos.", e);
      }
      oos = null;
    }
    if (connector != null)
    {
      connector.interrupted = true;
      connector = null;
    }
  }
  
  void connect(InetAddress address, int port) {
    if (this.address == null) {
      return;
    }
    try {
      cleanUp();
      oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
    } catch (IOException e) {
      if ((e instanceof InterruptedIOException)) {
        Thread.currentThread().interrupt();
      }
      String msg = "Could not connect to remote log4j server at [" + address.getHostName() + "].";
      
      if (reconnectionDelay > 0) {
        msg = msg + " We will try again later.";
        fireConnector();
      } else {
        msg = msg + " We are not retrying.";
        errorHandler.error(msg, e, 0);
      }
      LogLog.error(msg);
    }
  }
  
  public void append(LoggingEvent event)
  {
    if (event == null) {
      return;
    }
    if (address == null) {
      errorHandler.error("No remote host is set for SocketAppender named \"" + name + "\".");
      
      return;
    }
    
    if (oos != null) {
      try
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
        
        oos.writeObject(event);
        
        oos.flush();
        if (++counter >= 1) {
          counter = 0;
          


          oos.reset();
        }
      } catch (IOException e) {
        if ((e instanceof InterruptedIOException)) {
          Thread.currentThread().interrupt();
        }
        oos = null;
        LogLog.warn("Detected problem with connection: " + e);
        if (reconnectionDelay > 0) {
          fireConnector();
        } else {
          errorHandler.error("Detected problem with connection, not reconnecting.", e, 0);
        }
      }
    }
  }
  
  public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS)
  {
    this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
  }
  
  public boolean isAdvertiseViaMulticastDNS() {
    return advertiseViaMulticastDNS;
  }
  
  void fireConnector() {
    if (connector == null) {
      LogLog.debug("Starting a new connector thread.");
      connector = new Connector();
      connector.setDaemon(true);
      connector.setPriority(1);
      connector.start();
    }
  }
  
  static InetAddress getAddressByName(String host)
  {
    try {
      return InetAddress.getByName(host);
    } catch (Exception e) {
      if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
        Thread.currentThread().interrupt();
      }
      LogLog.error("Could not find address of [" + host + "].", e); }
    return null;
  }
  




  public boolean requiresLayout()
  {
    return false;
  }
  




  public void setRemoteHost(String host)
  {
    address = getAddressByName(host);
    remoteHost = host;
  }
  


  public String getRemoteHost()
  {
    return remoteHost;
  }
  



  public void setPort(int port)
  {
    this.port = port;
  }
  


  public int getPort()
  {
    return port;
  }
  




  public void setLocationInfo(boolean locationInfo)
  {
    this.locationInfo = locationInfo;
  }
  


  public boolean getLocationInfo()
  {
    return locationInfo;
  }
  





  public void setApplication(String lapp)
  {
    application = lapp;
  }
  



  public String getApplication()
  {
    return application;
  }
  








  public void setReconnectionDelay(int delay)
  {
    reconnectionDelay = delay;
  }
  


  public int getReconnectionDelay()
  {
    return reconnectionDelay;
  }
  











  class Connector
    extends Thread
  {
    boolean interrupted = false;
    
    Connector() {}
    
    public void run() {
      while (!interrupted) {
        try {
          sleep(reconnectionDelay);
          LogLog.debug("Attempting connection to " + address.getHostName());
          Socket socket = new Socket(address, port);
          synchronized (this) {
            oos = new ObjectOutputStream(socket.getOutputStream());
            connector = null;
            LogLog.debug("Connection established. Exiting connector thread.");
          }
        }
        catch (InterruptedException e) {
          LogLog.debug("Connector interrupted. Leaving loop.");
          return;
        } catch (ConnectException e) {
          LogLog.debug("Remote host " + address.getHostName() + " refused connection.");
        }
        catch (IOException e) {
          if ((e instanceof InterruptedIOException)) {
            Thread.currentThread().interrupt();
          }
          LogLog.debug("Could not connect to " + address.getHostName() + ". Exception is " + e);
        }
      }
    }
  }
}
