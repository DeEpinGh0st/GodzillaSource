package org.apache.log4j;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.helpers.LogLog;


























public class ConsoleAppender
  extends WriterAppender
{
  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";
  protected String target = "System.out";




  
  private boolean follow = false;





  
  public ConsoleAppender() {}




  
  public ConsoleAppender(Layout layout) {
    this(layout, "System.out");
  }





  
  public ConsoleAppender(Layout layout, String target) {
    setLayout(layout);
    setTarget(target);
    activateOptions();
  }






  
  public void setTarget(String value) {
    String v = value.trim();
    
    if ("System.out".equalsIgnoreCase(v)) {
      this.target = "System.out";
    } else if ("System.err".equalsIgnoreCase(v)) {
      this.target = "System.err";
    } else {
      targetWarn(value);
    } 
  }







  
  public String getTarget() {
    return this.target;
  }







  
  public final void setFollow(boolean newValue) {
    this.follow = newValue;
  }







  
  public final boolean getFollow() {
    return this.follow;
  }
  
  void targetWarn(String val) {
    LogLog.warn("[" + val + "] should be System.out or System.err.");
    LogLog.warn("Using previously set target, System.out by default.");
  }



  
  public void activateOptions() {
    if (this.follow) {
      if (this.target.equals("System.err")) {
        setWriter(createWriter(new SystemErrStream()));
      } else {
        setWriter(createWriter(new SystemOutStream()));
      }
    
    } else if (this.target.equals("System.err")) {
      setWriter(createWriter(System.err));
    } else {
      setWriter(createWriter(System.out));
    } 

    
    super.activateOptions();
  }





  
  protected final void closeWriter() {
    if (this.follow) {
      super.closeWriter();
    }
  }




  
  private static class SystemErrStream
    extends OutputStream
  {
    public void close() {}




    
    public void flush() {
      System.err.flush();
    }
    
    public void write(byte[] b) throws IOException {
      System.err.write(b);
    }

    
    public void write(byte[] b, int off, int len) throws IOException {
      System.err.write(b, off, len);
    }
    
    public void write(int b) throws IOException {
      System.err.write(b);
    }
  }




  
  private static class SystemOutStream
    extends OutputStream
  {
    public void close() {}



    
    public void flush() {
      System.out.flush();
    }
    
    public void write(byte[] b) throws IOException {
      System.out.write(b);
    }

    
    public void write(byte[] b, int off, int len) throws IOException {
      System.out.write(b, off, len);
    }
    
    public void write(int b) throws IOException {
      System.out.write(b);
    }
  }
}
