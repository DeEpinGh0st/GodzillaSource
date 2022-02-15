package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;






































@Beta
@GwtIncompatible
public final class PairedStats
  implements Serializable
{
  private final Stats xStats;
  private final Stats yStats;
  private final double sumOfProductsOfDeltas;
  private static final int BYTES = 88;
  private static final long serialVersionUID = 0L;
  
  PairedStats(Stats xStats, Stats yStats, double sumOfProductsOfDeltas) {
    this.xStats = xStats;
    this.yStats = yStats;
    this.sumOfProductsOfDeltas = sumOfProductsOfDeltas;
  }

  
  public long count() {
    return this.xStats.count();
  }

  
  public Stats xStats() {
    return this.xStats;
  }

  
  public Stats yStats() {
    return this.yStats;
  }














  
  public double populationCovariance() {
    Preconditions.checkState((count() != 0L));
    return this.sumOfProductsOfDeltas / count();
  }













  
  public double sampleCovariance() {
    Preconditions.checkState((count() > 1L));
    return this.sumOfProductsOfDeltas / (count() - 1L);
  }
















  
  public double pearsonsCorrelationCoefficient() {
    Preconditions.checkState((count() > 1L));
    if (Double.isNaN(this.sumOfProductsOfDeltas)) {
      return Double.NaN;
    }
    double xSumOfSquaresOfDeltas = xStats().sumOfSquaresOfDeltas();
    double ySumOfSquaresOfDeltas = yStats().sumOfSquaresOfDeltas();
    Preconditions.checkState((xSumOfSquaresOfDeltas > 0.0D));
    Preconditions.checkState((ySumOfSquaresOfDeltas > 0.0D));


    
    double productOfSumsOfSquaresOfDeltas = ensurePositive(xSumOfSquaresOfDeltas * ySumOfSquaresOfDeltas);
    return ensureInUnitRange(this.sumOfProductsOfDeltas / Math.sqrt(productOfSumsOfSquaresOfDeltas));
  }































  
  public LinearTransformation leastSquaresFit() {
    Preconditions.checkState((count() > 1L));
    if (Double.isNaN(this.sumOfProductsOfDeltas)) {
      return LinearTransformation.forNaN();
    }
    double xSumOfSquaresOfDeltas = this.xStats.sumOfSquaresOfDeltas();
    if (xSumOfSquaresOfDeltas > 0.0D) {
      if (this.yStats.sumOfSquaresOfDeltas() > 0.0D) {
        return LinearTransformation.mapping(this.xStats.mean(), this.yStats.mean())
          .withSlope(this.sumOfProductsOfDeltas / xSumOfSquaresOfDeltas);
      }
      return LinearTransformation.horizontal(this.yStats.mean());
    } 
    
    Preconditions.checkState((this.yStats.sumOfSquaresOfDeltas() > 0.0D));
    return LinearTransformation.vertical(this.xStats.mean());
  }

















  
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PairedStats other = (PairedStats)obj;
    return (this.xStats.equals(other.xStats) && this.yStats
      .equals(other.yStats) && 
      Double.doubleToLongBits(this.sumOfProductsOfDeltas) == Double.doubleToLongBits(other.sumOfProductsOfDeltas));
  }







  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.xStats, this.yStats, Double.valueOf(this.sumOfProductsOfDeltas) });
  }

  
  public String toString() {
    if (count() > 0L) {
      return MoreObjects.toStringHelper(this)
        .add("xStats", this.xStats)
        .add("yStats", this.yStats)
        .add("populationCovariance", populationCovariance())
        .toString();
    }
    return MoreObjects.toStringHelper(this)
      .add("xStats", this.xStats)
      .add("yStats", this.yStats)
      .toString();
  }

  
  double sumOfProductsOfDeltas() {
    return this.sumOfProductsOfDeltas;
  }
  
  private static double ensurePositive(double value) {
    if (value > 0.0D) {
      return value;
    }
    return Double.MIN_VALUE;
  }

  
  private static double ensureInUnitRange(double value) {
    if (value >= 1.0D) {
      return 1.0D;
    }
    if (value <= -1.0D) {
      return -1.0D;
    }
    return value;
  }











  
  public byte[] toByteArray() {
    ByteBuffer buffer = ByteBuffer.allocate(88).order(ByteOrder.LITTLE_ENDIAN);
    this.xStats.writeTo(buffer);
    this.yStats.writeTo(buffer);
    buffer.putDouble(this.sumOfProductsOfDeltas);
    return buffer.array();
  }







  
  public static PairedStats fromByteArray(byte[] byteArray) {
    Preconditions.checkNotNull(byteArray);
    Preconditions.checkArgument((byteArray.length == 88), "Expected PairedStats.BYTES = %s, got %s", 88, byteArray.length);



    
    ByteBuffer buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);
    Stats xStats = Stats.readFrom(buffer);
    Stats yStats = Stats.readFrom(buffer);
    double sumOfProductsOfDeltas = buffer.getDouble();
    return new PairedStats(xStats, yStats, sumOfProductsOfDeltas);
  }
}
