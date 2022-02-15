package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;








































public class Circle
  extends ShapeElement
{
  public static final String TAG_NAME = "circle";
  float cx = 0.0F;
  float cy = 0.0F;
  float r = 0.0F;
  Ellipse2D.Float circle = new Ellipse2D.Float();









  
  public String getTagName() {
    return "circle";
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
    
    if (getPres(sty.setName("r")))
    {
      this.r = sty.getFloatValueWithUnits();
    }
    
    this.circle.setFrame(this.cx - this.r, this.cy - this.r, this.r * 2.0F, this.r * 2.0F);
  }


  
  public void render(Graphics2D g) throws SVGException {
    beginLayer(g);
    renderShape(g, this.circle);
    finishLayer(g);
  }


  
  public Shape getShape() {
    return shapeToParent(this.circle);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    return boundsToParent(includeStrokeInBounds(this.circle.getBounds2D()));
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
    
    if (getPres(sty.setName("r"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.r) {
        
        this.r = newVal;
        shapeChange = true;
      } 
    } 
    
    if (shapeChange)
    {
      build();
    }


    
    return (changeState || shapeChange);
  }
}
