package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;




































public abstract class DateLayout
  extends Layout
{
  public static final String NULL_DATE_FORMAT = "NULL";
  public static final String RELATIVE_TIME_DATE_FORMAT = "RELATIVE";
  protected FieldPosition pos = new FieldPosition(0);
  

  /**
   * @deprecated
   */
  public static final String DATE_FORMAT_OPTION = "DateFormat";
  

  /**
   * @deprecated
   */
  public static final String TIMEZONE_OPTION = "TimeZone";
  
  private String timeZoneID;
  
  private String dateFormatOption;
  
  protected DateFormat dateFormat;
  
  protected Date date = new Date();
  
  public DateLayout() {}
  
  /**
   * @deprecated
   */
  public String[] getOptionStrings() {
    return new String[] { "DateFormat", "TimeZone" };
  }
  

  /**
   * @deprecated
   */
  public void setOption(String option, String value)
  {
    if (option.equalsIgnoreCase("DateFormat")) {
      dateFormatOption = value.toUpperCase();
    } else if (option.equalsIgnoreCase("TimeZone")) {
      timeZoneID = value;
    }
  }
  






  public void setDateFormat(String dateFormat)
  {
    if (dateFormat != null) {
      dateFormatOption = dateFormat;
    }
    setDateFormat(dateFormatOption, TimeZone.getDefault());
  }
  



  public String getDateFormat()
  {
    return dateFormatOption;
  }
  




  public void setTimeZone(String timeZone)
  {
    timeZoneID = timeZone;
  }
  



  public String getTimeZone()
  {
    return timeZoneID;
  }
  
  public void activateOptions()
  {
    setDateFormat(dateFormatOption);
    if ((timeZoneID != null) && (dateFormat != null)) {
      dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
    }
  }
  
  public void dateFormat(StringBuffer buf, LoggingEvent event)
  {
    if (dateFormat != null) {
      date.setTime(timeStamp);
      dateFormat.format(date, buf, pos);
      buf.append(' ');
    }
  }
  




  public void setDateFormat(DateFormat dateFormat, TimeZone timeZone)
  {
    this.dateFormat = dateFormat;
    this.dateFormat.setTimeZone(timeZone);
  }
  














  public void setDateFormat(String dateFormatType, TimeZone timeZone)
  {
    if (dateFormatType == null) {
      dateFormat = null;
      return;
    }
    
    if (dateFormatType.equalsIgnoreCase("NULL")) {
      dateFormat = null;
    } else if (dateFormatType.equalsIgnoreCase("RELATIVE")) {
      dateFormat = new RelativeTimeDateFormat();
    } else if (dateFormatType.equalsIgnoreCase("ABSOLUTE"))
    {
      dateFormat = new AbsoluteTimeDateFormat(timeZone);
    } else if (dateFormatType.equalsIgnoreCase("DATE"))
    {
      dateFormat = new DateTimeDateFormat(timeZone);
    } else if (dateFormatType.equalsIgnoreCase("ISO8601"))
    {
      dateFormat = new ISO8601DateFormat(timeZone);
    } else {
      dateFormat = new SimpleDateFormat(dateFormatType);
      dateFormat.setTimeZone(timeZone);
    }
  }
}
