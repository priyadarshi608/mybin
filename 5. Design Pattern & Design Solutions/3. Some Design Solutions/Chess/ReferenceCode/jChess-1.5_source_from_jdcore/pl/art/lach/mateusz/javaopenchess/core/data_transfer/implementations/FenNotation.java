package pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations;

import java.util.Stack;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameFactory;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.Squares;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;



















public class FenNotation
  implements DataImporter, DataExporter
{
  public static final String INITIAL_STATE = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  private static final String BLACK_QUEEN_SYMBOL = "q";
  private static final String BLACK_KING_SYMBOL = "k";
  private final String WHITE_QUEEN_SYMBOL = "Q";
  
  private static final String WHITE_KING_SYMBOL = "K";
  
  private static final int PIECES_STATE_NUM = 0;
  
  private static final int ACTIVE_PLAYER_NUM = 1;
  
  private static final int CASTLING_STATE_NUM = 2;
  
  private static final int EN_PASSANT_STATE_NUM = 3;
  
  private static final int HALF_COUNTER_STATE_NUM = 4;
  
  private static final int FULL_COUNTER_STATE_NUM = 5;
  
  private static final String CHAR_PLAYER_WHITE = "w";
  
  private static final String CHAR_PLAYER_BLACK = "b";
  public static final String ROW_SEPARATOR = "/";
  public static final String FIELD_SEPARATOR = " ";
  public static final String FIELD_EMPTY = "-";
  private static final String SQUARE_PREFIX = "SQ_";
  private static final int NUMBER_OF_FIELDS = 6;
  private static final int NUMBER_OF_ROWS = 8;
  
  public FenNotation() {}
  
  public Game importData(String data)
    throws ReadGameError
  {
    String whiteName = "--";
    String blackName = "--";
    Game game = GameFactory.instance(GameModes.LOAD_GAME, GameTypes.LOCAL, whiteName, blackName, PlayerType.LOCAL_USER, PlayerType.LOCAL_USER, true, false);
    








    importData(data, game);
    game.getChessboard().repaint();
    return game;
  }
  
  public void importData(String data, Game game)
    throws ReadGameError
  {
    Chessboard chessboard = game.getChessboard();
    chessboard.clear();
    String[] fields = data.split(" ");
    if (6 != fields.length)
    {


      throw new ReadGameError(Settings.lang("invalid_fen_state"), Settings.lang("invalid_fen_number_of_fields"));
    }
    
    Player blackPlayer = game.getSettings().getPlayerBlack();
    Player whitePlayer = game.getSettings().getPlayerWhite();
    importPieces(fields[0], game, whitePlayer, blackPlayer);
    importActivePlayer(fields[1], game);
    importCastlingState(fields[2], chessboard);
    importEnPassantState(fields[3], chessboard, game);
    importCounters(fields, game);
  }
  
  private void importCounters(String[] fields, Game game)
    throws ReadGameError
  {
    try
    {
      Integer halfCounter = Integer.valueOf(Integer.parseInt(fields[4]));
      game.getChessboard().setHalfCounter(halfCounter.intValue());
      game.getChessboard().setFullMoveCounterAdd(
        Integer.parseInt(fields[5]));

    }
    catch (NumberFormatException exc)
    {
      throw new ReadGameError(Settings.lang("invalid_fen_state"), fields[4]);
    }
  }
  
  private void importEnPassantState(String enPassantState, Chessboard chessboard, Game game) throws ReadGameError
  {
    if ((!"-".equals(enPassantState)) && (enPassantState.length() == 2))
    {
      try
      {
        Squares sqX = Squares.valueOf("SQ_" + enPassantState.substring(0, 1).toUpperCase());
        Squares sqY = Squares.valueOf("SQ_" + enPassantState.substring(1, 2).toUpperCase());
        if (Squares.SQ_3 == sqY)
        {
          sqY = Squares.SQ_4;
        }
        else if (Squares.SQ_5 == sqY)
        {
          sqY = Squares.SQ_6;
        }
        else
        {
          throw new ReadGameError(Settings.lang("invalid_fen_state"), enPassantState);
        }
        Square sq = chessboard.getSquare(sqX, sqY);
        Piece piece = sq.getPiece();
        if ((null != piece) && (Pawn.class == piece.getClass()))
        {
          game.getChessboard().setTwoSquareMovedPawn((Pawn)sq.getPiece());
        }
      }
      catch (IllegalStateException exc)
      {
        throw new ReadGameError(Settings.lang("invalid_fen_state"), enPassantState);
      }
    }
  }
  
  private void importCastlingState(String castlingState, Chessboard chessboard) throws ReadGameError
  {
    int i = 0; for (int size = castlingState.length(); i < size; i++)
    {
      String state = castlingState.substring(i, i + 1);
      if (!"-".equals(state))
      {
        switch (state)
        {
        case "K": 
          setupCastlingState(chessboard
            .getSquare(Squares.SQ_E, Squares.SQ_1), chessboard
            .getSquare(Squares.SQ_H, Squares.SQ_1));
          
          break;
        case "Q": 
          setupCastlingState(chessboard
            .getSquare(Squares.SQ_E, Squares.SQ_1), chessboard
            .getSquare(Squares.SQ_A, Squares.SQ_1));
          
          break;
        case "k": 
          setupCastlingState(chessboard
            .getSquare(Squares.SQ_E, Squares.SQ_8), chessboard
            .getSquare(Squares.SQ_H, Squares.SQ_8));
          
          break;
        case "q": 
          setupCastlingState(chessboard
            .getSquare(Squares.SQ_E, Squares.SQ_8), chessboard
            .getSquare(Squares.SQ_A, Squares.SQ_8));
        }
        
      }
    }
  }
  





  private void setupCastlingState(Square kingSquare, Square rookSquare)
    throws ReadGameError
  {
    Piece piece = kingSquare.getPiece();
    if (King.class == piece.getClass())
    {
      King king = (King)piece;
      king.setWasMotioned(false);
    }
    else
    {
      throw new ReadGameError(Settings.lang("invalid_fen_state")); }
    King king;
    piece = rookSquare.getPiece();
    if (Rook.class == piece.getClass())
    {
      Rook rook = (Rook)piece;
      rook.setWasMotioned(false);
    }
    else
    {
      throw new ReadGameError(Settings.lang("invalid_fen_state"));
    }
    Rook rook;
  }
  
  private void importActivePlayer(String activePlayer, Game game) {
    if ("w".equals(activePlayer))
    {
      game.setActivePlayer(game.getSettings().getPlayerWhite());
    }
    else
    {
      game.setActivePlayer(game.getSettings().getPlayerBlack());
    }
  }
  

  private void importPieces(String piecesStateString, Game game, Player whitePlayer, Player blackPlayer)
    throws ReadGameError
  {
    int currentY = Squares.SQ_8.getValue();
    String[] rows = piecesStateString.split("/");
    if (8 != rows.length)
    {


      throw new ReadGameError(Settings.lang("invalid_fen_state"), Settings.lang("invalid_fen_number_of_rows"));
    }
    
    for (String row : piecesStateString.split("/"))
    {
      int currentX = Squares.SQ_A.getValue();
      for (int i = 0; i < row.length(); i++)
      {
        String currChar = row.substring(i, i + 1);
        try
        {
          Integer currNumber = Integer.valueOf(Integer.parseInt(currChar));
          currentX += currNumber.intValue();
        }
        catch (NumberFormatException nfe)
        {
          Piece piece = PieceFactory.getPieceFromFenNotation(game
            .getChessboard(), currChar, whitePlayer, blackPlayer);
          



          Square square = game.getChessboard().getSquare(currentX, currentY);
          square.setPiece(piece);
          currentX++;
        }
      }
      currentY++;
    }
  }
  


  public String exportData(Game game)
  {
    StringBuilder result = new StringBuilder();
    result.append(exportChessboardFields(game));
    result.append(" ");
    result.append(exportActivePlayer(game));
    result.append(" ");
    result.append(exportCastlingState(game));
    result.append(" ");
    result.append(exportEnPassantState(game));
    result.append(" ");
    result.append(game.getChessboard().getHalfCounter());
    result.append(exportFullMoveCounter(game));
    return result.toString();
  }
  
  private String exportFullMoveCounter(Game game)
  {
    int size = game.getMoves().getMoveBackStack().size();
    int counter = size / 2 + 1;
    int counterAdd = game.getChessboard().getFullMoveCounterAdd();
    if (counterAdd > 0)
    {
      counter += counterAdd - 1;
    }
    return " " + counter;
  }
  
  private String exportEnPassantState(Game game)
  {
    StringBuilder result = new StringBuilder();
    Pawn pawn = game.getChessboard().getTwoSquareMovedPawn();
    if (null != pawn)
    {
      Square pawnSquare = pawn.getSquare();
      Square testSquare = null;
      if (Colors.WHITE == pawn.getPlayer().getColor())
      {
        testSquare = new Square(pawnSquare.getPozX(), pawnSquare.getPozY() + 1, null);
      }
      else
      {
        testSquare = new Square(pawnSquare.getPozX(), pawnSquare.getPozY() - 1, null);
      }
      result.append(testSquare.getAlgebraicNotation());
    }
    else
    {
      result.append("-");
    }
    return result.toString();
  }
  
  private String exportCastlingState(Game game)
  {
    String result = "";
    Chessboard chessboard = game.getChessboard();
    result = result + exportCastlingOfOneColor(chessboard.getKingWhite(), chessboard, Squares.SQ_1);
    result = result + exportCastlingOfOneColor(chessboard.getKingBlack(), chessboard, Squares.SQ_8);
    return result;
  }
  
  private String exportCastlingOfOneColor(King king, Chessboard chessboard, Squares squareLine)
  {
    String result = "";
    Colors color = king.getPlayer().getColor();
    if (!king.getWasMotioned())
    {
      Piece piece = chessboard.getSquare(Squares.SQ_A, squareLine).getPiece();
      if ((piece instanceof Rook))
      {
        Rook rightRook = (Rook)piece;
        if (rightRook.getWasMotioned())
        {
          result = result + "-";
        }
        else
        {
          result = result + (color == Colors.WHITE ? "K" : "k");
        }
      }
      
      piece = chessboard.getSquare(Squares.SQ_H, squareLine).getPiece();
      if ((piece instanceof Rook))
      {
        Rook leftRook = (Rook)piece;
        if (leftRook.getWasMotioned())
        {
          result = result + "-";
        }
        else
        {
          result = result + (color == Colors.WHITE ? "Q" : "q");
        }
        
      }
    }
    else
    {
      result = result + "-";
    }
    return result;
  }
  
  private String exportChessboardFields(Game game)
  {
    String result = "";
    Chessboard chessboard = game.getChessboard();
    for (int y = 0; y <= 7; y++)
    {
      int emptySquares = 0;
      for (int x = 0; x <= 7; x++)
      {
        Square sq = chessboard.getSquare(x, y);
        Piece piece = sq.getPiece();
        if (null == piece)
        {
          emptySquares++;
        }
        else
        {
          if (0 != emptySquares)
          {
            result = result + emptySquares;
            emptySquares = 0;
          }
          String symbol = null;
          if ((piece instanceof Pawn))
          {
            symbol = "P";
          }
          else
          {
            symbol = piece.getSymbol();
          }
          result = result + (piece.getPlayer().getColor() == Colors.WHITE ? symbol.toUpperCase() : symbol.toLowerCase());
        }
      }
      if (0 != emptySquares)
      {
        result = result + emptySquares;
      }
      if (7 != y)
      {
        result = result + "/";
      }
      emptySquares = 0;
    }
    return result;
  }
  
  private String exportActivePlayer(Game game)
  {
    String result = "";
    if (Colors.WHITE == game.getActivePlayer().getColor())
    {
      result = result + "w";
    }
    else
    {
      result = result + "b";
    }
    return result;
  }
}
