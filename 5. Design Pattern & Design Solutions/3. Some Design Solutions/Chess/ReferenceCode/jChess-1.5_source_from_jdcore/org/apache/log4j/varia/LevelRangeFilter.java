package org.apache.log4j.varia;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

















































public class LevelRangeFilter
  extends Filter
{
  boolean acceptOnMatch = false;
  
  Level levelMin;
  
  Level levelMax;
  

  public LevelRangeFilter() {}
  
  public int decide(LoggingEvent event)
  {
    if ((levelMin != null) && 
      (!event.getLevel().isGreaterOrEqual(levelMin)))
    {
      return -1;
    }
    

    if ((levelMax != null) && 
      (event.getLevel().toInt() > levelMax.toInt()))
    {



      return -1;
    }
    

    if (acceptOnMatch)
    {

      return 1;
    }
    

    return 0;
  }
  



  public Level getLevelMax()
  {
    return levelMax;
  }
  



  public Level getLevelMin()
  {
    return levelMin;
  }
  



  public boolean getAcceptOnMatch()
  {
    return acceptOnMatch;
  }
  



  public void setLevelMax(Level levelMax)
  {
    this.levelMax = levelMax;
  }
  



  public void setLevelMin(Level levelMin)
  {
    this.levelMin = levelMin;
  }
  



  public void setAcceptOnMatch(boolean acceptOnMatch)
  {
    this.acceptOnMatch = acceptOnMatch;
  }
}
