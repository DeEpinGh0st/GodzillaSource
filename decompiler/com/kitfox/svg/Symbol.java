package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

















































public class Symbol
  extends Group
{
  public static final String TAG_NAME = "symbol";
  AffineTransform viewXform;
  Rectangle2D viewBox;
  
  public String getTagName() {
    return "symbol";
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();




    
    if (getPres(sty.setName("viewBox"))) {
      
      float[] dim = sty.getFloatList();
      this.viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
    } 
    
    if (this.viewBox == null)
    {
      
      this.viewBox = new Rectangle(0, 0, 1, 1);
    }

    
    this.viewXform = new AffineTransform();
    this.viewXform.scale(1.0D / this.viewBox.getWidth(), 1.0D / this.viewBox.getHeight());
    this.viewXform.translate(-this.viewBox.getX(), -this.viewBox.getY());
  }


  
  protected boolean outsideClip(Graphics2D g) throws SVGException {
    Shape clip = g.getClip();
    
    Rectangle2D rect = super.getBoundingBox();
    if (clip == null || clip.intersects(rect))
    {
      return false;
    }
    
    return true;
  }



  
  public void render(Graphics2D g) throws SVGException {
    AffineTransform oldXform = g.getTransform();
    g.transform(this.viewXform);
    
    super.render(g);
    
    g.setTransform(oldXform);
  }


  
  public Shape getShape() {
    Shape shape = super.getShape();
    return this.viewXform.createTransformedShape(shape);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    Rectangle2D rect = super.getBoundingBox();
    return this.viewXform.createTransformedShape(rect).getBounds2D();
  }










  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);


    
    return changeState;
  }
}
