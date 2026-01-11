package pl.art.lach.mateusz.javaopenchess.core.moves;

import pl.art.lach.mateusz.javaopenchess.core.Colors;


















public enum Castling
{
  NONE("", new int[4], new int[4]), 
  
  SHORT_CASTLING("0-0", new int[] { 4, 7, 6, 7 }, new int[] { 4, 0, 6, 0 }), 
  
  LONG_CASTLING("0-0-0", new int[] { 4, 7, 2, 7 }, new int[] { 4, 0, 2, 0 });
  

  protected String symbol;
  
  protected int[] whiteMove;
  protected int[] blackMove;
  
  private Castling(String symbol, int[] whiteMove, int[] blackMove)
  {
    this.symbol = symbol;
    this.whiteMove = whiteMove;
    this.blackMove = blackMove;
  }
  
  public String getSymbol()
  {
    return symbol;
  }
  
  public int[] getMove(Colors color)
  {
    if (Colors.BLACK == color)
    {
      return blackMove;
    }
    

    return whiteMove;
  }
  


  public static boolean isCastling(String moveInPGN)
  {
    return (moveInPGN.equals(SHORT_CASTLING.getSymbol())) || (moveInPGN.equals(LONG_CASTLING.getSymbol()));
  }
}
