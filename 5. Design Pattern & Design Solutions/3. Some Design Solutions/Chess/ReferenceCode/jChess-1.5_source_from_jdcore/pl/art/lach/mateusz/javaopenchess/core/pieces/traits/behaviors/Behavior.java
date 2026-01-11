package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
















public abstract class Behavior
{
  protected Piece piece;
  
  public Behavior(Piece piece)
  {
    this.piece = piece;
  }
  







  public abstract Set<Square> getSquaresInRange();
  







  public Set<Square> getLegalMoves()
  {
    King ourKing = piece.getPlayer().getColor() == Colors.WHITE ? piece.getChessboard().getKingWhite() : piece.getChessboard().getKingBlack();
    

    King oponentsKing = piece.getPlayer().getColor() == Colors.WHITE ? piece.getChessboard().getKingBlack() : piece.getChessboard().getKingWhite();
    
    Set<Square> result = new HashSet();
    for (Square sq : getSquaresInRange())
    {
      if (canMove(piece, sq, ourKing, oponentsKing))
      {
        result.add(sq);
      }
    }
    return result;
  }
  


  private boolean canMove(Piece piece, Square sq, King ourKing, King oponentsKing)
  {
    return (ourKing.willBeSafeAfterMove(piece.getSquare(), sq)) && ((null == sq.getPiece()) || (piece.getPlayer() != sq.getPiece().getPlayer())) && (sq.getPiece() != oponentsKing);
  }
}
