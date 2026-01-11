package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;


















public class PawnBehavior
  extends Behavior
{
  public PawnBehavior(Piece piece)
  {
    super(piece);
  }
  

  public Set<Square> getSquaresInRange()
  {
    Set<Square> list = new HashSet();
    

    int first = piece.getSquare().getPozY() - 1;
    int second = piece.getSquare().getPozY() - 2;
    Chessboard chessboard = piece.getChessboard();
    
    if (piece.getPlayer().isGoDown())
    {
      first = piece.getSquare().getPozY() + 1;
      second = piece.getSquare().getPozY() + 2;
    }
    if (piece.isOut(first, first))
    {
      return list;
    }
    Square sq = chessboard.getSquare(piece.getSquare().getPozX(), first);
    if (sq.getPiece() == null)
    {
      King kingWhite = chessboard.getKingWhite();
      King kingBlack = chessboard.getKingBlack();
      
      list.add(chessboard.getSquares()[piece.getSquare().getPozX()][first]);
      
      if (((piece.getPlayer().isGoDown()) && (piece.getSquare().getPozY() == 1)) || ((!piece.getPlayer().isGoDown()) && (piece.getSquare().getPozY() == 6)))
      {
        Square sq1 = chessboard.getSquare(piece.getSquare().getPozX(), second);
        if (sq1.getPiece() == null)
        {
          list.add(chessboard.getSquare(piece.getSquare().getPozX(), second));
        }
      }
    }
    if (!piece.isOut(piece.getSquare().getPozX() - 1, piece.getSquare().getPozY()))
    {

      sq = chessboard.getSquares()[(piece.getSquare().getPozX() - 1)][first];
      if (sq.getPiece() != null)
      {
        if (piece.getPlayer() != sq.getPiece().getPlayer())
        {
          list.add(chessboard.getSquares()[(piece.getSquare().getPozX() - 1)][first]);
        }
      }
      

      sq = chessboard.getSquares()[(piece.getSquare().getPozX() - 1)][piece.getSquare().getPozY()];
      if (Chessboard.wasEnPassant(sq))
      {
        if (piece.getPlayer() != sq.getPiece().getPlayer())
        {
          list.add(chessboard.getSquares()[(piece.getSquare().getPozX() - 1)][first]);
        }
      }
    }
    if (!piece.isOut(piece.getSquare().getPozX() + 1, piece.getSquare().getPozY()))
    {

      sq = chessboard.getSquares()[(piece.getSquare().getPozX() + 1)][first];
      if (sq.getPiece() != null)
      {
        if (piece.getPlayer() != sq.getPiece().getPlayer())
        {
          list.add(chessboard.getSquares()[(piece.getSquare().getPozX() + 1)][first]);
        }
      }
      

      sq = chessboard.getSquares()[(piece.getSquare().getPozX() + 1)][piece.getSquare().getPozY()];
      if (Chessboard.wasEnPassant(sq))
      {
        if (piece.getPlayer() != sq.getPiece().getPlayer())
        {


          if (piece.getPlayer().getColor() == Colors.WHITE)
          {
            list.add(chessboard.getSquares()[(piece.getSquare().getPozX() + 1)][first]);
          }
          else
          {
            list.add(chessboard.getSquares()[(piece.getSquare().getPozX() + 1)][first]);
          }
        }
      }
    }
    return list;
  }
}
