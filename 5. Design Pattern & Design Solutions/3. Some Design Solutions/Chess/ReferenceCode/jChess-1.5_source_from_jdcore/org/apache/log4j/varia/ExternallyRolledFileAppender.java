package org.apache.log4j.varia;

import org.apache.log4j.RollingFileAppender;























































public class ExternallyRolledFileAppender
  extends RollingFileAppender
{
  public static final String ROLL_OVER = "RollOver";
  public static final String OK = "OK";
  int port = 0;
  


  HUP hup;
  



  public ExternallyRolledFileAppender() {}
  



  public void setPort(int port)
  {
    this.port = port;
  }
  



  public int getPort()
  {
    return port;
  }
  



  public void activateOptions()
  {
    super.activateOptions();
    if (port != 0) {
      if (hup != null) {
        hup.interrupt();
      }
      hup = new HUP(this, port);
      hup.setDaemon(true);
      hup.start();
    }
  }
}
