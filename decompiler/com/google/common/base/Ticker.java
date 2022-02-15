package com.google.common.base;

import com.google.common.annotations.GwtCompatible;


































@GwtCompatible
public abstract class Ticker
{
  public abstract long read();
  
  public static Ticker systemTicker() {
    return SYSTEM_TICKER;
  }
  
  private static final Ticker SYSTEM_TICKER = new Ticker()
    {
      public long read()
      {
        return Platform.systemNanoTime();
      }
    };
}
