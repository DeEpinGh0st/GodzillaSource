package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;

















































public class Terminal
  extends PathCommand
{
  public String toString() {
    return "Z";
  }




  
  public void appendPath(GeneralPath path, BuildHistory hist) {
    path.closePath();
    hist.setLastPoint(hist.startPoint.x, hist.startPoint.y);
    hist.setLastKnot(hist.startPoint.x, hist.startPoint.y);
  }


  
  public int getNumKnotsAdded() {
    return 0;
  }
}
