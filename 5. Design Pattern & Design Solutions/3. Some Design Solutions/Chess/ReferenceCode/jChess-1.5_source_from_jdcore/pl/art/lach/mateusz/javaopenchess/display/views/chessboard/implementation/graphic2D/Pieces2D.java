package pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;

















public class Pieces2D
{
  private static final Logger LOG = Logger.getLogger(Pieces2D.class);
  
  private static final String FILE_EXT = ".png";
  
  protected Map<Colors, Map<String, Image>> currentImageSet = new HashMap();
  
  protected static Pieces2D instance = null;
  
  protected static int currentSize = 55;
  
  protected static TreeSet<Integer> setsSizes = new TreeSet(Arrays.asList(new Integer[] { Integer.valueOf(25), Integer.valueOf(55), Integer.valueOf(70), Integer.valueOf(100) }));
  
  protected static Map<Integer, Map<Colors, Map<String, Image>>> imageSets = initImagesSets();
  



  public Map<Colors, Map<String, Image>> getCurrentImageSet()
  {
    return currentImageSet;
  }
  



  public void setCurrentImageSet(Map<Colors, Map<String, Image>> aImages)
  {
    currentImageSet = aImages;
  }
  



  public static int getCurrentSize()
  {
    return currentSize;
  }
  



  public static void setCurrentSize(int aCurrentSize)
  {
    currentSize = aCurrentSize;
  }
  
  protected Pieces2D()
  {
    currentImageSet = ((Map)imageSets.get(Integer.valueOf(currentSize)));
  }
  
  public final void resize(int squareSize)
  {
    if (null != setsSizes)
    {
      int closest = getSizeToLoad(squareSize);
      if (currentSize != closest)
      {
        currentSize = closest;
        currentImageSet = ((Map)imageSets.get(Integer.valueOf(currentSize)));
      }
    }
  }
  
  private static Map<Integer, Map<Colors, Map<String, Image>>> initImagesSets()
  {
    Map<Integer, Map<Colors, Map<String, Image>>> result = new HashMap();
    for (Iterator localIterator = setsSizes.iterator(); localIterator.hasNext();) { int size = ((Integer)localIterator.next()).intValue();
      
      Map<Colors, Map<String, Image>> localImages = new HashMap();
      localImages.put(Colors.BLACK, getPieceMap(Colors.BLACK, size));
      localImages.put(Colors.WHITE, getPieceMap(Colors.WHITE, size));
      result.put(Integer.valueOf(size), localImages);
    }
    return result;
  }
  
  private static Map<String, Image> getPieceMap(Colors color, int size)
  {
    Map<String, Image> result = new HashMap();
    
    result.put(Pawn.class.getName(), GUI.loadPieceImage(Pawn.class.getSimpleName(), color, size, ".png"));
    result.put(Knight.class.getName(), GUI.loadPieceImage(Knight.class.getSimpleName(), color, size, ".png"));
    result.put(Queen.class.getName(), GUI.loadPieceImage(Queen.class.getSimpleName(), color, size, ".png"));
    result.put(Rook.class.getName(), GUI.loadPieceImage(Rook.class.getSimpleName(), color, size, ".png"));
    result.put(King.class.getName(), GUI.loadPieceImage(King.class.getSimpleName(), color, size, ".png"));
    result.put(Bishop.class.getName(), GUI.loadPieceImage(Bishop.class.getSimpleName(), color, size, ".png"));
    
    return result;
  }
  
  private int getSizeToLoad(int squareHeight)
  {
    Integer closest = (Integer)setsSizes.ceiling(Integer.valueOf(squareHeight));
    if (null == closest)
    {
      closest = (Integer)setsSizes.last();
    }
    return closest.intValue();
  }
  
  public static synchronized Pieces2D getInstance()
  {
    if (null == instance)
    {
      instance = new Pieces2D();
    }
    return instance;
  }
  
  public Image getImage(Colors color, Piece piece)
  {
    return (Image)((Map)getCurrentImageSet().get(color)).get(piece.getClass().getName());
  }
  
  public static void draw(ChessboardView chessboardView, Piece piece, Graphics g, Image image)
  {
    draw(chessboardView, piece, piece.getSquare().getPozX(), piece.getSquare().getPozY(), g, image);
  }
  
  public static void draw(ChessboardView chessboardView, Piece piece, int pozX, int pozY, Graphics g, Image image)
  {
    try
    {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Point topLeft = chessboardView.getTopLeftPoint();
      int height = chessboardView.getSquareHeight();
      int x = pozX * height + x;
      int y = pozY * height + y;
      if ((image != null) && (g != null))
      {
        Image tempImage = image;
        BufferedImage resized = new BufferedImage(height, height, 3);
        Graphics2D imageGr = resized.createGraphics();
        imageGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageGr.drawImage(tempImage, 0, 0, height, height, null);
        imageGr.dispose();
        image = resized.getScaledInstance(height, height, 0);
        g2d.drawImage(image, x, y, null);
      }
      else
      {
        LOG.error("Piece/draw: image is null!");
      }
    }
    catch (NullPointerException exc)
    {
      LOG.error("Something wrong when painting piece: " + exc.getMessage());
    }
  }
}
