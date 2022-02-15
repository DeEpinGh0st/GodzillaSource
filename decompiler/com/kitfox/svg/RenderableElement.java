package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.util.List;









































public abstract class RenderableElement
  extends TransformableElement
{
  AffineTransform cachedXform = null;
  
  Shape cachedClip = null;

  
  public static final int VECTOR_EFFECT_NONE = 0;
  
  public static final int VECTOR_EFFECT_NON_SCALING_STROKE = 1;
  
  int vectorEffect;

  
  public RenderableElement() {}

  
  public RenderableElement(String id, SVGElement parent) {
    super(id, parent);
  }


  
  protected void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("vector-effect"))) {
      
      if ("non-scaling-stroke".equals(sty.getStringValue())) {
        
        this.vectorEffect = 1;
      } else {
        
        this.vectorEffect = 0;
      } 
    } else {
      
      this.vectorEffect = 0;
    } 
  }



  
  public abstract void render(Graphics2D paramGraphics2D) throws SVGException;



  
  abstract void pick(Point2D paramPoint2D, boolean paramBoolean, List<List<SVGElement>> paramList) throws SVGException;



  
  abstract void pick(Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, boolean paramBoolean, List<List<SVGElement>> paramList) throws SVGException;



  
  public abstract Rectangle2D getBoundingBox() throws SVGException;


  
  protected void beginLayer(Graphics2D g) throws SVGException {
    if (this.xform != null) {
      
      this.cachedXform = g.getTransform();
      g.transform(this.xform);
    } 
    
    StyleAttribute styleAttrib = new StyleAttribute();


    
    Shape clipPath = null;
    int clipPathUnits = 0;
    if (getStyle(styleAttrib.setName("clip-path"), false) && 
      !"none".equals(styleAttrib.getStringValue())) {
      
      URI uri = styleAttrib.getURIValue(getXMLBase());
      if (uri != null) {
        
        ClipPath ele = (ClipPath)this.diagram.getUniverse().getElement(uri);
        clipPath = ele.getClipPathShape();
        clipPathUnits = ele.getClipPathUnits();
      } 
    } 

    
    if (clipPath != null) {
      
      if (clipPathUnits == 1 && this instanceof ShapeElement) {
        
        Rectangle2D rect = ((ShapeElement)this).getBoundingBox();
        AffineTransform at = new AffineTransform();
        at.scale(rect.getWidth(), rect.getHeight());
        clipPath = at.createTransformedShape(clipPath);
      } 
      
      this.cachedClip = g.getClip();
      if (this.cachedClip == null) {
        
        g.setClip(clipPath);
      } else {
        
        Area newClip = new Area(this.cachedClip);
        newClip.intersect(new Area(clipPath));
        g.setClip(newClip);
      } 
    } 
  }






  
  protected void finishLayer(Graphics2D g) {
    if (this.cachedClip != null)
    {
      g.setClip(this.cachedClip);
    }
    
    if (this.cachedXform != null)
    {
      g.setTransform(this.cachedXform);
    }
  }
}
