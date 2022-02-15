package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;


















































public class Group
  extends ShapeElement
{
  public static final String TAG_NAME = "group";
  Rectangle2D boundingBox;
  Shape cachedShape;
  
  public String getTagName() {
    return "group";
  }






  
  public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
    super.loaderAddChild(helper, child);
  }

  
  protected boolean outsideClip(Graphics2D g) throws SVGException {
    Shape clip = g.getClip();
    if (clip == null)
    {
      return false;
    }
    
    Rectangle2D rect = getBoundingBox();
    
    if (clip.intersects(rect))
    {
      return false;
    }
    
    return true;
  }


  
  void pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
    if (this.xform != null) {
      
      try {
        
        this.xform.inverseTransform(point, xPoint);
      } catch (NoninvertibleTransformException ex) {
        
        throw new SVGException(ex);
      } 
    }

    
    for (SVGElement ele : this.children) {
      if (ele instanceof RenderableElement) {
        
        RenderableElement rendEle = (RenderableElement)ele;
        
        rendEle.pick(xPoint, boundingBox, retVec);
      } 
    } 
  }


  
  void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    if (this.xform != null) {
      
      ltw = new AffineTransform(ltw);
      ltw.concatenate(this.xform);
    } 

    
    for (SVGElement ele : this.children) {
      if (ele instanceof RenderableElement) {
        
        RenderableElement rendEle = (RenderableElement)ele;
        
        rendEle.pick(pickArea, ltw, boundingBox, retVec);
      } 
    } 
  }



  
  public void render(Graphics2D g) throws SVGException {
    StyleAttribute styleAttrib = new StyleAttribute();

    
    if (getStyle(styleAttrib.setName("display")))
    {
      if (styleAttrib.getStringValue().equals("none")) {
        return;
      }
    }


    
    boolean ignoreClip = this.diagram.ignoringClipHeuristic();




    
    beginLayer(g);
    
    Iterator<SVGElement> it = this.children.iterator();











    
    Shape clip = g.getClip();
    while (it.hasNext()) {
      
      SVGElement ele = it.next();
      if (ele instanceof RenderableElement) {
        
        RenderableElement rendEle = (RenderableElement)ele;


        
        if (!(ele instanceof Group))
        {
          
          if (!ignoreClip && clip != null && 
            !clip.intersects(rendEle.getBoundingBox())) {
            continue;
          }
        }

        
        rendEle.render(g);
      } 
    } 
    
    finishLayer(g);
  }





  
  public Shape getShape() {
    if (this.cachedShape == null)
    {
      calcShape();
    }
    return this.cachedShape;
  }

  
  public void calcShape() {
    Area retShape = new Area();
    
    for (SVGElement ele : this.children) {
      if (ele instanceof ShapeElement) {
        
        ShapeElement shpEle = (ShapeElement)ele;
        Shape shape = shpEle.getShape();
        if (shape != null)
        {
          retShape.add(new Area(shape));
        }
      } 
    } 
    
    this.cachedShape = shapeToParent(retShape);
  }





  
  public Rectangle2D getBoundingBox() throws SVGException {
    if (this.boundingBox == null)
    {
      calcBoundingBox();
    }
    
    return this.boundingBox;
  }






  
  public void calcBoundingBox() throws SVGException {
    Rectangle2D retRect = null;
    
    for (SVGElement ele : this.children) {
      if (ele instanceof RenderableElement) {
        
        RenderableElement rendEle = (RenderableElement)ele;
        Rectangle2D bounds = rendEle.getBoundingBox();
        if (bounds != null && (bounds.getWidth() != 0.0D || bounds.getHeight() != 0.0D)) {
          
          if (retRect == null) {
            
            retRect = bounds;
            
            continue;
          } 
          if (retRect.getWidth() != 0.0D || retRect.getHeight() != 0.0D)
          {
            retRect = retRect.createUnion(bounds);
          }
        } 
      } 
    } 







    
    if (retRect == null)
    {
      retRect = new Rectangle2D.Float();
    }
    
    this.boundingBox = boundsToParent(retRect);
  }


  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);
    Iterator<SVGElement> it = this.children.iterator();

    
    while (it.hasNext()) {
      
      SVGElement ele = it.next();
      boolean updateVal = ele.updateTime(curTime);
      
      changeState = (changeState || updateVal);

      
      if (ele instanceof ShapeElement)
      {
        this.cachedShape = null;
      }
      if (ele instanceof RenderableElement)
      {
        this.boundingBox = null;
      }
    } 
    
    return changeState;
  }
}
