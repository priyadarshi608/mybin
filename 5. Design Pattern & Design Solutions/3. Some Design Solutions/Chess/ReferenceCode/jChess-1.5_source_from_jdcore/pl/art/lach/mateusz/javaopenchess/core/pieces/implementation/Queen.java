package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.BishopBehavior;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.RookBehavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;

































public class Queen
  extends Piece
{
  public Queen(Chessboard chessboard, Player player)
  {
    super(chessboard, player);
    value = 9;
    symbol = "Q";
    addBehavior(new RookBehavior(this));
    addBehavior(new BishopBehavior(this));
  }
}
