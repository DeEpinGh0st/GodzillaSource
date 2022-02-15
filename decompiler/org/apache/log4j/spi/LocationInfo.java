package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;










































public class LocationInfo
  implements Serializable
{
  transient String lineNumber;
  transient String fileName;
  transient String className;
  transient String methodName;
  public String fullInfo;
  private static StringWriter sw = new StringWriter();
  private static PrintWriter pw = new PrintWriter(sw);

  
  private static Method getStackTraceMethod;

  
  private static Method getClassNameMethod;

  
  private static Method getMethodNameMethod;

  
  private static Method getFileNameMethod;

  
  private static Method getLineNumberMethod;
  
  public static final String NA = "?";
  
  static final long serialVersionUID = -1325822038990805636L;
  
  public static final LocationInfo NA_LOCATION_INFO = new LocationInfo("?", "?", "?", "?");

  
  static boolean inVisualAge = false;


  
  static {
    try {
      inVisualAge = (Class.forName("com.ibm.uvm.tools.DebugSupport") != null);
      LogLog.debug("Detected IBM VisualAge environment.");
    } catch (Throwable e) {}

    
    try {
      Class[] noArgs = null;
      getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
      Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
      getClassNameMethod = stackTraceElementClass.getMethod("getClassName", noArgs);
      getMethodNameMethod = stackTraceElementClass.getMethod("getMethodName", noArgs);
      getFileNameMethod = stackTraceElementClass.getMethod("getFileName", noArgs);
      getLineNumberMethod = stackTraceElementClass.getMethod("getLineNumber", noArgs);
    } catch (ClassNotFoundException ex) {
      LogLog.debug("LocationInfo will use pre-JDK 1.4 methods to determine location.");
    } catch (NoSuchMethodException ex) {
      LogLog.debug("LocationInfo will use pre-JDK 1.4 methods to determine location.");
    } 
  }





















  
  public LocationInfo(Throwable t, String fqnOfCallingClass) {
    String s;
    if (t == null || fqnOfCallingClass == null)
      return; 
    if (getLineNumberMethod != null) {
      try {
        Object[] noArgs = null;
        Object[] elements = (Object[])getStackTraceMethod.invoke(t, noArgs);
        String prevClass = "?";
        for (int i = elements.length - 1; i >= 0; i--) {
          String thisClass = (String)getClassNameMethod.invoke(elements[i], noArgs);
          if (fqnOfCallingClass.equals(thisClass)) {
            int caller = i + 1;
            if (caller < elements.length) {
              this.className = prevClass;
              this.methodName = (String)getMethodNameMethod.invoke(elements[caller], noArgs);
              this.fileName = (String)getFileNameMethod.invoke(elements[caller], noArgs);
              if (this.fileName == null) {
                this.fileName = "?";
              }
              int line = ((Integer)getLineNumberMethod.invoke(elements[caller], noArgs)).intValue();
              if (line < 0) {
                this.lineNumber = "?";
              } else {
                this.lineNumber = String.valueOf(line);
              } 
              StringBuffer buf = new StringBuffer();
              buf.append(this.className);
              buf.append(".");
              buf.append(this.methodName);
              buf.append("(");
              buf.append(this.fileName);
              buf.append(":");
              buf.append(this.lineNumber);
              buf.append(")");
              this.fullInfo = buf.toString();
            } 
            return;
          } 
          prevClass = thisClass;
        } 
        return;
      } catch (IllegalAccessException ex) {
        LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
      } catch (InvocationTargetException ex) {
        if (ex.getTargetException() instanceof InterruptedException || ex.getTargetException() instanceof java.io.InterruptedIOException)
        {
          Thread.currentThread().interrupt();
        }
        LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
      } catch (RuntimeException ex) {
        LogLog.debug("LocationInfo failed using JDK 1.4 methods", ex);
      } 
    }


    
    synchronized (sw) {
      t.printStackTrace(pw);
      s = sw.toString();
      sw.getBuffer().setLength(0);
    } 









    
    int ibegin = s.lastIndexOf(fqnOfCallingClass);
    if (ibegin == -1) {
      return;
    }






    
    if (ibegin + fqnOfCallingClass.length() < s.length() && s.charAt(ibegin + fqnOfCallingClass.length()) != '.') {
      
      int i = s.lastIndexOf(fqnOfCallingClass + ".");
      if (i != -1) {
        ibegin = i;
      }
    } 

    
    ibegin = s.indexOf(Layout.LINE_SEP, ibegin);
    if (ibegin == -1)
      return; 
    ibegin += Layout.LINE_SEP_LEN;

    
    int iend = s.indexOf(Layout.LINE_SEP, ibegin);
    if (iend == -1) {
      return;
    }

    
    if (!inVisualAge) {
      
      ibegin = s.lastIndexOf("at ", iend);
      if (ibegin == -1) {
        return;
      }
      ibegin += 3;
    } 
    
    this.fullInfo = s.substring(ibegin, iend);
  }









  
  private static final void appendFragment(StringBuffer buf, String fragment) {
    if (fragment == null) {
      buf.append("?");
    } else {
      buf.append(fragment);
    } 
  }













  
  public LocationInfo(String file, String classname, String method, String line) {
    this.fileName = file;
    this.className = classname;
    this.methodName = method;
    this.lineNumber = line;
    StringBuffer buf = new StringBuffer();
    appendFragment(buf, classname);
    buf.append(".");
    appendFragment(buf, method);
    buf.append("(");
    appendFragment(buf, file);
    buf.append(":");
    appendFragment(buf, line);
    buf.append(")");
    this.fullInfo = buf.toString();
  }





  
  public String getClassName() {
    if (this.fullInfo == null) return "?"; 
    if (this.className == null) {

      
      int iend = this.fullInfo.lastIndexOf('(');
      if (iend == -1) {
        this.className = "?";
      } else {
        iend = this.fullInfo.lastIndexOf('.', iend);









        
        int ibegin = 0;
        if (inVisualAge) {
          ibegin = this.fullInfo.lastIndexOf(' ', iend) + 1;
        }
        
        if (iend == -1) {
          this.className = "?";
        } else {
          this.className = this.fullInfo.substring(ibegin, iend);
        } 
      } 
    }  return this.className;
  }






  
  public String getFileName() {
    if (this.fullInfo == null) return "?";
    
    if (this.fileName == null) {
      int iend = this.fullInfo.lastIndexOf(':');
      if (iend == -1) {
        this.fileName = "?";
      } else {
        int ibegin = this.fullInfo.lastIndexOf('(', iend - 1);
        this.fileName = this.fullInfo.substring(ibegin + 1, iend);
      } 
    } 
    return this.fileName;
  }






  
  public String getLineNumber() {
    if (this.fullInfo == null) return "?";
    
    if (this.lineNumber == null) {
      int iend = this.fullInfo.lastIndexOf(')');
      int ibegin = this.fullInfo.lastIndexOf(':', iend - 1);
      if (ibegin == -1) {
        this.lineNumber = "?";
      } else {
        this.lineNumber = this.fullInfo.substring(ibegin + 1, iend);
      } 
    }  return this.lineNumber;
  }




  
  public String getMethodName() {
    if (this.fullInfo == null) return "?"; 
    if (this.methodName == null) {
      int iend = this.fullInfo.lastIndexOf('(');
      int ibegin = this.fullInfo.lastIndexOf('.', iend);
      if (ibegin == -1) {
        this.methodName = "?";
      } else {
        this.methodName = this.fullInfo.substring(ibegin + 1, iend);
      } 
    }  return this.methodName;
  }
}
