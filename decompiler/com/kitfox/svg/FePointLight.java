package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;








































public class FePointLight
  extends FeLight
{
  public static final String TAG_NAME = "fepointlight";
  float x = 0.0F;
  float y = 0.0F;
  float z = 0.0F;









  
  public String getTagName() {
    return "fepointlight";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("x")))
    {
      this.x = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("y")))
    {
      this.y = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("z")))
    {
      this.z = sty.getFloatValueWithUnits();
    }
  }


  
  public float getX() {
    return this.x;
  }


  
  public float getY() {
    return this.y;
  }

  
  public float getZ() {
    return this.z;
  }





  
  public boolean updateTime(double curTime) throws SVGException {
    StyleAttribute sty = new StyleAttribute();
    boolean stateChange = false;
    
    if (getPres(sty.setName("x"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.x) {
        
        this.x = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("y"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.y) {
        
        this.y = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("z"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.z) {
        
        this.z = newVal;
        stateChange = true;
      } 
    } 
    
    return stateChange;
  }
}
