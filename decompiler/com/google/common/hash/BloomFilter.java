package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.math.DoubleMath;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.stream.Collector;





















































































@Beta
public final class BloomFilter<T>
  implements Predicate<T>, Serializable
{
  private final BloomFilterStrategies.LockFreeBitArray bits;
  private final int numHashFunctions;
  private final Funnel<? super T> funnel;
  private final Strategy strategy;
  
  private BloomFilter(BloomFilterStrategies.LockFreeBitArray bits, int numHashFunctions, Funnel<? super T> funnel, Strategy strategy) {
    Preconditions.checkArgument((numHashFunctions > 0), "numHashFunctions (%s) must be > 0", numHashFunctions);
    Preconditions.checkArgument((numHashFunctions <= 255), "numHashFunctions (%s) must be <= 255", numHashFunctions);
    
    this.bits = (BloomFilterStrategies.LockFreeBitArray)Preconditions.checkNotNull(bits);
    this.numHashFunctions = numHashFunctions;
    this.funnel = (Funnel<? super T>)Preconditions.checkNotNull(funnel);
    this.strategy = (Strategy)Preconditions.checkNotNull(strategy);
  }






  
  public BloomFilter<T> copy() {
    return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
  }




  
  public boolean mightContain(T object) {
    return this.strategy.mightContain(object, this.funnel, this.numHashFunctions, this.bits);
  }





  
  @Deprecated
  public boolean apply(T input) {
    return mightContain(input);
  }











  
  @CanIgnoreReturnValue
  public boolean put(T object) {
    return this.strategy.put(object, this.funnel, this.numHashFunctions, this.bits);
  }












  
  public double expectedFpp() {
    return Math.pow(this.bits.bitCount() / bitSize(), this.numHashFunctions);
  }







  
  public long approximateElementCount() {
    long bitSize = this.bits.bitSize();
    long bitCount = this.bits.bitCount();






    
    double fractionOfBitsSet = bitCount / bitSize;
    return DoubleMath.roundToLong(
        -Math.log1p(-fractionOfBitsSet) * bitSize / this.numHashFunctions, RoundingMode.HALF_UP);
  }

  
  @VisibleForTesting
  long bitSize() {
    return this.bits.bitSize();
  }















  
  public boolean isCompatible(BloomFilter<T> that) {
    Preconditions.checkNotNull(that);
    return (this != that && this.numHashFunctions == that.numHashFunctions && 
      
      bitSize() == that.bitSize() && this.strategy
      .equals(that.strategy) && this.funnel
      .equals(that.funnel));
  }









  
  public void putAll(BloomFilter<T> that) {
    Preconditions.checkNotNull(that);
    Preconditions.checkArgument((this != that), "Cannot combine a BloomFilter with itself.");
    Preconditions.checkArgument((this.numHashFunctions == that.numHashFunctions), "BloomFilters must have the same number of hash functions (%s != %s)", this.numHashFunctions, that.numHashFunctions);



    
    Preconditions.checkArgument(
        (bitSize() == that.bitSize()), "BloomFilters must have the same size underlying bit arrays (%s != %s)", 
        
        bitSize(), that
        .bitSize());
    Preconditions.checkArgument(this.strategy
        .equals(that.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, that.strategy);


    
    Preconditions.checkArgument(this.funnel
        .equals(that.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, that.funnel);


    
    this.bits.putAll(that.bits);
  }

  
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof BloomFilter) {
      BloomFilter<?> that = (BloomFilter)object;
      return (this.numHashFunctions == that.numHashFunctions && this.funnel
        .equals(that.funnel) && this.bits
        .equals(that.bits) && this.strategy
        .equals(that.strategy));
    } 
    return false;
  }

  
  public int hashCode() {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.numHashFunctions), this.funnel, this.strategy, this.bits });
  }






















  
  public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> funnel, long expectedInsertions) {
    return toBloomFilter(funnel, expectedInsertions, 0.03D);
  }























  
  public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> funnel, long expectedInsertions, double fpp) {
    Preconditions.checkNotNull(funnel);
    Preconditions.checkArgument((expectedInsertions >= 0L), "Expected insertions (%s) must be >= 0", expectedInsertions);
    
    Preconditions.checkArgument((fpp > 0.0D), "False positive probability (%s) must be > 0.0", Double.valueOf(fpp));
    Preconditions.checkArgument((fpp < 1.0D), "False positive probability (%s) must be < 1.0", Double.valueOf(fpp));
    return (Collector)Collector.of(() -> create(funnel, expectedInsertions, fpp), BloomFilter::put, (bf1, bf2) -> { bf1.putAll(bf2); return bf1; }new Collector.Characteristics[] { Collector.Characteristics.UNORDERED, Collector.Characteristics.CONCURRENT });
  }





























  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions, double fpp) {
    return create(funnel, expectedInsertions, fpp);
  }






















  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp) {
    return create(funnel, expectedInsertions, fpp, BloomFilterStrategies.MURMUR128_MITZ_64);
  }

  
  @VisibleForTesting
  static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp, Strategy strategy) {
    Preconditions.checkNotNull(funnel);
    Preconditions.checkArgument((expectedInsertions >= 0L), "Expected insertions (%s) must be >= 0", expectedInsertions);
    
    Preconditions.checkArgument((fpp > 0.0D), "False positive probability (%s) must be > 0.0", Double.valueOf(fpp));
    Preconditions.checkArgument((fpp < 1.0D), "False positive probability (%s) must be < 1.0", Double.valueOf(fpp));
    Preconditions.checkNotNull(strategy);
    
    if (expectedInsertions == 0L) {
      expectedInsertions = 1L;
    }




    
    long numBits = optimalNumOfBits(expectedInsertions, fpp);
    int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
    try {
      return new BloomFilter<>(new BloomFilterStrategies.LockFreeBitArray(numBits), numHashFunctions, funnel, strategy);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Could not create BloomFilter of " + numBits + " bits", e);
    } 
  }



















  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions) {
    return create(funnel, expectedInsertions);
  }




















  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions) {
    return create(funnel, expectedInsertions, 0.03D);
  }






















  
  @VisibleForTesting
  static int optimalNumOfHashFunctions(long n, long m) {
    return Math.max(1, (int)Math.round(m / n * Math.log(2.0D)));
  }










  
  @VisibleForTesting
  static long optimalNumOfBits(long n, double p) {
    if (p == 0.0D) {
      p = Double.MIN_VALUE;
    }
    return (long)(-n * Math.log(p) / Math.log(2.0D) * Math.log(2.0D));
  }
  
  private Object writeReplace() {
    return new SerialForm<>(this);
  }
  
  private static class SerialForm<T> implements Serializable { final long[] data;
    final int numHashFunctions;
    final Funnel<? super T> funnel;
    final BloomFilter.Strategy strategy;
    private static final long serialVersionUID = 1L;
    
    SerialForm(BloomFilter<T> bf) {
      this.data = BloomFilterStrategies.LockFreeBitArray.toPlainArray(bf.bits.data);
      this.numHashFunctions = bf.numHashFunctions;
      this.funnel = bf.funnel;
      this.strategy = bf.strategy;
    }
    
    Object readResolve() {
      return new BloomFilter(new BloomFilterStrategies.LockFreeBitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
    } }















  
  public void writeTo(OutputStream out) throws IOException {
    DataOutputStream dout = new DataOutputStream(out);
    dout.writeByte(SignedBytes.checkedCast(this.strategy.ordinal()));
    dout.writeByte(UnsignedBytes.checkedCast(this.numHashFunctions));
    dout.writeInt(this.bits.data.length());
    for (int i = 0; i < this.bits.data.length(); i++) {
      dout.writeLong(this.bits.data.get(i));
    }
  }












  
  public static <T> BloomFilter<T> readFrom(InputStream in, Funnel<? super T> funnel) throws IOException {
    Preconditions.checkNotNull(in, "InputStream");
    Preconditions.checkNotNull(funnel, "Funnel");
    int strategyOrdinal = -1;
    int numHashFunctions = -1;
    int dataLength = -1;
    try {
      DataInputStream din = new DataInputStream(in);


      
      strategyOrdinal = din.readByte();
      numHashFunctions = UnsignedBytes.toInt(din.readByte());
      dataLength = din.readInt();
      
      Strategy strategy = BloomFilterStrategies.values()[strategyOrdinal];
      long[] data = new long[dataLength];
      for (int i = 0; i < data.length; i++) {
        data[i] = din.readLong();
      }
      return new BloomFilter<>(new BloomFilterStrategies.LockFreeBitArray(data), numHashFunctions, funnel, strategy);
    } catch (RuntimeException e) {
      String message = "Unable to deserialize BloomFilter from InputStream. strategyOrdinal: " + strategyOrdinal + " numHashFunctions: " + numHashFunctions + " dataLength: " + dataLength;






      
      throw new IOException(message, e);
    } 
  }
  
  static interface Strategy extends Serializable {
    <T> boolean put(T param1T, Funnel<? super T> param1Funnel, int param1Int, BloomFilterStrategies.LockFreeBitArray param1LockFreeBitArray);
    
    <T> boolean mightContain(T param1T, Funnel<? super T> param1Funnel, int param1Int, BloomFilterStrategies.LockFreeBitArray param1LockFreeBitArray);
    
    int ordinal();
  }
}
