package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;








































public class FeSpotLight
  extends FeLight
{
  public static final String TAG_NAME = "fespotlight";
  float x = 0.0F;
  float y = 0.0F;
  float z = 0.0F;
  float pointsAtX = 0.0F;
  float pointsAtY = 0.0F;
  float pointsAtZ = 0.0F;
  float specularComponent = 0.0F;
  float limitingConeAngle = 0.0F;









  
  public String getTagName() {
    return "fespotlight";
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
    if (getPres(sty.setName("pointsAtX")))
    {
      this.pointsAtX = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("pointsAtY")))
    {
      this.pointsAtY = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("pointsAtZ")))
    {
      this.pointsAtZ = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("specularComponent")))
    {
      this.specularComponent = sty.getFloatValueWithUnits();
    }
    if (getPres(sty.setName("limitingConeAngle")))
    {
      this.limitingConeAngle = sty.getFloatValueWithUnits();
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

  
  public float getPointsAtX() {
    return this.pointsAtX;
  }

  
  public float getPointsAtY() {
    return this.pointsAtY;
  }

  
  public float getPointsAtZ() {
    return this.pointsAtZ;
  }

  
  public float getSpecularComponent() {
    return this.specularComponent;
  }

  
  public float getLimitingConeAngle() {
    return this.limitingConeAngle;
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
    
    if (getPres(sty.setName("pointsAtX"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.pointsAtX) {
        
        this.pointsAtX = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("pointsAtY"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.pointsAtY) {
        
        this.pointsAtY = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("pointsAtZ"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.pointsAtZ) {
        
        this.pointsAtZ = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("specularComponent"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.specularComponent) {
        
        this.specularComponent = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("limitingConeAngle"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.limitingConeAngle) {
        
        this.limitingConeAngle = newVal;
        stateChange = true;
      } 
    } 
    
    return stateChange;
  }
}
