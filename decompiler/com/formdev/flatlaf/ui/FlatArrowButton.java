package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;























public class FlatArrowButton
  extends BasicArrowButton
  implements UIResource
{
  public static final int DEFAULT_ARROW_WIDTH = 8;
  protected final boolean chevron;
  protected final Color foreground;
  protected final Color disabledForeground;
  protected final Color hoverForeground;
  protected final Color hoverBackground;
  protected final Color pressedForeground;
  protected final Color pressedBackground;
  private int arrowWidth = 8;
  private int xOffset = 0;
  private int yOffset = 0;
  
  private boolean hover;
  
  private boolean pressed;

  
  public FlatArrowButton(int direction, String type, Color foreground, Color disabledForeground, Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground) {
    super(direction, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
    
    this.chevron = FlatUIUtils.isChevron(type);
    this.foreground = foreground;
    this.disabledForeground = disabledForeground;
    this.hoverForeground = hoverForeground;
    this.hoverBackground = hoverBackground;
    this.pressedForeground = pressedForeground;
    this.pressedBackground = pressedBackground;
    
    setOpaque(false);
    setBorder((Border)null);
    
    if (hoverForeground != null || hoverBackground != null || pressedForeground != null || pressedBackground != null)
    {
      
      addMouseListener(new MouseAdapter()
          {
            public void mouseEntered(MouseEvent e) {
              FlatArrowButton.this.hover = true;
              FlatArrowButton.this.repaint();
            }

            
            public void mouseExited(MouseEvent e) {
              FlatArrowButton.this.hover = false;
              FlatArrowButton.this.repaint();
            }

            
            public void mousePressed(MouseEvent e) {
              FlatArrowButton.this.pressed = true;
              FlatArrowButton.this.repaint();
            }

            
            public void mouseReleased(MouseEvent e) {
              FlatArrowButton.this.pressed = false;
              FlatArrowButton.this.repaint();
            }
          });
    }
  }
  
  public int getArrowWidth() {
    return this.arrowWidth;
  }
  
  public void setArrowWidth(int arrowWidth) {
    this.arrowWidth = arrowWidth;
  }
  
  protected boolean isHover() {
    return this.hover;
  }
  
  protected boolean isPressed() {
    return this.pressed;
  }
  
  public int getXOffset() {
    return this.xOffset;
  }
  
  public void setXOffset(int xOffset) {
    this.xOffset = xOffset;
  }
  
  public int getYOffset() {
    return this.yOffset;
  }
  
  public void setYOffset(int yOffset) {
    this.yOffset = yOffset;
  }
  
  protected Color deriveBackground(Color background) {
    return background;
  }
  
  protected Color deriveForeground(Color foreground) {
    return FlatUIUtils.deriveColor(foreground, this.foreground);
  }

  
  public Dimension getPreferredSize() {
    return UIScale.scale(super.getPreferredSize());
  }

  
  public Dimension getMinimumSize() {
    return UIScale.scale(super.getMinimumSize());
  }

  
  public void paint(Graphics g) {
    Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g);

    
    if (isEnabled()) {

      
      Color background = (this.pressedBackground != null && isPressed()) ? this.pressedBackground : ((this.hoverBackground != null && isHover()) ? this.hoverBackground : null);


      
      if (background != null) {
        g.setColor(deriveBackground(background));
        paintBackground((Graphics2D)g);
      } 
    } 

    
    g.setColor(deriveForeground(isEnabled() ? ((this.pressedForeground != null && 
          isPressed()) ? this.pressedForeground : ((this.hoverForeground != null && 
          
          isHover()) ? this.hoverForeground : this.foreground)) : this.disabledForeground));


    
    paintArrow((Graphics2D)g);
    
    FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
  }
  
  protected void paintBackground(Graphics2D g) {
    g.fillRect(0, 0, getWidth(), getHeight());
  }
  
  protected void paintArrow(Graphics2D g) {
    int direction = getDirection();
    boolean vert = (direction == 1 || direction == 5);

    
    int w = UIScale.scale(this.arrowWidth + (this.chevron ? 0 : 1));
    int h = UIScale.scale(this.arrowWidth / 2 + (this.chevron ? 0 : 1));

    
    int rw = vert ? w : h;
    int rh = vert ? h : w;

    
    if (this.chevron) {
      
      rw++;
      rh++;
    } 
    
    int x = Math.round((getWidth() - rw) / 2.0F + UIScale.scale(this.xOffset));
    int y = Math.round((getHeight() - rh) / 2.0F + UIScale.scale(this.yOffset));

    
    Container parent = getParent();
    if (vert && parent instanceof JComponent && FlatUIUtils.hasRoundBorder((JComponent)parent)) {
      x -= UIScale.scale(parent.getComponentOrientation().isLeftToRight() ? 1 : -1);
    }
    
    g.translate(x, y);


    
    Shape arrowShape = createArrowShape(direction, this.chevron, w, h);
    if (this.chevron) {
      g.setStroke(new BasicStroke(UIScale.scale(1.0F)));
      g.draw(arrowShape);
    } else {
      
      g.fill(arrowShape);
    } 
    g.translate(-x, -y);
  }
  
  public static Shape createArrowShape(int direction, boolean chevron, float w, float h) {
    switch (direction) { case 1:
        return FlatUIUtils.createPath(!chevron, new double[] { 0.0D, h, (w / 2.0F), 0.0D, w, h });
      case 5: return FlatUIUtils.createPath(!chevron, new double[] { 0.0D, 0.0D, (w / 2.0F), h, w, 0.0D });
      case 7: return FlatUIUtils.createPath(!chevron, new double[] { h, 0.0D, 0.0D, (w / 2.0F), h, w });
      case 3: return FlatUIUtils.createPath(!chevron, new double[] { 0.0D, 0.0D, h, (w / 2.0F), 0.0D, w }); }
     return new Path2D.Float();
  }
}
