package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;























































public class FlatSliderUI
  extends BasicSliderUI
{
  protected int trackWidth;
  protected Dimension thumbSize;
  protected int focusWidth;
  protected Color trackValueColor;
  protected Color trackColor;
  protected Color thumbColor;
  protected Color thumbBorderColor;
  protected Color focusBaseColor;
  protected Color focusedColor;
  protected Color focusedThumbBorderColor;
  protected Color hoverThumbColor;
  protected Color pressedThumbColor;
  protected Color disabledTrackColor;
  protected Color disabledThumbColor;
  protected Color disabledThumbBorderColor;
  private Color defaultBackground;
  private Color defaultForeground;
  protected boolean thumbHover;
  protected boolean thumbPressed;
  private Object[] oldRenderingHints;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatSliderUI();
  }
  
  public FlatSliderUI() {
    super(null);
  }

  
  protected void installDefaults(JSlider slider) {
    super.installDefaults(slider);
    
    LookAndFeel.installProperty(slider, "opaque", Boolean.valueOf(false));
    
    this.trackWidth = UIManager.getInt("Slider.trackWidth");
    this.thumbSize = UIManager.getDimension("Slider.thumbSize");
    if (this.thumbSize == null) {
      
      int thumbWidth = UIManager.getInt("Slider.thumbWidth");
      this.thumbSize = new Dimension(thumbWidth, thumbWidth);
    } 
    this.focusWidth = FlatUIUtils.getUIInt("Slider.focusWidth", 4);
    
    this.trackValueColor = FlatUIUtils.getUIColor("Slider.trackValueColor", "Slider.thumbColor");
    this.trackColor = UIManager.getColor("Slider.trackColor");
    this.thumbColor = UIManager.getColor("Slider.thumbColor");
    this.thumbBorderColor = UIManager.getColor("Slider.thumbBorderColor");
    this.focusBaseColor = UIManager.getColor("Component.focusColor");
    this.focusedColor = FlatUIUtils.getUIColor("Slider.focusedColor", this.focusBaseColor);
    this.focusedThumbBorderColor = FlatUIUtils.getUIColor("Slider.focusedThumbBorderColor", "Component.focusedBorderColor");
    this.hoverThumbColor = UIManager.getColor("Slider.hoverThumbColor");
    this.pressedThumbColor = UIManager.getColor("Slider.pressedThumbColor");
    this.disabledTrackColor = UIManager.getColor("Slider.disabledTrackColor");
    this.disabledThumbColor = UIManager.getColor("Slider.disabledThumbColor");
    this.disabledThumbBorderColor = FlatUIUtils.getUIColor("Slider.disabledThumbBorderColor", "Component.disabledBorderColor");
    
    this.defaultBackground = UIManager.getColor("Slider.background");
    this.defaultForeground = UIManager.getColor("Slider.foreground");
  }

  
  protected void uninstallDefaults(JSlider slider) {
    super.uninstallDefaults(slider);
    
    this.trackValueColor = null;
    this.trackColor = null;
    this.thumbColor = null;
    this.thumbBorderColor = null;
    this.focusBaseColor = null;
    this.focusedColor = null;
    this.focusedThumbBorderColor = null;
    this.hoverThumbColor = null;
    this.pressedThumbColor = null;
    this.disabledTrackColor = null;
    this.disabledThumbColor = null;
    this.disabledThumbBorderColor = null;
    
    this.defaultBackground = null;
    this.defaultForeground = null;
  }

  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider slider) {
    return new FlatTrackListener();
  }

  
  public int getBaseline(JComponent c, int width, int height) {
    if (c == null)
      throw new NullPointerException(); 
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException();
    }
    
    if (this.slider.getOrientation() == 1) {
      return -1;
    }
    
    FontMetrics fm = this.slider.getFontMetrics(this.slider.getFont());
    return this.trackRect.y + Math.round((this.trackRect.height - fm.getHeight()) / 2.0F) + fm.getAscent() - 1;
  }

  
  public Dimension getPreferredHorizontalSize() {
    return UIScale.scale(super.getPreferredHorizontalSize());
  }

  
  public Dimension getPreferredVerticalSize() {
    return UIScale.scale(super.getPreferredVerticalSize());
  }

  
  public Dimension getMinimumHorizontalSize() {
    return UIScale.scale(super.getMinimumHorizontalSize());
  }

  
  public Dimension getMinimumVerticalSize() {
    return UIScale.scale(super.getMinimumVerticalSize());
  }

  
  protected int getTickLength() {
    return UIScale.scale(super.getTickLength());
  }

  
  protected Dimension getThumbSize() {
    return calcThumbSize(this.slider, this.thumbSize, this.focusWidth);
  }
  
  public static Dimension calcThumbSize(JSlider slider, Dimension thumbSize, int focusWidth) {
    int fw = UIScale.scale(focusWidth);
    int w = UIScale.scale(thumbSize.width) + fw + fw;
    int h = UIScale.scale(thumbSize.height) + fw + fw;
    return (slider.getOrientation() == 0) ? new Dimension(w, h) : new Dimension(h, w);
  }



  
  public void paint(Graphics g, JComponent c) {
    this.oldRenderingHints = FlatUIUtils.setRenderingHints(g);

















    
    super.paint(g, c);
    
    FlatUIUtils.resetRenderingHints(g, this.oldRenderingHints);
    this.oldRenderingHints = null;
  }

  
  public void paintLabels(Graphics g) {
    FlatUIUtils.runWithoutRenderingHints(g, this.oldRenderingHints, () -> super.paintLabels(g));
  }



  
  public void paintFocus(Graphics g) {}


  
  public void paintTrack(Graphics g) {
    RoundRectangle2D track;
    boolean enabled = this.slider.isEnabled();
    float tw = UIScale.scale(this.trackWidth);
    float arc = tw;
    
    RoundRectangle2D coloredTrack = null;
    
    if (this.slider.getOrientation() == 0)
    { float y = this.trackRect.y + (this.trackRect.height - tw) / 2.0F;
      if (enabled && isRoundThumb()) {
        if (this.slider.getComponentOrientation().isLeftToRight()) {
          int cw = this.thumbRect.x + this.thumbRect.width / 2 - this.trackRect.x;
          coloredTrack = new RoundRectangle2D.Float(this.trackRect.x, y, cw, tw, arc, arc);
          track = new RoundRectangle2D.Float((this.trackRect.x + cw), y, (this.trackRect.width - cw), tw, arc, arc);
        } else {
          int cw = this.trackRect.x + this.trackRect.width - this.thumbRect.x - this.thumbRect.width / 2;
          coloredTrack = new RoundRectangle2D.Float((this.trackRect.x + this.trackRect.width - cw), y, cw, tw, arc, arc);
          track = new RoundRectangle2D.Float(this.trackRect.x, y, (this.trackRect.width - cw), tw, arc, arc);
        } 
      } else {
        track = new RoundRectangle2D.Float(this.trackRect.x, y, this.trackRect.width, tw, arc, arc);
      }  }
    else { float x = this.trackRect.x + (this.trackRect.width - tw) / 2.0F;
      if (enabled && isRoundThumb()) {
        int ch = this.thumbRect.y + this.thumbRect.height / 2 - this.trackRect.y;
        track = new RoundRectangle2D.Float(x, this.trackRect.y, tw, ch, arc, arc);
        coloredTrack = new RoundRectangle2D.Float(x, (this.trackRect.y + ch), tw, (this.trackRect.height - ch), arc, arc);
      } else {
        track = new RoundRectangle2D.Float(x, this.trackRect.y, tw, this.trackRect.height, arc, arc);
      }  }
    
    if (coloredTrack != null) {
      if (this.slider.getInverted()) {
        RoundRectangle2D temp = track;
        track = coloredTrack;
        coloredTrack = temp;
      } 
      
      g.setColor(getTrackValueColor());
      ((Graphics2D)g).fill(coloredTrack);
    } 
    
    g.setColor(enabled ? getTrackColor() : this.disabledTrackColor);
    ((Graphics2D)g).fill(track);
  }

  
  public void paintThumb(Graphics g) {
    Color thumbColor = getThumbColor();
    Color color = stateColor(this.slider, this.thumbHover, this.thumbPressed, thumbColor, this.disabledThumbColor, (Color)null, this.hoverThumbColor, this.pressedThumbColor);
    
    color = FlatUIUtils.deriveColor(color, thumbColor);
    
    Color foreground = this.slider.getForeground();
    
    Color borderColor = (this.thumbBorderColor != null && foreground == this.defaultForeground) ? stateColor(this.slider, false, false, this.thumbBorderColor, this.disabledThumbBorderColor, this.focusedThumbBorderColor, (Color)null, (Color)null) : null;

    
    Color focusedColor = FlatUIUtils.deriveColor(this.focusedColor, (foreground != this.defaultForeground) ? foreground : this.focusBaseColor);

    
    paintThumb(g, this.slider, this.thumbRect, isRoundThumb(), color, borderColor, focusedColor, this.focusWidth);
  }


  
  public static void paintThumb(Graphics g, JSlider slider, Rectangle thumbRect, boolean roundThumb, Color thumbColor, Color thumbBorderColor, Color focusedColor, int focusWidth) {
    double systemScaleFactor = UIScale.getSystemScaleFactor((Graphics2D)g);
    if (systemScaleFactor != 1.0D && systemScaleFactor != 2.0D) {
      
      HiDPIUtils.paintAtScale1x((Graphics2D)g, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, (g2d, x2, y2, width2, height2, scaleFactor) -> paintThumbImpl(g, slider, x2, y2, width2, height2, roundThumb, thumbColor, thumbBorderColor, focusedColor, (float)(focusWidth * scaleFactor)));


      
      return;
    } 


    
    paintThumbImpl(g, slider, thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, roundThumb, thumbColor, thumbBorderColor, focusedColor, focusWidth);
  }




  
  private static void paintThumbImpl(Graphics g, JSlider slider, int x, int y, int width, int height, boolean roundThumb, Color thumbColor, Color thumbBorderColor, Color focusedColor, float focusWidth) {
    int fw = Math.round(UIScale.scale(focusWidth));
    int tx = x + fw;
    int ty = y + fw;
    int tw = width - fw - fw;
    int th = height - fw - fw;
    boolean focused = FlatUIUtils.isPermanentFocusOwner(slider);
    
    if (roundThumb) {
      
      if (focused) {
        g.setColor(focusedColor);
        ((Graphics2D)g).fill(createRoundThumbShape(x, y, width, height));
      } 
      
      if (thumbBorderColor != null) {
        
        g.setColor(thumbBorderColor);
        ((Graphics2D)g).fill(createRoundThumbShape(tx, ty, tw, th));

        
        float lw = UIScale.scale(1.0F);
        g.setColor(thumbColor);
        ((Graphics2D)g).fill(createRoundThumbShape(tx + lw, ty + lw, tw - lw - lw, th - lw - lw));
      }
      else {
        
        g.setColor(thumbColor);
        ((Graphics2D)g).fill(createRoundThumbShape(tx, ty, tw, th));
      } 
    } else {
      Graphics2D g2 = (Graphics2D)g.create();
      try {
        g2.translate(x, y);
        if (slider.getOrientation() == 1) {
          if (slider.getComponentOrientation().isLeftToRight()) {
            g2.translate(0, height);
            g2.rotate(Math.toRadians(270.0D));
          } else {
            g2.translate(width, 0);
            g2.rotate(Math.toRadians(90.0D));
          } 

          
          int temp = tw;
          tw = th;
          th = temp;
        } 

        
        if (focused) {
          g2.setColor(focusedColor);
          g2.fill(createDirectionalThumbShape(0.0F, 0.0F, (tw + fw + fw), (th + fw + fw) + fw * 0.4142F, fw));
        } 

        
        if (thumbBorderColor != null) {
          
          g2.setColor(thumbBorderColor);
          g2.fill(createDirectionalThumbShape(fw, fw, tw, th, 0.0F));

          
          float lw = UIScale.scale(1.0F);
          g2.setColor(thumbColor);
          g2.fill(createDirectionalThumbShape(fw + lw, fw + lw, tw - lw - lw, th - lw - lw - lw * 0.4142F, 0.0F));
        }
        else {
          
          g2.setColor(thumbColor);
          g2.fill(createDirectionalThumbShape(fw, fw, tw, th, 0.0F));
        } 
      } finally {
        g2.dispose();
      } 
    } 
  }
  
  public static Shape createRoundThumbShape(float x, float y, float w, float h) {
    if (w == h) {
      return new Ellipse2D.Float(x, y, w, h);
    }
    float arc = Math.min(w, h);
    return new RoundRectangle2D.Float(x, y, w, h, arc, arc);
  }

  
  public static Shape createDirectionalThumbShape(float x, float y, float w, float h, float arc) {
    float wh = w / 2.0F;
    
    Path2D path = new Path2D.Float();
    path.moveTo((x + wh), (y + h));
    path.lineTo(x, (y + h - wh));
    path.lineTo(x, (y + arc));
    path.quadTo(x, y, (x + arc), y);
    path.lineTo((x + w - arc), y);
    path.quadTo((x + w), y, (x + w), (y + arc));
    path.lineTo((x + w), (y + h - wh));
    path.closePath();
    
    return path;
  }
  
  protected Color getTrackValueColor() {
    Color foreground = this.slider.getForeground();
    return (foreground != this.defaultForeground) ? foreground : this.trackValueColor;
  }
  
  protected Color getTrackColor() {
    Color backround = this.slider.getBackground();
    return (backround != this.defaultBackground) ? backround : this.trackColor;
  }
  
  protected Color getThumbColor() {
    Color foreground = this.slider.getForeground();
    return (foreground != this.defaultForeground) ? foreground : this.thumbColor;
  }


  
  public static Color stateColor(JSlider slider, boolean hover, boolean pressed, Color enabledColor, Color disabledColor, Color focusedColor, Color hoverColor, Color pressedColor) {
    if (disabledColor != null && !slider.isEnabled())
      return disabledColor; 
    if (pressedColor != null && pressed)
      return pressedColor; 
    if (hoverColor != null && hover)
      return hoverColor; 
    if (focusedColor != null && FlatUIUtils.isPermanentFocusOwner(slider))
      return focusedColor; 
    return enabledColor;
  }
  
  protected boolean isRoundThumb() {
    return (!this.slider.getPaintTicks() && !this.slider.getPaintLabels());
  }

  
  public void setThumbLocation(int x, int y) {
    if (!isRoundThumb()) {



      
      Rectangle r = new Rectangle(this.thumbRect);
      this.thumbRect.setLocation(x, y);
      SwingUtilities.computeUnion(this.thumbRect.x, this.thumbRect.y, this.thumbRect.width, this.thumbRect.height, r);

      
      int extra = (int)Math.ceil((UIScale.scale(this.focusWidth) * 0.4142F));
      if (this.slider.getOrientation() == 0) {
        r.height += extra;
      } else {
        r.width += extra;
        if (!this.slider.getComponentOrientation().isLeftToRight()) {
          r.x -= extra;
        }
      } 
      this.slider.repaint(r);
    } else {
      super.setThumbLocation(x, y);
    } 
  }


  
  protected class FlatTrackListener
    extends BasicSliderUI.TrackListener
  {
    public void mouseEntered(MouseEvent e) {
      setThumbHover(isOverThumb(e));
      super.mouseEntered(e);
    }

    
    public void mouseExited(MouseEvent e) {
      setThumbHover(false);
      super.mouseExited(e);
    }

    
    public void mouseMoved(MouseEvent e) {
      setThumbHover(isOverThumb(e));
      super.mouseMoved(e);
    }

    
    public void mousePressed(MouseEvent e) {
      setThumbPressed(isOverThumb(e));
      
      if (!FlatSliderUI.this.slider.isEnabled()) {
        return;
      }
      
      if (UIManager.getBoolean("Slider.scrollOnTrackClick")) {
        super.mousePressed(e);

        
        return;
      } 
      
      int x = e.getX();
      int y = e.getY();

      
      FlatSliderUI.this.calculateGeometry();
      if (FlatSliderUI.this.thumbRect.contains(x, y)) {
        super.mousePressed(e);
        
        return;
      } 
      if (UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag") && 
        !SwingUtilities.isLeftMouseButton(e)) {
        return;
      }
      
      int tx = FlatSliderUI.this.thumbRect.x + FlatSliderUI.this.thumbRect.width / 2 - x;
      int ty = FlatSliderUI.this.thumbRect.y + FlatSliderUI.this.thumbRect.height / 2 - y;
      e.translatePoint(tx, ty);

      
      super.mousePressed(e);

      
      e.translatePoint(-tx, -ty);

      
      mouseDragged(e);
      
      setThumbPressed(true);
    }

    
    public void mouseReleased(MouseEvent e) {
      setThumbPressed(false);
      super.mouseReleased(e);
    }

    
    public void mouseDragged(MouseEvent e) {
      super.mouseDragged(e);
      
      if (FlatSliderUI.this.isDragging() && FlatSliderUI.this
        .slider.getSnapToTicks() && FlatSliderUI.this
        .slider.isEnabled() && 
        !UIManager.getBoolean("Slider.snapToTicksOnReleased")) {
        
        FlatSliderUI.this.calculateThumbLocation();
        FlatSliderUI.this.slider.repaint();
      } 
    }
    
    protected void setThumbHover(boolean hover) {
      if (hover != FlatSliderUI.this.thumbHover) {
        FlatSliderUI.this.thumbHover = hover;
        FlatSliderUI.this.slider.repaint(FlatSliderUI.this.thumbRect);
      } 
    }
    
    protected void setThumbPressed(boolean pressed) {
      if (pressed != FlatSliderUI.this.thumbPressed) {
        FlatSliderUI.this.thumbPressed = pressed;
        FlatSliderUI.this.slider.repaint(FlatSliderUI.this.thumbRect);
      } 
    }
    
    protected boolean isOverThumb(MouseEvent e) {
      return (e != null && FlatSliderUI.this.slider.isEnabled() && FlatSliderUI.this.thumbRect.contains(e.getX(), e.getY()));
    }
  }
}
