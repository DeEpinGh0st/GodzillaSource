package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.net.URI;
import java.net.URL;








































public class FilterEffects
  extends SVGElement
{
  public static final String TAG_NAME = "filtereffects";
  public static final int FP_SOURCE_GRAPHIC = 0;
  public static final int FP_SOURCE_ALPHA = 1;
  public static final int FP_BACKGROUND_IMAGE = 2;
  public static final int FP_BACKGROUND_ALPHA = 3;
  public static final int FP_FILL_PAINT = 4;
  public static final int FP_STROKE_PAINT = 5;
  public static final int FP_CUSTOM = 5;
  private int filterPrimitiveTypeIn;
  private String filterPrimitiveRefIn;
  float x = 0.0F;
  float y = 0.0F;
  float width = 1.0F;
  float height = 1.0F;
  String result = "defaultFilterName";
  URL href = null;









  
  public String getTagName() {
    return "filtereffects";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
    
    if (child instanceof FilterEffects);
  }





  
  protected void build() throws SVGException {
    super.build();
  }







































  
  public float getX() {
    return this.x;
  }

  
  public float getY() {
    return this.y;
  }

  
  public float getWidth() {
    return this.width;
  }

  
  public float getHeight() {
    return this.height;
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
    
    if (getPres(sty.setName("width"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.width) {
        
        this.width = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("height"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.height) {
        
        this.height = newVal;
        stateChange = true;
      } 
    } 

    
    try {
      if (getPres(sty.setName("xlink:href"))) {
        
        URI src = sty.getURIValue(getXMLBase());
        URL newVal = src.toURL();
        
        if (!newVal.equals(this.href)) {
          
          this.href = newVal;
          stateChange = true;
        } 
      } 
    } catch (Exception e) {
      
      throw new SVGException(e);
    } 





























    
    return stateChange;
  }
}
