package pl.art.lach.mateusz.javaopenchess.core;

import pl.art.lach.mateusz.javaopenchess.core.players.Player;


























public class Clock
{
  private int timeLeft;
  private Player player;
  
  Clock()
  {
    init(timeLeft);
  }
  




  Clock(int time)
  {
    init(time);
  }
  



  public final void init(int time)
  {
    timeLeft = time;
  }
  



  public boolean decrement()
  {
    if (timeLeft > 0)
    {
      timeLeft -= 1;
      return true;
    }
    return false;
  }
  



  public void pause() {}
  


  public int getLeftTime()
  {
    return timeLeft;
  }
  



  public void setPlayer(Player player)
  {
    this.player = player;
  }
  



  public Player getPlayer()
  {
    return player;
  }
  



  public String prepareString()
  {
    String strMin = "";
    Integer time_min = new Integer(getLeftTime() / 60);
    Integer time_sec = new Integer(getLeftTime() % 60);
    if (time_min.intValue() < 10)
    {
      strMin = "0" + time_min.toString();
    }
    else
    {
      strMin = time_min.toString();
    }
    String result = strMin + ":";
    if (time_sec.intValue() < 10)
    {
      result = result + "0" + time_sec.toString();
    }
    else
    {
      result = result + time_sec.toString();
    }
    
    return result;
  }
}
