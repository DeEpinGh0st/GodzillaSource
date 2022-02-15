package com.kitfox.svg.pathcmd;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;













































public class Arc
  extends PathCommand
{
  public float rx = 0.0F;
  public float ry = 0.0F;
  public float xAxisRot = 0.0F;
  public boolean largeArc = false;
  public boolean sweep = false;
  public float x = 0.0F;
  public float y = 0.0F;

  
  public Arc() {}

  
  public Arc(boolean isRelative, float rx, float ry, float xAxisRot, boolean largeArc, boolean sweep, float x, float y) {
    super(isRelative);
    this.rx = rx;
    this.ry = ry;
    this.xAxisRot = xAxisRot;
    this.largeArc = largeArc;
    this.sweep = sweep;
    this.x = x;
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    arcTo(path, this.rx, this.ry, this.xAxisRot, this.largeArc, this.sweep, this.x + offx, this.y + offy, hist.lastPoint.x, hist.lastPoint.y);



    
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(this.x + offx, this.y + offy);
  }


  
  public int getNumKnotsAdded() {
    return 6;
  }


































  
  public void arcTo(GeneralPath path, float rx, float ry, float angle, boolean largeArcFlag, boolean sweepFlag, float x, float y, float x0, float y0) {
    if (rx == 0.0F || ry == 0.0F) {
      path.lineTo(x, y);
      
      return;
    } 
    if (x0 == x && y0 == y) {
      return;
    }


    
    Arc2D arc = computeArc(x0, y0, rx, ry, angle, largeArcFlag, sweepFlag, x, y);
    
    if (arc == null) {
      return;
    }
    AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(angle), arc.getCenterX(), arc.getCenterY());
    Shape s = t.createTransformedShape(arc);
    path.append(s, true);
  }































  
  public static Arc2D computeArc(double x0, double y0, double rx, double ry, double angle, boolean largeArcFlag, boolean sweepFlag, double x, double y) {
    double dx2 = (x0 - x) / 2.0D;
    double dy2 = (y0 - y) / 2.0D;
    
    angle = Math.toRadians(angle % 360.0D);
    double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);



    
    double x1 = cosAngle * dx2 + sinAngle * dy2;
    double y1 = -sinAngle * dx2 + cosAngle * dy2;
    
    rx = Math.abs(rx);
    ry = Math.abs(ry);
    double Prx = rx * rx;
    double Pry = ry * ry;
    double Px1 = x1 * x1;
    double Py1 = y1 * y1;
    
    double radiiCheck = Px1 / Prx + Py1 / Pry;
    if (radiiCheck > 1.0D) {
      rx = Math.sqrt(radiiCheck) * rx;
      ry = Math.sqrt(radiiCheck) * ry;
      Prx = rx * rx;
      Pry = ry * ry;
    } 



    
    double sign = (largeArcFlag == sweepFlag) ? -1.0D : 1.0D;
    double sq = (Prx * Pry - Prx * Py1 - Pry * Px1) / (Prx * Py1 + Pry * Px1);
    sq = (sq < 0.0D) ? 0.0D : sq;
    double coef = sign * Math.sqrt(sq);
    double cx1 = coef * rx * y1 / ry;
    double cy1 = coef * -(ry * x1 / rx);



    
    double sx2 = (x0 + x) / 2.0D;
    double sy2 = (y0 + y) / 2.0D;
    double cx = sx2 + cosAngle * cx1 - sinAngle * cy1;
    double cy = sy2 + sinAngle * cx1 + cosAngle * cy1;



    
    double ux = (x1 - cx1) / rx;
    double uy = (y1 - cy1) / ry;
    double vx = (-x1 - cx1) / rx;
    double vy = (-y1 - cy1) / ry;

    
    double n = Math.sqrt(ux * ux + uy * uy);
    double p = ux;
    sign = (uy < 0.0D) ? -1.0D : 1.0D;
    double angleStart = Math.toDegrees(sign * Math.acos(p / n));

    
    n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
    p = ux * vx + uy * vy;
    sign = (ux * vy - uy * vx < 0.0D) ? -1.0D : 1.0D;
    double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
    if (!sweepFlag && angleExtent > 0.0D) {
      angleExtent -= 360.0D;
    } else if (sweepFlag && angleExtent < 0.0D) {
      angleExtent += 360.0D;
    } 
    angleExtent %= 360.0D;
    angleStart %= 360.0D;



    
    Arc2D.Double arc = new Arc2D.Double();
    arc.x = cx - rx;
    arc.y = cy - ry;
    arc.width = rx * 2.0D;
    arc.height = ry * 2.0D;
    arc.start = -angleStart;
    arc.extent = -angleExtent;
    
    return arc;
  }


  
  public String toString() {
    return "A " + this.rx + " " + this.ry + " " + this.xAxisRot + " " + this.largeArc + " " + this.sweep + " " + this.x + " " + this.y;
  }
}
