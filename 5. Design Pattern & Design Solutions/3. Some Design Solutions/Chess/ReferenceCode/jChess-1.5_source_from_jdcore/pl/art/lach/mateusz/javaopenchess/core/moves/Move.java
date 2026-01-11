package pl.art.lach.mateusz.javaopenchess.core.moves;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;



















public class Move
{
  protected Square from = null;
  
  protected Square to = null;
  
  protected Piece movedPiece = null;
  
  protected Piece takenPiece = null;
  
  protected Piece promotedTo = null;
  
  protected boolean wasEnPassant = false;
  
  protected Castling castlingMove = Castling.NONE;
  
  protected boolean wasPawnTwoFieldsMove = false;
  
  public Move(Square from, Square to, Piece movedPiece, Piece takenPiece, Piece promotedPiece)
  {
    this(from, to, movedPiece, takenPiece, Castling.NONE, false, promotedPiece);
    if (King.class == movedPiece.getClass())
    {
      castlingMove = King.getCastling(from, to);
    }
    if (Pawn.class == movedPiece.getClass())
    {
      wasEnPassant = Pawn.wasEnPassant(from, to);
    }
  }
  

  public Move(Square from, Square to, Piece movedPiece, Piece takenPiece, Castling castlingMove, boolean wasEnPassant, Piece promotedPiece)
  {
    this.from = from;
    this.to = to;
    
    this.movedPiece = movedPiece;
    this.takenPiece = takenPiece;
    
    this.castlingMove = castlingMove;
    this.wasEnPassant = wasEnPassant;
    
    if ((Pawn.class == movedPiece.getClass()) && 
      (Math.abs(to.getPozY() - from.getPozY()) == 2))
    {
      wasPawnTwoFieldsMove = true;
    }
    else if (((Pawn.class == movedPiece.getClass()) && (to.getPozY() == Chessboard.getBottom())) || (
      (to.getPozY() == Chessboard.getTop()) && (promotedPiece != null)))
    {
      promotedTo = promotedPiece;
    }
  }
  
  public Square getFrom()
  {
    return from;
  }
  
  public Square getTo()
  {
    return to;
  }
  
  public Piece getMovedPiece()
  {
    return movedPiece;
  }
  
  public Piece getTakenPiece()
  {
    return takenPiece;
  }
  
  public boolean wasEnPassant()
  {
    return wasEnPassant;
  }
  
  public boolean wasPawnTwoFieldsMove()
  {
    return wasPawnTwoFieldsMove;
  }
  
  public Castling getCastlingMove()
  {
    return castlingMove;
  }
  
  public Piece getPromotedPiece()
  {
    return promotedTo;
  }
}
