package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;








































public class Rect
  extends ShapeElement
{
  public static final String TAG_NAME = "rect";
  float x = 0.0F;
  float y = 0.0F;
  float width = 0.0F;
  float height = 0.0F;
  float rx = 0.0F;
  float ry = 0.0F;




  
  RectangularShape rect;




  
  public String getTagName() {
    return "rect";
  }

  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeFloat(this.x);
    out.writeFloat(this.y);
    out.writeFloat(this.width);
    out.writeFloat(this.height);
    out.writeFloat(this.rx);
    out.writeFloat(this.ry);
  }

  
  private void readObject(ObjectInputStream in) throws IOException {
    this.x = in.readFloat();
    this.y = in.readFloat();
    this.width = in.readFloat();
    this.height = in.readFloat();
    this.rx = in.readFloat();
    this.ry = in.readFloat();
    
    if (this.rx == 0.0F && this.ry == 0.0F) {
      
      this.rect = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
    } else {
      
      this.rect = new RoundRectangle2D.Float(this.x, this.y, this.width, this.height, this.rx * 2.0F, this.ry * 2.0F);
    } 
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
    
    boolean rxSet = false;
    if (getPres(sty.setName("rx"))) {
      
      this.rx = sty.getFloatValueWithUnits();
      rxSet = true;
    } 
    
    boolean rySet = false;
    if (getPres(sty.setName("ry"))) {
      
      this.ry = sty.getFloatValueWithUnits();
      rySet = true;
    } 
    
    if (!rxSet)
    {
      this.rx = this.ry;
    }
    if (!rySet)
    {
      this.ry = this.rx;
    }

    
    if (this.rx == 0.0F && this.ry == 0.0F) {
      
      this.rect = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
    } else {
      
      this.rect = new RoundRectangle2D.Float(this.x, this.y, this.width, this.height, this.rx * 2.0F, this.ry * 2.0F);
    } 
  }


  
  public void render(Graphics2D g) throws SVGException {
    beginLayer(g);
    renderShape(g, this.rect);
    finishLayer(g);
  }


  
  public Shape getShape() {
    return shapeToParent(this.rect);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    return boundsToParent(includeStrokeInBounds(this.rect.getBounds2D()));
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
    
    if (getPres(sty.setName("rx"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.rx) {
        
        this.rx = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("ry"))) {
      
      float newVal = sty.getFloatValueWithUnits();
      if (newVal != this.ry) {
        
        this.ry = newVal;
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
