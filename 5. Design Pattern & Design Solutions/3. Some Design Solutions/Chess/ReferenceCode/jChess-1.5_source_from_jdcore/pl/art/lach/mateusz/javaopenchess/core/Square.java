package pl.art.lach.mateusz.javaopenchess.core;

import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;


































public class Square
{
  protected int pozX;
  protected int pozY;
  public Piece piece = null;
  
  public Square(int pozX, int pozY, Piece piece)
  {
    this.pozX = pozX;
    this.pozY = pozY;
    this.piece = piece;
  }
  

  public Square(Square square)
  {
    pozX = pozX;
    pozY = pozY;
    piece = piece;
  }
  
  public Square clone(Square square)
  {
    return new Square(square);
  }
  
  public void setPiece(Piece piece)
  {
    this.piece = piece;
    if (null != this.piece)
    {
      this.piece.setSquare(this);
    }
  }
  



  public int getPozX()
  {
    return pozX;
  }
  



  public void setPozX(int pozX)
  {
    this.pozX = pozX;
  }
  



  public int getPozY()
  {
    return pozY;
  }
  



  public void setPozY(int pozY)
  {
    this.pozY = pozY;
  }
  
  public Piece getPiece() {
    return piece;
  }
  
  public boolean isEmptyOrSamePiece(Piece piece)
  {
    return (null == this.piece) || (this.piece == piece);
  }
  
  public String getAlgebraicNotation()
  {
    String letter = String.valueOf((char)(pozX + 97));
    String result = letter + (Math.abs(7 - pozY) + 1);
    return result;
  }
}
