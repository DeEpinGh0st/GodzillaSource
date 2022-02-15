package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.geom.Point2D;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;








































public class Filter
  extends SVGElement
{
  public static final String TAG_NAME = "filter";
  public static final int FU_OBJECT_BOUNDING_BOX = 0;
  public static final int FU_USER_SPACE_ON_USE = 1;
  protected int filterUnits = 0;
  public static final int PU_OBJECT_BOUNDING_BOX = 0;
  public static final int PU_USER_SPACE_ON_USE = 1;
  protected int primitiveUnits = 0;
  float x = 0.0F;
  float y = 0.0F;
  float width = 1.0F;
  float height = 1.0F;
  Point2D filterRes = new Point2D.Double();
  URL href = null;
  final ArrayList<SVGElement> filterEffects = new ArrayList<SVGElement>();









  
  public String getTagName() {
    return "filter";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
    
    if (child instanceof FilterEffects)
    {
      this.filterEffects.add(child);
    }
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();

    
    if (getPres(sty.setName("filterUnits"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        this.filterUnits = 1;
      } else {
        
        this.filterUnits = 0;
      } 
    } 
    
    if (getPres(sty.setName("primitiveUnits"))) {
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        this.primitiveUnits = 1;
      } else {
        
        this.primitiveUnits = 0;
      } 
    } 
    
    if (getPres(sty.setName("x")))
    {
      this.x = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("y")))
    {
      this.y = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("width")))
    {
      this.width = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("height")))
    {
      this.height = sty.getFloatValueWithUnits();
    }

    
    try {
      if (getPres(sty.setName("xlink:href"))) {
        
        URI src = sty.getURIValue(getXMLBase());
        this.href = src.toURL();
      } 
    } catch (Exception e) {
      
      throw new SVGException(e);
    } 
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
    
    if (getPres(sty.setName("filterUnits"))) {
      int newVal;
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        newVal = 1;
      } else {
        
        newVal = 0;
      } 
      if (newVal != this.filterUnits) {
        
        this.filterUnits = newVal;
        stateChange = true;
      } 
    } 
    
    if (getPres(sty.setName("primitiveUnits"))) {
      int newVal;
      
      String strn = sty.getStringValue().toLowerCase();
      if (strn.equals("userspaceonuse")) {
        
        newVal = 1;
      } else {
        
        newVal = 0;
      } 
      if (newVal != this.filterUnits) {
        
        this.primitiveUnits = newVal;
        stateChange = true;
      } 
    } 


    
    return stateChange;
  }
}
