package org.apache.log4j.chainsaw;

import java.util.StringTokenizer;
import org.apache.log4j.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;










































class XMLFileHandler
  extends DefaultHandler
{
  private static final String TAG_EVENT = "log4j:event";
  private static final String TAG_MESSAGE = "log4j:message";
  private static final String TAG_NDC = "log4j:NDC";
  private static final String TAG_THROWABLE = "log4j:throwable";
  private static final String TAG_LOCATION_INFO = "log4j:locationInfo";
  private final MyTableModel mModel;
  private int mNumEvents;
  private long mTimeStamp;
  private Level mLevel;
  private String mCategoryName;
  private String mNDC;
  private String mThreadName;
  private String mMessage;
  private String[] mThrowableStrRep;
  private String mLocationDetails;
  private final StringBuffer mBuf = new StringBuffer();
  




  XMLFileHandler(MyTableModel aModel)
  {
    mModel = aModel;
  }
  

  public void startDocument()
    throws SAXException
  {
    mNumEvents = 0;
  }
  
  public void characters(char[] aChars, int aStart, int aLength)
  {
    mBuf.append(String.valueOf(aChars, aStart, aLength));
  }
  



  public void endElement(String aNamespaceURI, String aLocalName, String aQName)
  {
    if ("log4j:event".equals(aQName)) {
      addEvent();
      resetData();
    } else if ("log4j:NDC".equals(aQName)) {
      mNDC = mBuf.toString();
    } else if ("log4j:message".equals(aQName)) {
      mMessage = mBuf.toString();
    } else if ("log4j:throwable".equals(aQName)) {
      StringTokenizer st = new StringTokenizer(mBuf.toString(), "\n\t");
      
      mThrowableStrRep = new String[st.countTokens()];
      if (mThrowableStrRep.length > 0) {
        mThrowableStrRep[0] = st.nextToken();
        for (int i = 1; i < mThrowableStrRep.length; i++) {
          mThrowableStrRep[i] = ("\t" + st.nextToken());
        }
      }
    }
  }
  




  public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts)
  {
    mBuf.setLength(0);
    
    if ("log4j:event".equals(aQName)) {
      mThreadName = aAtts.getValue("thread");
      mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
      mCategoryName = aAtts.getValue("logger");
      mLevel = Level.toLevel(aAtts.getValue("level"));
    } else if ("log4j:locationInfo".equals(aQName)) {
      mLocationDetails = (aAtts.getValue("class") + "." + aAtts.getValue("method") + "(" + aAtts.getValue("file") + ":" + aAtts.getValue("line") + ")");
    }
  }
  



  int getNumEvents()
  {
    return mNumEvents;
  }
  




  private void addEvent()
  {
    mModel.addEvent(new EventDetails(mTimeStamp, mLevel, mCategoryName, mNDC, mThreadName, mMessage, mThrowableStrRep, mLocationDetails));
    






    mNumEvents += 1;
  }
  
  private void resetData()
  {
    mTimeStamp = 0L;
    mLevel = null;
    mCategoryName = null;
    mNDC = null;
    mThreadName = null;
    mMessage = null;
    mThrowableStrRep = null;
    mLocationDetails = null;
  }
}
