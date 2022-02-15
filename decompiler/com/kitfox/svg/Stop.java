package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;








































public class Stop
  extends SVGElement
{
  public static final String TAG_NAME = "stop";
  float offset = 0.0F;
  float opacity = 1.0F;
  Color color = Color.black;









  
  public String getTagName() {
    return "stop";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("offset"))) {
      
      this.offset = sty.getFloatValue();
      String units = sty.getUnits();
      if (units != null && units.equals("%"))
      {
        this.offset /= 100.0F;
      }
      if (this.offset > 1.0F)
      {
        this.offset = 1.0F;
      }
      if (this.offset < 0.0F)
      {
        this.offset = 0.0F;
      }
    } 
    
    if (getStyle(sty.setName("stop-color")))
    {
      this.color = sty.getColorValue();
    }
    
    if (getStyle(sty.setName("stop-opacity")))
    {
      this.opacity = sty.getRatioValue();
    }
  }












  
  public boolean updateTime(double curTime) throws SVGException {
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getPres(sty.setName("offset"))) {
      
      float newVal = sty.getFloatValue();
      if (newVal != this.offset) {
        
        this.offset = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getStyle(sty.setName("stop-color"))) {
      
      Color newVal = sty.getColorValue();
      if (newVal != this.color) {
        
        this.color = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getStyle(sty.setName("stop-opacity"))) {
      
      float newVal = sty.getFloatValue();
      if (newVal != this.opacity) {
        
        this.opacity = newVal;
        shapeChange = true;
      } 
    } 
    
    return shapeChange;
  }
}
