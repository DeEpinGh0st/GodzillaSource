package com.kitfox.svg;

import com.kitfox.svg.app.data.Handler;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;








































public class ImageSVG
  extends RenderableElement
{
  public static final String TAG_NAME = "image";
  float x = 0.0F;
  float y = 0.0F;
  float width = 0.0F;
  float height = 0.0F;
  
  URL imageSrc = null;


  
  AffineTransform xform;


  
  Rectangle2D bounds;



  
  public String getTagName() {
    return "image";
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

    
    try {
      if (getPres(sty.setName("xlink:href"))) {
        
        URI src = sty.getURIValue(getXMLBase());
        if ("data".equals(src.getScheme())) {
          
          this.imageSrc = new URL(null, src.toASCIIString(), (URLStreamHandler)new Handler());
        }
        else if (!this.diagram.getUniverse().isImageDataInlineOnly()) {

          
          try {
            this.imageSrc = src.toURL();
          } catch (Exception e) {
            
            Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse xlink:href " + src, e);
            
            this.imageSrc = null;
          } 
        } 
      } 
    } catch (Exception e) {
      
      throw new SVGException(e);
    } 
    
    if (this.imageSrc != null) {
      
      this.diagram.getUniverse().registerImage(this.imageSrc);

      
      BufferedImage img = this.diagram.getUniverse().getImage(this.imageSrc);
      if (img == null) {
        
        this.xform = new AffineTransform();
        this.bounds = new Rectangle2D.Float();
        
        return;
      } 
      if (this.width == 0.0F)
      {
        this.width = img.getWidth();
      }
      if (this.height == 0.0F)
      {
        this.height = img.getHeight();
      }

      
      this.xform = new AffineTransform();
      this.xform.translate(this.x, this.y);
      this.xform.scale((this.width / img.getWidth()), (this.height / img.getHeight()));
    } 
    
    this.bounds = new Rectangle2D.Float(this.x, this.y, this.width, this.height);
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


  
  void pick(Point2D point, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    if (getBoundingBox().contains(point))
    {
      retVec.add(getPath(null));
    }
  }


  
  void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List<List<SVGElement>> retVec) throws SVGException {
    if (ltw.createTransformedShape(getBoundingBox()).intersects(pickArea))
    {
      retVec.add(getPath(null));
    }
  }


  
  public void render(Graphics2D g) throws SVGException {
    StyleAttribute styleAttrib = new StyleAttribute();
    if (getStyle(styleAttrib.setName("visibility")))
    {
      if (!styleAttrib.getStringValue().equals("visible")) {
        return;
      }
    }

    
    if (getStyle(styleAttrib.setName("display")))
    {
      if (styleAttrib.getStringValue().equals("none")) {
        return;
      }
    }

    
    beginLayer(g);
    
    float opacity = 1.0F;
    if (getStyle(styleAttrib.setName("opacity")))
    {
      opacity = styleAttrib.getRatioValue();
    }
    
    if (opacity <= 0.0F) {
      return;
    }

    
    Composite oldComp = null;
    
    if (opacity < 1.0F) {
      
      oldComp = g.getComposite();
      Composite comp = AlphaComposite.getInstance(3, opacity);
      g.setComposite(comp);
    } 
    
    BufferedImage img = this.diagram.getUniverse().getImage(this.imageSrc);
    if (img == null) {
      return;
    }

    
    AffineTransform curXform = g.getTransform();
    g.transform(this.xform);
    
    g.drawImage(img, 0, 0, (ImageObserver)null);
    
    g.setTransform(curXform);
    if (oldComp != null)
    {
      g.setComposite(oldComp);
    }
    
    finishLayer(g);
  }


  
  public Rectangle2D getBoundingBox() {
    return boundsToParent(this.bounds);
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

    
    try {
      if (getPres(sty.setName("xlink:href"))) {
        
        URI src = sty.getURIValue(getXMLBase());
        
        URL newVal = null;
        if ("data".equals(src.getScheme())) {
          
          newVal = new URL(null, src.toASCIIString(), (URLStreamHandler)new Handler());
        } else if (!this.diagram.getUniverse().isImageDataInlineOnly()) {
          
          newVal = src.toURL();
        } 
        
        if (newVal != null && !newVal.equals(this.imageSrc)) {
          
          this.imageSrc = newVal;
          shapeChange = true;
        } 
      } 
    } catch (IllegalArgumentException ie) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Image provided with illegal value for href: \"" + sty
          
          .getStringValue() + '"', ie);
    } catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not parse xlink:href", e);
    } 


    
    if (shapeChange)
    {
      build();
    }

























    
    return (changeState || shapeChange);
  }
}
