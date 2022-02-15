package org.apache.log4j.chainsaw;

import java.util.StringTokenizer;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
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





  
  XMLFileHandler(MyTableModel aModel) {
    this.mModel = aModel;
  }



  
  public void startDocument() throws SAXException {
    this.mNumEvents = 0;
  }

  
  public void characters(char[] aChars, int aStart, int aLength) {
    this.mBuf.append(String.valueOf(aChars, aStart, aLength));
  }




  
  public void endElement(String aNamespaceURI, String aLocalName, String aQName) {
    if ("log4j:event".equals(aQName)) {
      addEvent();
      resetData();
    } else if ("log4j:NDC".equals(aQName)) {
      this.mNDC = this.mBuf.toString();
    } else if ("log4j:message".equals(aQName)) {
      this.mMessage = this.mBuf.toString();
    } else if ("log4j:throwable".equals(aQName)) {
      StringTokenizer st = new StringTokenizer(this.mBuf.toString(), "\n\t");
      
      this.mThrowableStrRep = new String[st.countTokens()];
      if (this.mThrowableStrRep.length > 0) {
        this.mThrowableStrRep[0] = st.nextToken();
        for (int i = 1; i < this.mThrowableStrRep.length; i++) {
          this.mThrowableStrRep[i] = "\t" + st.nextToken();
        }
      } 
    } 
  }





  
  public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts) {
    this.mBuf.setLength(0);
    
    if ("log4j:event".equals(aQName)) {
      this.mThreadName = aAtts.getValue("thread");
      this.mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
      this.mCategoryName = aAtts.getValue("logger");
      this.mLevel = Level.toLevel(aAtts.getValue("level"));
    } else if ("log4j:locationInfo".equals(aQName)) {
      this.mLocationDetails = aAtts.getValue("class") + "." + aAtts.getValue("method") + "(" + aAtts.getValue("file") + ":" + aAtts.getValue("line") + ")";
    } 
  }




  
  int getNumEvents() {
    return this.mNumEvents;
  }





  
  private void addEvent() {
    this.mModel.addEvent(new EventDetails(this.mTimeStamp, (Priority)this.mLevel, this.mCategoryName, this.mNDC, this.mThreadName, this.mMessage, this.mThrowableStrRep, this.mLocationDetails));






    
    this.mNumEvents++;
  }

  
  private void resetData() {
    this.mTimeStamp = 0L;
    this.mLevel = null;
    this.mCategoryName = null;
    this.mNDC = null;
    this.mThreadName = null;
    this.mMessage = null;
    this.mThrowableStrRep = null;
    this.mLocationDetails = null;
  }
}
