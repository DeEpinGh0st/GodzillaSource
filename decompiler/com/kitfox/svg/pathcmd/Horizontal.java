package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class Horizontal
  extends PathCommand
{
  public float x = 0.0F;


  
  public Horizontal() {}


  
  public String toString() {
    return "H " + this.x;
  }
  
  public Horizontal(boolean isRelative, float x) {
    super(isRelative);
    this.x = x;
  }




  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = hist.lastPoint.y;
    
    path.lineTo(this.x + offx, offy);
    hist.setLastPoint(this.x + offx, offy);
    hist.setLastKnot(this.x + offx, offy);
  }


  
  public int getNumKnotsAdded() {
    return 2;
  }
}
