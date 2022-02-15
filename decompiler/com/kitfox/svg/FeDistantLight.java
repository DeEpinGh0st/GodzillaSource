package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;








































public class FeDistantLight
  extends FeLight
{
  public static final String TAG_NAME = "fedistantlight";
  float azimuth = 0.0F;
  float elevation = 0.0F;









  
  public String getTagName() {
    return "fedistantlight";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("azimuth")))
    {
      this.azimuth = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("elevation")))
    {
      this.elevation = sty.getFloatValueWithUnits();
    }
  }

  
  public float getAzimuth() {
    return this.azimuth;
  }

  
  public float getElevation() {
    return this.elevation;
  }





  
  public boolean updateTime(double curTime) throws SVGException {
    StyleAttribute sty = new StyleAttribute();
    boolean stateChange = false;
    
    if (getPres(sty.setName("azimuth"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.azimuth) {
        
        this.azimuth = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("elevation"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.elevation) {
        
        this.elevation = newVal;
        stateChange = true;
      } 
    } 
    
    return stateChange;
  }
}
