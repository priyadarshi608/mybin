package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;




































public class PatternParser
{
  private static final char ESCAPE_CHAR = '%';
  private static final int LITERAL_STATE = 0;
  private static final int CONVERTER_STATE = 1;
  private static final int DOT_STATE = 3;
  private static final int MIN_STATE = 4;
  private static final int MAX_STATE = 5;
  static final int FULL_LOCATION_CONVERTER = 1000;
  static final int METHOD_LOCATION_CONVERTER = 1001;
  static final int CLASS_LOCATION_CONVERTER = 1002;
  static final int LINE_LOCATION_CONVERTER = 1003;
  static final int FILE_LOCATION_CONVERTER = 1004;
  static final int RELATIVE_TIME_CONVERTER = 2000;
  static final int THREAD_CONVERTER = 2001;
  static final int LEVEL_CONVERTER = 2002;
  static final int NDC_CONVERTER = 2003;
  static final int MESSAGE_CONVERTER = 2004;
  int state;
  protected StringBuffer currentLiteral = new StringBuffer(32);
  protected int patternLength;
  protected int i;
  PatternConverter head;
  PatternConverter tail;
  protected FormattingInfo formattingInfo = new FormattingInfo();
  protected String pattern;
  
  public PatternParser(String pattern)
  {
    this.pattern = pattern;
    patternLength = pattern.length();
    state = 0;
  }
  
  private void addToList(PatternConverter pc)
  {
    if (head == null) {
      head = (this.tail = pc);
    } else {
      tail.next = pc;
      tail = pc;
    }
  }
  
  protected String extractOption()
  {
    if ((i < patternLength) && (pattern.charAt(i) == '{')) {
      int end = pattern.indexOf('}', i);
      if (end > i) {
        String r = pattern.substring(i + 1, end);
        i = (end + 1);
        return r;
      }
    }
    return null;
  }
  




  protected int extractPrecisionOption()
  {
    String opt = extractOption();
    int r = 0;
    if (opt != null) {
      try {
        r = Integer.parseInt(opt);
        if (r <= 0) {
          LogLog.error("Precision option (" + opt + ") isn't a positive integer.");
          
          r = 0;
        }
      }
      catch (NumberFormatException e) {
        LogLog.error("Category option \"" + opt + "\" not a decimal integer.", e);
      }
    }
    return r;
  }
  

  public PatternConverter parse()
  {
    i = 0;
    while (i < patternLength) {
      char c = pattern.charAt(i++);
      switch (state)
      {
      case 0: 
        if (i == patternLength) {
          currentLiteral.append(c);

        }
        else if (c == '%')
        {
          switch (pattern.charAt(i)) {
          case '%': 
            currentLiteral.append(c);
            i += 1;
            break;
          case 'n': 
            currentLiteral.append(Layout.LINE_SEP);
            i += 1;
            break;
          default: 
            if (currentLiteral.length() != 0) {
              addToList(new LiteralPatternConverter(currentLiteral.toString()));
            }
            


            currentLiteral.setLength(0);
            currentLiteral.append(c);
            state = 1;
            formattingInfo.reset();break;
          }
          
        } else {
          currentLiteral.append(c);
        }
        break;
      case 1: 
        currentLiteral.append(c);
        switch (c) {
        case '-': 
          formattingInfo.leftAlign = true;
          break;
        case '.': 
          state = 3;
          break;
        default: 
          if ((c >= '0') && (c <= '9')) {
            formattingInfo.min = (c - '0');
            state = 4;
          }
          else {
            finalizeConverter(c); }
          break; }
        break;
      case 4: 
        currentLiteral.append(c);
        if ((c >= '0') && (c <= '9')) {
          formattingInfo.min = (formattingInfo.min * 10 + (c - '0'));
        } else if (c == '.') {
          state = 3;
        } else {
          finalizeConverter(c);
        }
        break;
      case 3: 
        currentLiteral.append(c);
        if ((c >= '0') && (c <= '9')) {
          formattingInfo.max = (c - '0');
          state = 5;
        }
        else {
          LogLog.error("Error occured in position " + i + ".\n Was expecting digit, instead got char \"" + c + "\".");
          
          state = 0;
        }
        break;
      case 5: 
        currentLiteral.append(c);
        if ((c >= '0') && (c <= '9')) {
          formattingInfo.max = (formattingInfo.max * 10 + (c - '0'));
        } else {
          finalizeConverter(c);
          state = 0;
        }
        break;
      }
    }
    if (currentLiteral.length() != 0) {
      addToList(new LiteralPatternConverter(currentLiteral.toString()));
    }
    
    return head;
  }
  
  protected void finalizeConverter(char c)
  {
    PatternConverter pc = null;
    switch (c) {
    case 'c': 
      pc = new CategoryPatternConverter(formattingInfo, extractPrecisionOption());
      


      currentLiteral.setLength(0);
      break;
    case 'C': 
      pc = new ClassNamePatternConverter(formattingInfo, extractPrecisionOption());
      


      currentLiteral.setLength(0);
      break;
    case 'd': 
      String dateFormatStr = "ISO8601";
      
      String dOpt = extractOption();
      if (dOpt != null)
        dateFormatStr = dOpt;
      DateFormat df;
      DateFormat df; if (dateFormatStr.equalsIgnoreCase("ISO8601"))
      {
        df = new ISO8601DateFormat(); } else { DateFormat df;
        if (dateFormatStr.equalsIgnoreCase("ABSOLUTE"))
        {
          df = new AbsoluteTimeDateFormat(); } else { DateFormat df;
          if (dateFormatStr.equalsIgnoreCase("DATE"))
          {
            df = new DateTimeDateFormat();
          } else {
            try {
              df = new SimpleDateFormat(dateFormatStr);
            }
            catch (IllegalArgumentException e) {
              LogLog.error("Could not instantiate SimpleDateFormat with " + dateFormatStr, e);
              
              df = (DateFormat)OptionConverter.instantiateByClassName("org.apache.log4j.helpers.ISO8601DateFormat", DateFormat.class, null);
            }
          }
        }
      }
      pc = new DatePatternConverter(formattingInfo, df);
      

      currentLiteral.setLength(0);
      break;
    case 'F': 
      pc = new LocationPatternConverter(formattingInfo, 1004);
      


      currentLiteral.setLength(0);
      break;
    case 'l': 
      pc = new LocationPatternConverter(formattingInfo, 1000);
      


      currentLiteral.setLength(0);
      break;
    case 'L': 
      pc = new LocationPatternConverter(formattingInfo, 1003);
      


      currentLiteral.setLength(0);
      break;
    case 'm': 
      pc = new BasicPatternConverter(formattingInfo, 2004);
      

      currentLiteral.setLength(0);
      break;
    case 'M': 
      pc = new LocationPatternConverter(formattingInfo, 1001);
      


      currentLiteral.setLength(0);
      break;
    case 'p': 
      pc = new BasicPatternConverter(formattingInfo, 2002);
      

      currentLiteral.setLength(0);
      break;
    case 'r': 
      pc = new BasicPatternConverter(formattingInfo, 2000);
      


      currentLiteral.setLength(0);
      break;
    case 't': 
      pc = new BasicPatternConverter(formattingInfo, 2001);
      

      currentLiteral.setLength(0);
      break;
    













    case 'x': 
      pc = new BasicPatternConverter(formattingInfo, 2003);
      
      currentLiteral.setLength(0);
      break;
    case 'X': 
      String xOpt = extractOption();
      pc = new MDCPatternConverter(formattingInfo, xOpt);
      currentLiteral.setLength(0);
      break;
    case 'D': case 'E': case 'G': case 'H': case 'I': case 'J': case 'K': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'Y': case 'Z': case '[': case '\\': case ']': case '^': case '_': case '`': case 'a': case 'b': case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k': case 'n': case 'o': case 'q': case 's': case 'u': case 'v': case 'w': default: 
      LogLog.error("Unexpected char [" + c + "] at position " + i + " in conversion patterrn.");
      
      pc = new LiteralPatternConverter(currentLiteral.toString());
      currentLiteral.setLength(0);
    }
    
    addConverter(pc);
  }
  
  protected void addConverter(PatternConverter pc)
  {
    currentLiteral.setLength(0);
    
    addToList(pc);
    
    state = 0;
    
    formattingInfo.reset();
  }
  

  private static class BasicPatternConverter
    extends PatternConverter
  {
    int type;
    
    BasicPatternConverter(FormattingInfo formattingInfo, int type)
    {
      super();
      this.type = type;
    }
    
    public String convert(LoggingEvent event)
    {
      switch (type) {
      case 2000: 
        return Long.toString(timeStamp - LoggingEvent.getStartTime());
      case 2001: 
        return event.getThreadName();
      case 2002: 
        return event.getLevel().toString();
      case 2003: 
        return event.getNDC();
      case 2004: 
        return event.getRenderedMessage();
      }
      return null;
    }
  }
  
  private static class LiteralPatternConverter extends PatternConverter
  {
    private String literal;
    
    LiteralPatternConverter(String value) {
      literal = value;
    }
    

    public final void format(StringBuffer sbuf, LoggingEvent event)
    {
      sbuf.append(literal);
    }
    
    public String convert(LoggingEvent event)
    {
      return literal;
    }
  }
  
  private static class DatePatternConverter extends PatternConverter {
    private DateFormat df;
    private Date date;
    
    DatePatternConverter(FormattingInfo formattingInfo, DateFormat df) {
      super();
      date = new Date();
      this.df = df;
    }
    
    public String convert(LoggingEvent event)
    {
      date.setTime(timeStamp);
      String converted = null;
      try {
        converted = df.format(date);
      }
      catch (Exception ex) {
        LogLog.error("Error occured while converting date.", ex);
      }
      return converted;
    }
  }
  
  private static class MDCPatternConverter extends PatternConverter {
    private String key;
    
    MDCPatternConverter(FormattingInfo formattingInfo, String key) {
      super();
      this.key = key;
    }
    
    public String convert(LoggingEvent event)
    {
      if (key == null) {
        StringBuffer buf = new StringBuffer("{");
        Map properties = event.getProperties();
        if (properties.size() > 0) {
          Object[] keys = properties.keySet().toArray();
          Arrays.sort(keys);
          for (int i = 0; i < keys.length; i++) {
            buf.append('{');
            buf.append(keys[i]);
            buf.append(',');
            buf.append(properties.get(keys[i]));
            buf.append('}');
          }
        }
        buf.append('}');
        return buf.toString();
      }
      Object val = event.getMDC(key);
      if (val == null) {
        return null;
      }
      return val.toString();
    }
  }
  
  private class LocationPatternConverter
    extends PatternConverter
  {
    int type;
    
    LocationPatternConverter(FormattingInfo formattingInfo, int type)
    {
      super();
      this.type = type;
    }
    
    public String convert(LoggingEvent event)
    {
      LocationInfo locationInfo = event.getLocationInformation();
      switch (type) {
      case 1000: 
        return fullInfo;
      case 1001: 
        return locationInfo.getMethodName();
      case 1003: 
        return locationInfo.getLineNumber();
      case 1004: 
        return locationInfo.getFileName(); }
      return null;
    }
  }
  
  private static abstract class NamedPatternConverter extends PatternConverter
  {
    int precision;
    
    NamedPatternConverter(FormattingInfo formattingInfo, int precision) {
      super();
      this.precision = precision;
    }
    

    abstract String getFullyQualifiedName(LoggingEvent paramLoggingEvent);
    
    public String convert(LoggingEvent event)
    {
      String n = getFullyQualifiedName(event);
      if (precision <= 0) {
        return n;
      }
      int len = n.length();
      



      int end = len - 1;
      for (int i = precision; i > 0; i--) {
        end = n.lastIndexOf('.', end - 1);
        if (end == -1)
          return n;
      }
      return n.substring(end + 1, len);
    }
  }
  
  private class ClassNamePatternConverter extends PatternParser.NamedPatternConverter
  {
    ClassNamePatternConverter(FormattingInfo formattingInfo, int precision)
    {
      super(precision);
    }
    
    String getFullyQualifiedName(LoggingEvent event) {
      return event.getLocationInformation().getClassName();
    }
  }
  
  private class CategoryPatternConverter extends PatternParser.NamedPatternConverter
  {
    CategoryPatternConverter(FormattingInfo formattingInfo, int precision) {
      super(precision);
    }
    
    String getFullyQualifiedName(LoggingEvent event) {
      return event.getLoggerName();
    }
  }
}
