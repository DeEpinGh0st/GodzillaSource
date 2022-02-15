package com.kitfox.svg.animation;

import java.awt.geom.Point2D;










































public class Bezier
{
  double length;
  double[] coord;
  
  public Bezier(double sx, double sy, double[] coords, int numCoords) {
    setCoords(sx, sy, coords, numCoords);
  }

  
  public void setCoords(double sx, double sy, double[] coords, int numCoords) {
    this.coord = new double[numCoords * 2 + 2];
    this.coord[0] = sx;
    this.coord[1] = sy;
    for (int i = 0; i < numCoords; i++) {
      
      this.coord[i * 2 + 2] = coords[i * 2];
      this.coord[i * 2 + 3] = coords[i * 2 + 1];
    } 
    
    calcLength();
  }




  
  public double getLength() {
    return this.length;
  }

  
  private void calcLength() {
    this.length = 0.0D;
    for (int i = 2; i < this.coord.length; i += 2)
    {
      this.length += lineLength(this.coord[i - 2], this.coord[i - 1], this.coord[i], this.coord[i + 1]);
    }
  }

  
  private double lineLength(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1, dy = y2 - y1;
    return Math.sqrt(dx * dx + dy * dy);
  }

  
  public Point2D.Double getFinalPoint(Point2D.Double point) {
    point.x = this.coord[this.coord.length - 2];
    point.y = this.coord[this.coord.length - 1];
    return point;
  }

  
  public Point2D.Double eval(double param, Point2D.Double point) {
    point.x = 0.0D;
    point.y = 0.0D;
    int numKnots = this.coord.length / 2;
    
    for (int i = 0; i < numKnots; i++) {
      
      double scale = bernstein(numKnots - 1, i, param);
      point.x += this.coord[i * 2] * scale;
      point.y += this.coord[i * 2 + 1] * scale;
    } 
    
    return point;
  }







  
  private double bernstein(int numKnots, int knotNo, double param) {
    double iParam = 1.0D - param;
    
    switch (numKnots) {
      
      case 0:
        return 1.0D;
      
      case 1:
        switch (knotNo) {
          
          case 0:
            return iParam;
          case 1:
            return param;
        } 
        
        break;
      
      case 2:
        switch (knotNo) {
          
          case 0:
            return iParam * iParam;
          case 1:
            return 2.0D * iParam * param;
          case 2:
            return param * param;
        } 
        
        break;
      
      case 3:
        switch (knotNo) {
          
          case 0:
            return iParam * iParam * iParam;
          case 1:
            return 3.0D * iParam * iParam * param;
          case 2:
            return 3.0D * iParam * param * param;
          case 3:
            return param * param * param;
        } 

        
        break;
    } 
    
    double retVal = 1.0D; int i;
    for (i = 0; i < knotNo; i++)
    {
      retVal *= param;
    }
    for (i = 0; i < numKnots - knotNo; i++)
    {
      retVal *= iParam;
    }
    retVal *= choose(numKnots, knotNo);
    
    return retVal;
  }



  
  private int choose(int num, int denom) {
    int denom2 = num - denom;
    if (denom < denom2) {
      
      int tmp = denom;
      denom = denom2;
      denom2 = tmp;
    } 
    
    int prod = 1; int i;
    for (i = num; i > denom; i--)
    {
      prod *= num;
    }
    
    for (i = 2; i <= denom2; i++)
    {
      prod /= i;
    }
    
    return prod;
  }
}
