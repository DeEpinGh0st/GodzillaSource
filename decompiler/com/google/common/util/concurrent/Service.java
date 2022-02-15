package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;













































































































































@Beta
@GwtIncompatible
public interface Service
{
  @CanIgnoreReturnValue
  Service startAsync();
  
  boolean isRunning();
  
  State state();
  
  @CanIgnoreReturnValue
  Service stopAsync();
  
  void awaitRunning();
  
  void awaitRunning(long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
  
  void awaitTerminated();
  
  void awaitTerminated(long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
  
  Throwable failureCause();
  
  void addListener(Listener paramListener, Executor paramExecutor);
  
  @Beta
  public enum State
  {
    NEW
    {
      boolean isTerminal() {
        return false;
      }
    },

    
    STARTING
    {
      boolean isTerminal() {
        return false;
      }
    },

    
    RUNNING
    {
      boolean isTerminal() {
        return false;
      }
    },

    
    STOPPING
    {
      boolean isTerminal() {
        return false;
      }
    },




    
    TERMINATED
    {
      boolean isTerminal() {
        return true;
      }
    },




    
    FAILED
    {
      boolean isTerminal() {
        return true;
      }
    };
    
    abstract boolean isTerminal();
  }
  
  @Beta
  public static abstract class Listener {
    public void starting() {}
    
    public void running() {}
    
    public void stopping(Service.State from) {}
    
    public void terminated(Service.State from) {}
    
    public void failed(Service.State from, Throwable failure) {}
  }
}
