package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;






































@GwtCompatible
public abstract class DiscreteDomain<C extends Comparable>
{
  final boolean supportsFastOffset;
  
  public static DiscreteDomain<Integer> integers() {
    return IntegerDomain.INSTANCE;
  }
  
  private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
    private static final IntegerDomain INSTANCE = new IntegerDomain();
    
    IntegerDomain() {
      super(true);
    }
    private static final long serialVersionUID = 0L;
    
    public Integer next(Integer value) {
      int i = value.intValue();
      return (i == Integer.MAX_VALUE) ? null : Integer.valueOf(i + 1);
    }

    
    public Integer previous(Integer value) {
      int i = value.intValue();
      return (i == Integer.MIN_VALUE) ? null : Integer.valueOf(i - 1);
    }

    
    Integer offset(Integer origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      return Integer.valueOf(Ints.checkedCast(origin.longValue() + distance));
    }

    
    public long distance(Integer start, Integer end) {
      return end.intValue() - start.intValue();
    }

    
    public Integer minValue() {
      return Integer.valueOf(-2147483648);
    }

    
    public Integer maxValue() {
      return Integer.valueOf(2147483647);
    }
    
    private Object readResolve() {
      return INSTANCE;
    }

    
    public String toString() {
      return "DiscreteDomain.integers()";
    }
  }







  
  public static DiscreteDomain<Long> longs() {
    return LongDomain.INSTANCE;
  }
  
  private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
    private static final LongDomain INSTANCE = new LongDomain(); private static final long serialVersionUID = 0L;
    
    LongDomain() {
      super(true);
    }

    
    public Long next(Long value) {
      long l = value.longValue();
      return (l == Long.MAX_VALUE) ? null : Long.valueOf(l + 1L);
    }

    
    public Long previous(Long value) {
      long l = value.longValue();
      return (l == Long.MIN_VALUE) ? null : Long.valueOf(l - 1L);
    }

    
    Long offset(Long origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      long result = origin.longValue() + distance;
      if (result < 0L) {
        Preconditions.checkArgument((origin.longValue() < 0L), "overflow");
      }
      return Long.valueOf(result);
    }

    
    public long distance(Long start, Long end) {
      long result = end.longValue() - start.longValue();
      if (end.longValue() > start.longValue() && result < 0L) {
        return Long.MAX_VALUE;
      }
      if (end.longValue() < start.longValue() && result > 0L) {
        return Long.MIN_VALUE;
      }
      return result;
    }

    
    public Long minValue() {
      return Long.valueOf(Long.MIN_VALUE);
    }

    
    public Long maxValue() {
      return Long.valueOf(Long.MAX_VALUE);
    }
    
    private Object readResolve() {
      return INSTANCE;
    }

    
    public String toString() {
      return "DiscreteDomain.longs()";
    }
  }







  
  public static DiscreteDomain<BigInteger> bigIntegers() {
    return BigIntegerDomain.INSTANCE;
  }
  
  private static final class BigIntegerDomain
    extends DiscreteDomain<BigInteger> implements Serializable {
    private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
    
    BigIntegerDomain() {
      super(true);
    }
    
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final long serialVersionUID = 0L;
    
    public BigInteger next(BigInteger value) {
      return value.add(BigInteger.ONE);
    }

    
    public BigInteger previous(BigInteger value) {
      return value.subtract(BigInteger.ONE);
    }

    
    BigInteger offset(BigInteger origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      return origin.add(BigInteger.valueOf(distance));
    }

    
    public long distance(BigInteger start, BigInteger end) {
      return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
    }
    
    private Object readResolve() {
      return INSTANCE;
    }

    
    public String toString() {
      return "DiscreteDomain.bigIntegers()";
    }
  }





  
  protected DiscreteDomain() {
    this(false);
  }

  
  private DiscreteDomain(boolean supportsFastOffset) {
    this.supportsFastOffset = supportsFastOffset;
  }




  
  C offset(C origin, long distance) {
    CollectPreconditions.checkNonnegative(distance, "distance"); long i;
    for (i = 0L; i < distance; i++) {
      origin = next(origin);
    }
    return origin;
  }












































  
  @CanIgnoreReturnValue
  public C minValue() {
    throw new NoSuchElementException();
  }











  
  @CanIgnoreReturnValue
  public C maxValue() {
    throw new NoSuchElementException();
  }
  
  public abstract C next(C paramC);
  
  public abstract C previous(C paramC);
  
  public abstract long distance(C paramC1, C paramC2);
}
