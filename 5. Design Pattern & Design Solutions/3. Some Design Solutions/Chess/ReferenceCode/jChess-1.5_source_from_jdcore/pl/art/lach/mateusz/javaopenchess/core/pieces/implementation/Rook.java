package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.RookBehavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;

































public class Rook
  extends Piece
{
  protected boolean wasMotioned = false;
  
  public Rook(Chessboard chessboard, Player player)
  {
    super(chessboard, player);
    value = 5;
    symbol = "R";
    addBehavior(new RookBehavior(this));
  }
  



  public boolean getWasMotioned()
  {
    return wasMotioned;
  }
  



  public void setWasMotioned(boolean wasMotioned)
  {
    this.wasMotioned = wasMotioned;
  }
}
