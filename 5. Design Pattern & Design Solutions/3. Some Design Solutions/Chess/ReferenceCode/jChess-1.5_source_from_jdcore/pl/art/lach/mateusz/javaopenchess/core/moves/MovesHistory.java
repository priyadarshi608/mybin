package pl.art.lach.mateusz.javaopenchess.core.moves;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;



















public class MovesHistory
  extends AbstractTableModel
{
  private static final Logger LOG = Logger.getLogger(MovesHistory.class);
  
  private static final int CHAR_TINY_X_ASCII = 120;
  
  private static final int CHAR_HYPHEN_ASCII = 45;
  
  private static final int CHAR_R_ASCII = 82;
  
  private static final int CHAR_Q_ASCII = 81;
  
  private static final int CHAR_N_ASCII = 78;
  
  private static final int CHAR_K_ASCII = 75;
  
  private static final int CHAR_B_ASCII = 66;
  
  private static final int CHAR_TINY_H_ASCII = 104;
  
  public static final String SYMBOL_CHECK = "+";
  
  public static final String SYMBOL_CHECK_MATE = "#";
  
  private static final int CHAR_TINY_A_ASCII = 97;
  
  public static final String SYMBOL_NORMAL_MOVE = "-";
  
  public static final String SYMBOL_PIECE_TAKEN = "x";
  
  public static final String SYMBOL_EN_PASSANT = "(e.p)";
  
  private ArrayList<String> moves = new ArrayList();
  
  private int columnsNum = 3;
  
  private int rowsNum = 0;
  
  private String[] names = {
  
    Settings.lang("white"), Settings.lang("black") };
  

  private NotEditableTableModel tableModel;
  
  private JScrollPane scrollPane;
  
  private JTable table;
  
  private boolean enterBlack = false;
  
  private Game game;
  
  private Stack<Move> moveBackStack = new Stack();
  
  protected Stack<Move> moveForwardStack = new Stack();
  
  private int fiftyMoveRuleCounter = 0;
  

  public MovesHistory(Game game)
  {
    tableModel = new NotEditableTableModel();
    table = new JTable(tableModel);
    scrollPane = new JScrollPane(table);
    
    scrollPane.setMaximumSize(new Dimension(100, 100));
    table.setMinimumSize(new Dimension(100, 100));
    this.game = game;
    
    tableModel.addColumn(names[0]);
    tableModel.addColumn(names[1]);
    addTableModelListener(null);
    tableModel.addTableModelListener(null);
    scrollPane.setAutoscrolls(true);
  }
  


  public void draw() {}
  

  public String getValueAt(int x, int y)
  {
    return (String)moves.get(y * 2 - 1 + (x - 1));
  }
  

  public int getRowCount()
  {
    return rowsNum;
  }
  

  public int getColumnCount()
  {
    return columnsNum;
  }
  
  protected void addRow()
  {
    tableModel.addRow(new String[2]);
  }
  
  protected void addCastling(String move)
  {
    moves.remove(moves.size() - 1);
    if (!enterBlack)
    {
      tableModel.setValueAt(move, tableModel.getRowCount() - 1, 1);
    }
    else
    {
      tableModel.setValueAt(move, tableModel.getRowCount() - 1, 0);
    }
    moves.add(move);
  }
  

  public boolean isCellEditable(int a, int b)
  {
    return false;
  }
  



  protected void addMove2Table(String str)
  {
    try
    {
      if (!enterBlack)
      {
        addRow();
        rowsNum = (tableModel.getRowCount() - 1);
        tableModel.setValueAt(str, rowsNum, 0);
      }
      else
      {
        tableModel.setValueAt(str, rowsNum, 1);
        rowsNum = (tableModel.getRowCount() - 1);
      }
      enterBlack = (!enterBlack);
      table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));

    }
    catch (ArrayIndexOutOfBoundsException exc)
    {
      if (rowsNum > 0)
      {
        rowsNum -= 1;
        addMove2Table(str);
      }
    }
  }
  




  public void addMove(String move)
  {
    if (isMoveCorrect(move))
    {
      moves.add(move);
      addMove2Table(move);
      moveForwardStack.clear();
    }
  }
  


  public void addMove(Square begin, Square end, boolean registerInHistory, Castling castlingMove, boolean wasEnPassant, Piece promotedPiece)
  {
    String locMove = begin.getPiece().getSymbol();
    
    if (game.getSettings().isUpsideDown())
    {
      locMove = addMoveHandleUpsideDown(locMove, begin);
    }
    else
    {
      locMove = addMoveHandleNormalSetup(locMove, begin);
    }
    
    if (piece != null)
    {
      locMove = locMove + "x";
    }
    else
    {
      locMove = locMove + "-";
    }
    
    if (game.getSettings().isUpsideDown())
    {
      locMove = addMoveHandleUpsideDown(locMove, end);
    }
    else
    {
      locMove = addMoveHandleNormalSetup(locMove, end);
    }
    
    if ((Pawn.class == begin.getPiece().getClass()) && (begin.getPozX() - end.getPozX() != 0) && (piece == null))
    {
      locMove = locMove + "(e.p)";
      wasEnPassant = true;
    }
    if (isBlackOrWhiteKingCheck())
    {
      if (isBlackOrWhiteKingCheckmatedOrStalemated())
      {
        locMove = locMove + "#";
      }
      else
      {
        locMove = locMove + "+";
      }
    }
    if (castlingMove != Castling.NONE)
    {
      addCastling(castlingMove.getSymbol());
    }
    else
    {
      moves.add(locMove);
      addMove2Table(locMove);
    }
    scrollPane.scrollRectToVisible(new Rectangle(0, scrollPane.getHeight() - 2, 1, 1));
    
    if (registerInHistory)
    {
      Move moveToAdd = new Move(new Square(begin), new Square(end), piece, piece, castlingMove, wasEnPassant, promotedPiece);
      getMoveBackStack().add(moveToAdd);
    }
  }
  

  private boolean isBlackOrWhiteKingCheckmatedOrStalemated()
  {
    return ((!enterBlack) && (game.getChessboard().getKingBlack().isCheckmatedOrStalemated() == 1)) || ((enterBlack) && (game.getChessboard().getKingWhite().isCheckmatedOrStalemated() == 1));
  }
  

  private boolean isBlackOrWhiteKingCheck()
  {
    return ((!enterBlack) && (game.getChessboard().getKingBlack().isChecked())) || ((enterBlack) && (game.getChessboard().getKingWhite().isChecked()));
  }
  
  private String addMoveHandleNormalSetup(String locMove, Square begin)
  {
    locMove = locMove + Character.toString((char)(begin.getPozX() + 97));
    locMove = locMove + Integer.toString(8 - begin.getPozY());
    return locMove;
  }
  
  private String addMoveHandleUpsideDown(String locMove, Square begin)
  {
    locMove = locMove + Character.toString((char)(Chessboard.getBottom() - begin.getPozX() + 97));
    locMove = locMove + Integer.toString(begin.getPozY() + 1);
    return locMove;
  }
  

  public void clearMoveForwardStack()
  {
    moveForwardStack.clear();
  }
  
  public JScrollPane getScrollPane()
  {
    return scrollPane;
  }
  
  public ArrayList<String> getMoves()
  {
    return moves;
  }
  
  public synchronized Move getLastMoveFromHistory()
  {
    try
    {
      return (Move)getMoveBackStack().get(getMoveBackStack().size() - 1);
    }
    catch (ArrayIndexOutOfBoundsException exc) {}
    

    return null;
  }
  

  public synchronized Move getNextMoveFromHistory()
  {
    try
    {
      return (Move)moveForwardStack.get(moveForwardStack.size() - 1);

    }
    catch (ArrayIndexOutOfBoundsException exc)
    {
      LOG.error("ArrayIndexOutOfBoundsException: ", exc); }
    return null;
  }
  


  public synchronized Move undo()
  {
    try
    {
      Move last = (Move)getMoveBackStack().pop();
      if (last != null)
      {
        if (game.getSettings().getGameType() == GameTypes.LOCAL)
        {
          moveForwardStack.push(last);
        }
        if (enterBlack)
        {
          tableModel.setValueAt("", tableModel.getRowCount() - 1, 0);
          tableModel.removeRow(tableModel.getRowCount() - 1);
          
          if (rowsNum > 0)
          {
            rowsNum -= 1;
          }
          

        }
        else if (tableModel.getRowCount() > 0)
        {
          tableModel.setValueAt("", tableModel.getRowCount() - 1, 1);
        }
        
        moves.remove(moves.size() - 1);
        enterBlack = (!enterBlack);
      }
      return last;
    }
    catch (EmptyStackException exc)
    {
      LOG.error("EmptyStackException: ", exc);
      enterBlack = false;
      return null;
    }
    catch (ArrayIndexOutOfBoundsException exc)
    {
      LOG.error("ArrayIndexOutOfBoundsException: ", exc); }
    return null;
  }
  

  public synchronized Move redo()
  {
    try
    {
      if (game.getSettings().getGameType() == GameTypes.LOCAL)
      {
        Move first = (Move)moveForwardStack.pop();
        getMoveBackStack().push(first);
        
        return first;
      }
      return null;
    }
    catch (EmptyStackException exc)
    {
      LOG.error("redo: EmptyStackException: ", exc); }
    return null;
  }
  





  public static boolean isMoveCorrect(String move)
  {
    if ((move.equals(Castling.SHORT_CASTLING.getSymbol())) || (move.equals(Castling.LONG_CASTLING.getSymbol())))
    {
      return true;
    }
    try
    {
      int from = 0;
      int sign = move.charAt(from);
      switch (sign)
      {
      case 66: 
      case 75: 
      case 78: 
      case 81: 
      case 82: 
        from = 1;
      }
      
      sign = move.charAt(from);
      LOG.debug("isMoveCorrect/sign: " + sign);
      if ((sign < 97) || (sign > 104))
      {
        return false;
      }
      sign = move.charAt(from + 1);
      if ((sign < 49) || (sign > 56))
      {
        return false;
      }
      if (move.length() > 3)
      {
        sign = move.charAt(from + 2);
        if ((sign != 45) && (sign != 120))
        {
          return false;
        }
        sign = move.charAt(from + 3);
        if ((sign < 97) || (sign > 104))
        {
          return false;
        }
        sign = move.charAt(from + 4);
        if ((sign < 49) || (sign > 56))
        {
          return false;
        }
      }
    }
    catch (StringIndexOutOfBoundsException exc)
    {
      LOG.error("isMoveCorrect/StringIndexOutOfBoundsException: ", exc);
      return false;
    }
    
    return true;
  }
  
  public void addMoves(ArrayList<String> list)
  {
    for (String singleMove : list)
    {
      if (isMoveCorrect(singleMove))
      {
        addMove(singleMove);
      }
    }
  }
  



  public String getMovesInString()
  {
    int n = 1;
    int i = 0;
    String str = new String();
    for (String locMove : getMoves())
    {
      if (i % 2 == 0)
      {
        str = str + n + ". ";
        n++;
      }
      str = str + locMove + " ";
      i++;
    }
    return str;
  }
  



  public void setMoves(String moves)
    throws ReadGameError
  {
    int from = 0;
    int to = 0;
    int n = 1;
    String currentMove = "";
    ArrayList<String> tempArray = new ArrayList();
    int tempStrSize = moves.length() - 1;
    for (;;)
    {
      from = moves.indexOf(" ", from);
      to = moves.indexOf(" ", from + 1);
      if ((0 <= from) && (0 <= to))
      {

        try
        {

          currentMove = moves.substring(from + 1, to).trim();
          tempArray.add(currentMove);
          LOG.debug(String.format("Processed following move in PGN: %s", new Object[] { currentMove }));
        }
        catch (StringIndexOutOfBoundsException exc)
        {
          LOG.error("setMoves/StringIndexOutOfBoundsException: error parsing file to load: ", exc);
          break;
        }
        if (n % 2 == 0)
        {
          from = moves.indexOf(".", to);
          if (from < to) {
            break;
          }
          
        }
        else
        {
          from = to;
        }
        n++;
        if (from <= tempStrSize) if (to > tempStrSize) {
            break;
          }
      }
    }
    for (exc = tempArray.iterator(); exc.hasNext();) { locMove = (String)exc.next();
      
      if (!isMoveCorrect(locMove.trim()))
      {

        throw new ReadGameError(String.format(Settings.lang("invalid_file_to_load"), new Object[] { locMove }), locMove);
      }
    }
    
    String locMove;
    boolean canMove = false;
    for (String locMove : tempArray)
    {
      if (Castling.isCastling(locMove))
      {
        int[] values = new int[4];
        Colors color = game.getActivePlayer().getColor();
        if (locMove.equals(Castling.LONG_CASTLING.getSymbol()))
        {
          values = Castling.LONG_CASTLING.getMove(color);
        }
        else if (locMove.equals(Castling.SHORT_CASTLING.getSymbol()))
        {
          values = Castling.SHORT_CASTLING.getMove(color);
        }
        canMove = game.simulateMove(values[0], values[1], values[2], values[3], null);
        
        if (!canMove)
        {

          throw new ReadGameError(String.format(Settings.lang("illegal_move_on"), new Object[] { locMove }), locMove);
        }
        
      }
      else
      {
        from = 0;
        int num = locMove.charAt(from);
        if ((num <= 90) && (num >= 65))
        {
          from = 1;
        }
        int xFrom = 9;
        int yFrom = 9;
        int xTo = 9;
        int yTo = 9;
        boolean pieceFound = false;
        if (locMove.length() <= 3)
        {
          Square[][] squares = game.getChessboard().getSquares();
          xTo = locMove.charAt(from) - 'a';
          yTo = Chessboard.getBottom() - (locMove.charAt(from + 1) - '1');
          for (int i = 0; (i < squares.length) && (!pieceFound); i++)
          {
            for (int j = 0; (j < squares[i].length) && (!pieceFound); j++)
            {
              if ((piece != null) && 
                (game.getActivePlayer().getColor() == squares[i][j].getPiece().getPlayer().getColor()))
              {


                Set<Square> pieceMoves = squares[i][j].getPiece().getAllMoves();
                for (Object square : pieceMoves)
                {
                  Square currSquare = (Square)square;
                  if ((currSquare.getPozX() == xTo) && (currSquare.getPozY() == yTo))
                  {
                    xFrom = squares[i][j].getPiece().getSquare().getPozX();
                    yFrom = squares[i][j].getPiece().getSquare().getPozY();
                    pieceFound = true;
                  }
                }
              }
            }
          }
        }
        else {
          xFrom = locMove.charAt(from) - 'a';
          yFrom = Chessboard.getBottom() - (locMove.charAt(from + 1) - '1');
          xTo = locMove.charAt(from + 3) - 'a';
          yTo = Chessboard.getBottom() - (locMove.charAt(from + 4) - '1');
        }
        canMove = game.simulateMove(xFrom, yFrom, xTo, yTo, null);
        if (!canMove)
        {
          game.getChessboard().resetActiveSquare();
          
          throw new ReadGameError(String.format(Settings.lang("illegal_move_on"), new Object[] { locMove }), locMove);
        }
      }
    }
  }
  




  public Stack<Move> getMoveBackStack()
  {
    return moveBackStack;
  }
  
  public void decrementFiftyMoveRule()
  {
    fiftyMoveRuleCounter -= 1;
  }
  
  public void incrementFiftyMoveRule(Move move)
  {
    if ((!(move.getMovedPiece() instanceof Pawn)) && (null == move.getTakenPiece()))
    {
      fiftyMoveRuleCounter += 1;
    }
  }
  



  public int getFiftyMoveRuleCounter()
  {
    return fiftyMoveRuleCounter;
  }
  



  public void setFiftyMoveRuleCounter(int fiftyMoveRuleCounter)
  {
    this.fiftyMoveRuleCounter = fiftyMoveRuleCounter;
  }
}
