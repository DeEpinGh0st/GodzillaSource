package net.miginfocom.layout;

import java.io.Serializable;






































public class AnimSpec
  implements Serializable
{
  public static final AnimSpec DEF = new AnimSpec(0, 0, 0.2F, 0.2F);

  
  private final int prio;

  
  private final int durMillis;

  
  private final float easeIn;
  
  private final float easeOut;

  
  public AnimSpec(int prio, int durMillis, float easeIn, float easeOut) {
    this.prio = prio;
    this.durMillis = durMillis;
    this.easeIn = LayoutUtil.clamp(easeIn, 0.0F, 1.0F);
    this.easeOut = LayoutUtil.clamp(easeOut, 0.0F, 1.0F);
  }





  
  public int getPriority() {
    return this.prio;
  }





  
  public int getDurationMillis(int defMillis) {
    return (this.durMillis > 0) ? this.durMillis : defMillis;
  }




  
  public int getDurationMillis() {
    return this.durMillis;
  }




  
  public float getEaseIn() {
    return this.easeIn;
  }




  
  public float getEaseOut() {
    return this.easeOut;
  }
}
