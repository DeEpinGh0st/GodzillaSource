package org.springframework.util.backoff;































public class FixedBackOff
  implements BackOff
{
  public static final long DEFAULT_INTERVAL = 5000L;
  public static final long UNLIMITED_ATTEMPTS = 9223372036854775807L;
  private long interval = 5000L;
  
  private long maxAttempts = Long.MAX_VALUE;













  
  public FixedBackOff(long interval, long maxAttempts) {
    this.interval = interval;
    this.maxAttempts = maxAttempts;
  }




  
  public void setInterval(long interval) {
    this.interval = interval;
  }



  
  public long getInterval() {
    return this.interval;
  }



  
  public void setMaxAttempts(long maxAttempts) {
    this.maxAttempts = maxAttempts;
  }



  
  public long getMaxAttempts() {
    return this.maxAttempts;
  }

  
  public BackOffExecution start() {
    return new FixedBackOffExecution();
  }
  
  public FixedBackOff() {}
  
  private class FixedBackOffExecution implements BackOffExecution {
    private long currentAttempts = 0L;

    
    public long nextBackOff() {
      this.currentAttempts++;
      if (this.currentAttempts <= FixedBackOff.this.getMaxAttempts()) {
        return FixedBackOff.this.getInterval();
      }
      
      return -1L;
    }



    
    public String toString() {
      String attemptValue = (FixedBackOff.this.maxAttempts == Long.MAX_VALUE) ? "unlimited" : String.valueOf(FixedBackOff.this.maxAttempts);
      return "FixedBackOff{interval=" + FixedBackOff.this.interval + ", currentAttempts=" + this.currentAttempts + ", maxAttempts=" + attemptValue + '}';
    }
    
    private FixedBackOffExecution() {}
  }
}
