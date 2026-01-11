package pl.art.lach.mateusz.javaopenchess.core.exceptions;







public class ReadGameError
  extends Exception
{
  private String message;
  





  private String move;
  





  public ReadGameError(String message)
  {
    this.message = message;
  }
  
  public ReadGameError(String message, String move)
  {
    this(message);
    this.move = move;
  }
  




  public String getMessage()
  {
    return message;
  }
  



  public void setMessage(String message)
  {
    this.message = message;
  }
  



  public String getMove()
  {
    return move;
  }
  



  public void setMove(String move)
  {
    this.move = move;
  }
}
