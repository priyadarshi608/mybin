package org.jdesktop.application;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;













public class LocalStorage
  extends AbstractBean
{
  private static Logger logger = Logger.getLogger(LocalStorage.class.getName());
  private final ApplicationContext context;
  private long storageLimit = -1L;
  private LocalIO localIO = null;
  private final File unspecifiedFile = new File("unspecified");
  private File directory = unspecifiedFile;
  
  protected LocalStorage(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null) {
      throw new IllegalArgumentException("null context");
    }
    context = paramApplicationContext;
  }
  
  protected final ApplicationContext getContext()
  {
    return context;
  }
  
  private void checkFileName(String paramString) {
    if (paramString == null) {
      throw new IllegalArgumentException("null fileName");
    }
  }
  
  public InputStream openInputFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().openInputFile(paramString);
  }
  
  public OutputStream openOutputFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().openOutputFile(paramString);
  }
  
  public boolean deleteFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().deleteFile(paramString);
  }
  

  private static class AbortExceptionListener
    implements ExceptionListener
  {
    private AbortExceptionListener() {}
    
    public Exception exception = null;
    
    public void exceptionThrown(Exception paramException) { if (exception == null) exception = paramException;
    }
  }
  
  private static boolean persistenceDelegatesInitialized = false;
  
  public void save(Object paramObject, String paramString) throws IOException {
    AbortExceptionListener localAbortExceptionListener = new AbortExceptionListener(null);
    XMLEncoder localXMLEncoder = null;
    


    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try {
      localXMLEncoder = new XMLEncoder(localByteArrayOutputStream);
      if (!persistenceDelegatesInitialized) {
        localXMLEncoder.setPersistenceDelegate(Rectangle.class, new RectanglePD());
        persistenceDelegatesInitialized = true;
      }
      localXMLEncoder.setExceptionListener(localAbortExceptionListener);
      localXMLEncoder.writeObject(paramObject);
    }
    finally {
      if (localXMLEncoder != null) localXMLEncoder.close();
    }
    if (exception != null) {
      throw new LSException("save failed \"" + paramString + "\"", exception);
    }
    OutputStream localOutputStream = null;
    try {
      localOutputStream = openOutputFile(paramString);
      localOutputStream.write(localByteArrayOutputStream.toByteArray());
    }
    finally {
      if (localOutputStream != null) localOutputStream.close();
    }
  }
  
  public Object load(String paramString) throws IOException {
    InputStream localInputStream = null;
    try {
      localInputStream = openInputFile(paramString);
    }
    catch (IOException localIOException) {
      return null;
    }
    AbortExceptionListener localAbortExceptionListener = new AbortExceptionListener(null);
    XMLDecoder localXMLDecoder = null;
    try {
      localXMLDecoder = new XMLDecoder(localInputStream);
      localXMLDecoder.setExceptionListener(localAbortExceptionListener);
      Object localObject1 = localXMLDecoder.readObject();
      if (exception != null) {
        throw new LSException("load failed \"" + paramString + "\"", exception);
      }
      return localObject1;
    }
    finally {
      if (localXMLDecoder != null) localXMLDecoder.close();
    }
  }
  
  private void closeStream(Closeable paramCloseable, String paramString) throws IOException {
    if (paramCloseable != null) {
      try { paramCloseable.close();
      } catch (IOException localIOException) {
        throw new LSException("close failed \"" + paramString + "\"", localIOException);
      }
    }
  }
  
  public long getStorageLimit() {
    return storageLimit;
  }
  
  public void setStorageLimit(long paramLong) {
    if (paramLong < -1L) {
      throw new IllegalArgumentException("invalid storageLimit");
    }
    long l = storageLimit;
    storageLimit = paramLong;
    firePropertyChange("storageLimit", Long.valueOf(l), Long.valueOf(storageLimit));
  }
  
  private String getId(String paramString1, String paramString2) {
    ResourceMap localResourceMap = getContext().getResourceMap();
    String str = localResourceMap.getString(paramString1, new Object[0]);
    if (str == null) {
      logger.log(Level.WARNING, "unspecified resource " + paramString1 + " using " + paramString2);
      str = paramString2;
    }
    else if (str.trim().length() == 0) {
      logger.log(Level.WARNING, "empty resource " + paramString1 + " using " + paramString2);
      str = paramString2;
    }
    return str;
  }
  
  private String getApplicationId() { return getId("Application.id", getContext().getApplicationClass().getSimpleName()); }
  
  private String getVendorId() {
    return getId("Application.vendorId", "UnknownApplicationVendor");
  }
  

  private static enum OSId
  {
    WINDOWS,  OSX,  UNIX;
    private OSId() {} }
  private OSId getOSId() { PrivilegedAction local1 = new PrivilegedAction() {
      public String run() {
        return System.getProperty("os.name");
      }
    };
    OSId localOSId = OSId.UNIX;
    String str = (String)AccessController.doPrivileged(local1);
    if (str != null) {
      if (str.toLowerCase().startsWith("mac os x")) {
        localOSId = OSId.OSX;
      }
      else if (str.contains("Windows")) {
        localOSId = OSId.WINDOWS;
      }
    }
    return localOSId;
  }
  
  public File getDirectory() {
    if (directory == unspecifiedFile) {
      directory = null;
      String str1 = null;
      try {
        str1 = System.getProperty("user.home");
      }
      catch (SecurityException localSecurityException1) {}
      
      if (str1 != null) {
        String str2 = getApplicationId();
        OSId localOSId = getOSId();
        Object localObject; if (localOSId == OSId.WINDOWS) {
          localObject = null;
          try {
            String str3 = System.getenv("APPDATA");
            if ((str3 != null) && (str3.length() > 0)) {
              localObject = new File(str3);
            }
          }
          catch (SecurityException localSecurityException2) {}
          
          String str4 = getVendorId();
          String str5; if ((localObject != null) && (((File)localObject).isDirectory()))
          {
            str5 = str4 + "\\" + str2 + "\\";
            directory = new File((File)localObject, str5);
          }
          else
          {
            str5 = "Application Data\\" + str4 + "\\" + str2 + "\\";
            directory = new File(str1, str5);
          }
        }
        else if (localOSId == OSId.OSX)
        {
          localObject = "Library/Application Support/" + str2 + "/";
          directory = new File(str1, (String)localObject);
        }
        else
        {
          localObject = "." + str2 + "/";
          directory = new File(str1, (String)localObject);
        }
      }
    }
    return directory;
  }
  
  public void setDirectory(File paramFile) {
    File localFile = directory;
    directory = paramFile;
    firePropertyChange("directory", localFile, directory);
  }
  
  private static class LSException
    extends IOException
  {
    public LSException(String paramString, Throwable paramThrowable)
    {
      super();
      initCause(paramThrowable);
    }
    
    public LSException(String paramString) { super(); }
  }
  







  private static class RectanglePD
    extends DefaultPersistenceDelegate
  {
    public RectanglePD() { super(); }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
      Rectangle localRectangle = (Rectangle)paramObject;
      Object[] arrayOfObject = { Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(width), Integer.valueOf(height) };
      

      return new Expression(paramObject, paramObject.getClass(), "new", arrayOfObject); } }
  private abstract class LocalIO { private LocalIO() {}
    public abstract InputStream openInputFile(String paramString) throws IOException;
    public abstract OutputStream openOutputFile(String paramString) throws IOException;
    public abstract boolean deleteFile(String paramString) throws IOException; }
  private synchronized LocalIO getLocalIO() { if (localIO == null) {
      localIO = getPersistenceServiceIO();
      if (localIO == null) {
        localIO = new LocalFileIO(null);
      }
    }
    return localIO;
  }
  



  private class LocalFileIO
    extends LocalStorage.LocalIO
  {
    private LocalFileIO() { super(null); }
    
    public InputStream openInputFile(String paramString) throws IOException { File localFile = new File(getDirectory(), paramString);
      try {
        return new BufferedInputStream(new FileInputStream(localFile));
      }
      catch (IOException localIOException) {
        throw new LocalStorage.LSException("couldn't open input file \"" + paramString + "\"", localIOException);
      }
    }
    
    public OutputStream openOutputFile(String paramString) throws IOException { File localFile1 = getDirectory();
      if ((!localFile1.isDirectory()) && 
        (!localFile1.mkdirs())) {
        throw new LocalStorage.LSException("couldn't create directory " + localFile1);
      }
      
      File localFile2 = new File(localFile1, paramString);
      try {
        return new BufferedOutputStream(new FileOutputStream(localFile2));
      }
      catch (IOException localIOException) {
        throw new LocalStorage.LSException("couldn't open output file \"" + paramString + "\"", localIOException);
      }
    }
    
    public boolean deleteFile(String paramString) throws IOException { File localFile = new File(getDirectory(), paramString);
      return localFile.delete();
    }
  }
  



  private LocalIO getPersistenceServiceIO()
  {
    try
    {
      Class localClass = Class.forName("javax.jnlp.ServiceManager");
      Method localMethod = localClass.getMethod("getServiceNames", new Class[0]);
      String[] arrayOfString1 = (String[])localMethod.invoke(null, new Object[0]);
      int i = 0;
      int j = 0;
      for (String str : arrayOfString1) {
        if (str.equals("javax.jnlp.BasicService")) {
          j = 1;
        }
        else if (str.equals("javax.jnlp.PersistenceService")) {
          i = 1;
        }
      }
      if ((j != 0) && (i != 0)) {
        return new PersistenceServiceIO();
      }
    }
    catch (Exception localException) {}
    

    return null;
  }
  
  private class PersistenceServiceIO
    extends LocalStorage.LocalIO {
    private BasicService bs;
    private PersistenceService ps;
    
    private String initFailedMessage(String paramString) { return getClass().getName() + " initialization failed: " + paramString; }
    
    PersistenceServiceIO() {
      super(null);
      try {
        bs = ((BasicService)ServiceManager.lookup("javax.jnlp.BasicService"));
        ps = ((PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService"));
      }
      catch (UnavailableServiceException localUnavailableServiceException) {
        LocalStorage.logger.log(Level.SEVERE, initFailedMessage("ServiceManager.lookup"), localUnavailableServiceException);
        bs = null;ps = null;
      }
    }
    
    private void checkBasics(String paramString) throws IOException {
      if ((bs == null) || (ps == null)) {
        throw new IOException(initFailedMessage(paramString));
      }
    }
    
    private URL fileNameToURL(String paramString) throws IOException {
      try {
        return new URL(bs.getCodeBase(), paramString);
      }
      catch (MalformedURLException localMalformedURLException) {
        throw new LocalStorage.LSException("invalid filename \"" + paramString + "\"", localMalformedURLException);
      }
    }
    
    public InputStream openInputFile(String paramString) throws IOException {
      checkBasics("openInputFile");
      URL localURL = fileNameToURL(paramString);
      try {
        return new BufferedInputStream(ps.get(localURL).getInputStream());
      }
      catch (Exception localException) {
        throw new LocalStorage.LSException("openInputFile \"" + paramString + "\" failed", localException);
      }
    }
    
    public OutputStream openOutputFile(String paramString) throws IOException {
      checkBasics("openOutputFile");
      URL localURL = fileNameToURL(paramString);
      try {
        FileContents localFileContents = null;
        try {
          localFileContents = ps.get(localURL);

        }
        catch (FileNotFoundException localFileNotFoundException)
        {

          long l1 = 131072L;
          long l2 = ps.create(localURL, l1);
          if (l2 >= l1) {
            localFileContents = ps.get(localURL);
          }
        }
        if ((localFileContents != null) && (localFileContents.canWrite())) {
          return new BufferedOutputStream(localFileContents.getOutputStream(true));
        }
        
        throw new IOException("unable to create FileContents object");
      }
      catch (Exception localException)
      {
        throw new LocalStorage.LSException("openOutputFile \"" + paramString + "\" failed", localException);
      }
    }
    
    public boolean deleteFile(String paramString) throws IOException {
      checkBasics("deleteFile");
      URL localURL = fileNameToURL(paramString);
      try {
        ps.delete(localURL);
        return true;
      }
      catch (Exception localException) {
        throw new LocalStorage.LSException("openInputFile \"" + paramString + "\" failed", localException);
      }
    }
  }
}
