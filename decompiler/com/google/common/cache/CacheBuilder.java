package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Ticker;
import com.google.errorprone.annotations.CheckReturnValue;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;















































































































































@GwtCompatible(emulated = true)
public final class CacheBuilder<K, V>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
  private static final int DEFAULT_EXPIRATION_NANOS = 0;
  private static final int DEFAULT_REFRESH_NANOS = 0;
  
  static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter()
      {
        public void recordHits(int count) {}


        
        public void recordMisses(int count) {}


        
        public void recordLoadSuccess(long loadTime) {}


        
        public void recordLoadException(long loadTime) {}

        
        public void recordEviction() {}

        
        public CacheStats snapshot() {
          return CacheBuilder.EMPTY_STATS;
        }
      });
  static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
  
  static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER = new Supplier<AbstractCache.StatsCounter>()
    {
      public AbstractCache.StatsCounter get()
      {
        return new AbstractCache.SimpleStatsCounter();
      }
    };
  
  enum NullListener implements RemovalListener<Object, Object> {
    INSTANCE;
    
    public void onRemoval(RemovalNotification<Object, Object> notification) {}
  }
  
  enum OneWeigher
    implements Weigher<Object, Object> {
    INSTANCE;

    
    public int weigh(Object key, Object value) {
      return 1;
    }
  }
  
  static final Ticker NULL_TICKER = new Ticker()
    {
      public long read()
      {
        return 0L;
      }
    };
  
  private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());
  
  static final int UNSET_INT = -1;
  
  boolean strictParsing = true;
  
  int initialCapacity = -1;
  int concurrencyLevel = -1;
  long maximumSize = -1L;
  long maximumWeight = -1L;
  
  Weigher<? super K, ? super V> weigher;
  
  LocalCache.Strength keyStrength;
  LocalCache.Strength valueStrength;
  long expireAfterWriteNanos = -1L;

  
  long expireAfterAccessNanos = -1L;

  
  long refreshNanos = -1L;
  
  Equivalence<Object> keyEquivalence;
  
  Equivalence<Object> valueEquivalence;
  
  RemovalListener<? super K, ? super V> removalListener;
  
  Ticker ticker;
  Supplier<? extends AbstractCache.StatsCounter> statsCounterSupplier = NULL_STATS_COUNTER;









  
  public static CacheBuilder<Object, Object> newBuilder() {
    return new CacheBuilder<>();
  }





  
  @GwtIncompatible
  public static CacheBuilder<Object, Object> from(CacheBuilderSpec spec) {
    return spec.toCacheBuilder().lenientParsing();
  }







  
  @GwtIncompatible
  public static CacheBuilder<Object, Object> from(String spec) {
    return from(CacheBuilderSpec.parse(spec));
  }





  
  @GwtIncompatible
  CacheBuilder<K, V> lenientParsing() {
    this.strictParsing = false;
    return this;
  }








  
  @GwtIncompatible
  CacheBuilder<K, V> keyEquivalence(Equivalence<Object> equivalence) {
    Preconditions.checkState((this.keyEquivalence == null), "key equivalence was already set to %s", this.keyEquivalence);
    this.keyEquivalence = (Equivalence<Object>)Preconditions.checkNotNull(equivalence);
    return this;
  }
  
  Equivalence<Object> getKeyEquivalence() {
    return (Equivalence<Object>)MoreObjects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
  }









  
  @GwtIncompatible
  CacheBuilder<K, V> valueEquivalence(Equivalence<Object> equivalence) {
    Preconditions.checkState((this.valueEquivalence == null), "value equivalence was already set to %s", this.valueEquivalence);
    
    this.valueEquivalence = (Equivalence<Object>)Preconditions.checkNotNull(equivalence);
    return this;
  }
  
  Equivalence<Object> getValueEquivalence() {
    return (Equivalence<Object>)MoreObjects.firstNonNull(this.valueEquivalence, getValueStrength().defaultEquivalence());
  }











  
  public CacheBuilder<K, V> initialCapacity(int initialCapacity) {
    Preconditions.checkState((this.initialCapacity == -1), "initial capacity was already set to %s", this.initialCapacity);


    
    Preconditions.checkArgument((initialCapacity >= 0));
    this.initialCapacity = initialCapacity;
    return this;
  }
  
  int getInitialCapacity() {
    return (this.initialCapacity == -1) ? 16 : this.initialCapacity;
  }































  
  public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
    Preconditions.checkState((this.concurrencyLevel == -1), "concurrency level was already set to %s", this.concurrencyLevel);


    
    Preconditions.checkArgument((concurrencyLevel > 0));
    this.concurrencyLevel = concurrencyLevel;
    return this;
  }
  
  int getConcurrencyLevel() {
    return (this.concurrencyLevel == -1) ? 4 : this.concurrencyLevel;
  }





















  
  public CacheBuilder<K, V> maximumSize(long maximumSize) {
    Preconditions.checkState((this.maximumSize == -1L), "maximum size was already set to %s", this.maximumSize);
    
    Preconditions.checkState((this.maximumWeight == -1L), "maximum weight was already set to %s", this.maximumWeight);


    
    Preconditions.checkState((this.weigher == null), "maximum size can not be combined with weigher");
    Preconditions.checkArgument((maximumSize >= 0L), "maximum size must not be negative");
    this.maximumSize = maximumSize;
    return this;
  }



























  
  @GwtIncompatible
  public CacheBuilder<K, V> maximumWeight(long maximumWeight) {
    Preconditions.checkState((this.maximumWeight == -1L), "maximum weight was already set to %s", this.maximumWeight);


    
    Preconditions.checkState((this.maximumSize == -1L), "maximum size was already set to %s", this.maximumSize);
    
    this.maximumWeight = maximumWeight;
    Preconditions.checkArgument((maximumWeight >= 0L), "maximum weight must not be negative");
    return this;
  }






























  
  @GwtIncompatible
  public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(Weigher<? super K1, ? super V1> weigher) {
    Preconditions.checkState((this.weigher == null));
    if (this.strictParsing) {
      Preconditions.checkState((this.maximumSize == -1L), "weigher can not be combined with maximum size", this.maximumSize);
    }





    
    CacheBuilder<K1, V1> me = this;
    me.weigher = (Weigher<? super K, ? super V>)Preconditions.checkNotNull(weigher);
    return me;
  }
  
  long getMaximumWeight() {
    if (this.expireAfterWriteNanos == 0L || this.expireAfterAccessNanos == 0L) {
      return 0L;
    }
    return (this.weigher == null) ? this.maximumSize : this.maximumWeight;
  }


  
  <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher() {
    return (Weigher<K1, V1>)MoreObjects.firstNonNull(this.weigher, OneWeigher.INSTANCE);
  }
















  
  @GwtIncompatible
  public CacheBuilder<K, V> weakKeys() {
    return setKeyStrength(LocalCache.Strength.WEAK);
  }
  
  CacheBuilder<K, V> setKeyStrength(LocalCache.Strength strength) {
    Preconditions.checkState((this.keyStrength == null), "Key strength was already set to %s", this.keyStrength);
    this.keyStrength = (LocalCache.Strength)Preconditions.checkNotNull(strength);
    return this;
  }
  
  LocalCache.Strength getKeyStrength() {
    return (LocalCache.Strength)MoreObjects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
  }

















  
  @GwtIncompatible
  public CacheBuilder<K, V> weakValues() {
    return setValueStrength(LocalCache.Strength.WEAK);
  }




















  
  @GwtIncompatible
  public CacheBuilder<K, V> softValues() {
    return setValueStrength(LocalCache.Strength.SOFT);
  }
  
  CacheBuilder<K, V> setValueStrength(LocalCache.Strength strength) {
    Preconditions.checkState((this.valueStrength == null), "Value strength was already set to %s", this.valueStrength);
    this.valueStrength = (LocalCache.Strength)Preconditions.checkNotNull(strength);
    return this;
  }
  
  LocalCache.Strength getValueStrength() {
    return (LocalCache.Strength)MoreObjects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
  }






















  
  @GwtIncompatible
  public CacheBuilder<K, V> expireAfterWrite(Duration duration) {
    return expireAfterWrite(duration.toNanos(), TimeUnit.NANOSECONDS);
  }























  
  public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit) {
    Preconditions.checkState((this.expireAfterWriteNanos == -1L), "expireAfterWrite was already set to %s ns", this.expireAfterWriteNanos);


    
    Preconditions.checkArgument((duration >= 0L), "duration cannot be negative: %s %s", duration, unit);
    this.expireAfterWriteNanos = unit.toNanos(duration);
    return this;
  }
  
  long getExpireAfterWriteNanos() {
    return (this.expireAfterWriteNanos == -1L) ? 0L : this.expireAfterWriteNanos;
  }

























  
  @GwtIncompatible
  public CacheBuilder<K, V> expireAfterAccess(Duration duration) {
    return expireAfterAccess(duration.toNanos(), TimeUnit.NANOSECONDS);
  }


























  
  public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
    Preconditions.checkState((this.expireAfterAccessNanos == -1L), "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);


    
    Preconditions.checkArgument((duration >= 0L), "duration cannot be negative: %s %s", duration, unit);
    this.expireAfterAccessNanos = unit.toNanos(duration);
    return this;
  }
  
  long getExpireAfterAccessNanos() {
    return (this.expireAfterAccessNanos == -1L) ? 0L : this.expireAfterAccessNanos;
  }






























  
  @GwtIncompatible
  public CacheBuilder<K, V> refreshAfterWrite(Duration duration) {
    return refreshAfterWrite(duration.toNanos(), TimeUnit.NANOSECONDS);
  }






























  
  @GwtIncompatible
  public CacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit) {
    Preconditions.checkNotNull(unit);
    Preconditions.checkState((this.refreshNanos == -1L), "refresh was already set to %s ns", this.refreshNanos);
    Preconditions.checkArgument((duration > 0L), "duration must be positive: %s %s", duration, unit);
    this.refreshNanos = unit.toNanos(duration);
    return this;
  }
  
  long getRefreshNanos() {
    return (this.refreshNanos == -1L) ? 0L : this.refreshNanos;
  }










  
  public CacheBuilder<K, V> ticker(Ticker ticker) {
    Preconditions.checkState((this.ticker == null));
    this.ticker = (Ticker)Preconditions.checkNotNull(ticker);
    return this;
  }
  
  Ticker getTicker(boolean recordsTime) {
    if (this.ticker != null) {
      return this.ticker;
    }
    return recordsTime ? Ticker.systemTicker() : NULL_TICKER;
  }






















  
  @CheckReturnValue
  public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> listener) {
    Preconditions.checkState((this.removalListener == null));


    
    CacheBuilder<K1, V1> me = this;
    me.removalListener = (RemovalListener<? super K, ? super V>)Preconditions.checkNotNull(listener);
    return me;
  }


  
  <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener() {
    return 
      (RemovalListener<K1, V1>)MoreObjects.firstNonNull(this.removalListener, NullListener.INSTANCE);
  }









  
  public CacheBuilder<K, V> recordStats() {
    this.statsCounterSupplier = CACHE_STATS_COUNTER;
    return this;
  }
  
  boolean isRecordingStats() {
    return (this.statsCounterSupplier == CACHE_STATS_COUNTER);
  }
  
  Supplier<? extends AbstractCache.StatsCounter> getStatsCounterSupplier() {
    return this.statsCounterSupplier;
  }













  
  public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> loader) {
    checkWeightWithWeigher();
    return new LocalCache.LocalLoadingCache<>(this, loader);
  }












  
  public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
    checkWeightWithWeigher();
    checkNonLoadingCache();
    return new LocalCache.LocalManualCache<>(this);
  }
  
  private void checkNonLoadingCache() {
    Preconditions.checkState((this.refreshNanos == -1L), "refreshAfterWrite requires a LoadingCache");
  }
  
  private void checkWeightWithWeigher() {
    if (this.weigher == null) {
      Preconditions.checkState((this.maximumWeight == -1L), "maximumWeight requires weigher");
    }
    else if (this.strictParsing) {
      Preconditions.checkState((this.maximumWeight != -1L), "weigher requires maximumWeight");
    }
    else if (this.maximumWeight == -1L) {
      logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
    } 
  }







  
  public String toString() {
    MoreObjects.ToStringHelper s = MoreObjects.toStringHelper(this);
    if (this.initialCapacity != -1) {
      s.add("initialCapacity", this.initialCapacity);
    }
    if (this.concurrencyLevel != -1) {
      s.add("concurrencyLevel", this.concurrencyLevel);
    }
    if (this.maximumSize != -1L) {
      s.add("maximumSize", this.maximumSize);
    }
    if (this.maximumWeight != -1L) {
      s.add("maximumWeight", this.maximumWeight);
    }
    if (this.expireAfterWriteNanos != -1L) {
      s.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
    }
    if (this.expireAfterAccessNanos != -1L) {
      s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
    }
    if (this.keyStrength != null) {
      s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
    }
    if (this.valueStrength != null) {
      s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
    }
    if (this.keyEquivalence != null) {
      s.addValue("keyEquivalence");
    }
    if (this.valueEquivalence != null) {
      s.addValue("valueEquivalence");
    }
    if (this.removalListener != null) {
      s.addValue("removalListener");
    }
    return s.toString();
  }
}
