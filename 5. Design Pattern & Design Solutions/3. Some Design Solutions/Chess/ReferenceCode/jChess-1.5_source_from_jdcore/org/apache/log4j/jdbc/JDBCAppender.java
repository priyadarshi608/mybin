package org.apache.log4j.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
































































public class JDBCAppender
  extends AppenderSkeleton
  implements Appender
{
  protected String databaseURL = "jdbc:odbc:myDB";
  



  protected String databaseUser = "me";
  



  protected String databasePassword = "mypassword";
  







  protected Connection connection = null;
  









  protected String sqlStatement = "";
  




  protected int bufferSize = 1;
  


  protected ArrayList buffer;
  


  protected ArrayList removes;
  


  private boolean locationInfo = false;
  
  public JDBCAppender()
  {
    buffer = new ArrayList(bufferSize);
    removes = new ArrayList(bufferSize);
  }
  






  public boolean getLocationInfo()
  {
    return locationInfo;
  }
  













  public void setLocationInfo(boolean flag)
  {
    locationInfo = flag;
  }
  



  public void append(LoggingEvent event)
  {
    event.getNDC();
    event.getThreadName();
    
    event.getMDCCopy();
    if (locationInfo) {
      event.getLocationInformation();
    }
    event.getRenderedMessage();
    event.getThrowableStrRep();
    buffer.add(event);
    
    if (buffer.size() >= bufferSize) {
      flushBuffer();
    }
  }
  






  protected String getLogStatement(LoggingEvent event)
  {
    return getLayout().format(event);
  }
  







  protected void execute(String sql)
    throws SQLException
  {
    Connection con = null;
    Statement stmt = null;
    try
    {
      con = getConnection();
      
      stmt = con.createStatement();
      stmt.executeUpdate(sql);
    } finally {
      if (stmt != null) {
        stmt.close();
      }
      closeConnection(con);
    }
  }
  








  protected void closeConnection(Connection con) {}
  







  protected Connection getConnection()
    throws SQLException
  {
    if (!DriverManager.getDrivers().hasMoreElements()) {
      setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
    }
    if (connection == null) {
      connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
    }
    

    return connection;
  }
  




  public void close()
  {
    flushBuffer();
    try
    {
      if ((connection != null) && (!connection.isClosed()))
        connection.close();
    } catch (SQLException e) {
      errorHandler.error("Error closing connection", e, 0);
    }
    closed = true;
  }
  







  public void flushBuffer()
  {
    removes.ensureCapacity(buffer.size());
    for (Iterator i = buffer.iterator(); i.hasNext();) {
      LoggingEvent logEvent = (LoggingEvent)i.next();
      try {
        String sql = getLogStatement(logEvent);
        execute(sql);
      }
      catch (SQLException e) {
        errorHandler.error("Failed to excute sql", e, 2);
      }
      finally {
        removes.add(logEvent);
      }
    }
    

    buffer.removeAll(removes);
    

    removes.clear();
  }
  

  public void finalize()
  {
    close();
  }
  



  public boolean requiresLayout()
  {
    return true;
  }
  



  public void setSql(String s)
  {
    sqlStatement = s;
    if (getLayout() == null) {
      setLayout(new PatternLayout(s));
    }
    else {
      ((PatternLayout)getLayout()).setConversionPattern(s);
    }
  }
  



  public String getSql()
  {
    return sqlStatement;
  }
  
  public void setUser(String user)
  {
    databaseUser = user;
  }
  
  public void setURL(String url)
  {
    databaseURL = url;
  }
  
  public void setPassword(String password)
  {
    databasePassword = password;
  }
  
  public void setBufferSize(int newBufferSize)
  {
    bufferSize = newBufferSize;
    buffer.ensureCapacity(bufferSize);
    removes.ensureCapacity(bufferSize);
  }
  
  public String getUser()
  {
    return databaseUser;
  }
  
  public String getURL()
  {
    return databaseURL;
  }
  
  public String getPassword()
  {
    return databasePassword;
  }
  
  public int getBufferSize()
  {
    return bufferSize;
  }
  



  public void setDriver(String driverClass)
  {
    try
    {
      Class.forName(driverClass);
    } catch (Exception e) {
      errorHandler.error("Failed to load driver", e, 0);
    }
  }
}
