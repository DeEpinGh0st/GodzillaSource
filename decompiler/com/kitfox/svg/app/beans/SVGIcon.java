package com.kitfox.svg.app.beans;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import javax.swing.ImageIcon;











































public class SVGIcon
  extends ImageIcon
{
  public static final long serialVersionUID = 1L;
  public static final String PROP_AUTOSIZE = "PROP_AUTOSIZE";
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  
  SVGUniverse svgUniverse = SVGCache.getSVGUniverse();
  
  public static final int INTERP_NEAREST_NEIGHBOR = 0;
  public static final int INTERP_BILINEAR = 1;
  public static final int INTERP_BICUBIC = 2;
  private boolean antiAlias;
  private int interpolation = 0;
  
  private boolean clipToViewbox;
  
  URI svgURI;
  
  AffineTransform scaleXform = new AffineTransform();
  
  public static final int AUTOSIZE_NONE = 0;
  public static final int AUTOSIZE_HORIZ = 1;
  public static final int AUTOSIZE_VERT = 2;
  public static final int AUTOSIZE_BESTFIT = 3;
  public static final int AUTOSIZE_STRETCH = 4;
  private int autosize = 0;



  
  Dimension preferredSize;



  
  public void addPropertyChangeListener(PropertyChangeListener p) {
    this.changes.addPropertyChangeListener(p);
  }

  
  public void removePropertyChangeListener(PropertyChangeListener p) {
    this.changes.removePropertyChangeListener(p);
  }


  
  public Image getImage() {
    BufferedImage bi = new BufferedImage(getIconWidth(), getIconHeight(), 2);
    paintIcon((Component)null, bi.getGraphics(), 0, 0);
    return bi;
  }




  
  public int getIconHeightIgnoreAutosize() {
    if (this.preferredSize != null && (this.autosize == 2 || this.autosize == 4 || this.autosize == 3))
    {

      
      return this.preferredSize.height;
    }
    
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (diagram == null)
    {
      return 0;
    }
    return (int)diagram.getHeight();
  }





  
  public int getIconWidthIgnoreAutosize() {
    if (this.preferredSize != null && (this.autosize == 1 || this.autosize == 4 || this.autosize == 3))
    {

      
      return this.preferredSize.width;
    }
    
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (diagram == null)
    {
      return 0;
    }
    return (int)diagram.getWidth();
  }


  
  private boolean isAutoSizeBestFitUseFixedHeight(int iconWidthIgnoreAutosize, int iconHeightIgnoreAutosize, SVGDiagram diagram) {
    return (iconHeightIgnoreAutosize / diagram.getHeight() < iconWidthIgnoreAutosize / diagram.getWidth());
  }


  
  public int getIconWidth() {
    int iconWidthIgnoreAutosize = getIconWidthIgnoreAutosize();
    int iconHeightIgnoreAutosize = getIconHeightIgnoreAutosize();
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (this.preferredSize != null && (this.autosize == 2 || (this.autosize == 3 && 
      isAutoSizeBestFitUseFixedHeight(iconWidthIgnoreAutosize, iconHeightIgnoreAutosize, diagram)))) {
      
      double aspectRatio = (diagram.getHeight() / diagram.getWidth());
      return (int)(iconHeightIgnoreAutosize / aspectRatio);
    } 

    
    return iconWidthIgnoreAutosize;
  }



  
  public int getIconHeight() {
    int iconWidthIgnoreAutosize = getIconWidthIgnoreAutosize();
    int iconHeightIgnoreAutosize = getIconHeightIgnoreAutosize();
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (this.preferredSize != null && (this.autosize == 1 || (this.autosize == 3 && 
      !isAutoSizeBestFitUseFixedHeight(iconWidthIgnoreAutosize, iconHeightIgnoreAutosize, diagram)))) {
      
      double aspectRatio = (diagram.getHeight() / diagram.getWidth());
      return (int)(iconWidthIgnoreAutosize * aspectRatio);
    } 

    
    return iconHeightIgnoreAutosize;
  }












  
  public void paintIcon(Component comp, Graphics gg, int x, int y) {
    Graphics2D g = (Graphics2D)gg.create();
    paintIcon(comp, g, x, y);
    g.dispose();
  }

  
  private void paintIcon(Component comp, Graphics2D g, int x, int y) {
    Object oldAliasHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    
    Object oldInterpolationHint = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
    switch (this.interpolation) {
      
      case 0:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        break;
      case 1:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        break;
      case 2:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        break;
    } 

    
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (diagram == null) {
      return;
    }

    
    g.translate(x, y);
    diagram.setIgnoringClipHeuristic(!this.clipToViewbox);
    if (this.clipToViewbox)
    {
      g.setClip(new Rectangle2D.Float(0.0F, 0.0F, diagram.getWidth(), diagram.getHeight()));
    }

    
    if (this.autosize == 0) {

      
      try {
        diagram.render(g);
        g.translate(-x, -y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
      }
      catch (Exception e) {
        
        throw new RuntimeException(e);
      } 
      
      return;
    } 
    int width = getIconWidthIgnoreAutosize();
    int height = getIconHeightIgnoreAutosize();


    
    if (width == 0 || height == 0) {
      return;
    }

















    
    double diaWidth = diagram.getWidth();
    double diaHeight = diagram.getHeight();
    
    double scaleW = 1.0D;
    double scaleH = 1.0D;
    if (this.autosize == 3) {
      
      scaleW = scaleH = (height / diaHeight < width / diaWidth) ? (height / diaHeight) : (width / diaWidth);
    
    }
    else {
      
      scaleW = scaleH = width / diaWidth;


      
      scaleW = scaleH = height / diaHeight;
      
      if (this.autosize == 4) {
        
        scaleW = width / diaWidth;
        scaleH = height / diaHeight;
      } 
    }  this.scaleXform.setToScale(scaleW, scaleH);
    
    AffineTransform oldXform = g.getTransform();
    g.transform(this.scaleXform);

    
    try {
      diagram.render(g);
    }
    catch (SVGException e) {
      
      throw new RuntimeException(e);
    } 
    
    g.setTransform(oldXform);

    
    g.translate(-x, -y);
    
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
    if (oldInterpolationHint != null)
    {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolationHint);
    }
  }




  
  public SVGUniverse getSvgUniverse() {
    return this.svgUniverse;
  }

  
  public void setSvgUniverse(SVGUniverse svgUniverse) {
    SVGUniverse old = this.svgUniverse;
    this.svgUniverse = svgUniverse;
    this.changes.firePropertyChange("svgUniverse", old, svgUniverse);
  }




  
  public URI getSvgURI() {
    return this.svgURI;
  }





  
  public void setSvgURI(URI svgURI) {
    URI old = this.svgURI;
    this.svgURI = svgURI;
    
    SVGDiagram diagram = this.svgUniverse.getDiagram(svgURI);
    if (diagram != null) {
      
      Dimension size = getPreferredSize();
      if (size == null)
      {
        size = new Dimension((int)diagram.getRoot().getDeviceWidth(), (int)diagram.getRoot().getDeviceHeight());
      }
      diagram.setDeviceViewport(new Rectangle(0, 0, size.width, size.height));
    } 
    
    this.changes.firePropertyChange("svgURI", old, svgURI);
  }






  
  public void setSvgResourcePath(String resourcePath) {
    URI old = this.svgURI;

    
    try {
      this.svgURI = new URI(getClass().getResource(resourcePath).toString());
      this.changes.firePropertyChange("svgURI", old, this.svgURI);
      
      SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
      if (diagram != null)
      {
        diagram.setDeviceViewport(new Rectangle(0, 0, this.preferredSize.width, this.preferredSize.height));
      
      }
    }
    catch (Exception e) {
      
      this.svgURI = old;
    } 
  }







  
  public boolean isScaleToFit() {
    return (this.autosize == 4);
  }




  
  public void setScaleToFit(boolean scaleToFit) {
    setAutosize(4);
  }




  
  public Dimension getPreferredSize() {
    if (this.preferredSize == null) {
      
      SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
      if (diagram != null)
      {
        
        setPreferredSize(new Dimension((int)diagram.getWidth(), (int)diagram.getHeight()));
      }
    } 
    
    return new Dimension(this.preferredSize);
  }

  
  public void setPreferredSize(Dimension preferredSize) {
    Dimension old = this.preferredSize;
    this.preferredSize = preferredSize;
    
    SVGDiagram diagram = this.svgUniverse.getDiagram(this.svgURI);
    if (diagram != null)
    {
      diagram.setDeviceViewport(new Rectangle(0, 0, preferredSize.width, preferredSize.height));
    }
    
    this.changes.firePropertyChange("preferredSize", old, preferredSize);
  }






  
  public boolean getUseAntiAlias() {
    return getAntiAlias();
  }





  
  public void setUseAntiAlias(boolean antiAlias) {
    setAntiAlias(antiAlias);
  }




  
  public boolean getAntiAlias() {
    return this.antiAlias;
  }




  
  public void setAntiAlias(boolean antiAlias) {
    boolean old = this.antiAlias;
    this.antiAlias = antiAlias;
    this.changes.firePropertyChange("antiAlias", old, antiAlias);
  }




  
  public int getInterpolation() {
    return this.interpolation;
  }








  
  public void setInterpolation(int interpolation) {
    int old = this.interpolation;
    this.interpolation = interpolation;
    this.changes.firePropertyChange("interpolation", old, interpolation);
  }





  
  public boolean isClipToViewbox() {
    return this.clipToViewbox;
  }

  
  public void setClipToViewbox(boolean clipToViewbox) {
    this.clipToViewbox = clipToViewbox;
  }




  
  public int getAutosize() {
    return this.autosize;
  }




  
  public void setAutosize(int autosize) {
    int oldAutosize = this.autosize;
    this.autosize = autosize;
    this.changes.firePropertyChange("PROP_AUTOSIZE", oldAutosize, autosize);
  }
}
