package pl.art.lach.mateusz.javaopenchess.utils;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;





















public class GUI
{
  private static Properties configFile;
  private static final String IMAGE_PATH = "theme/%s/images/%s";
  private static final Logger LOG = Logger.getLogger(GUI.class);
  

  private static final String JAR_FILENAME = "[a-zA-Z0-9%!@\\-#$%^&*\\(\\)\\[\\]\\{\\}\\.\\,\\s]+\\.jar";
  

  private static final String CONFIG_FILENAME = "config.txt";
  
  private static final String THEME = "THEME";
  
  private static final String SLASH = "/";
  
  private static final String SPACE_IN_HEX = "%20";
  
  private Game game;
  

  public GUI()
  {
    game = new Game();
  }
  
  public static Image loadPieceImage(String pieceName, Colors color, int size, String fileExt)
  {
    String colorSymbol = String.valueOf(color.getSymbol()).toUpperCase();
    return loadImage(pieceName + "-" + colorSymbol + size + fileExt);
  }
  





  public static Image loadImage(String name)
  {
    if (null == getConfigFile())
    {
      return null;
    }
    return loadAndReturnImage(name);
  }
  
  private static Image loadAndReturnImage(String name)
  {
    Image img = null;
    try
    {
      String imageLink = String.format("theme/%s/images/%s", new Object[] { getConfigFile().getProperty("THEME", "default"), name });
      LOG.debug("THEME: " + getConfigFile().getProperty("THEME"));
      img = ImageIO.read(JChessApp.class.getResourceAsStream(imageLink));
    }
    catch (Exception e)
    {
      LOG.error("some error loading image!, message: " + e.getMessage() + " stackTrace: " + 
        Arrays.toString(e.getStackTrace()));
    }
    
    return img;
  }
  

  public static boolean themeIsValid(String name)
  {
    return true;
  }
  




  public static String getJarPath()
  {
    String path = GUI.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    path = path.replaceAll("[a-zA-Z0-9%!@\\-#$%^&*\\(\\)\\[\\]\\{\\}\\.\\,\\s]+\\.jar", "");
    int lastSlash = path.lastIndexOf(File.separator);
    if (path.length() - 1 == lastSlash)
    {
      path = path.substring(0, lastSlash);
    }
    path = path.replace("%20", " ");
    return path;
  }
  

  private static String getFullConfigFilePath()
  {
    String result = getJarPath() + "config.txt";
    if (result.startsWith("/"))
    {
      result = result.replaceFirst("/", "");
    }
    return result;
  }
  

  public static synchronized Properties getConfigFile()
  {
    if (null == configFile)
    {
      loadConfigFile();
    }
    return configFile;
  }
  
  private static void loadConfigFile()
  {
    Properties defConfFile = new Properties();
    Properties confFile = new Properties();
    
    File outFile = new File(getFullConfigFilePath());
    loadDefaultConfigFile(defConfFile);
    if (!outFile.exists())
    {
      saveConfigFileOutsideJar(defConfFile, outFile);
    }
    loadOuterConfigFile(confFile, outFile);
    configFile = confFile;
  }
  
  private static void loadOuterConfigFile(Properties confFile, File outFile)
  {
    try
    {
      confFile.load(new FileInputStream(outFile));
    }
    catch (IOException e)
    {
      LOG.error("IOException, some error during getting config file!,, message: " + e.getMessage() + " stackTrace: " + 
        Arrays.toString(e.getStackTrace()));
    }
  }
  

  private static void saveConfigFileOutsideJar(Properties defConfFile, File outFile)
  {
    try
    {
      defConfFile.store(new FileOutputStream(outFile), null);
    }
    catch (IOException e)
    {
      LOG.error("IOException, some error during getting config file!,, message: " + e.getMessage() + " stackTrace: " + 
        Arrays.toString(e.getStackTrace()));
    }
  }
  

  private static void loadDefaultConfigFile(Properties defConfFile)
  {
    try
    {
      InputStream is = GUI.class.getResourceAsStream("config.txt");
      if (null != is) {
        defConfFile.load(is);
      }
    }
    catch (IOException|NullPointerException e)
    {
      LOG.error("IOException, some error during getting config file!, message: " + e.getMessage() + " stackTrace: " + 
        Arrays.toString(e.getStackTrace()));
    }
  }
  




  public Game getGame()
  {
    return game;
  }
}
