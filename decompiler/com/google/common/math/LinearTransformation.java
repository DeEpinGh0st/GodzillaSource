package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;


































@Beta
@GwtIncompatible
public abstract class LinearTransformation
{
  public static LinearTransformationBuilder mapping(double x1, double y1) {
    Preconditions.checkArgument((DoubleUtils.isFinite(x1) && DoubleUtils.isFinite(y1)));
    return new LinearTransformationBuilder(x1, y1);
  }



  
  public static final class LinearTransformationBuilder
  {
    private final double x1;


    
    private final double y1;


    
    private LinearTransformationBuilder(double x1, double y1) {
      this.x1 = x1;
      this.y1 = y1;
    }






    
    public LinearTransformation and(double x2, double y2) {
      Preconditions.checkArgument((DoubleUtils.isFinite(x2) && DoubleUtils.isFinite(y2)));
      if (x2 == this.x1) {
        Preconditions.checkArgument((y2 != this.y1));
        return new LinearTransformation.VerticalLinearTransformation(this.x1);
      } 
      return withSlope((y2 - this.y1) / (x2 - this.x1));
    }






    
    public LinearTransformation withSlope(double slope) {
      Preconditions.checkArgument(!Double.isNaN(slope));
      if (DoubleUtils.isFinite(slope)) {
        double yIntercept = this.y1 - this.x1 * slope;
        return new LinearTransformation.RegularLinearTransformation(slope, yIntercept);
      } 
      return new LinearTransformation.VerticalLinearTransformation(this.x1);
    }
  }





  
  public static LinearTransformation vertical(double x) {
    Preconditions.checkArgument(DoubleUtils.isFinite(x));
    return new VerticalLinearTransformation(x);
  }




  
  public static LinearTransformation horizontal(double y) {
    Preconditions.checkArgument(DoubleUtils.isFinite(y));
    double slope = 0.0D;
    return new RegularLinearTransformation(slope, y);
  }






  
  public static LinearTransformation forNaN() {
    return NaNLinearTransformation.INSTANCE;
  }



  
  public abstract boolean isVertical();


  
  public abstract boolean isHorizontal();


  
  public abstract double slope();


  
  public abstract double transform(double paramDouble);


  
  public abstract LinearTransformation inverse();


  
  private static final class RegularLinearTransformation
    extends LinearTransformation
  {
    final double slope;

    
    final double yIntercept;

    
    @LazyInit
    LinearTransformation inverse;


    
    RegularLinearTransformation(double slope, double yIntercept) {
      this.slope = slope;
      this.yIntercept = yIntercept;
      this.inverse = null;
    }
    
    RegularLinearTransformation(double slope, double yIntercept, LinearTransformation inverse) {
      this.slope = slope;
      this.yIntercept = yIntercept;
      this.inverse = inverse;
    }

    
    public boolean isVertical() {
      return false;
    }

    
    public boolean isHorizontal() {
      return (this.slope == 0.0D);
    }

    
    public double slope() {
      return this.slope;
    }

    
    public double transform(double x) {
      return x * this.slope + this.yIntercept;
    }

    
    public LinearTransformation inverse() {
      LinearTransformation result = this.inverse;
      return (result == null) ? (this.inverse = createInverse()) : result;
    }

    
    public String toString() {
      return String.format("y = %g * x + %g", new Object[] { Double.valueOf(this.slope), Double.valueOf(this.yIntercept) });
    }
    
    private LinearTransformation createInverse() {
      if (this.slope != 0.0D) {
        return new RegularLinearTransformation(1.0D / this.slope, -1.0D * this.yIntercept / this.slope, this);
      }
      return new LinearTransformation.VerticalLinearTransformation(this.yIntercept, this);
    }
  }
  
  private static final class VerticalLinearTransformation
    extends LinearTransformation
  {
    final double x;
    @LazyInit
    LinearTransformation inverse;
    
    VerticalLinearTransformation(double x) {
      this.x = x;
      this.inverse = null;
    }
    
    VerticalLinearTransformation(double x, LinearTransformation inverse) {
      this.x = x;
      this.inverse = inverse;
    }

    
    public boolean isVertical() {
      return true;
    }

    
    public boolean isHorizontal() {
      return false;
    }

    
    public double slope() {
      throw new IllegalStateException();
    }

    
    public double transform(double x) {
      throw new IllegalStateException();
    }

    
    public LinearTransformation inverse() {
      LinearTransformation result = this.inverse;
      return (result == null) ? (this.inverse = createInverse()) : result;
    }

    
    public String toString() {
      return String.format("x = %g", new Object[] { Double.valueOf(this.x) });
    }
    
    private LinearTransformation createInverse() {
      return new LinearTransformation.RegularLinearTransformation(0.0D, this.x, this);
    }
  }
  
  private static final class NaNLinearTransformation
    extends LinearTransformation {
    static final NaNLinearTransformation INSTANCE = new NaNLinearTransformation();

    
    public boolean isVertical() {
      return false;
    }

    
    public boolean isHorizontal() {
      return false;
    }

    
    public double slope() {
      return Double.NaN;
    }

    
    public double transform(double x) {
      return Double.NaN;
    }

    
    public LinearTransformation inverse() {
      return this;
    }

    
    public String toString() {
      return "NaN";
    }
  }
}
