package pl.art.lach.mateusz.javaopenchess.core;

import java.util.ArrayList;
import java.util.Set;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.moves.Castling;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;






















public class Chessboard
{
  private static final Logger LOG = Logger.getLogger(Chessboard.class);
  

  protected static final int TOP = 0;
  

  protected static final int BOTTOM = 7;
  

  public static final int LAST_SQUARE = 7;
  

  public static final int FIRST_SQUARE = 0;
  

  public static final int NUMBER_OF_SQUARES = 8;
  

  protected Square[][] squares;
  

  private Set<Square> moves;
  
  private Settings settings;
  
  protected King kingWhite;
  
  protected King kingBlack;
  
  private Pawn twoSquareMovedPawn = null;
  

  private MovesHistory movesObject;
  

  protected Square activeSquare;
  

  protected int activeSquareX;
  
  protected int activeSquareY;
  
  private ChessboardView chessboardView;
  
  private int halfCounter = 0;
  




  private int fullMoveCounterAdd = 0;
  





  public Chessboard(Settings settings, MovesHistory moves)
  {
    this.settings = settings;
    chessboardView = new Chessboard2D(this);
    
    activeSquareX = 0;
    activeSquareY = 0;
    
    squares = new Square[8][8];
    
    for (int i = 0; i < 8; i++)
    {
      for (int y = 0; y < 8; y++)
      {
        squares[i][y] = new Square(i, y, null);
      }
    }
    movesObject = moves;
  }
  
  public Chessboard(Settings settings, MovesHistory moves, ChessboardView chessboardView)
  {
    this(settings, moves);
    this.chessboardView = chessboardView;
  }
  



  public static int getTop()
  {
    return 0;
  }
  



  public static int getBottom()
  {
    return 7;
  }
  
  public void setPieces4NewGame(Player plWhite, Player plBlack)
  {
    Player player = plBlack;
    Player player1 = plWhite;
    setFigures4NewGame(0, player);
    setPawns4NewGame(1, player);
    setFigures4NewGame(7, player1);
    setPawns4NewGame(6, player1);
  }
  






  private void setFigures4NewGame(int i, Player player)
  {
    if ((i != 0) && (i != 7))
    {
      LOG.error("error setting figures like rook etc.");
      return;
    }
    if (i == 0)
    {
      player.setGoDown(true);
    }
    
    getSquare(0, i).setPiece(new Rook(this, player));
    getSquare(7, i).setPiece(new Rook(this, player));
    getSquare(1, i).setPiece(new Knight(this, player));
    getSquare(6, i).setPiece(new Knight(this, player));
    getSquare(2, i).setPiece(new Bishop(this, player));
    getSquare(5, i).setPiece(new Bishop(this, player));
    

    getSquare(3, i).setPiece(new Queen(this, player));
    if (player.getColor() == Colors.WHITE)
    {
      kingWhite = new King(this, player);
      getSquare(4, i).setPiece(kingWhite);
    }
    else
    {
      kingBlack = new King(this, player);
      getSquare(4, i).setPiece(kingBlack);
    }
  }
  




  private void setPawns4NewGame(int i, Player player)
  {
    if ((i != 1) && (i != 6))
    {
      LOG.error("error setting pawns etc.");
      return;
    }
    for (int x = 0; x < 8; x++)
    {
      getSquare(x, i).setPiece(new Pawn(this, player));
    }
  }
  



  public void select(Square sq)
  {
    setActiveSquare(sq);
    setActiveSquareX(sq.getPozX() + 1);
    setActiveSquareY(sq.getPozY() + 1);
    
    LOG.debug(String.format("active_x: %s active_y: %s", new Object[] {
      Integer.valueOf(getActiveSquareX()), Integer.valueOf(getActiveSquareY()) }));
    
    getChessboardView().repaint();
  }
  
  public void unselect()
  {
    setActiveSquareX(0);
    setActiveSquareY(0);
    setActiveSquare(null);
    
    getChessboardView().unselect();
  }
  
  public void resetActiveSquare()
  {
    setActiveSquare(null);
  }
  
  public void move(Square begin, Square end)
  {
    move(begin, end, true);
  }
  









  public void move(int xFrom, int yFrom, int xTo, int yTo)
  {
    try
    {
      Square fromSQ = getSquare(xFrom, yFrom);
      toSQ = getSquare(xTo, yTo);
    }
    catch (IndexOutOfBoundsException exc) {
      Square toSQ;
      LOG.error("error moving piece: " + exc.getMessage()); return; }
    Square toSQ;
    Square fromSQ;
    move(fromSQ, toSQ, true);
  }
  
  public void move(Square begin, Square end, boolean refresh)
  {
    move(begin, end, refresh, true);
  }
  







  public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory)
  {
    Castling castling = Castling.NONE;
    Piece promotedPiece = null;
    Piece takenPiece = null;
    boolean wasEnPassant = false;
    if (null != piece)
    {
      takenPiece = piece;
      end.getPiece().setSquare(null);
    }
    
    Square tempBegin = new Square(begin);
    Square tempEnd = new Square(end);
    
    begin.getPiece().setSquare(end);
    piece = piece;
    piece = null;
    
    if (King.class == end.getPiece().getClass())
    {
      castling = moveKing(end, castling, begin);
    }
    else if (Rook.class == end.getPiece().getClass())
    {
      moveRook(end);
    }
    else if (Pawn.class == end.getPiece().getClass())
    {
      wasEnPassant = movePawn(end, begin, tempEnd, wasEnPassant);
      
      if (Pawn.canBePromoted(end))
      {
        promotedPiece = promotePawn(clearForwardHistory, end, promotedPiece);
      }
    }
    else if (Pawn.class != end.getPiece().getClass())
    {
      setTwoSquareMovedPawn(null);
    }
    
    if (refresh)
    {
      unselect();
      repaint();
    }
    
    handleHalfMoveCounter(end, takenPiece);
    handleHistory(clearForwardHistory, tempBegin, tempEnd, castling, wasEnPassant, promotedPiece);
  }
  

  private void handleHalfMoveCounter(Square end, Piece takenPiece)
  {
    if (isHalfMove(end, takenPiece))
    {
      halfCounter += 1;
    }
    else
    {
      halfCounter = 0;
    }
  }
  
  private static boolean isHalfMove(Square end, Piece takenPiece)
  {
    return (!(end.getPiece() instanceof Pawn)) && (null == takenPiece);
  }
  
  private void handleHistory(boolean clearForwardHistory, Square tempBegin, Square tempEnd, Castling castling, boolean wasEnPassant, Piece promotedPiece)
  {
    if (clearForwardHistory)
    {
      movesObject.clearMoveForwardStack();
      movesObject.addMove(tempBegin, tempEnd, true, castling, wasEnPassant, promotedPiece);
    }
    else
    {
      movesObject.addMove(tempBegin, tempEnd, false, castling, wasEnPassant, promotedPiece);
    }
    if (getSettings().isGameAgainstComputer()) {}
  }
  



  public boolean movePawn(Square end, Square begin, Square tempEnd, boolean wasEnPassant)
  {
    if ((getTwoSquareMovedPawn() != null) && (getSquares()[end.getPozX()][begin.getPozY()] == getTwoSquareMovedPawn().getSquare()))
    {
      piece = getSquaresgetPozXgetPozYpiece;
      
      squares[pozX][pozY].piece = null;
      wasEnPassant = true;
    }
    if ((begin.getPozY() - end.getPozY() == 2) || (end.getPozY() - begin.getPozY() == 2))
    {
      setTwoSquareMovedPawn((Pawn)piece);
    }
    else
    {
      setTwoSquareMovedPawn(null);
    }
    return wasEnPassant;
  }
  
  public Piece promotePawn(boolean clearForwardHistory, Square end, Piece promotedPiece)
  {
    if (clearForwardHistory)
    {
      Piece piece = end.getPiece().getPlayer().getPromotionPiece(this);
      if (null != piece)
      {
        piece.setChessboard(end.getPiece().getChessboard());
        piece.setPlayer(end.getPiece().getPlayer());
        piece.setSquare(end.getPiece().getSquare());
        piece = piece;
        promotedPiece = piece;
      }
    }
    return promotedPiece;
  }
  
  private void moveRook(Square end)
  {
    if (!((Rook)piece).getWasMotioned())
    {
      ((Rook)piece).setWasMotioned(true);
    }
  }
  
  private Castling moveKing(Square end, Castling castling, Square begin)
  {
    if (!((King)piece).getWasMotioned())
    {
      ((King)piece).setWasMotioned(true);
    }
    
    castling = King.getCastling(begin, end);
    if (Castling.SHORT_CASTLING == castling)
    {
      move(getSquare(7, begin.getPozY()), getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
    }
    else if (Castling.LONG_CASTLING == castling)
    {
      move(getSquare(0, begin.getPozY()), getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
    }
    
    return castling;
  }
  

  public boolean redo()
  {
    return redo(true);
  }
  
  public boolean redo(boolean refresh)
  {
    if (getSettings().getGameType() == GameTypes.LOCAL)
    {
      Move first = movesObject.redo();
      



      if (first != null)
      {
        Square from = first.getFrom();
        Square to = first.getTo();
        
        move(getSquares()[from.getPozX()][from.getPozY()], getSquares()[to.getPozX()][to.getPozY()], true, false);
        if (first.getPromotedPiece() != null)
        {
          Pawn pawn = (Pawn)getSquaresgetPozXgetPozYpiece;
          pawn.setSquare(null);
          
          squares[pozX][pozY].piece = first.getPromotedPiece();
          Piece promoted = getSquaresgetPozXgetPozYpiece;
          promoted.setSquare(getSquares()[to.getPozX()][to.getPozY()]);
        }
        return true;
      }
    }
    
    return false;
  }
  
  public boolean undo()
  {
    return undo(true);
  }
  
  public synchronized boolean undo(boolean refresh)
  {
    Move last = movesObject.undo();
    
    if (canUndo(last))
    {
      return processUndoOperation(last, refresh);
    }
    return false;
  }
  
  private boolean processUndoOperation(Move last, boolean refresh)
  {
    Square begin = last.getFrom();
    Square end = last.getTo();
    try
    {
      Piece moved = last.getMovedPiece();
      squares[pozX][pozY].piece = moved;
      
      moved.setSquare(getSquares()[begin.getPozX()][begin.getPozY()]);
      
      Piece taken = last.getTakenPiece();
      if (last.getCastlingMove() != Castling.NONE)
      {
        handleUndoCastling(last, end, begin, moved);
      }
      else if (Rook.class == moved.getClass())
      {
        ((Rook)moved).setWasMotioned(false);
      }
      else if ((Pawn.class == moved.getClass()) && (last.wasEnPassant()))
      {
        handleEnPessant(last, end, begin);
      }
      else if ((Pawn.class == moved.getClass()) && (last.getPromotedPiece() != null))
      {
        handlePawnPromotion(end);
      }
      

      Move oneMoveEarlier = movesObject.getLastMoveFromHistory();
      if ((oneMoveEarlier != null) && (oneMoveEarlier.wasPawnTwoFieldsMove()))
      {
        int toPozX = oneMoveEarlier.getTo().getPozX();
        int toPozY = oneMoveEarlier.getTo().getPozY();
        Piece canBeTakenEnPassant = getSquare(toPozX, toPozY).getPiece();
        if (Pawn.class == canBeTakenEnPassant.getClass())
        {
          setTwoSquareMovedPawn((Pawn)canBeTakenEnPassant);
        }
      }
      
      if ((taken != null) && (!last.wasEnPassant()))
      {
        squares[pozX][pozY].piece = taken;
        taken.setSquare(getSquares()[end.getPozX()][end.getPozY()]);
      }
      else
      {
        squares[pozX][pozY].piece = null;
      }
      
      if (refresh)
      {
        unselect();
        repaint();
      }
      
    }
    catch (ArrayIndexOutOfBoundsException|NullPointerException exc)
    {
      LOG.error(
        String.format("error: %s exc object: ", new Object[] {exc.getClass() }), exc);
      

      return false;
    }
    
    return true;
  }
  
  private void handleUndoCastling(Move last, Square end, Square begin, Piece moved)
  {
    Piece rook = null;
    if (last.getCastlingMove() == Castling.SHORT_CASTLING)
    {
      rook = handleShortCastling(rook, end, begin);
    }
    else
    {
      rook = handleLongCastling(rook, end, begin);
    }
    ((King)moved).setWasMotioned(false);
    ((Rook)rook).setWasMotioned(false);
  }
  
  private static boolean canUndo(Move last)
  {
    return (last != null) && (last.getFrom() != null);
  }
  
  private void handlePawnPromotion(Square end)
  {
    Piece promoted = getSquaresgetPozXgetPozYpiece;
    promoted.setSquare(null);
    squares[pozX][pozY].piece = null;
  }
  
  private void handleEnPessant(Move last, Square end, Square begin)
  {
    Pawn pawn = (Pawn)last.getTakenPiece();
    squares[pozX][pozY].piece = pawn;
    pawn.setSquare(getSquares()[end.getPozX()][begin.getPozY()]);
  }
  
  private Piece handleLongCastling(Piece rook, Square end, Square begin)
  {
    rook = getSquaresgetPozX1getPozYpiece;
    squares[0][pozY].piece = rook;
    rook.setSquare(getSquares()[0][begin.getPozY()]);
    squares[(pozX + 1)][pozY].piece = null;
    return rook;
  }
  
  private Piece handleShortCastling(Piece rook, Square end, Square begin)
  {
    rook = getSquaresgetPozX1getPozYpiece;
    squares[7][pozY].piece = rook;
    rook.setSquare(getSquares()[7][begin.getPozY()]);
    squares[(pozX - 1)][pozY].piece = null;
    return rook;
  }
  



  public Square[][] getSquares()
  {
    return squares;
  }
  
  public Square getSquare(int x, int y)
  {
    try
    {
      return squares[x][y];
    }
    catch (ArrayIndexOutOfBoundsException exc) {}
    
    return null;
  }
  

  public Square getSquare(Squares squareX, Squares squareY)
  {
    return getSquare(squareX.getValue(), squareY.getValue());
  }
  
  public void clear()
  {
    for (int i = 0; i < squares.length; i++)
    {
      for (int j = 0; j < squares[i].length; j++)
      {
        Piece piece = squares[i][j].getPiece();
        piece = null;
        squares[i][j].setPiece(null);
      }
    }
  }
  



  public Square getActiveSquare()
  {
    return activeSquare;
  }
  
  public ArrayList<Piece> getAllPieces(Colors color)
  {
    ArrayList<Piece> result = new ArrayList();
    for (int i = 0; i < squares.length; i++)
    {
      for (int j = 0; j < squares[i].length; j++)
      {
        Square sq = squares[i][j];
        if ((null != sq.getPiece()) && (
          (sq.getPiece().getPlayer().getColor() == color) || (color == null)))
        {
          result.add(sq.getPiece());
        }
      }
    }
    return result;
  }
  


  public static boolean wasEnPassant(Square sq)
  {
    return (sq.getPiece() != null) && (sq.getPiece().getChessboard().getTwoSquareMovedPawn() != null) && (sq == sq.getPiece().getChessboard().getTwoSquareMovedPawn().getSquare());
  }
  



  public King getKingWhite()
  {
    return kingWhite;
  }
  



  public King getKingBlack()
  {
    return kingBlack;
  }
  
  public void setKingWhite(King kingWhite, Square sq)
  {
    this.kingWhite = kingWhite;
    getSquare(sq.getPozX(), sq.getPozY()).setPiece(this.kingWhite);
  }
  
  public void setKingBlack(King kingBlack, Square sq)
  {
    this.kingBlack = kingBlack;
    getSquare(sq.getPozX(), sq.getPozY()).setPiece(this.kingBlack);
  }
  


  public Pawn getTwoSquareMovedPawn()
  {
    return twoSquareMovedPawn;
  }
  



  public ChessboardView getChessboardView()
  {
    return chessboardView;
  }
  



  public void setChessboardView(ChessboardView chessboardView)
  {
    this.chessboardView = chessboardView;
  }
  
  public void repaint()
  {
    getChessboardView().repaint();
  }
  



  public Settings getSettings()
  {
    return settings;
  }
  



  public void setSettings(Settings settings)
  {
    this.settings = settings;
    
    for (int i = 0; i < 7; i++)
    {
      for (int j = 0; j < 7; j++)
      {
        Square sq = getSquare(i, j);
        if (null != sq.getPiece())
        {
          if (Colors.WHITE == sq.getPiece().getPlayer().getColor())
          {
            sq.getPiece().setPlayer(settings.getPlayerWhite());
          }
          else
          {
            sq.getPiece().setPlayer(settings.getPlayerBlack());
          }
        }
      }
    }
  }
  



  public Set<Square> getMoves()
  {
    return moves;
  }
  



  public void setMoves(Set<Square> moves)
  {
    this.moves = moves;
  }
  



  public void setActiveSquare(Square activeSquare)
  {
    this.activeSquare = activeSquare;
  }
  



  public int getActiveSquareX()
  {
    return activeSquareX;
  }
  



  public void setActiveSquareX(int activeSquareX)
  {
    this.activeSquareX = activeSquareX;
  }
  



  public int getActiveSquareY()
  {
    return activeSquareY;
  }
  



  public void setActiveSquareY(int activeSquareY)
  {
    this.activeSquareY = activeSquareY;
  }
  



  public void setTwoSquareMovedPawn(Pawn twoSquareMovedPawn)
  {
    this.twoSquareMovedPawn = twoSquareMovedPawn;
  }
  



  public int getHalfCounter()
  {
    return halfCounter;
  }
  



  public void setHalfCounter(int halfCounter)
  {
    this.halfCounter = halfCounter;
  }
  



  public int getFullMoveCounterAdd()
  {
    return fullMoveCounterAdd;
  }
  



  public void setFullMoveCounterAdd(int fullMoveCounterAdd)
  {
    this.fullMoveCounterAdd = fullMoveCounterAdd;
  }
}
