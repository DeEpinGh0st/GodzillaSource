package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;









































public class MoveTo
  extends PathCommand
{
  public float x = 0.0F;
  public float y = 0.0F;

  
  public MoveTo() {}

  
  public MoveTo(boolean isRelative, float x, float y) {
    super(isRelative);
    this.x = x;
    this.y = y;
  }



  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    float offx = this.isRelative ? hist.lastPoint.x : 0.0F;
    float offy = this.isRelative ? hist.lastPoint.y : 0.0F;
    
    path.moveTo(this.x + offx, this.y + offy);
    hist.setStartPoint(this.x + offx, this.y + offy);
    hist.setLastPoint(this.x + offx, this.y + offy);
    hist.setLastKnot(this.x + offx, this.y + offy);
  }


  
  public int getNumKnotsAdded() {
    return 2;
  }


  
  public String toString() {
    return "M " + this.x + " " + this.y;
  }
}
