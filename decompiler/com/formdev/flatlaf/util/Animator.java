package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatSystemProperties;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Timer;






















public class Animator
{
  private int duration;
  private int resolution = 10;
  private Interpolator interpolator;
  private final ArrayList<TimingTarget> targets = new ArrayList<>();
  
  private final Runnable endRunnable;
  
  private boolean running;
  
  private boolean hasBegun;
  
  private boolean timeToStop;
  
  private long startTime;
  
  private Timer timer;
  
  public static boolean useAnimation() {
    return FlatSystemProperties.getBoolean("flatlaf.animation", true);
  }







  
  public Animator(int duration) {
    this(duration, null, null);
  }







  
  public Animator(int duration, TimingTarget target) {
    this(duration, target, null);
  }








  
  public Animator(int duration, TimingTarget target, Runnable endRunnable) {
    setDuration(duration);
    addTarget(target);
    this.endRunnable = endRunnable;
  }



  
  public int getDuration() {
    return this.duration;
  }






  
  public void setDuration(int duration) {
    throwExceptionIfRunning();
    if (duration <= 0)
      throw new IllegalArgumentException(); 
    this.duration = duration;
  }




  
  public int getResolution() {
    return this.resolution;
  }







  
  public void setResolution(int resolution) {
    throwExceptionIfRunning();
    if (resolution <= 0)
      throw new IllegalArgumentException(); 
    this.resolution = resolution;
  }




  
  public Interpolator getInterpolator() {
    return this.interpolator;
  }





  
  public void setInterpolator(Interpolator interpolator) {
    throwExceptionIfRunning();
    this.interpolator = interpolator;
  }





  
  public void addTarget(TimingTarget target) {
    if (target == null) {
      return;
    }
    synchronized (this.targets) {
      if (!this.targets.contains(target)) {
        this.targets.add(target);
      }
    } 
  }




  
  public void removeTarget(TimingTarget target) {
    synchronized (this.targets) {
      this.targets.remove(target);
    } 
  }





  
  public void start() {
    throwExceptionIfRunning();
    
    this.running = true;
    this.hasBegun = false;
    this.timeToStop = false;
    this.startTime = System.nanoTime() / 1000000L;
    
    if (this.timer == null) {
      this.timer = new Timer(this.resolution, e -> {
            if (!this.hasBegun) {
              begin();
              
              this.hasBegun = true;
            } 
            timingEvent(getTimingFraction());
          });
    } else {
      this.timer.setDelay(this.resolution);
    }  this.timer.setInitialDelay(0);
    this.timer.start();
  }




  
  public void stop() {
    stop(false);
  }




  
  public void cancel() {
    stop(true);
  }
  
  private void stop(boolean cancel) {
    if (!this.running) {
      return;
    }
    if (this.timer != null) {
      this.timer.stop();
    }
    if (!cancel) {
      end();
    }
    this.running = false;
    this.timeToStop = false;
  }




  
  public void restart() {
    cancel();
    start();
  }



  
  public boolean isRunning() {
    return this.running;
  }
  
  private float getTimingFraction() {
    long currentTime = System.nanoTime() / 1000000L;
    long elapsedTime = currentTime - this.startTime;
    this.timeToStop = (elapsedTime >= this.duration);
    
    float fraction = clampFraction((float)elapsedTime / this.duration);
    if (this.interpolator != null)
      fraction = clampFraction(this.interpolator.interpolate(fraction)); 
    return fraction;
  }
  
  private float clampFraction(float fraction) {
    if (fraction < 0.0F)
      return 0.0F; 
    if (fraction > 1.0F)
      return 1.0F; 
    return fraction;
  }
  
  private void timingEvent(float fraction) {
    synchronized (this.targets) {
      for (TimingTarget target : this.targets) {
        target.timingEvent(fraction);
      }
    } 
    if (this.timeToStop)
      stop(); 
  }
  
  private void begin() {
    synchronized (this.targets) {
      for (TimingTarget target : this.targets)
        target.begin(); 
    } 
  }
  
  private void end() {
    synchronized (this.targets) {
      for (TimingTarget target : this.targets) {
        target.end();
      }
    } 
    if (this.endRunnable != null)
      this.endRunnable.run(); 
  }
  
  private void throwExceptionIfRunning() {
    if (isRunning())
      throw new IllegalStateException(); 
  }
  
  @FunctionalInterface
  public static interface Interpolator {
    float interpolate(float param1Float);
  }
  
  @FunctionalInterface
  public static interface TimingTarget {
    void timingEvent(float param1Float);
    
    default void begin() {}
    
    default void end() {}
  }
}
