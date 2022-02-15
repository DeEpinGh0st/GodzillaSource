package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URI;








































public class Use
  extends ShapeElement
{
  public static final String TAG_NAME = "use";
  float x = 0.0F;
  float y = 0.0F;
  float width = 1.0F;
  float height = 1.0F;
  
  URI href = null;




  
  AffineTransform refXform;




  
  public String getTagName() {
    return "use";
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
    
    if (getPres(sty.setName("width")))
    {
      this.width = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("height")))
    {
      this.height = sty.getFloatValueWithUnits();
    }
    
    if (getPres(sty.setName("xlink:href"))) {
      
      URI src = sty.getURIValue(getXMLBase());
      this.href = src;
    } 


    
    this.refXform = new AffineTransform();
    this.refXform.translate(this.x, this.y);
  }


  
  public void render(Graphics2D g) throws SVGException {
    beginLayer(g);

    
    AffineTransform oldXform = g.getTransform();
    g.transform(this.refXform);
    
    SVGElement ref = this.diagram.getUniverse().getElement(this.href);
    
    if (ref == null || !(ref instanceof RenderableElement)) {
      return;
    }

    
    RenderableElement rendEle = (RenderableElement)ref;
    rendEle.pushParentContext(this);
    rendEle.render(g);
    rendEle.popParentContext();
    
    g.setTransform(oldXform);
    
    finishLayer(g);
  }


  
  public Shape getShape() {
    SVGElement ref = this.diagram.getUniverse().getElement(this.href);
    if (ref instanceof ShapeElement) {
      
      Shape shape = ((ShapeElement)ref).getShape();
      shape = this.refXform.createTransformedShape(shape);
      shape = shapeToParent(shape);
      return shape;
    } 
    
    return null;
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    SVGElement ref = this.diagram.getUniverse().getElement(this.href);
    if (ref instanceof ShapeElement) {
      
      ShapeElement shapeEle = (ShapeElement)ref;
      shapeEle.pushParentContext(this);
      Rectangle2D bounds = shapeEle.getBoundingBox();
      shapeEle.popParentContext();
      
      bounds = this.refXform.createTransformedShape(bounds).getBounds2D();
      bounds = boundsToParent(bounds);
      
      return bounds;
    } 
    
    return null;
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);

    
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getPres(sty.setName("x"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.x) {
        
        this.x = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("y"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.y) {
        
        this.y = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("width"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.width) {
        
        this.width = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("height"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.height) {
        
        this.height = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("xlink:href"))) {
      
      URI src = sty.getURIValue(getXMLBase());
      
      if (!src.equals(this.href)) {
        
        this.href = src;
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
