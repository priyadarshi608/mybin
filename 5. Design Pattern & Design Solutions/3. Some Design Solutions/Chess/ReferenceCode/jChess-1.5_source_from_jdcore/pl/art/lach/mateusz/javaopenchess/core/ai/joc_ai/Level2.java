package pl.art.lach.mateusz.javaopenchess.core.ai.joc_ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.ai.AI;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;















public class Level2
  implements AI
{
  public Level2() {}
  
  public Move getMove(Game game, Move lastMove)
  {
    Chessboard chessboard = game.getChessboard();
    List<Piece> pieces = chessboard.getAllPieces(game.getActivePlayer().getColor());
    
    int bestMark = 0;
    List<Move> movesList = new ArrayList();
    for (Iterator localIterator1 = pieces.iterator(); localIterator1.hasNext();) { piece = (Piece)localIterator1.next();
      
      if (0 < piece.getAllMoves().size())
      {
        List<Square> squares = new ArrayList(piece.getAllMoves());
        if (squares.size() > 0)
        {
          for (Square sq : squares)
          {
            Piece takenPiece = sq.getPiece();
            Piece promotedPiece = null;
            if ((piece instanceof Pawn))
            {
              if (Pawn.canBePromoted(sq))
              {
                promotedPiece = new Queen(chessboard, game.getActivePlayer());
              }
            }
            Move move = new Move(piece.getSquare(), sq, piece, sq.getPiece(), promotedPiece);
            int currentMark = 0;
            if (null != takenPiece)
            {
              currentMark = takenPiece.getValue();
            }
            if (currentMark > bestMark)
            {
              movesList.clear();
              movesList.add(move);
              bestMark = currentMark;
            }
            else if (currentMark == bestMark)
            {
              movesList.add(move);
            }
          } }
      }
    }
    Piece piece;
    int size = movesList.size();
    Random rand = new Random();
    return (Move)movesList.get(rand.nextInt(size));
  }
}
