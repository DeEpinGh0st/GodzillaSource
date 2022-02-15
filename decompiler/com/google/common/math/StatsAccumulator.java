package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import java.util.Iterator;





























@Beta
@GwtIncompatible
public final class StatsAccumulator
{
  private long count = 0L;
  private double mean = 0.0D;
  private double sumOfSquaresOfDeltas = 0.0D;
  private double min = Double.NaN;
  private double max = Double.NaN;

  
  public void add(double value) {
    if (this.count == 0L) {
      this.count = 1L;
      this.mean = value;
      this.min = value;
      this.max = value;
      if (!Doubles.isFinite(value)) {
        this.sumOfSquaresOfDeltas = Double.NaN;
      }
    } else {
      this.count++;
      if (Doubles.isFinite(value) && Doubles.isFinite(this.mean)) {
        
        double delta = value - this.mean;
        this.mean += delta / this.count;
        this.sumOfSquaresOfDeltas += delta * (value - this.mean);
      } else {
        this.mean = calculateNewMeanNonFinite(this.mean, value);
        this.sumOfSquaresOfDeltas = Double.NaN;
      } 
      this.min = Math.min(this.min, value);
      this.max = Math.max(this.max, value);
    } 
  }






  
  public void addAll(Iterable<? extends Number> values) {
    for (Number value : values) {
      add(value.doubleValue());
    }
  }






  
  public void addAll(Iterator<? extends Number> values) {
    while (values.hasNext()) {
      add(((Number)values.next()).doubleValue());
    }
  }





  
  public void addAll(double... values) {
    for (double value : values) {
      add(value);
    }
  }





  
  public void addAll(int... values) {
    for (int value : values) {
      add(value);
    }
  }






  
  public void addAll(long... values) {
    for (long value : values) {
      add(value);
    }
  }




  
  public void addAll(Stats values) {
    if (values.count() == 0L) {
      return;
    }
    
    if (this.count == 0L) {
      this.count = values.count();
      this.mean = values.mean();
      this.sumOfSquaresOfDeltas = values.sumOfSquaresOfDeltas();
      this.min = values.min();
      this.max = values.max();
    } else {
      this.count += values.count();
      if (Doubles.isFinite(this.mean) && Doubles.isFinite(values.mean())) {
        
        double delta = values.mean() - this.mean;
        this.mean += delta * values.count() / this.count;
        this.sumOfSquaresOfDeltas += values
          .sumOfSquaresOfDeltas() + delta * (values.mean() - this.mean) * values.count();
      } else {
        this.mean = calculateNewMeanNonFinite(this.mean, values.mean());
        this.sumOfSquaresOfDeltas = Double.NaN;
      } 
      this.min = Math.min(this.min, values.min());
      this.max = Math.max(this.max, values.max());
    } 
  }

  
  public Stats snapshot() {
    return new Stats(this.count, this.mean, this.sumOfSquaresOfDeltas, this.min, this.max);
  }

  
  public long count() {
    return this.count;
  }


















  
  public double mean() {
    Preconditions.checkState((this.count != 0L));
    return this.mean;
  }












  
  public final double sum() {
    return this.mean * this.count;
  }















  
  public final double populationVariance() {
    Preconditions.checkState((this.count != 0L));
    if (Double.isNaN(this.sumOfSquaresOfDeltas)) {
      return Double.NaN;
    }
    if (this.count == 1L) {
      return 0.0D;
    }
    return DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / this.count;
  }
















  
  public final double populationStandardDeviation() {
    return Math.sqrt(populationVariance());
  }
















  
  public final double sampleVariance() {
    Preconditions.checkState((this.count > 1L));
    if (Double.isNaN(this.sumOfSquaresOfDeltas)) {
      return Double.NaN;
    }
    return DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / (this.count - 1L);
  }


















  
  public final double sampleStandardDeviation() {
    return Math.sqrt(sampleVariance());
  }













  
  public double min() {
    Preconditions.checkState((this.count != 0L));
    return this.min;
  }













  
  public double max() {
    Preconditions.checkState((this.count != 0L));
    return this.max;
  }
  
  double sumOfSquaresOfDeltas() {
    return this.sumOfSquaresOfDeltas;
  }


















  
  static double calculateNewMeanNonFinite(double previousMean, double value) {
    if (Doubles.isFinite(previousMean))
    {
      return value; } 
    if (Doubles.isFinite(value) || previousMean == value)
    {
      return previousMean;
    }
    
    return Double.NaN;
  }
}
