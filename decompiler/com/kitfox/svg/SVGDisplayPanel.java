package com.kitfox.svg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.Scrollable;































public class SVGDisplayPanel
  extends JPanel
  implements Scrollable
{
  public static final long serialVersionUID = 1L;
  SVGDiagram diagram = null;
  float scale = 1.0F;
  Color bgColor = null;


  
  public SVGDisplayPanel() {
    initComponents();
  }

  
  public SVGDiagram getDiagram() {
    return this.diagram;
  }

  
  public void setDiagram(SVGDiagram diagram) {
    this.diagram = diagram;
    diagram.setDeviceViewport(getBounds());
    
    setDimension();
  }

  
  public void setScale(float scale) {
    this.scale = scale;
    setDimension();
  }

  
  public void setBgColor(Color col) {
    this.bgColor = col;
  }

  
  private void setDimension() {
    if (this.diagram == null) {
      
      setPreferredSize(new Dimension(1, 1));
      revalidate();
      
      return;
    } 
    Rectangle2D.Float rect = new Rectangle2D.Float();
    this.diagram.getViewRect(rect);
    
    int w = (int)(rect.width * this.scale);
    int h = (int)(rect.height * this.scale);
    
    setPreferredSize(new Dimension(w, h));
    revalidate();
  }






  
  public void updateTime(double curTime) throws SVGException {
    if (this.diagram == null)
      return; 
    this.diagram.updateTime(curTime);
  }


  
  public void paintComponent(Graphics gg) {
    Graphics2D g = (Graphics2D)gg;
    
    if (this.bgColor != null) {
      
      Dimension dim = getSize();
      g.setColor(this.bgColor);
      g.fillRect(0, 0, dim.width, dim.height);
    } 
    
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    if (this.diagram != null) {
      
      try {
        
        this.diagram.render(g);
      }
      catch (SVGException e) {
        
        Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, "Could not render diagram", e);
      } 
    }
  }









  
  private void initComponents() {
    setLayout(new BorderLayout());
    
    addComponentListener(new ComponentAdapter()
        {
          
          public void componentResized(ComponentEvent evt)
          {
            SVGDisplayPanel.this.formComponentResized(evt);
          }
        });
  }


  
  private void formComponentResized(ComponentEvent evt) {
    if (this.diagram != null) {
      
      this.diagram.setDeviceViewport(getBounds());
      setDimension();
    } 
  }


  
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    if (orientation == 0)
    {
      return visibleRect.width;
    }
    return visibleRect.height;
  }

  
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return getScrollableBlockIncrement(visibleRect, orientation, direction) / 16;
  }
}
