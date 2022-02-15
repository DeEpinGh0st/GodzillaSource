package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.math.LongMath;
import java.util.concurrent.TimeUnit;


































































































































































































@GwtIncompatible
abstract class SmoothRateLimiter
  extends RateLimiter
{
  double storedPermits;
  double maxPermits;
  double stableIntervalMicros;
  
  static final class SmoothWarmingUp
    extends SmoothRateLimiter
  {
    private final long warmupPeriodMicros;
    private double slope;
    private double thresholdPermits;
    private double coldFactor;
    
    SmoothWarmingUp(RateLimiter.SleepingStopwatch stopwatch, long warmupPeriod, TimeUnit timeUnit, double coldFactor) {
      super(stopwatch);
      this.warmupPeriodMicros = timeUnit.toMicros(warmupPeriod);
      this.coldFactor = coldFactor;
    }

    
    void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
      double oldMaxPermits = this.maxPermits;
      double coldIntervalMicros = stableIntervalMicros * this.coldFactor;
      this.thresholdPermits = 0.5D * this.warmupPeriodMicros / stableIntervalMicros;
      this.maxPermits = this.thresholdPermits + 2.0D * this.warmupPeriodMicros / (stableIntervalMicros + coldIntervalMicros);
      
      this.slope = (coldIntervalMicros - stableIntervalMicros) / (this.maxPermits - this.thresholdPermits);
      if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        
        this.storedPermits = 0.0D;
      } else {
        this.storedPermits = (oldMaxPermits == 0.0D) ? this.maxPermits : (this.storedPermits * this.maxPermits / oldMaxPermits);
      } 
    }




    
    long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
      double availablePermitsAboveThreshold = storedPermits - this.thresholdPermits;
      long micros = 0L;
      
      if (availablePermitsAboveThreshold > 0.0D) {
        double permitsAboveThresholdToTake = Math.min(availablePermitsAboveThreshold, permitsToTake);


        
        double length = permitsToTime(availablePermitsAboveThreshold) + permitsToTime(availablePermitsAboveThreshold - permitsAboveThresholdToTake);
        micros = (long)(permitsAboveThresholdToTake * length / 2.0D);
        permitsToTake -= permitsAboveThresholdToTake;
      } 
      
      micros += (long)(this.stableIntervalMicros * permitsToTake);
      return micros;
    }
    
    private double permitsToTime(double permits) {
      return this.stableIntervalMicros + permits * this.slope;
    }

    
    double coolDownIntervalMicros() {
      return this.warmupPeriodMicros / this.maxPermits;
    }
  }



  
  static final class SmoothBursty
    extends SmoothRateLimiter
  {
    final double maxBurstSeconds;


    
    SmoothBursty(RateLimiter.SleepingStopwatch stopwatch, double maxBurstSeconds) {
      super(stopwatch);
      this.maxBurstSeconds = maxBurstSeconds;
    }

    
    void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
      double oldMaxPermits = this.maxPermits;
      this.maxPermits = this.maxBurstSeconds * permitsPerSecond;
      if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        
        this.storedPermits = this.maxPermits;
      } else {
        this.storedPermits = (oldMaxPermits == 0.0D) ? 0.0D : (this.storedPermits * this.maxPermits / oldMaxPermits);
      } 
    }




    
    long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
      return 0L;
    }

    
    double coolDownIntervalMicros() {
      return this.stableIntervalMicros;
    }
  }
















  
  private long nextFreeTicketMicros = 0L;
  
  private SmoothRateLimiter(RateLimiter.SleepingStopwatch stopwatch) {
    super(stopwatch);
  }

  
  final void doSetRate(double permitsPerSecond, long nowMicros) {
    resync(nowMicros);
    double stableIntervalMicros = TimeUnit.SECONDS.toMicros(1L) / permitsPerSecond;
    this.stableIntervalMicros = stableIntervalMicros;
    doSetRate(permitsPerSecond, stableIntervalMicros);
  }



  
  final double doGetRate() {
    return TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
  }

  
  final long queryEarliestAvailable(long nowMicros) {
    return this.nextFreeTicketMicros;
  }

  
  final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
    resync(nowMicros);
    long returnValue = this.nextFreeTicketMicros;
    double storedPermitsToSpend = Math.min(requiredPermits, this.storedPermits);
    double freshPermits = requiredPermits - storedPermitsToSpend;
    
    long waitMicros = storedPermitsToWaitTime(this.storedPermits, storedPermitsToSpend) + (long)(freshPermits * this.stableIntervalMicros);

    
    this.nextFreeTicketMicros = LongMath.saturatedAdd(this.nextFreeTicketMicros, waitMicros);
    this.storedPermits -= storedPermitsToSpend;
    return returnValue;
  }
















  
  void resync(long nowMicros) {
    if (nowMicros > this.nextFreeTicketMicros) {
      double newPermits = (nowMicros - this.nextFreeTicketMicros) / coolDownIntervalMicros();
      this.storedPermits = Math.min(this.maxPermits, this.storedPermits + newPermits);
      this.nextFreeTicketMicros = nowMicros;
    } 
  }
  
  abstract void doSetRate(double paramDouble1, double paramDouble2);
  
  abstract long storedPermitsToWaitTime(double paramDouble1, double paramDouble2);
  
  abstract double coolDownIntervalMicros();
}
