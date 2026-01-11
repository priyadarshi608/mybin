package org.apache.log4j.varia;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;





































public class StringMatchFilter
  extends Filter
{
  /**
   * @deprecated
   */
  public static final String STRING_TO_MATCH_OPTION = "StringToMatch";
  /**
   * @deprecated
   */
  public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
  boolean acceptOnMatch = true;
  String stringToMatch;
  
  public StringMatchFilter() {}
  
  /**
   * @deprecated
   */
  public String[] getOptionStrings() {
    return new String[] { "StringToMatch", "AcceptOnMatch" };
  }
  


  /**
   * @deprecated
   */
  public void setOption(String key, String value)
  {
    if (key.equalsIgnoreCase("StringToMatch")) {
      stringToMatch = value;
    } else if (key.equalsIgnoreCase("AcceptOnMatch")) {
      acceptOnMatch = OptionConverter.toBoolean(value, acceptOnMatch);
    }
  }
  
  public void setStringToMatch(String s)
  {
    stringToMatch = s;
  }
  
  public String getStringToMatch()
  {
    return stringToMatch;
  }
  
  public void setAcceptOnMatch(boolean acceptOnMatch)
  {
    this.acceptOnMatch = acceptOnMatch;
  }
  
  public boolean getAcceptOnMatch()
  {
    return acceptOnMatch;
  }
  



  public int decide(LoggingEvent event)
  {
    String msg = event.getRenderedMessage();
    
    if ((msg == null) || (stringToMatch == null)) {
      return 0;
    }
    
    if (msg.indexOf(stringToMatch) == -1) {
      return 0;
    }
    if (acceptOnMatch) {
      return 1;
    }
    return -1;
  }
}
