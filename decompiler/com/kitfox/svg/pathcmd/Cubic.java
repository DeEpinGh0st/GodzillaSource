package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class Cubic
  extends PathCommand
{
  public float k1x = 0.0F;
  public float k1y = 0.0F;
  public float k2x = 0.0F;
  public float k2y = 0.0F;
  public float x = 0.0F;
  public float y = 0.0F;


  
  public Cubic() {}


  
  public String toString() {
    return "C " + this.k1x + " " + this.k1y + " " + this.k2x + " " + this.k2y + " " + this.x + " " + this.y;
  }


  
  public Cubic(boolean isRelative, float k1x, float k1y, float k2x, float k2y, float x, float y) {
    super(isRelative);
    this.k1x = k1x;
    this.k1y = k1y;
    this.k2x = k2x;
    this.k2y = k2y;
    this.x = x;
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    path.curveTo(this.k1x + offx, this.k1y + offy, this.k2x + offx, this.k2y + offy, this.x + offx, this.y + offy);


    
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(this.k2x + offx, this.k2y + offy);
  }


  
  public int getNumKnotsAdded() {
    return 6;
  }
}
