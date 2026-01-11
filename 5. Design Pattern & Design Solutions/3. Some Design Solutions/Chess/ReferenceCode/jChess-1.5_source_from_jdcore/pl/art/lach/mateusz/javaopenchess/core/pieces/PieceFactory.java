package pl.art.lach.mateusz.javaopenchess.core.pieces;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;


















public class PieceFactory
{
  public PieceFactory() {}
  
  public static final Piece getPiece(Chessboard chessboard, Colors color, String pieceType, Player player)
  {
    return getPiece(chessboard, color.getColorName(), pieceType, player);
  }
  
  public static final Piece getPiece(Chessboard chessboard, String color, String pieceType, Player player)
  {
    Piece piece = null;
    switch (pieceType)
    {
    case "Queen": 
      piece = new Queen(chessboard, player);
      break;
    case "Rook": 
      piece = new Rook(chessboard, player);
      break;
    case "Bishop": 
      piece = new Bishop(chessboard, player);
      break;
    case "Knight": 
      piece = new Knight(chessboard, player);
      break;
    case "Pawn": 
      piece = new Pawn(chessboard, player);
    }
    
    return piece;
  }
  
  /* Error */
  public static final Piece getPieceFromFenNotation(Chessboard chessboard, String pieceChar, Player whitePlayer, Player blackPlayer)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aload_1
    //   7: invokevirtual 21	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   10: aload_1
    //   11: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   14: ifeq +9 -> 23
    //   17: aload_3
    //   18: astore 5
    //   20: goto +6 -> 26
    //   23: aload_2
    //   24: astore 5
    //   26: aload_1
    //   27: invokevirtual 21	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   30: astore_1
    //   31: aload_1
    //   32: astore 6
    //   34: iconst_m1
    //   35: istore 7
    //   37: aload 6
    //   39: invokevirtual 4	java/lang/String:hashCode	()I
    //   42: tableswitch	default:+175->217, 98:+98->140, 99:+175->217, 100:+175->217, 101:+175->217, 102:+175->217, 103:+175->217, 104:+175->217, 105:+175->217, 106:+175->217, 107:+146->188, 108:+175->217, 109:+175->217, 110:+162->204, 111:+175->217, 112:+82->124, 113:+114->156, 114:+130->172
    //   124: aload 6
    //   126: ldc 22
    //   128: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   131: ifeq +86 -> 217
    //   134: iconst_0
    //   135: istore 7
    //   137: goto +80 -> 217
    //   140: aload 6
    //   142: ldc 23
    //   144: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   147: ifeq +70 -> 217
    //   150: iconst_1
    //   151: istore 7
    //   153: goto +64 -> 217
    //   156: aload 6
    //   158: ldc 24
    //   160: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   163: ifeq +54 -> 217
    //   166: iconst_2
    //   167: istore 7
    //   169: goto +48 -> 217
    //   172: aload 6
    //   174: ldc 25
    //   176: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   179: ifeq +38 -> 217
    //   182: iconst_3
    //   183: istore 7
    //   185: goto +32 -> 217
    //   188: aload 6
    //   190: ldc 26
    //   192: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   195: ifeq +22 -> 217
    //   198: iconst_4
    //   199: istore 7
    //   201: goto +16 -> 217
    //   204: aload 6
    //   206: ldc 27
    //   208: invokevirtual 6	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   211: ifeq +6 -> 217
    //   214: iconst_5
    //   215: istore 7
    //   217: iload 7
    //   219: tableswitch	default:+127->346, 0:+37->256, 1:+52->271, 2:+67->286, 3:+82->301, 4:+97->316, 5:+112->331
    //   256: new 19	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Pawn
    //   259: dup
    //   260: aload_0
    //   261: aload 5
    //   263: invokespecial 20	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Pawn:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   266: astore 4
    //   268: goto +81 -> 349
    //   271: new 15	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Bishop
    //   274: dup
    //   275: aload_0
    //   276: aload 5
    //   278: invokespecial 16	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Bishop:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   281: astore 4
    //   283: goto +66 -> 349
    //   286: new 11	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Queen
    //   289: dup
    //   290: aload_0
    //   291: aload 5
    //   293: invokespecial 12	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Queen:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   296: astore 4
    //   298: goto +51 -> 349
    //   301: new 13	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Rook
    //   304: dup
    //   305: aload_0
    //   306: aload 5
    //   308: invokespecial 14	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Rook:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   311: astore 4
    //   313: goto +36 -> 349
    //   316: new 28	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/King
    //   319: dup
    //   320: aload_0
    //   321: aload 5
    //   323: invokespecial 29	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/King:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   326: astore 4
    //   328: goto +21 -> 349
    //   331: new 17	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Knight
    //   334: dup
    //   335: aload_0
    //   336: aload 5
    //   338: invokespecial 18	pl/art/lach/mateusz/javaopenchess/core/pieces/implementation/Knight:<init>	(Lpl/art/lach/mateusz/javaopenchess/core/Chessboard;Lpl/art/lach/mateusz/javaopenchess/core/players/Player;)V
    //   341: astore 4
    //   343: goto +6 -> 349
    //   346: aconst_null
    //   347: astore 4
    //   349: aload 4
    //   351: areturn
    // Line number table:
    //   Java source line #64	-> byte code offset #0
    //   Java source line #65	-> byte code offset #3
    //   Java source line #66	-> byte code offset #6
    //   Java source line #68	-> byte code offset #17
    //   Java source line #72	-> byte code offset #23
    //   Java source line #74	-> byte code offset #26
    //   Java source line #75	-> byte code offset #31
    //   Java source line #78	-> byte code offset #256
    //   Java source line #79	-> byte code offset #268
    //   Java source line #81	-> byte code offset #271
    //   Java source line #82	-> byte code offset #283
    //   Java source line #84	-> byte code offset #286
    //   Java source line #85	-> byte code offset #298
    //   Java source line #87	-> byte code offset #301
    //   Java source line #88	-> byte code offset #313
    //   Java source line #90	-> byte code offset #316
    //   Java source line #91	-> byte code offset #328
    //   Java source line #93	-> byte code offset #331
    //   Java source line #94	-> byte code offset #343
    //   Java source line #96	-> byte code offset #346
    //   Java source line #99	-> byte code offset #349
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	chessboard	Chessboard
    //   0	352	1	pieceChar	String
    //   0	352	2	whitePlayer	Player
    //   0	352	3	blackPlayer	Player
    //   1	349	4	result	Piece
    //   4	333	5	player	Player
    //   32	173	6	str	String
    //   35	183	7	i	int
  }
}
