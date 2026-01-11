package org.apache.log4j.net;

import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;


























































































public class JMSAppender
  extends AppenderSkeleton
{
  String securityPrincipalName;
  String securityCredentials;
  String initialContextFactoryName;
  String urlPkgPrefixes;
  String providerURL;
  String topicBindingName;
  String tcfBindingName;
  String userName;
  String password;
  boolean locationInfo;
  TopicConnection topicConnection;
  TopicSession topicSession;
  TopicPublisher topicPublisher;
  
  public JMSAppender() {}
  
  public void setTopicConnectionFactoryBindingName(String tcfBindingName)
  {
    this.tcfBindingName = tcfBindingName;
  }
  



  public String getTopicConnectionFactoryBindingName()
  {
    return tcfBindingName;
  }
  





  public void setTopicBindingName(String topicBindingName)
  {
    this.topicBindingName = topicBindingName;
  }
  



  public String getTopicBindingName()
  {
    return topicBindingName;
  }
  





  public boolean getLocationInfo()
  {
    return locationInfo;
  }
  





  public void activateOptions()
  {
    try
    {
      LogLog.debug("Getting initial context.");
      Context jndi; Context jndi; if (initialContextFactoryName != null) {
        Properties env = new Properties();
        env.put("java.naming.factory.initial", initialContextFactoryName);
        if (providerURL != null) {
          env.put("java.naming.provider.url", providerURL);
        } else {
          LogLog.warn("You have set InitialContextFactoryName option but not the ProviderURL. This is likely to cause problems.");
        }
        
        if (urlPkgPrefixes != null) {
          env.put("java.naming.factory.url.pkgs", urlPkgPrefixes);
        }
        
        if (securityPrincipalName != null) {
          env.put("java.naming.security.principal", securityPrincipalName);
          if (securityCredentials != null) {
            env.put("java.naming.security.credentials", securityCredentials);
          } else {
            LogLog.warn("You have set SecurityPrincipalName option but not the SecurityCredentials. This is likely to cause problems.");
          }
        }
        
        jndi = new InitialContext(env);
      } else {
        jndi = new InitialContext();
      }
      
      LogLog.debug("Looking up [" + tcfBindingName + "]");
      TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)lookup(jndi, tcfBindingName);
      LogLog.debug("About to create TopicConnection.");
      if (userName != null) {
        topicConnection = topicConnectionFactory.createTopicConnection(userName, password);
      }
      else {
        topicConnection = topicConnectionFactory.createTopicConnection();
      }
      
      LogLog.debug("Creating TopicSession, non-transactional, in AUTO_ACKNOWLEDGE mode.");
      
      topicSession = topicConnection.createTopicSession(false, 1);
      

      LogLog.debug("Looking up topic name [" + topicBindingName + "].");
      Topic topic = (Topic)lookup(jndi, topicBindingName);
      
      LogLog.debug("Creating TopicPublisher.");
      topicPublisher = topicSession.createPublisher(topic);
      
      LogLog.debug("Starting TopicConnection.");
      topicConnection.start();
      
      jndi.close();
    } catch (JMSException e) {
      errorHandler.error("Error while activating options for appender named [" + name + "].", e, 0);
    }
    catch (NamingException e) {
      errorHandler.error("Error while activating options for appender named [" + name + "].", e, 0);
    }
    catch (RuntimeException e) {
      errorHandler.error("Error while activating options for appender named [" + name + "].", e, 0);
    }
  }
  
  protected Object lookup(Context ctx, String name) throws NamingException
  {
    try {
      return ctx.lookup(name);
    } catch (NameNotFoundException e) {
      LogLog.error("Could not find name [" + name + "].");
      throw e;
    }
  }
  
  protected boolean checkEntryConditions() {
    String fail = null;
    
    if (topicConnection == null) {
      fail = "No TopicConnection";
    } else if (topicSession == null) {
      fail = "No TopicSession";
    } else if (topicPublisher == null) {
      fail = "No TopicPublisher";
    }
    
    if (fail != null) {
      errorHandler.error(fail + " for JMSAppender named [" + name + "].");
      return false;
    }
    return true;
  }
  





  public synchronized void close()
  {
    if (closed) {
      return;
    }
    LogLog.debug("Closing appender [" + name + "].");
    closed = true;
    try
    {
      if (topicSession != null)
        topicSession.close();
      if (topicConnection != null)
        topicConnection.close();
    } catch (JMSException e) {
      LogLog.error("Error while closing JMSAppender [" + name + "].", e);
    } catch (RuntimeException e) {
      LogLog.error("Error while closing JMSAppender [" + name + "].", e);
    }
    
    topicPublisher = null;
    topicSession = null;
    topicConnection = null;
  }
  


  public void append(LoggingEvent event)
  {
    if (!checkEntryConditions()) {
      return;
    }
    try
    {
      ObjectMessage msg = topicSession.createObjectMessage();
      if (locationInfo) {
        event.getLocationInformation();
      }
      msg.setObject(event);
      topicPublisher.publish(msg);
    } catch (JMSException e) {
      errorHandler.error("Could not publish message in JMSAppender [" + name + "].", e, 0);
    }
    catch (RuntimeException e) {
      errorHandler.error("Could not publish message in JMSAppender [" + name + "].", e, 0);
    }
  }
  





  public String getInitialContextFactoryName()
  {
    return initialContextFactoryName;
  }
  








  public void setInitialContextFactoryName(String initialContextFactoryName)
  {
    this.initialContextFactoryName = initialContextFactoryName;
  }
  
  public String getProviderURL() {
    return providerURL;
  }
  
  public void setProviderURL(String providerURL) {
    this.providerURL = providerURL;
  }
  
  String getURLPkgPrefixes() {
    return urlPkgPrefixes;
  }
  
  public void setURLPkgPrefixes(String urlPkgPrefixes) {
    this.urlPkgPrefixes = urlPkgPrefixes;
  }
  
  public String getSecurityCredentials() {
    return securityCredentials;
  }
  
  public void setSecurityCredentials(String securityCredentials) {
    this.securityCredentials = securityCredentials;
  }
  
  public String getSecurityPrincipalName()
  {
    return securityPrincipalName;
  }
  
  public void setSecurityPrincipalName(String securityPrincipalName) {
    this.securityPrincipalName = securityPrincipalName;
  }
  
  public String getUserName() {
    return userName;
  }
  






  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getPassword() {
    return password;
  }
  


  public void setPassword(String password)
  {
    this.password = password;
  }
  




  public void setLocationInfo(boolean locationInfo)
  {
    this.locationInfo = locationInfo;
  }
  



  protected TopicConnection getTopicConnection()
  {
    return topicConnection;
  }
  



  protected TopicSession getTopicSession()
  {
    return topicSession;
  }
  



  protected TopicPublisher getTopicPublisher()
  {
    return topicPublisher;
  }
  



  public boolean requiresLayout()
  {
    return false;
  }
}
