package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class Quadratic
  extends PathCommand
{
  public float kx = 0.0F;
  public float ky = 0.0F;
  public float x = 0.0F;
  public float y = 0.0F;


  
  public Quadratic() {}


  
  public String toString() {
    return "Q " + this.kx + " " + this.ky + " " + this.x + " " + this.y;
  }

  
  public Quadratic(boolean isRelative, float kx, float ky, float x, float y) {
    super(isRelative);
    this.kx = kx;
    this.ky = ky;
    this.x = x;
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    path.quadTo(this.kx + offx, this.ky + offy, this.x + offx, this.y + offy);
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(this.kx + offx, this.ky + offy);
  }


  
  public int getNumKnotsAdded() {
    return 4;
  }
}
