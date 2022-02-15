package org.fife.ui.rtextarea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;































public final class ClipboardHistory
{
  private static ClipboardHistory instance;
  private List<String> history = new ArrayList<>();
  private int maxSize = 12;



  
  private static final int DEFAULT_MAX_SIZE = 12;



  
  public void add(String str) {
    int size = this.history.size();
    if (size == 0) {
      this.history.add(str);
    } else {
      
      int index = this.history.indexOf(str);
      if (index != size - 1) {
        if (index > -1) {
          this.history.remove(index);
        }
        this.history.add(str);
      } 
      trim();
    } 
  }









  
  public static ClipboardHistory get() {
    if (instance == null) {
      instance = new ClipboardHistory();
    }
    return instance;
  }






  
  public List<String> getHistory() {
    List<String> copy = new ArrayList<>(this.history);
    Collections.reverse(copy);
    return copy;
  }







  
  public int getMaxSize() {
    return this.maxSize;
  }









  
  public void setMaxSize(int maxSize) {
    if (maxSize <= 0) {
      throw new IllegalArgumentException("Maximum size must be >= 0");
    }
    this.maxSize = maxSize;
    trim();
  }





  
  private void trim() {
    while (this.history.size() > this.maxSize)
      this.history.remove(0); 
  }
}
