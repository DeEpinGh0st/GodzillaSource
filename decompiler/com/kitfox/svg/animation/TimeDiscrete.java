package com.kitfox.svg.animation;













































public class TimeDiscrete
  extends TimeBase
{
  double secs;
  
  public TimeDiscrete(double secs) {
    this.secs = secs;
  }


  
  public double evalTime() {
    return this.secs;
  }
}
