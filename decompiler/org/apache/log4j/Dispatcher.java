package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.BoundedFIFO;
import org.apache.log4j.spi.LoggingEvent;






























class Dispatcher
  extends Thread
{
  private BoundedFIFO bf;
  private AppenderAttachableImpl aai;
  private boolean interrupted = false;
  AsyncAppender container;
  
  Dispatcher(BoundedFIFO bf, AsyncAppender container) {
    this.bf = bf;
    this.container = container;
    this.aai = container.aai;


    
    setDaemon(true);

    
    setPriority(1);
    setName("Dispatcher-" + getName());
  }




  
  void close() {
    synchronized (this.bf) {
      this.interrupted = true;


      
      if (this.bf.length() == 0) {
        this.bf.notify();
      }
    } 
  }












  
  public void run() {
    while (true) {
      LoggingEvent event;
      synchronized (this.bf) {
        if (this.bf.length() == 0) {
          
          if (this.interrupted) {
            break;
          }


          
          try {
            this.bf.wait();
          } catch (InterruptedException e) {
            break;
          } 
        } 
        
        event = this.bf.get();
        
        if (this.bf.wasFull())
        {
          this.bf.notify();
        }
      } 

      
      synchronized (this.container.aai) {
        if (this.aai != null && event != null) {
          this.aai.appendLoopOnAppenders(event);
        }
      } 
    } 


    
    this.aai.removeAllAppenders();
  }
}
