package org.apache.log4j.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.SyslogQuietWriter;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;





















































public class SyslogAppender
  extends AppenderSkeleton
{
  public static final int LOG_KERN = 0;
  public static final int LOG_USER = 8;
  public static final int LOG_MAIL = 16;
  public static final int LOG_DAEMON = 24;
  public static final int LOG_AUTH = 32;
  public static final int LOG_SYSLOG = 40;
  public static final int LOG_LPR = 48;
  public static final int LOG_NEWS = 56;
  public static final int LOG_UUCP = 64;
  public static final int LOG_CRON = 72;
  public static final int LOG_AUTHPRIV = 80;
  public static final int LOG_FTP = 88;
  public static final int LOG_LOCAL0 = 128;
  public static final int LOG_LOCAL1 = 136;
  public static final int LOG_LOCAL2 = 144;
  public static final int LOG_LOCAL3 = 152;
  public static final int LOG_LOCAL4 = 160;
  public static final int LOG_LOCAL5 = 168;
  public static final int LOG_LOCAL6 = 176;
  public static final int LOG_LOCAL7 = 184;
  protected static final int SYSLOG_HOST_OI = 0;
  protected static final int FACILITY_OI = 1;
  static final String TAB = "    ";
  int syslogFacility = 8;
  String facilityStr;
  boolean facilityPrinting = false;
  


  SyslogQuietWriter sqw;
  


  String syslogHost;
  

  private boolean header = false;
  



  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss ", Locale.ENGLISH);
  



  private String localHostname;
  



  private boolean layoutHeaderChecked = false;
  
  public SyslogAppender()
  {
    initSyslogFacilityStr();
  }
  
  public SyslogAppender(Layout layout, int syslogFacility)
  {
    this.layout = layout;
    this.syslogFacility = syslogFacility;
    initSyslogFacilityStr();
  }
  
  public SyslogAppender(Layout layout, String syslogHost, int syslogFacility)
  {
    this(layout, syslogFacility);
    setSyslogHost(syslogHost);
  }
  






  public synchronized void close()
  {
    closed = true;
    if (sqw != null) {
      try {
        if ((layoutHeaderChecked) && (layout != null) && (layout.getFooter() != null)) {
          sendLayoutMessage(layout.getFooter());
        }
        sqw.close();
        sqw = null;
      } catch (InterruptedIOException e) {
        Thread.currentThread().interrupt();
        sqw = null;
      } catch (IOException e) {
        sqw = null;
      }
    }
  }
  
  private void initSyslogFacilityStr()
  {
    facilityStr = getFacilityString(syslogFacility);
    
    if (facilityStr == null) {
      System.err.println("\"" + syslogFacility + "\" is an unknown syslog facility. Defaulting to \"USER\".");
      
      syslogFacility = 8;
      facilityStr = "user:";
    } else {
      facilityStr += ":";
    }
  }
  





  public static String getFacilityString(int syslogFacility)
  {
    switch (syslogFacility) {
    case 0:  return "kern";
    case 8:  return "user";
    case 16:  return "mail";
    case 24:  return "daemon";
    case 32:  return "auth";
    case 40:  return "syslog";
    case 48:  return "lpr";
    case 56:  return "news";
    case 64:  return "uucp";
    case 72:  return "cron";
    case 80:  return "authpriv";
    case 88:  return "ftp";
    case 128:  return "local0";
    case 136:  return "local1";
    case 144:  return "local2";
    case 152:  return "local3";
    case 160:  return "local4";
    case 168:  return "local5";
    case 176:  return "local6";
    case 184:  return "local7"; }
    return null;
  }
  













  public static int getFacility(String facilityName)
  {
    if (facilityName != null) {
      facilityName = facilityName.trim();
    }
    if ("KERN".equalsIgnoreCase(facilityName))
      return 0;
    if ("USER".equalsIgnoreCase(facilityName))
      return 8;
    if ("MAIL".equalsIgnoreCase(facilityName))
      return 16;
    if ("DAEMON".equalsIgnoreCase(facilityName))
      return 24;
    if ("AUTH".equalsIgnoreCase(facilityName))
      return 32;
    if ("SYSLOG".equalsIgnoreCase(facilityName))
      return 40;
    if ("LPR".equalsIgnoreCase(facilityName))
      return 48;
    if ("NEWS".equalsIgnoreCase(facilityName))
      return 56;
    if ("UUCP".equalsIgnoreCase(facilityName))
      return 64;
    if ("CRON".equalsIgnoreCase(facilityName))
      return 72;
    if ("AUTHPRIV".equalsIgnoreCase(facilityName))
      return 80;
    if ("FTP".equalsIgnoreCase(facilityName))
      return 88;
    if ("LOCAL0".equalsIgnoreCase(facilityName))
      return 128;
    if ("LOCAL1".equalsIgnoreCase(facilityName))
      return 136;
    if ("LOCAL2".equalsIgnoreCase(facilityName))
      return 144;
    if ("LOCAL3".equalsIgnoreCase(facilityName))
      return 152;
    if ("LOCAL4".equalsIgnoreCase(facilityName))
      return 160;
    if ("LOCAL5".equalsIgnoreCase(facilityName))
      return 168;
    if ("LOCAL6".equalsIgnoreCase(facilityName))
      return 176;
    if ("LOCAL7".equalsIgnoreCase(facilityName)) {
      return 184;
    }
    return -1;
  }
  

  private void splitPacket(String header, String packet)
  {
    int byteCount = packet.getBytes().length;
    




    if (byteCount <= 1019) {
      sqw.write(packet);
    } else {
      int split = header.length() + (packet.length() - header.length()) / 2;
      splitPacket(header, packet.substring(0, split) + "...");
      splitPacket(header, header + "..." + packet.substring(split));
    }
  }
  

  public void append(LoggingEvent event)
  {
    if (!isAsSevereAsThreshold(event.getLevel())) {
      return;
    }
    
    if (sqw == null) {
      errorHandler.error("No syslog host is set for SyslogAppedender named \"" + name + "\".");
      
      return;
    }
    
    if (!layoutHeaderChecked) {
      if ((layout != null) && (layout.getHeader() != null)) {
        sendLayoutMessage(layout.getHeader());
      }
      layoutHeaderChecked = true;
    }
    
    String hdr = getPacketHeader(timeStamp);
    String packet;
    String packet; if (layout == null) {
      packet = String.valueOf(event.getMessage());
    } else {
      packet = layout.format(event);
    }
    if ((facilityPrinting) || (hdr.length() > 0)) {
      StringBuffer buf = new StringBuffer(hdr);
      if (facilityPrinting) {
        buf.append(facilityStr);
      }
      buf.append(packet);
      packet = buf.toString();
    }
    
    sqw.setLevel(event.getLevel().getSyslogEquivalent());
    


    if (packet.length() > 256) {
      splitPacket(hdr, packet);
    } else {
      sqw.write(packet);
    }
    
    if ((layout == null) || (layout.ignoresThrowable())) {
      String[] s = event.getThrowableStrRep();
      if (s != null) {
        for (int i = 0; i < s.length; i++) {
          if (s[i].startsWith("\t")) {
            sqw.write(hdr + "    " + s[i].substring(1));
          } else {
            sqw.write(hdr + s[i]);
          }
        }
      }
    }
  }
  




  public void activateOptions()
  {
    if (header) {
      getLocalHostname();
    }
    if ((layout != null) && (layout.getHeader() != null)) {
      sendLayoutMessage(layout.getHeader());
    }
    layoutHeaderChecked = true;
  }
  





  public boolean requiresLayout()
  {
    return true;
  }
  









  public void setSyslogHost(String syslogHost)
  {
    sqw = new SyslogQuietWriter(new SyslogWriter(syslogHost), syslogFacility, errorHandler);
    

    this.syslogHost = syslogHost;
  }
  



  public String getSyslogHost()
  {
    return syslogHost;
  }
  









  public void setFacility(String facilityName)
  {
    if (facilityName == null) {
      return;
    }
    syslogFacility = getFacility(facilityName);
    if (syslogFacility == -1) {
      System.err.println("[" + facilityName + "] is an unknown syslog facility. Defaulting to [USER].");
      
      syslogFacility = 8;
    }
    
    initSyslogFacilityStr();
    

    if (sqw != null) {
      sqw.setSyslogFacility(syslogFacility);
    }
  }
  



  public String getFacility()
  {
    return getFacilityString(syslogFacility);
  }
  





  public void setFacilityPrinting(boolean on)
  {
    facilityPrinting = on;
  }
  



  public boolean getFacilityPrinting()
  {
    return facilityPrinting;
  }
  





  public final boolean getHeader()
  {
    return header;
  }
  




  public final void setHeader(boolean val)
  {
    header = val;
  }
  




  private String getLocalHostname()
  {
    if (localHostname == null) {
      try {
        InetAddress addr = InetAddress.getLocalHost();
        localHostname = addr.getHostName();
      } catch (UnknownHostException uhe) {
        localHostname = "UNKNOWN_HOST";
      }
    }
    return localHostname;
  }
  





  private String getPacketHeader(long timeStamp)
  {
    if (header) {
      StringBuffer buf = new StringBuffer(dateFormat.format(new Date(timeStamp)));
      
      if (buf.charAt(4) == '0') {
        buf.setCharAt(4, ' ');
      }
      buf.append(getLocalHostname());
      buf.append(' ');
      return buf.toString();
    }
    return "";
  }
  



  private void sendLayoutMessage(String msg)
  {
    if (sqw != null) {
      String packet = msg;
      String hdr = getPacketHeader(new Date().getTime());
      if ((facilityPrinting) || (hdr.length() > 0)) {
        StringBuffer buf = new StringBuffer(hdr);
        if (facilityPrinting) {
          buf.append(facilityStr);
        }
        buf.append(msg);
        packet = buf.toString();
      }
      sqw.setLevel(6);
      sqw.write(packet);
    }
  }
}
