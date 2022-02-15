package org.apache.log4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.spi.ThrowableRenderer;

































public final class EnhancedThrowableRenderer
  implements ThrowableRenderer
{
  private Method getStackTraceMethod;
  private Method getClassNameMethod;
  
  public EnhancedThrowableRenderer() {
    try {
      Class[] noArgs = null;
      this.getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
      Class ste = Class.forName("java.lang.StackTraceElement");
      this.getClassNameMethod = ste.getMethod("getClassName", noArgs);
    } catch (Exception ex) {}
  }




  
  public String[] doRender(Throwable throwable) {
    if (this.getStackTraceMethod != null) {
      try {
        Object[] noArgs = null;
        Object[] elements = (Object[])this.getStackTraceMethod.invoke(throwable, noArgs);
        String[] lines = new String[elements.length + 1];
        lines[0] = throwable.toString();
        Map classMap = new HashMap();
        for (int i = 0; i < elements.length; i++) {
          lines[i + 1] = formatElement(elements[i], classMap);
        }
        return lines;
      } catch (Exception ex) {}
    }
    
    return DefaultThrowableRenderer.render(throwable);
  }






  
  private String formatElement(Object element, Map classMap) {
    StringBuffer buf = new StringBuffer("\tat ");
    buf.append(element);
    try {
      String className = this.getClassNameMethod.invoke(element, (Object[])null).toString();
      Object classDetails = classMap.get(className);
      if (classDetails != null) {
        buf.append(classDetails);
      } else {
        Class cls = findClass(className);
        int detailStart = buf.length();
        buf.append('[');
        try {
          CodeSource source = cls.getProtectionDomain().getCodeSource();
          if (source != null) {
            URL locationURL = source.getLocation();
            if (locationURL != null)
            {

              
              if ("file".equals(locationURL.getProtocol())) {
                String path = locationURL.getPath();
                if (path != null) {


                  
                  int lastSlash = path.lastIndexOf('/');
                  int lastBack = path.lastIndexOf(File.separatorChar);
                  if (lastBack > lastSlash) {
                    lastSlash = lastBack;
                  }



                  
                  if (lastSlash <= 0 || lastSlash == path.length() - 1) {
                    buf.append(locationURL);
                  } else {
                    buf.append(path.substring(lastSlash + 1));
                  } 
                } 
              } else {
                buf.append(locationURL);
              } 
            }
          } 
        } catch (SecurityException ex) {}
        
        buf.append(':');
        Package pkg = cls.getPackage();
        if (pkg != null) {
          String implVersion = pkg.getImplementationVersion();
          if (implVersion != null) {
            buf.append(implVersion);
          }
        } 
        buf.append(']');
        classMap.put(className, buf.substring(detailStart));
      } 
    } catch (Exception ex) {}
    
    return buf.toString();
  }






  
  private Class findClass(String className) throws ClassNotFoundException {
    try {
      return Thread.currentThread().getContextClassLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e1) {
        return getClass().getClassLoader().loadClass(className);
      } 
    } 
  }
}
