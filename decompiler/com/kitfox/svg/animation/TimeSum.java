package com.kitfox.svg.animation;













































public class TimeSum
  extends TimeBase
{
  TimeBase t1;
  TimeBase t2;
  boolean add;
  
  public TimeSum(TimeBase t1, TimeBase t2, boolean add) {
    this.t1 = t1;
    this.t2 = t2;
    this.add = add;
  }


  
  public double evalTime() {
    return this.add ? (this.t1.evalTime() + this.t2.evalTime()) : (this.t1.evalTime() - this.t2.evalTime());
  }


  
  public void setParentElement(AnimationElement ele) {
    this.t1.setParentElement(ele);
    this.t2.setParentElement(ele);
  }
}
