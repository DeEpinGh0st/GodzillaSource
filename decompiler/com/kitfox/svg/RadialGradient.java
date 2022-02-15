package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;








































public class RadialGradient
  extends Gradient
{
  public static final String TAG_NAME = "radialgradient";
  float cx = 0.5F;
  float cy = 0.5F;
  boolean hasFocus = false;
  float fx = 0.0F;
  float fy = 0.0F;
  float r = 0.5F;









  
  public String getTagName() {
    return "radialgradient";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("cx")))
    {
      this.cx = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("cy")))
    {
      this.cy = sty.getFloatValueWithUnits();
    }
    
    this.hasFocus = false;
    if (getPres(sty.setName("fx"))) {
      
      this.fx = sty.getFloatValueWithUnits();
      this.hasFocus = true;
    } 
    
    if (getPres(sty.setName("fy"))) {
      
      this.fy = sty.getFloatValueWithUnits();
      this.hasFocus = true;
    } 
    
    if (getPres(sty.setName("r")))
    {
      this.r = sty.getFloatValueWithUnits();
    }
  }

  
  public Paint getPaint(Rectangle2D bounds, AffineTransform xform) {
    MultipleGradientPaint.CycleMethod method;
    Paint paint;
    switch (this.spreadMethod) {

      
      default:
        method = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        break;
      case 1:
        method = MultipleGradientPaint.CycleMethod.REPEAT;
        break;
      case 2:
        method = MultipleGradientPaint.CycleMethod.REFLECT;
        break;
    } 

    
    Point2D.Float pt1 = new Point2D.Float(this.cx, this.cy);
    Point2D.Float pt2 = this.hasFocus ? new Point2D.Float(this.fx, this.fy) : pt1;
    float[] stopFractions = getStopFractions();
    Color[] stopColors = getStopColors();





    
    if (this.gradientUnits == 1) {
      
      paint = new RadialGradientPaint(pt1, this.r, pt2, stopFractions, stopColors, method, MultipleGradientPaint.ColorSpaceType.SRGB, this.gradientTransform);



    
    }
    else {



      
      AffineTransform viewXform = new AffineTransform();
      viewXform.translate(bounds.getX(), bounds.getY());
      viewXform.scale(bounds.getWidth(), bounds.getHeight());
      
      viewXform.concatenate(this.gradientTransform);
      
      paint = new RadialGradientPaint(pt1, this.r, pt2, stopFractions, stopColors, method, MultipleGradientPaint.ColorSpaceType.SRGB, viewXform);
    } 








    
    return paint;
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);

    
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getPres(sty.setName("cx"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.cx) {
        
        this.cx = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("cy"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.cy) {
        
        this.cy = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("fx"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.fx) {
        
        this.fx = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("fy"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.fy) {
        
        this.fy = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("r"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.r) {
        
        this.r = newVal;
        shapeChange = true;
      } 
    } 
    
    return changeState;
  }
}
