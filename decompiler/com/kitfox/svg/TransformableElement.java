package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;









































public abstract class TransformableElement
  extends SVGElement
{
  AffineTransform xform = null;



  
  public TransformableElement() {}



  
  public TransformableElement(String id, SVGElement parent) {
    super(id, parent);
  }







  
  public AffineTransform getXForm() {
    return (this.xform == null) ? null : new AffineTransform(this.xform);
  }















  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("transform")))
    {
      this.xform = parseTransform(sty.getStringValue());
    }
  }

  
  protected Shape shapeToParent(Shape shape) {
    if (this.xform == null)
    {
      return shape;
    }
    return this.xform.createTransformedShape(shape);
  }

  
  protected Rectangle2D boundsToParent(Rectangle2D rect) {
    if (this.xform == null || rect == null)
    {
      return rect;
    }
    return this.xform.createTransformedShape(rect).getBounds2D();
  }









  
  public boolean updateTime(double curTime) throws SVGException {
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("transform"))) {
      
      AffineTransform newXform = parseTransform(sty.getStringValue());
      if (!newXform.equals(this.xform)) {
        
        this.xform = newXform;
        return true;
      } 
    } 
    
    return false;
  }
}
