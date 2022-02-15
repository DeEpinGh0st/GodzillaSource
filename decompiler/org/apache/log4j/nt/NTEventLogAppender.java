package org.apache.log4j.nt;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;































public class NTEventLogAppender
  extends AppenderSkeleton
{
  private int _handle = 0;
  
  private String source = null;
  private String server = null;

  
  public NTEventLogAppender() {
    this(null, null, null);
  }
  
  public NTEventLogAppender(String source) {
    this(null, source, null);
  }
  
  public NTEventLogAppender(String server, String source) {
    this(server, source, null);
  }
  
  public NTEventLogAppender(Layout layout) {
    this(null, null, layout);
  }
  
  public NTEventLogAppender(String source, Layout layout) {
    this(null, source, layout);
  }
  
  public NTEventLogAppender(String server, String source, Layout layout) {
    if (source == null) {
      source = "Log4j";
    }
    if (layout == null) {
      this.layout = (Layout)new TTCCLayout();
    } else {
      this.layout = layout;
    } 
    
    try {
      this._handle = registerEventSource(server, source);
    } catch (Exception e) {
      e.printStackTrace();
      this._handle = 0;
    } 
  }


  
  public void close() {}


  
  public void activateOptions() {
    if (this.source != null) {
      try {
        this._handle = registerEventSource(this.server, this.source);
      } catch (Exception e) {
        LogLog.error("Could not register event source.", e);
        this._handle = 0;
      } 
    }
  }


  
  public void append(LoggingEvent event) {
    StringBuffer sbuf = new StringBuffer();
    
    sbuf.append(this.layout.format(event));
    if (this.layout.ignoresThrowable()) {
      String[] s = event.getThrowableStrRep();
      if (s != null) {
        int len = s.length;
        for (int i = 0; i < len; i++) {
          sbuf.append(s[i]);
        }
      } 
    } 
    
    int nt_category = event.getLevel().toInt();




    
    reportEvent(this._handle, sbuf.toString(), nt_category);
  }


  
  public void finalize() {
    deregisterEventSource(this._handle);
    this._handle = 0;
  }





  
  public void setSource(String source) {
    this.source = source.trim();
  }

  
  public String getSource() {
    return this.source;
  }




  
  public boolean requiresLayout() {
    return true;
  }

  
  private native int registerEventSource(String paramString1, String paramString2);

  
  static {
    String[] arrayOfString;
    try {
      arrayOfString = new String[] { System.getProperty("os.arch") };
    } catch (SecurityException e) {
      arrayOfString = new String[] { "amd64", "ia64", "x86" };
    } 
    boolean loaded = false;
    for (int i = 0; i < arrayOfString.length; i++) {
      try {
        System.loadLibrary("NTEventLogAppender." + arrayOfString[i]);
        loaded = true;
        break;
      } catch (UnsatisfiedLinkError e) {
        loaded = false;
      } 
    } 
    if (!loaded)
      System.loadLibrary("NTEventLogAppender"); 
  }
  
  private native void reportEvent(int paramInt1, String paramString, int paramInt2);
  
  private native void deregisterEventSource(int paramInt);
}
