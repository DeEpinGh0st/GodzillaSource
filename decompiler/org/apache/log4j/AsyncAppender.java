package org.apache.log4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
















































public class AsyncAppender
  extends AppenderSkeleton
  implements AppenderAttachable
{
  public static final int DEFAULT_BUFFER_SIZE = 128;
  private final List buffer = new ArrayList();



  
  private final Map discardMap = new HashMap();



  
  private int bufferSize = 128;



  
  AppenderAttachableImpl aai;



  
  private final AppenderAttachableImpl appenders;



  
  private final Thread dispatcher;


  
  private boolean locationInfo = false;


  
  private boolean blocking = true;



  
  public AsyncAppender() {
    this.appenders = new AppenderAttachableImpl();


    
    this.aai = this.appenders;
    
    this.dispatcher = new Thread(new Dispatcher(this, this.buffer, this.discardMap, this.appenders));



    
    this.dispatcher.setDaemon(true);


    
    this.dispatcher.setName("AsyncAppender-Dispatcher-" + this.dispatcher.getName());
    this.dispatcher.start();
  }





  
  public void addAppender(Appender newAppender) {
    synchronized (this.appenders) {
      this.appenders.addAppender(newAppender);
    } 
  }







  
  public void append(LoggingEvent event) {
    if (this.dispatcher == null || !this.dispatcher.isAlive() || this.bufferSize <= 0) {
      synchronized (this.appenders) {
        this.appenders.appendLoopOnAppenders(event);
      } 

      
      return;
    } 

    
    event.getNDC();
    event.getThreadName();
    
    event.getMDCCopy();
    if (this.locationInfo) {
      event.getLocationInformation();
    }
    event.getRenderedMessage();
    event.getThrowableStrRep();
    
    synchronized (this.buffer) {
      while (true) {
        int previousSize = this.buffer.size();
        
        if (previousSize < this.bufferSize) {
          this.buffer.add(event);





          
          if (previousSize == 0) {
            this.buffer.notifyAll();
          }




          
          break;
        } 



        
        boolean discard = true;
        if (this.blocking && !Thread.interrupted() && Thread.currentThread() != this.dispatcher) {
          
          try {
            
            this.buffer.wait();
            discard = false;
          } catch (InterruptedException e) {



            
            Thread.currentThread().interrupt();
          } 
        }




        
        if (discard) {
          String loggerName = event.getLoggerName();
          DiscardSummary summary = (DiscardSummary)this.discardMap.get(loggerName);
          
          if (summary == null) {
            summary = new DiscardSummary(event);
            this.discardMap.put(loggerName, summary); break;
          } 
          summary.add(event);
          break;
        } 
      } 
    } 
  }










  
  public void close() {
    synchronized (this.buffer) {
      this.closed = true;
      this.buffer.notifyAll();
    } 
    
    try {
      this.dispatcher.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LogLog.error("Got an InterruptedException while waiting for the dispatcher to finish.", e);
    } 





    
    synchronized (this.appenders) {
      Enumeration iter = this.appenders.getAllAppenders();
      
      if (iter != null) {
        while (iter.hasMoreElements()) {
          Object next = iter.nextElement();
          
          if (next instanceof Appender) {
            ((Appender)next).close();
          }
        } 
      }
    } 
  }




  
  public Enumeration getAllAppenders() {
    synchronized (this.appenders) {
      return this.appenders.getAllAppenders();
    } 
  }






  
  public Appender getAppender(String name) {
    synchronized (this.appenders) {
      return this.appenders.getAppender(name);
    } 
  }






  
  public boolean getLocationInfo() {
    return this.locationInfo;
  }





  
  public boolean isAttached(Appender appender) {
    synchronized (this.appenders) {
      return this.appenders.isAttached(appender);
    } 
  }



  
  public boolean requiresLayout() {
    return false;
  }



  
  public void removeAllAppenders() {
    synchronized (this.appenders) {
      this.appenders.removeAllAppenders();
    } 
  }




  
  public void removeAppender(Appender appender) {
    synchronized (this.appenders) {
      this.appenders.removeAppender(appender);
    } 
  }




  
  public void removeAppender(String name) {
    synchronized (this.appenders) {
      this.appenders.removeAppender(name);
    } 
  }













  
  public void setLocationInfo(boolean flag) {
    this.locationInfo = flag;
  }












  
  public void setBufferSize(int size) {
    if (size < 0) {
      throw new NegativeArraySizeException("size");
    }
    
    synchronized (this.buffer) {


      
      this.bufferSize = (size < 1) ? 1 : size;
      this.buffer.notifyAll();
    } 
  }




  
  public int getBufferSize() {
    return this.bufferSize;
  }







  
  public void setBlocking(boolean value) {
    synchronized (this.buffer) {
      this.blocking = value;
      this.buffer.notifyAll();
    } 
  }








  
  public boolean getBlocking() {
    return this.blocking;
  }





  
  private static final class DiscardSummary
  {
    private LoggingEvent maxEvent;




    
    private int count;




    
    public DiscardSummary(LoggingEvent event) {
      this.maxEvent = event;
      this.count = 1;
    }





    
    public void add(LoggingEvent event) {
      if (event.getLevel().toInt() > this.maxEvent.getLevel().toInt()) {
        this.maxEvent = event;
      }
      
      this.count++;
    }





    
    public LoggingEvent createEvent() {
      String msg = MessageFormat.format("Discarded {0} messages due to full event buffer including: {1}", new Object[] { new Integer(this.count), this.maxEvent.getMessage() });



      
      return new LoggingEvent("org.apache.log4j.AsyncAppender.DONT_REPORT_LOCATION", Logger.getLogger(this.maxEvent.getLoggerName()), this.maxEvent.getLevel(), msg, null);
    }
  }







  
  private static class Dispatcher
    implements Runnable
  {
    private final AsyncAppender parent;






    
    private final List buffer;





    
    private final Map discardMap;





    
    private final AppenderAttachableImpl appenders;






    
    public Dispatcher(AsyncAppender parent, List buffer, Map discardMap, AppenderAttachableImpl appenders) {
      this.parent = parent;
      this.buffer = buffer;
      this.appenders = appenders;
      this.discardMap = discardMap;
    }



    
    public void run() {
      boolean isActive = true;






      
      try {
        while (isActive) {
          LoggingEvent[] events = null;




          
          synchronized (this.buffer) {
            int bufferSize = this.buffer.size();
            isActive = !this.parent.closed;
            
            while (bufferSize == 0 && isActive) {
              this.buffer.wait();
              bufferSize = this.buffer.size();
              isActive = !this.parent.closed;
            } 
            
            if (bufferSize > 0) {
              events = new LoggingEvent[bufferSize + this.discardMap.size()];
              this.buffer.toArray((Object[])events);



              
              int index = bufferSize;

              
              Iterator iter = this.discardMap.values().iterator();
              while (iter.hasNext()) {
                events[index++] = ((AsyncAppender.DiscardSummary)iter.next()).createEvent();
              }



              
              this.buffer.clear();
              this.discardMap.clear();


              
              this.buffer.notifyAll();
            } 
          } 



          
          if (events != null) {
            for (int i = 0; i < events.length; i++) {
              synchronized (this.appenders) {
                this.appenders.appendLoopOnAppenders(events[i]);
              } 
            } 
          }
        } 
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } 
    }
  }
}
