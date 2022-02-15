package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class QuadraticSmooth
  extends PathCommand
{
  public float x = 0.0F;
  public float y = 0.0F;


  
  public QuadraticSmooth() {}


  
  public String toString() {
    return "T " + this.x + " " + this.y;
  }
  
  public QuadraticSmooth(boolean isRelative, float x, float y) {
    super(isRelative);
    this.x = x;
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    float oldKx = hist.lastKnot.x;
    float oldKy = hist.lastKnot.y;
    float oldX = hist.lastPoint.x;
    float oldY = hist.lastPoint.y;
    
    float kx = oldX * 2.0F - oldKx;
    float ky = oldY * 2.0F - oldKy;
    
    path.quadTo(kx, ky, this.x + offx, this.y + offy);
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(kx, ky);
  }


  
  public int getNumKnotsAdded() {
    return 4;
  }
}
