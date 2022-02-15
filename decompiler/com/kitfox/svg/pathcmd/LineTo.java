package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class LineTo
  extends PathCommand
{
  public float x = 0.0F;
  public float y = 0.0F;

  
  public LineTo() {}

  
  public LineTo(boolean isRelative, float x, float y) {
    super(isRelative);
    this.x = x;
    this.y = y;
  }




  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    path.lineTo(this.x + offx, this.y + offy);
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(this.x + offx, this.y + offy);
  }


  
  public int getNumKnotsAdded() {
    return 2;
  }


  
  public String toString() {
    return "L " + this.x + " " + this.y;
  }
}
