package com.kitfox.svg;

import com.kitfox.svg.xml.NumberWithUnits;
import com.kitfox.svg.xml.StyleAttribute;
import com.kitfox.svg.xml.StyleSheet;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;












































public class SVGRoot
  extends Group
{
  public static final String TAG_NAME = "svg";
  NumberWithUnits x;
  NumberWithUnits y;
  NumberWithUnits width;
  NumberWithUnits height;
  Rectangle2D.Float viewBox = null;
  
  public static final int PA_X_NONE = 0;
  
  public static final int PA_X_MIN = 1;
  
  public static final int PA_X_MID = 2;
  
  public static final int PA_X_MAX = 3;
  public static final int PA_Y_NONE = 0;
  public static final int PA_Y_MIN = 1;
  public static final int PA_Y_MID = 2;
  public static final int PA_Y_MAX = 3;
  public static final int PS_MEET = 0;
  public static final int PS_SLICE = 1;
  int parSpecifier = 0;
  int parAlignX = 2;
  int parAlignY = 2;
  
  final AffineTransform viewXform = new AffineTransform();
  final Rectangle2D.Float clipRect = new Rectangle2D.Float();



  
  private StyleSheet styleSheet;




  
  public String getTagName() {
    return "svg";
  }


  
  public void build() throws SVGException {
    super.build();
    
    StyleAttribute sty = new StyleAttribute();
    
    if (getPres(sty.setName("x")))
    {
      this.x = sty.getNumberWithUnits();
    }
    
    if (getPres(sty.setName("y")))
    {
      this.y = sty.getNumberWithUnits();
    }
    
    if (getPres(sty.setName("width")))
    {
      this.width = sty.getNumberWithUnits();
    }
    
    if (getPres(sty.setName("height")))
    {
      this.height = sty.getNumberWithUnits();
    }
    
    if (getPres(sty.setName("viewBox"))) {
      
      float[] coords = sty.getFloatList();
      this.viewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
    } 
    
    if (getPres(sty.setName("preserveAspectRatio"))) {
      
      String preserve = sty.getStringValue();
      
      if (contains(preserve, "none")) { this.parAlignX = 0; this.parAlignY = 0; }
      else if (contains(preserve, "xMinYMin")) { this.parAlignX = 1; this.parAlignY = 1; }
      else if (contains(preserve, "xMidYMin")) { this.parAlignX = 2; this.parAlignY = 1; }
      else if (contains(preserve, "xMaxYMin")) { this.parAlignX = 3; this.parAlignY = 1; }
      else if (contains(preserve, "xMinYMid")) { this.parAlignX = 1; this.parAlignY = 2; }
      else if (contains(preserve, "xMidYMid")) { this.parAlignX = 2; this.parAlignY = 2; }
      else if (contains(preserve, "xMaxYMid")) { this.parAlignX = 3; this.parAlignY = 2; }
      else if (contains(preserve, "xMinYMax")) { this.parAlignX = 1; this.parAlignY = 3; }
      else if (contains(preserve, "xMidYMax")) { this.parAlignX = 2; this.parAlignY = 3; }
      else if (contains(preserve, "xMaxYMax")) { this.parAlignX = 3; this.parAlignY = 3; }
      
      if (contains(preserve, "meet")) {
        
        this.parSpecifier = 0;
      }
      else if (contains(preserve, "slice")) {
        
        this.parSpecifier = 1;
      } 
    } 
    
    prepareViewport();
  }

  
  private boolean contains(String text, String find) {
    return (text.indexOf(find) != -1);
  }


  
  public SVGRoot getRoot() {
    return this;
  }
  protected void prepareViewport() {
    Rectangle2D defaultBounds;
    float xx, yy, ww, hh;
    Rectangle deviceViewport = this.diagram.getDeviceViewport();


    
    try {
      defaultBounds = getBoundingBox();
    }
    catch (SVGException ex) {
      
      defaultBounds = new Rectangle2D.Float();
    } 


    
    if (this.width != null) {
      
      xx = (this.x == null) ? 0.0F : StyleAttribute.convertUnitsToPixels(this.x.getUnits(), this.x.getValue());
      if (this.width.getUnits() == 9)
      {
        ww = this.width.getValue() * deviceViewport.width;
      }
      else
      {
        ww = StyleAttribute.convertUnitsToPixels(this.width.getUnits(), this.width.getValue());
      }
    
    } else if (this.viewBox != null) {
      
      xx = this.viewBox.x;
      ww = this.viewBox.width;
      this.width = new NumberWithUnits(ww, 1);
      this.x = new NumberWithUnits(xx, 1);
    
    }
    else {
      
      xx = (float)defaultBounds.getX();
      ww = (float)defaultBounds.getWidth();
      this.width = new NumberWithUnits(ww, 1);
      this.x = new NumberWithUnits(xx, 1);
    } 
    
    if (this.height != null) {
      
      yy = (this.y == null) ? 0.0F : StyleAttribute.convertUnitsToPixels(this.y.getUnits(), this.y.getValue());
      if (this.height.getUnits() == 9)
      {
        hh = this.height.getValue() * deviceViewport.height;
      }
      else
      {
        hh = StyleAttribute.convertUnitsToPixels(this.height.getUnits(), this.height.getValue());
      }
    
    } else if (this.viewBox != null) {
      
      yy = this.viewBox.y;
      hh = this.viewBox.height;
      this.height = new NumberWithUnits(hh, 1);
      this.y = new NumberWithUnits(yy, 1);
    
    }
    else {
      
      yy = (float)defaultBounds.getY();
      hh = (float)defaultBounds.getHeight();
      this.height = new NumberWithUnits(hh, 1);
      this.y = new NumberWithUnits(yy, 1);
    } 
    
    this.clipRect.setRect(xx, yy, ww, hh);
  }

  
  public void renderToViewport(Graphics2D g) throws SVGException {
    render(g);
  }


  
  public void render(Graphics2D g) throws SVGException {
    prepareViewport();
    
    Rectangle targetViewport = g.getClipBounds();








    
    Rectangle deviceViewport = this.diagram.getDeviceViewport();
    if (this.width != null && this.height != null) {


      
      float ww, hh, xx = (this.x == null) ? 0.0F : StyleAttribute.convertUnitsToPixels(this.x.getUnits(), this.x.getValue());
      if (this.width.getUnits() == 9) {
        
        ww = this.width.getValue() * deviceViewport.width;
      }
      else {
        
        ww = StyleAttribute.convertUnitsToPixels(this.width.getUnits(), this.width.getValue());
      } 
      
      float yy = (this.y == null) ? 0.0F : StyleAttribute.convertUnitsToPixels(this.y.getUnits(), this.y.getValue());
      if (this.height.getUnits() == 9) {
        
        hh = this.height.getValue() * deviceViewport.height;
      }
      else {
        
        hh = StyleAttribute.convertUnitsToPixels(this.height.getUnits(), this.height.getValue());
      } 
      
      targetViewport = new Rectangle((int)xx, (int)yy, (int)ww, (int)hh);
    }
    else {
      
      targetViewport = new Rectangle(deviceViewport);
    } 
    this.clipRect.setRect(targetViewport);
    
    this.viewXform.setTransform(calcViewportTransform(targetViewport));
    
    AffineTransform cachedXform = g.getTransform();
    g.transform(this.viewXform);
    
    super.render(g);
    
    g.setTransform(cachedXform);
  }

  
  public AffineTransform calcViewportTransform(Rectangle targetViewport) {
    AffineTransform xform = new AffineTransform();
    
    if (this.viewBox == null) {
      
      xform.setToIdentity();
    }
    else {
      
      xform.setToIdentity();
      xform.setToTranslation(targetViewport.x, targetViewport.y);
      xform.scale(targetViewport.width, targetViewport.height);
      xform.scale((1.0F / this.viewBox.width), (1.0F / this.viewBox.height));
      xform.translate(-this.viewBox.x, -this.viewBox.y);
    } 
    
    return xform;
  }


  
  public void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    if (this.viewXform != null) {
      
      ltw = new AffineTransform(ltw);
      ltw.concatenate(this.viewXform);
    } 
    
    super.pick(pickArea, ltw, boundingBox, retVec);
  }


  
  public void pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
    if (this.viewXform != null) {
      
      try {
        
        this.viewXform.inverseTransform(point, xPoint);
      } catch (NoninvertibleTransformException ex) {
        
        throw new SVGException(ex);
      } 
    }
    
    super.pick(xPoint, boundingBox, retVec);
  }


  
  public Shape getShape() {
    Shape shape = super.getShape();
    return this.viewXform.createTransformedShape(shape);
  }


  
  public Rectangle2D getBoundingBox() throws SVGException {
    Rectangle2D bbox = super.getBoundingBox();
    return this.viewXform.createTransformedShape(bbox).getBounds2D();
  }

  
  public float getDeviceWidth() {
    return this.clipRect.width;
  }

  
  public float getDeviceHeight() {
    return this.clipRect.height;
  }

  
  public Rectangle2D getDeviceRect(Rectangle2D rect) {
    rect.setRect(this.clipRect);
    return rect;
  }








  
  public boolean updateTime(double curTime) throws SVGException {
    boolean changeState = super.updateTime(curTime);
    
    StyleAttribute sty = new StyleAttribute();
    boolean shapeChange = false;
    
    if (getPres(sty.setName("x"))) {
      
      NumberWithUnits newVal = sty.getNumberWithUnits();
      if (!newVal.equals(this.x)) {
        
        this.x = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("y"))) {
      
      NumberWithUnits newVal = sty.getNumberWithUnits();
      if (!newVal.equals(this.y)) {
        
        this.y = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("width"))) {
      
      NumberWithUnits newVal = sty.getNumberWithUnits();
      if (!newVal.equals(this.width)) {
        
        this.width = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("height"))) {
      
      NumberWithUnits newVal = sty.getNumberWithUnits();
      if (!newVal.equals(this.height)) {
        
        this.height = newVal;
        shapeChange = true;
      } 
    } 
    
    if (getPres(sty.setName("viewBox"))) {
      
      float[] coords = sty.getFloatList();
      Rectangle2D.Float newViewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
      if (!newViewBox.equals(this.viewBox)) {
        
        this.viewBox = newViewBox;
        shapeChange = true;
      } 
    } 
    
    if (shapeChange)
    {
      build();
    }
    
    return (changeState || shapeChange);
  }




  
  public StyleSheet getStyleSheet() {
    if (this.styleSheet == null)
    {
      for (int i = 0; i < getNumChildren(); i++) {
        
        SVGElement ele = getChild(i);
        if (ele instanceof Style)
        {
          return ((Style)ele).getStyleSheet();
        }
      } 
    }
    
    return this.styleSheet;
  }




  
  public void setStyleSheet(StyleSheet styleSheet) {
    this.styleSheet = styleSheet;
  }
}
