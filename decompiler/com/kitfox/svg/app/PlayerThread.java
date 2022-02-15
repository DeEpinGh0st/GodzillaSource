package com.kitfox.svg.app;

import java.util.HashSet;









































public class PlayerThread
  implements Runnable
{
  HashSet<PlayerThreadListener> listeners = new HashSet<PlayerThreadListener>();
  
  double curTime = 0.0D;
  double timeStep = 0.2D;
  
  public static final int PS_STOP = 0;
  
  public static final int PS_PLAY_FWD = 1;
  public static final int PS_PLAY_BACK = 2;
  int playState = 0;

  
  Thread thread;

  
  public PlayerThread() {
    this.thread = new Thread(this);
    this.thread.start();
  }

  
  public void run() {
    while (this.thread != null) {
      
      synchronized (this) {
        
        switch (this.playState) {
          
          case 1:
            this.curTime += this.timeStep;
            break;
          case 2:
            this.curTime -= this.timeStep;
            if (this.curTime < 0.0D) this.curTime = 0.0D;
            
            break;
        } 


        
        fireTimeUpdateEvent();
      } 

      
      try {
        Thread.sleep((long)(this.timeStep * 1000.0D));
      }
      catch (Exception e) {
        
        throw new RuntimeException(e);
      } 
    } 
  }
  public void exit() {
    this.thread = null;
  }
  public synchronized void addListener(PlayerThreadListener listener) {
    this.listeners.add(listener);
  }
  public synchronized double getCurTime() {
    return this.curTime;
  }
  
  public synchronized void setCurTime(double time) {
    this.curTime = time;
  }
  public synchronized double getTimeStep() {
    return this.timeStep;
  }
  
  public synchronized void setTimeStep(double time) {
    this.timeStep = time;
    if (this.timeStep < 0.01D) this.timeStep = 0.01D; 
  }
  public synchronized int getPlayState() {
    return this.playState;
  }
  
  public synchronized void setPlayState(int playState) {
    this.playState = playState;
  }

  
  private void fireTimeUpdateEvent() {
    for (PlayerThreadListener listener : this.listeners)
      listener.updateTime(this.curTime, this.timeStep, this.playState); 
  }
}
