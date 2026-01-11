package pl.art.lach.mateusz.javaopenchess.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.ai.AI;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.moves.MovesHistory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.display.panels.LocalSettingsView;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.display.windows.JChessTabbedPane;
import pl.art.lach.mateusz.javaopenchess.network.Chat;
import pl.art.lach.mateusz.javaopenchess.network.Client;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;



















public class Game
  extends JPanel
  implements ComponentListener, MouseListener
{
  private static final Logger LOG = Logger.getLogger(Game.class);
  


  protected Settings settings;
  


  private boolean blockedChessboard;
  


  protected Chessboard chessboard;
  


  protected Player activePlayer;
  


  protected GameClock gameClock;
  


  protected Client client;
  


  protected MovesHistory moves;
  


  protected Chat chat;
  


  protected JTabbedPane tabPane;
  


  protected JTextField fenState;
  


  protected LocalSettingsView localSettingsView;
  


  private AI ai = null;
  
  private boolean isEndOfGame = false;
  
  private static final String FEN_PREFIX_NAME = "FEN: ";
  
  private DataExporter fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
  
  public Game()
  {
    init();
  }
  
  protected final void init()
  {
    setLayout(null);
    moves = new MovesHistory(this);
    settings = new Settings();
    chessboard = new Chessboard(getSettings(), moves);
    
    ChessboardView chessboardView = chessboard.getChessboardView();
    int chessboardWidth = chessboardView.getChessboardWidht(true);
    add(chessboardView);
    

    gameClock = new GameClock(this);
    gameClock.setSize(new Dimension(200, 100));
    gameClock.setLocation(new Point(500, 0));
    add(gameClock);
    
    JScrollPane movesHistory = moves.getScrollPane();
    movesHistory.setSize(new Dimension(180, 350));
    movesHistory.setLocation(new Point(500, 121));
    add(movesHistory);
    
    chat = new Chat();
    tabPane = new JTabbedPane();
    localSettingsView = new LocalSettingsView(this);
    tabPane.addTab(Settings.lang("game_chat"), chat);
    tabPane.addTab(Settings.lang("game_settings"), localSettingsView);
    tabPane.setSize(new Dimension(380, 100));
    tabPane.setLocation(new Point(chessboardWidth, chessboardWidth / 2));
    tabPane.setMinimumSize(new Dimension(400, 100));
    add(tabPane);
    
    fenState = new JTextField();
    fenState.setEditable(false);
    fenState.setSize(new Dimension(chessboardWidth + 180, 20));
    fenState.setLocation(new Point(0, 500));
    add(fenState);
    
    setBlockedChessboard(false);
    setLayout(null);
    setDoubleBuffered(true);
    chessboardView.addMouseListener(this);
    addComponentListener(this);
  }
  
  public final void updateFenStateText()
  {
    fenState.setText("FEN: " + exportGame(fenExporter));
  }
  






  public String saveGame(File path, DataExporter dataExporter)
  {
    File file = path;
    FileWriter fileW = null;
    String str = null;
    try
    {
      fileW = new FileWriter(file);
      str = exportGame(dataExporter);
      fileW.write(str);
      fileW.flush();
      fileW.close();
      JOptionPane.showMessageDialog(this, Settings.lang("game_saved_properly"));
    }
    catch (IOException exc)
    {
      LOG.error("error writing to file: ", exc);
      JOptionPane.showMessageDialog(this, Settings.lang("error_writing_to_file") + ": " + exc);
      return null;
    }
    return str;
  }
  
  public String exportGame(DataExporter de)
  {
    if (null == de)
    {
      return null;
    }
    return de.exportData(this);
  }
  
  public void importGame(String dataInString, DataImporter di) throws ReadGameError
  {
    if (null == di)
    {
      return;
    }
    di.importData(dataInString, this);
  }
  



  public void newGame()
  {
    getChessboard().setPieces4NewGame(
      getSettings().getPlayerWhite(), 
      getSettings().getPlayerBlack());
    

    activePlayer = getSettings().getPlayerWhite();
    if (activePlayer.getPlayerType() != PlayerType.LOCAL_USER)
    {
      setBlockedChessboard(true);
    }
    runRenderingArtifactDirtyFix();
    updateFenStateText();
  }
  

  private void runRenderingArtifactDirtyFix()
    throws ArrayIndexOutOfBoundsException
  {
    JChessView jChessView = JChessApp.getJavaChessView();
    if (null != jChessView)
    {
      Game activeGame = JChessApp.getJavaChessView().getActiveTabGame();
      if (null != activeGame) {
        Chessboard activeChessboard = activeGame.getChessboard();
        ChessboardView chessboardView = activeChessboard.getChessboardView();
        if (JChessApp.getJavaChessView().getNumberOfOpenedTabs() == 0)
        {
          chessboardView.resizeChessboard(chessboardView.getChessboardHeight(false));
          activeChessboard.repaint();
          activeGame.repaint();
        }
      }
      chessboard.repaint();
      repaint();
    }
  }
  




  public void endGame(String message)
  {
    setBlockedChessboard(true);
    isEndOfGame = true;
    LOG.debug(message);
    JOptionPane.showMessageDialog(null, message);
  }
  


  public void switchActive()
  {
    if (activePlayer == getSettings().getPlayerWhite())
    {
      activePlayer = getSettings().getPlayerBlack();
    }
    else
    {
      activePlayer = getSettings().getPlayerWhite();
    }
    getGameClock().switchClocks();
  }
  



  public Player getActivePlayer()
  {
    return activePlayer;
  }
  
  public void setActivePlayer(Player player)
  {
    activePlayer = player;
  }
  


  public void nextMove()
  {
    switchActive();
    LOG.debug(String.format("next move, active player: %s, color: %s, type: %s", new Object[] {activePlayer
      .getName(), activePlayer
      .getColor().name(), activePlayer
      .getPlayerType().name() }));
    

    if (activePlayer.getPlayerType() == PlayerType.LOCAL_USER)
    {
      setBlockedChessboard(false);
    }
    else if ((activePlayer.getPlayerType() == PlayerType.NETWORK_USER) || 
      (activePlayer.getPlayerType() == PlayerType.COMPUTER))
    {
      setBlockedChessboard(true);
    }
    updateFenStateText();
  }
  








  public boolean simulateMove(int beginX, int beginY, int endX, int endY, String promoted)
  {
    try
    {
      Square begin = getChessboard().getSquare(beginX, beginY);
      Square end = getChessboard().getSquare(endX, endY);
      getChessboard().select(begin);
      if (getChessboard().getActiveSquare().getPiece().getAllMoves().contains(end))
      {
        getChessboard().move(begin, end);
        if ((null != promoted) && (!"".equals(promoted)))
        {
          Piece promotedPiece = PieceFactory.getPiece(
            getChessboard(), end
            .getPiece().getPlayer().getColor(), promoted, activePlayer);
          

          end.setPiece(promotedPiece);
        }
      }
      else
      {
        LOG.debug(String.format("Bad move: beginX: %s beginY: %s endX: %s endY: %s", new Object[] {
        
          Integer.valueOf(beginX), Integer.valueOf(beginY), Integer.valueOf(endX), Integer.valueOf(endY) }));
        
        return false;
      }
      getChessboard().unselect();
      nextMove();
      
      return true;

    }
    catch (StringIndexOutOfBoundsException|ArrayIndexOutOfBoundsException|NullPointerException exc)
    {
      LOG.error("simulateMove error: ", exc); }
    return false;
  }
  


  public void mouseClicked(MouseEvent arg0) {}
  


  public boolean undo()
  {
    boolean status = false;
    
    if (getSettings().getGameType() != GameTypes.NETWORK)
    {
      status = getChessboard().undo();
      if (status)
      {
        switchActive();
      }
      else
      {
        getChessboard().repaint();
      }
      if (getSettings().isGameAgainstComputer())
      {
        if (getActivePlayer().getPlayerType() == PlayerType.COMPUTER)
        {
          undo();
        }
      }
    }
    else if (getSettings().getGameType() == GameTypes.NETWORK)
    {
      getClient().sendUndoAsk();
      status = true;
    }
    updateFenStateText();
    return status;
  }
  
  public boolean rewindToBegin()
  {
    boolean result = false;
    
    if (getSettings().getGameType() == GameTypes.LOCAL)
    {
      while (getChessboard().undo())
      {
        result = true;
      }
    }
    


    throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
    

    updateFenStateText();
    return result;
  }
  
  public boolean rewindToEnd() throws UnsupportedOperationException
  {
    boolean result = false;
    
    if (getSettings().getGameType() == GameTypes.LOCAL)
    {
      while (getChessboard().redo())
      {
        result = true;
      }
    }
    


    throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
    

    updateFenStateText();
    return result;
  }
  
  public boolean redo()
  {
    boolean status = getChessboard().redo();
    if (getSettings().getGameType() == GameTypes.LOCAL)
    {
      if (status)
      {
        nextMove();
      }
      else
      {
        getChessboard().repaint();
      }
      

    }
    else {
      throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
    }
    
    updateFenStateText();
    return status;
  }
  


  public void mousePressed(MouseEvent event)
  {
    if (event.getButton() == 3)
    {
      undo();
    }
    else if ((event.getButton() == 2) && (getSettings().getGameType() == GameTypes.LOCAL))
    {
      redo();
    }
    else if (event.getButton() == 1)
    {
      if (!isChessboardBlocked())
      {
        moveActionInvoked(event);
      }
      else
      {
        LOG.debug("Chessboard is blocked");
      }
    }
    updateFenStateText();
  }
  
  private void moveActionInvoked(MouseEvent event) throws ArrayIndexOutOfBoundsException
  {
    try
    {
      int x = event.getX();
      int y = event.getY();
      
      Square sq = getChessboard().getChessboardView().getSquare(x, y);
      if (cannotInvokeMoveAction(sq))
      {
        return;
      }
      if (isSelectAction(sq))
      {
        selectSquare(sq);
      }
      else if (getChessboard().getActiveSquare() == sq)
      {
        getChessboard().unselect();
      }
      else if (canInvokeMoveAction(sq))
      {
        if (getSettings().getGameType() == GameTypes.LOCAL)
        {
          getChessboard().move(getChessboard().getActiveSquare(), sq);
        }
        else if (getSettings().getGameType() == GameTypes.NETWORK)
        {
          moveNetworkActionInvoked(sq);
        }
        getChessboard().unselect();
        

        nextMove();
        
        King king;
        King king;
        if (activePlayer == getSettings().getPlayerWhite())
        {
          king = getChessboard().getKingWhite();
        }
        else
        {
          king = getChessboard().getKingBlack();
        }
        
        switch (king.isCheckmatedOrStalemated())
        {
        case 1: 
          endGame("Checkmate! " + king.getPlayer().getColor().toString() + " player lose!");
          break;
        case 2: 
          endGame("Stalemate! Draw!");
        }
        
      }
      if (canDoComputerMove())
      {
        doComputerMove();
        highlighTabIfInactive();
      }
      
    }
    catch (NullPointerException exc)
    {
      LOG.error("NullPointerException: " + exc.getMessage(), exc);
      getChessboard().repaint();
    }
    updateFenStateText();
  }
  

  private boolean canInvokeMoveAction(Square sq)
  {
    return (getChessboard().getActiveSquare() != null) && (getChessboardgetActiveSquarepiece != null) && (getChessboard().getActiveSquare().getPiece().getAllMoves().contains(sq));
  }
  
  private void selectSquare(Square sq)
  {
    getChessboard().unselect();
    getChessboard().select(sq);
  }
  

  private boolean isSelectAction(Square sq)
  {
    return (piece != null) && (sq.getPiece().getPlayer() == activePlayer) && (sq != getChessboard().getActiveSquare());
  }
  


  private boolean cannotInvokeMoveAction(Square sq)
  {
    return ((sq == null) && (piece == null) && (getChessboard().getActiveSquare() == null)) || ((getChessboard().getActiveSquare() == null) && (piece != null) && (sq.getPiece().getPlayer() != activePlayer));
  }
  
  private void moveNetworkActionInvoked(Square sq)
  {
    Square from = getChessboard().getActiveSquare();
    boolean canBePromoted = (Pawn.class == from.getPiece().getClass()) && (Pawn.canBePromoted(sq));
    getChessboard().move(from, sq);
    Piece promoted = null;
    if (canBePromoted)
    {
      promoted = sq.getPiece();
    }
    getClient().sendMove(from
      .getPozX(), from
      .getPozY(), sq
      .getPozX(), sq
      .getPozY(), null == promoted ? "" : promoted
      .getName());
  }
  
  private void highlighTabIfInactive() throws ArrayIndexOutOfBoundsException
  {
    JChessView jChessView = JChessApp.getJavaChessView();
    int tabNumber = jChessView.getTabNumber(this);
    if (jChessView.getActiveTabGame() != this)
    {
      jChessView.getGamesPane().setForegroundAt(tabNumber, JChessTabbedPane.EVENT_COLOR);
    }
  }
  



  private boolean canDoComputerMove()
  {
    return (!isEndOfGame) && (getSettings().isGameAgainstComputer()) && (getActivePlayer().getPlayerType() == PlayerType.COMPUTER) && (null != getAi());
  }
  
  public void doComputerMove()
  {
    Move move = getAi().getMove(this, getMoves().getLastMoveFromHistory());
    getChessboard().move(move.getFrom(), move.getTo());
    if (null != move.getPromotedPiece())
    {
      move.getTo().setPiece(move.getPromotedPiece());
    }
    nextMove();
  }
  



  public void mouseReleased(MouseEvent arg0) {}
  



  public void mouseEntered(MouseEvent arg0) {}
  


  public void mouseExited(MouseEvent arg0) {}
  


  public void componentResized(ComponentEvent e)
  {
    resizeGame();
  }
  

  public void componentMoved(ComponentEvent e)
  {
    componentResized(e);
    repaint();
  }
  

  public void componentShown(ComponentEvent e)
  {
    componentResized(e);
  }
  



  public void componentHidden(ComponentEvent e) {}
  



  public Chessboard getChessboard()
  {
    return chessboard;
  }
  



  public final Settings getSettings()
  {
    return settings;
  }
  



  public boolean isChessboardBlocked()
  {
    return isBlockedChessboard();
  }
  



  public GameClock getGameClock()
  {
    return gameClock;
  }
  



  public Client getClient()
  {
    return client;
  }
  



  public MovesHistory getMoves()
  {
    return moves;
  }
  



  public Chat getChat()
  {
    return chat;
  }
  




  public void setSettings(Settings settings)
  {
    this.settings = settings;
    chessboard.setSettings(settings);
  }
  



  public void setClient(Client client)
  {
    this.client = client;
  }
  



  public void setChat(Chat chat)
  {
    this.chat = chat;
  }
  

  public void repaint()
  {
    super.repaint();
    if (null != tabPane)
    {
      tabPane.repaint();
    }
    if (null != localSettingsView)
    {
      localSettingsView.repaint();
    }
    if (null != chessboard)
    {
      getChessboard().repaint();
    }
  }
  
  public void resizeGame()
  {
    int height = getHeight() >= getWidth() ? getWidth() : getHeight();
    int chessHeight = (int)(Math.round(height * 0.8D / 8.0D) * 8L);
    
    JScrollPane movesScrollPane = getMoves().getScrollPane();
    ChessboardView chessboardView = getChessboard().getChessboardView();
    chessboardView.resizeChessboard(chessHeight);
    chessHeight = chessboardView.getHeight();
    int chessWidthWithLabels = chessboardView.getChessboardWidht(true);
    movesScrollPane.setLocation(new Point(chessHeight + 5, 100));
    movesScrollPane.setSize(movesScrollPane.getWidth(), chessHeight - 100 - chessWidthWithLabels / 4);
    fenState.setLocation(new Point(0, chessHeight + 5));
    fenState.setSize(new Dimension(chessWidthWithLabels + 180, 30));
    getGameClock().setLocation(new Point(chessHeight + 5, 0));
    if (null != tabPane)
    {
      tabPane.setLocation(new Point(chessWidthWithLabels + 5, chessWidthWithLabels / 4 * 3));
      tabPane.setSize(new Dimension(movesScrollPane.getWidth(), chessWidthWithLabels / 4));
      tabPane.repaint();
    }
  }
  


  public AI getAi()
  {
    return ai;
  }
  


  public void setAi(AI ai)
  {
    this.ai = ai;
  }
  


  public boolean isIsEndOfGame()
  {
    return isEndOfGame;
  }
  


  public void setIsEndOfGame(boolean isEndOfGame)
  {
    this.isEndOfGame = isEndOfGame;
  }
  



  public boolean isBlockedChessboard()
  {
    return blockedChessboard;
  }
  



  public void setBlockedChessboard(boolean blockedChessboard)
  {
    this.blockedChessboard = blockedChessboard;
  }
  



  public JTextField getFenState()
  {
    return fenState;
  }
  



  public void setFenState(JTextField fenState)
  {
    this.fenState = fenState;
  }
}
