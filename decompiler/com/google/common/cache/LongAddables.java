package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicLong;



















@GwtCompatible(emulated = true)
final class LongAddables
{
  private static final Supplier<LongAddable> SUPPLIER;
  
  static {
    Supplier<LongAddable> supplier;
    try {
      new LongAdder();
      supplier = new Supplier<LongAddable>()
        {
          public LongAddable get()
          {
            return new LongAdder();
          }
        };
    } catch (Throwable t) {
      supplier = new Supplier<LongAddable>()
        {
          public LongAddable get()
          {
            return new LongAddables.PureJavaLongAddable();
          }
        };
    } 
    SUPPLIER = supplier;
  }
  
  public static LongAddable create() {
    return (LongAddable)SUPPLIER.get();
  }
  
  private static final class PureJavaLongAddable
    extends AtomicLong implements LongAddable {
    public void increment() {
      getAndIncrement();
    }
    private PureJavaLongAddable() {}
    
    public void add(long x) {
      getAndAdd(x);
    }

    
    public long sum() {
      return get();
    }
  }
}
