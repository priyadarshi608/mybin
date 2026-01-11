package org.apache.log4j.rewrite;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;




























public class RewriteAppender
  extends AppenderSkeleton
  implements AppenderAttachable, UnrecognizedElementHandler
{
  private RewritePolicy policy;
  private final AppenderAttachableImpl appenders;
  
  public RewriteAppender()
  {
    appenders = new AppenderAttachableImpl();
  }
  


  protected void append(LoggingEvent event)
  {
    LoggingEvent rewritten = event;
    if (policy != null) {
      rewritten = policy.rewrite(event);
    }
    if (rewritten != null) {
      synchronized (appenders) {
        appenders.appendLoopOnAppenders(rewritten);
      }
    }
  }
  




  public void addAppender(Appender newAppender)
  {
    synchronized (appenders) {
      appenders.addAppender(newAppender);
    }
  }
  



  public Enumeration getAllAppenders()
  {
    synchronized (appenders) {
      return appenders.getAllAppenders();
    }
  }
  





  public Appender getAppender(String name)
  {
    synchronized (appenders) {
      return appenders.getAppender(name);
    }
  }
  




  public void close()
  {
    closed = true;
    


    synchronized (appenders) {
      Enumeration iter = appenders.getAllAppenders();
      
      if (iter != null) {
        while (iter.hasMoreElements()) {
          Object next = iter.nextElement();
          
          if ((next instanceof Appender)) {
            ((Appender)next).close();
          }
        }
      }
    }
  }
  




  public boolean isAttached(Appender appender)
  {
    synchronized (appenders) {
      return appenders.isAttached(appender);
    }
  }
  


  public boolean requiresLayout()
  {
    return false;
  }
  


  public void removeAllAppenders()
  {
    synchronized (appenders) {
      appenders.removeAllAppenders();
    }
  }
  



  public void removeAppender(Appender appender)
  {
    synchronized (appenders) {
      appenders.removeAppender(appender);
    }
  }
  



  public void removeAppender(String name)
  {
    synchronized (appenders) {
      appenders.removeAppender(name);
    }
  }
  
  public void setRewritePolicy(RewritePolicy rewritePolicy)
  {
    policy = rewritePolicy;
  }
  

  public boolean parseUnrecognizedElement(Element element, Properties props)
    throws Exception
  {
    String nodeName = element.getNodeName();
    if ("rewritePolicy".equals(nodeName)) {
      Object rewritePolicy = DOMConfigurator.parseElement(element, props, RewritePolicy.class);
      

      if (rewritePolicy != null) {
        if ((rewritePolicy instanceof OptionHandler)) {
          ((OptionHandler)rewritePolicy).activateOptions();
        }
        setRewritePolicy((RewritePolicy)rewritePolicy);
      }
      return true;
    }
    return false;
  }
}
