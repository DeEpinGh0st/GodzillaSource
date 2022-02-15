package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;



























@Beta
@GwtIncompatible
public final class PairedStatsAccumulator
{
  private final StatsAccumulator xStats = new StatsAccumulator();
  private final StatsAccumulator yStats = new StatsAccumulator();
  private double sumOfProductsOfDeltas = 0.0D;












  
  public void add(double x, double y) {
    this.xStats.add(x);
    if (Doubles.isFinite(x) && Doubles.isFinite(y)) {
      if (this.xStats.count() > 1L) {
        this.sumOfProductsOfDeltas += (x - this.xStats.mean()) * (y - this.yStats.mean());
      }
    } else {
      this.sumOfProductsOfDeltas = Double.NaN;
    } 
    this.yStats.add(y);
  }




  
  public void addAll(PairedStats values) {
    if (values.count() == 0L) {
      return;
    }
    
    this.xStats.addAll(values.xStats());
    if (this.yStats.count() == 0L) {
      this.sumOfProductsOfDeltas = values.sumOfProductsOfDeltas();
    
    }
    else {
      
      this.sumOfProductsOfDeltas += values
        .sumOfProductsOfDeltas() + (values
        .xStats().mean() - this.xStats.mean()) * (values
        .yStats().mean() - this.yStats.mean()) * values
        .count();
    } 
    this.yStats.addAll(values.yStats());
  }

  
  public PairedStats snapshot() {
    return new PairedStats(this.xStats.snapshot(), this.yStats.snapshot(), this.sumOfProductsOfDeltas);
  }

  
  public long count() {
    return this.xStats.count();
  }

  
  public Stats xStats() {
    return this.xStats.snapshot();
  }

  
  public Stats yStats() {
    return this.yStats.snapshot();
  }














  
  public double populationCovariance() {
    Preconditions.checkState((count() != 0L));
    return this.sumOfProductsOfDeltas / count();
  }













  
  public final double sampleCovariance() {
    Preconditions.checkState((count() > 1L));
    return this.sumOfProductsOfDeltas / (count() - 1L);
  }
















  
  public final double pearsonsCorrelationCoefficient() {
    Preconditions.checkState((count() > 1L));
    if (Double.isNaN(this.sumOfProductsOfDeltas)) {
      return Double.NaN;
    }
    double xSumOfSquaresOfDeltas = this.xStats.sumOfSquaresOfDeltas();
    double ySumOfSquaresOfDeltas = this.yStats.sumOfSquaresOfDeltas();
    Preconditions.checkState((xSumOfSquaresOfDeltas > 0.0D));
    Preconditions.checkState((ySumOfSquaresOfDeltas > 0.0D));


    
    double productOfSumsOfSquaresOfDeltas = ensurePositive(xSumOfSquaresOfDeltas * ySumOfSquaresOfDeltas);
    return ensureInUnitRange(this.sumOfProductsOfDeltas / Math.sqrt(productOfSumsOfSquaresOfDeltas));
  }































  
  public final LinearTransformation leastSquaresFit() {
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

  
  private double ensurePositive(double value) {
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
}
