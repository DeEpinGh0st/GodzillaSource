package com.kitfox.svg.animation;



























































public class TimeLookup
  extends TimeBase
{
  private AnimationElement parent;
  String node;
  String event;
  String paramList;
  
  public TimeLookup(AnimationElement parent, String node, String event, String paramList) {
    this.parent = parent;
    this.node = node;
    this.event = event;
    this.paramList = paramList;
  }


  
  public double evalTime() {
    return 0.0D;
  }


  
  public void setParentElement(AnimationElement ele) {
    this.parent = ele;
  }
}
