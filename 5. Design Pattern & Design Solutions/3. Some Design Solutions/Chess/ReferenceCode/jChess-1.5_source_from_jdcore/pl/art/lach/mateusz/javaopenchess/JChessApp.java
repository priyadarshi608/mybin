package pl.art.lach.mateusz.javaopenchess;

import java.awt.Window;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;




















public class JChessApp
  extends SingleFrameApplication
{
  protected static JChessView javaChessView;
  public static final String LOG_FILE = "log4j.properties";
  public static final String MAIN_PACKAGE_NAME = JChessApp.class.getPackage().getName();
  

  public JChessApp() {}
  
  public static JChessView getJavaChessView()
  {
    return javaChessView;
  }
  




  protected void startup()
  {
    javaChessView = new JChessView(this);
    show(getJavaChessView());
  }
  





  protected void configureWindow(Window root) {}
  





  public static JChessApp getApplication()
  {
    return (JChessApp)Application.getInstance(JChessApp.class);
  }
  




  public static void main(String[] args)
  {
    launch(JChessApp.class, args);
    Properties logProp = new Properties();
    try
    {
      logProp.load(JChessApp.class.getClassLoader().getResourceAsStream("log4j.properties"));
      PropertyConfigurator.configure(logProp);
    }
    catch (NullPointerException|IOException e)
    {
      System.err.println("Logging not enabled : " + e.getMessage());
    }
  }
}
