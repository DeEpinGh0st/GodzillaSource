package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;



























public class BoundedFIFO
{
  LoggingEvent[] buf;
  int numElements = 0;
  int first = 0;
  int next = 0;

  
  int maxSize;


  
  public BoundedFIFO(int maxSize) {
    if (maxSize < 1) {
      throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
    }
    
    this.maxSize = maxSize;
    this.buf = new LoggingEvent[maxSize];
  }




  
  public LoggingEvent get() {
    if (this.numElements == 0) {
      return null;
    }
    LoggingEvent r = this.buf[this.first];
    this.buf[this.first] = null;
    
    if (++this.first == this.maxSize) {
      this.first = 0;
    }
    this.numElements--;
    return r;
  }





  
  public void put(LoggingEvent o) {
    if (this.numElements != this.maxSize) {
      this.buf[this.next] = o;
      if (++this.next == this.maxSize) {
        this.next = 0;
      }
      this.numElements++;
    } 
  }




  
  public int getMaxSize() {
    return this.maxSize;
  }




  
  public boolean isFull() {
    return (this.numElements == this.maxSize);
  }






  
  public int length() {
    return this.numElements;
  }

  
  int min(int a, int b) {
    return (a < b) ? a : b;
  }









  
  public synchronized void resize(int newSize) {
    if (newSize == this.maxSize) {
      return;
    }
    
    LoggingEvent[] tmp = new LoggingEvent[newSize];

    
    int len1 = this.maxSize - this.first;

    
    len1 = min(len1, newSize);


    
    len1 = min(len1, this.numElements);

    
    System.arraycopy(this.buf, this.first, tmp, 0, len1);

    
    int len2 = 0;
    if (len1 < this.numElements && len1 < newSize) {
      len2 = this.numElements - len1;
      len2 = min(len2, newSize - len1);
      System.arraycopy(this.buf, 0, tmp, len1, len2);
    } 
    
    this.buf = tmp;
    this.maxSize = newSize;
    this.first = 0;
    this.numElements = len1 + len2;
    this.next = this.numElements;
    if (this.next == this.maxSize) {
      this.next = 0;
    }
  }





  
  public boolean wasEmpty() {
    return (this.numElements == 1);
  }





  
  public boolean wasFull() {
    return (this.numElements + 1 == this.maxSize);
  }
}
