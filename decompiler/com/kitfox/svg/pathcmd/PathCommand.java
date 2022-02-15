package com.kitfox.svg.pathcmd;

import java.awt.geom.GeneralPath;















































public abstract class PathCommand
{
  public boolean isRelative = false;
  
  public PathCommand() {}
  
  public PathCommand(boolean isRelative) {
    this.isRelative = isRelative;
  }
  
  public abstract void appendPath(GeneralPath paramGeneralPath, BuildHistory paramBuildHistory);
  
  public abstract int getNumKnotsAdded();
}
