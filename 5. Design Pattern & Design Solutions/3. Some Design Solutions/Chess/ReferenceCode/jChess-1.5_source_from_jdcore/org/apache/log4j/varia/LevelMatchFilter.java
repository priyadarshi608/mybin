package org.apache.log4j.varia;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;



































public class LevelMatchFilter
  extends Filter
{
  boolean acceptOnMatch = true;
  
  Level levelToMatch;
  

  public LevelMatchFilter() {}
  
  public void setLevelToMatch(String level)
  {
    levelToMatch = OptionConverter.toLevel(level, null);
  }
  
  public String getLevelToMatch()
  {
    return levelToMatch == null ? null : levelToMatch.toString();
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
    if (levelToMatch == null) {
      return 0;
    }
    
    boolean matchOccured = false;
    if (levelToMatch.equals(event.getLevel())) {
      matchOccured = true;
    }
    
    if (matchOccured) {
      if (acceptOnMatch) {
        return 1;
      }
      return -1;
    }
    return 0;
  }
}
