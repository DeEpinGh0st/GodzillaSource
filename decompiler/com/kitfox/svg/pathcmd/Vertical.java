package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class Vertical
  extends PathCommand
{
  public float y = 0.0F;


  
  public Vertical() {}


  
  public String toString() {
    return "V " + this.y;
  }
  
  public Vertical(boolean isRelative, float y) {
    super(isRelative);
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = hist.lastPoint.x;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    path.lineTo(offx, this.y + offy);
    hist.setLastPoint(offx, this.y + offy);
    hist.setLastKnot(offx, this.y + offy);
  }


  
  public int getNumKnotsAdded() {
    return 2;
  }
}
