package org.apache.log4j.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;

































public class LoggingEvent
  implements Serializable
{
  private static long startTime = System.currentTimeMillis();




  
  public final transient String fqnOfCategoryClass;




  
  private transient Category logger;




  
  public final String categoryName;




  
  public transient Priority level;




  
  private String ndc;



  
  private Hashtable mdcCopy;



  
  private boolean ndcLookupRequired = true;



  
  private boolean mdcCopyLookupRequired = true;



  
  private transient Object message;



  
  private String renderedMessage;



  
  private String threadName;



  
  private ThrowableInformation throwableInfo;



  
  public final long timeStamp;



  
  private LocationInfo locationInfo;



  
  static final long serialVersionUID = -868428216207166145L;



  
  static final Integer[] PARAM_ARRAY = new Integer[1];
  static final String TO_LEVEL = "toLevel";
  static final Class[] TO_LEVEL_PARAMS = new Class[] { int.class };
  static final Hashtable methodCache = new Hashtable(3);











  
  public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable) {
    this.fqnOfCategoryClass = fqnOfCategoryClass;
    this.logger = logger;
    this.categoryName = logger.getName();
    this.level = level;
    this.message = message;
    if (throwable != null) {
      this.throwableInfo = new ThrowableInformation(throwable, logger);
    }
    this.timeStamp = System.currentTimeMillis();
  }













  
  public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable) {
    this.fqnOfCategoryClass = fqnOfCategoryClass;
    this.logger = logger;
    this.categoryName = logger.getName();
    this.level = level;
    this.message = message;
    if (throwable != null) {
      this.throwableInfo = new ThrowableInformation(throwable, logger);
    }
    
    this.timeStamp = timeStamp;
  }

























  
  public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Level level, Object message, String threadName, ThrowableInformation throwable, String ndc, LocationInfo info, Map properties) {
    this.fqnOfCategoryClass = fqnOfCategoryClass;
    this.logger = logger;
    if (logger != null) {
      this.categoryName = logger.getName();
    } else {
      this.categoryName = null;
    } 
    this.level = (Priority)level;
    this.message = message;
    if (throwable != null) {
      this.throwableInfo = throwable;
    }
    
    this.timeStamp = timeStamp;
    this.threadName = threadName;
    this.ndcLookupRequired = false;
    this.ndc = ndc;
    this.locationInfo = info;
    this.mdcCopyLookupRequired = false;
    if (properties != null) {
      this.mdcCopy = new Hashtable(properties);
    }
  }





  
  public LocationInfo getLocationInformation() {
    if (this.locationInfo == null) {
      this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
    }
    return this.locationInfo;
  }



  
  public Level getLevel() {
    return (Level)this.level;
  }




  
  public String getLoggerName() {
    return this.categoryName;
  }





  
  public Category getLogger() {
    return this.logger;
  }










  
  public Object getMessage() {
    if (this.message != null) {
      return this.message;
    }
    return getRenderedMessage();
  }







  
  public String getNDC() {
    if (this.ndcLookupRequired) {
      this.ndcLookupRequired = false;
      this.ndc = NDC.get();
    } 
    return this.ndc;
  }

















  
  public Object getMDC(String key) {
    if (this.mdcCopy != null) {
      Object r = this.mdcCopy.get(key);
      if (r != null) {
        return r;
      }
    } 
    return MDC.get(key);
  }





  
  public void getMDCCopy() {
    if (this.mdcCopyLookupRequired) {
      this.mdcCopyLookupRequired = false;

      
      Hashtable t = MDC.getContext();
      if (t != null) {
        this.mdcCopy = (Hashtable)t.clone();
      }
    } 
  }

  
  public String getRenderedMessage() {
    if (this.renderedMessage == null && this.message != null) {
      if (this.message instanceof String) {
        this.renderedMessage = (String)this.message;
      } else {
        LoggerRepository repository = this.logger.getLoggerRepository();
        
        if (repository instanceof RendererSupport) {
          RendererSupport rs = (RendererSupport)repository;
          this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
        } else {
          this.renderedMessage = this.message.toString();
        } 
      } 
    }
    return this.renderedMessage;
  }



  
  public static long getStartTime() {
    return startTime;
  }

  
  public String getThreadName() {
    if (this.threadName == null)
      this.threadName = Thread.currentThread().getName(); 
    return this.threadName;
  }









  
  public ThrowableInformation getThrowableInformation() {
    return this.throwableInfo;
  }





  
  public String[] getThrowableStrRep() {
    if (this.throwableInfo == null) {
      return null;
    }
    return this.throwableInfo.getThrowableStrRep();
  }




  
  private void readLevel(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    int p = ois.readInt();
    try {
      String className = (String)ois.readObject();
      if (className == null) {
        this.level = (Priority)Level.toLevel(p);
      } else {
        Method m = (Method)methodCache.get(className);
        if (m == null) {
          Class clazz = Loader.loadClass(className);





          
          m = clazz.getDeclaredMethod("toLevel", TO_LEVEL_PARAMS);
          methodCache.put(className, m);
        } 
        this.level = (Priority)m.invoke(null, (Object[])new Integer[] { new Integer(p) });
      } 
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof InterruptedException || e.getTargetException() instanceof java.io.InterruptedIOException)
      {
        Thread.currentThread().interrupt();
      }
      LogLog.warn("Level deserialization failed, reverting to default.", e);
      this.level = (Priority)Level.toLevel(p);
    } catch (NoSuchMethodException e) {
      LogLog.warn("Level deserialization failed, reverting to default.", e);
      this.level = (Priority)Level.toLevel(p);
    } catch (IllegalAccessException e) {
      LogLog.warn("Level deserialization failed, reverting to default.", e);
      this.level = (Priority)Level.toLevel(p);
    } catch (RuntimeException e) {
      LogLog.warn("Level deserialization failed, reverting to default.", e);
      this.level = (Priority)Level.toLevel(p);
    } 
  }

  
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    readLevel(ois);

    
    if (this.locationInfo == null) {
      this.locationInfo = new LocationInfo(null, null);
    }
  }


  
  private void writeObject(ObjectOutputStream oos) throws IOException {
    getThreadName();

    
    getRenderedMessage();


    
    getNDC();


    
    getMDCCopy();

    
    getThrowableStrRep();
    
    oos.defaultWriteObject();

    
    writeLevel(oos);
  }


  
  private void writeLevel(ObjectOutputStream oos) throws IOException {
    oos.writeInt(this.level.toInt());
    
    Class clazz = this.level.getClass();
    if (clazz == Level.class) {
      oos.writeObject(null);
    
    }
    else {
      
      oos.writeObject(clazz.getName());
    } 
  }











  
  public final void setProperty(String propName, String propValue) {
    if (this.mdcCopy == null) {
      getMDCCopy();
    }
    if (this.mdcCopy == null) {
      this.mdcCopy = new Hashtable();
    }
    this.mdcCopy.put(propName, propValue);
  }










  
  public final String getProperty(String key) {
    Object value = getMDC(key);
    String retval = null;
    if (value != null) {
      retval = value.toString();
    }
    return retval;
  }






  
  public final boolean locationInformationExists() {
    return (this.locationInfo != null);
  }







  
  public final long getTimeStamp() {
    return this.timeStamp;
  }











  
  public Set getPropertyKeySet() {
    return getProperties().keySet();
  }










  
  public Map getProperties() {
    Map properties;
    getMDCCopy();
    
    if (this.mdcCopy == null) {
      properties = new HashMap();
    } else {
      properties = this.mdcCopy;
    } 
    return Collections.unmodifiableMap(properties);
  }






  
  public String getFQNOfLoggerClass() {
    return this.fqnOfCategoryClass;
  }









  
  public Object removeProperty(String propName) {
    if (this.mdcCopy == null) {
      getMDCCopy();
    }
    if (this.mdcCopy == null) {
      this.mdcCopy = new Hashtable();
    }
    return this.mdcCopy.remove(propName);
  }
}
