package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;






















































public class FlatScrollBarUI
  extends BasicScrollBarUI
{
  protected Insets trackInsets;
  protected Insets thumbInsets;
  protected int trackArc;
  protected int thumbArc;
  protected Color hoverTrackColor;
  protected Color hoverThumbColor;
  protected boolean hoverThumbWithTrack;
  protected Color pressedTrackColor;
  protected Color pressedThumbColor;
  protected boolean pressedThumbWithTrack;
  protected boolean showButtons;
  protected String arrowType;
  protected Color buttonArrowColor;
  protected Color buttonDisabledArrowColor;
  protected Color hoverButtonBackground;
  protected Color pressedButtonBackground;
  private MouseAdapter hoverListener;
  protected boolean hoverTrack;
  protected boolean hoverThumb;
  private static boolean isPressed;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatScrollBarUI();
  }

  
  protected void installListeners() {
    super.installListeners();
    
    this.hoverListener = new ScrollBarHoverListener();
    this.scrollbar.addMouseListener(this.hoverListener);
    this.scrollbar.addMouseMotionListener(this.hoverListener);
  }

  
  protected void uninstallListeners() {
    super.uninstallListeners();
    
    this.scrollbar.removeMouseListener(this.hoverListener);
    this.scrollbar.removeMouseMotionListener(this.hoverListener);
    this.hoverListener = null;
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    this.trackInsets = UIManager.getInsets("ScrollBar.trackInsets");
    this.thumbInsets = UIManager.getInsets("ScrollBar.thumbInsets");
    this.trackArc = UIManager.getInt("ScrollBar.trackArc");
    this.thumbArc = UIManager.getInt("ScrollBar.thumbArc");
    this.hoverTrackColor = UIManager.getColor("ScrollBar.hoverTrackColor");
    this.hoverThumbColor = UIManager.getColor("ScrollBar.hoverThumbColor");
    this.hoverThumbWithTrack = UIManager.getBoolean("ScrollBar.hoverThumbWithTrack");
    this.pressedTrackColor = UIManager.getColor("ScrollBar.pressedTrackColor");
    this.pressedThumbColor = UIManager.getColor("ScrollBar.pressedThumbColor");
    this.pressedThumbWithTrack = UIManager.getBoolean("ScrollBar.pressedThumbWithTrack");
    
    this.showButtons = UIManager.getBoolean("ScrollBar.showButtons");
    this.arrowType = UIManager.getString("Component.arrowType");
    this.buttonArrowColor = UIManager.getColor("ScrollBar.buttonArrowColor");
    this.buttonDisabledArrowColor = UIManager.getColor("ScrollBar.buttonDisabledArrowColor");
    this.hoverButtonBackground = UIManager.getColor("ScrollBar.hoverButtonBackground");
    this.pressedButtonBackground = UIManager.getColor("ScrollBar.pressedButtonBackground");

    
    if (this.trackInsets == null)
      this.trackInsets = new Insets(0, 0, 0, 0); 
    if (this.thumbInsets == null) {
      this.thumbInsets = new Insets(0, 0, 0, 0);
    }
  }
  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.trackInsets = null;
    this.thumbInsets = null;
    this.hoverTrackColor = null;
    this.hoverThumbColor = null;
    this.pressedTrackColor = null;
    this.pressedThumbColor = null;
    
    this.buttonArrowColor = null;
    this.buttonDisabledArrowColor = null;
    this.hoverButtonBackground = null;
    this.pressedButtonBackground = null;
  }

  
  protected PropertyChangeListener createPropertyChangeListener() {
    return new BasicScrollBarUI.PropertyChangeHandler() {
        public void propertyChange(PropertyChangeEvent e) {
          InputMap inputMap;
          super.propertyChange(e);
          
          switch (e.getPropertyName()) {
            case "JScrollBar.showButtons":
              FlatScrollBarUI.this.scrollbar.revalidate();
              FlatScrollBarUI.this.scrollbar.repaint();
              break;

            
            case "componentOrientation":
              inputMap = (InputMap)UIManager.get("ScrollBar.ancestorInputMap");
              if (!FlatScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
                InputMap rtlInputMap = (InputMap)UIManager.get("ScrollBar.ancestorInputMap.RightToLeft");
                if (rtlInputMap != null) {
                  rtlInputMap.setParent(inputMap);
                  inputMap = rtlInputMap;
                } 
              } 
              SwingUtilities.replaceUIInputMap(FlatScrollBarUI.this.scrollbar, 1, inputMap);
              break;
          } 
        }
      };
  }

  
  public Dimension getPreferredSize(JComponent c) {
    return UIScale.scale(super.getPreferredSize(c));
  }

  
  protected JButton createDecreaseButton(int orientation) {
    return new FlatScrollBarButton(orientation);
  }

  
  protected JButton createIncreaseButton(int orientation) {
    return new FlatScrollBarButton(orientation);
  }
  
  protected boolean isShowButtons() {
    Object showButtons = this.scrollbar.getClientProperty("JScrollBar.showButtons");
    if (showButtons == null && this.scrollbar.getParent() instanceof JScrollPane)
      showButtons = ((JScrollPane)this.scrollbar.getParent()).getClientProperty("JScrollBar.showButtons"); 
    return (showButtons != null) ? Objects.equals(showButtons, Boolean.valueOf(true)) : this.showButtons;
  }

  
  public void paint(Graphics g, JComponent c) {
    Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g);
    super.paint(g, c);
    FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
  }

  
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    g.setColor(getTrackColor(c, this.hoverTrack, (isPressed && this.hoverTrack && !this.hoverThumb)));
    paintTrackOrThumb(g, c, trackBounds, this.trackInsets, this.trackArc);
  }

  
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    if (thumbBounds.isEmpty() || !this.scrollbar.isEnabled()) {
      return;
    }
    g.setColor(getThumbColor(c, (this.hoverThumb || (this.hoverThumbWithTrack && this.hoverTrack)), (isPressed && (this.hoverThumb || (this.pressedThumbWithTrack && this.hoverTrack)))));
    
    paintTrackOrThumb(g, c, thumbBounds, this.thumbInsets, this.thumbArc);
  }

  
  protected void paintTrackOrThumb(Graphics g, JComponent c, Rectangle bounds, Insets insets, int arc) {
    if (this.scrollbar.getOrientation() == 0) {
      insets = new Insets(insets.right, insets.top, insets.left, insets.bottom);
    }
    
    bounds = FlatUIUtils.subtractInsets(bounds, UIScale.scale(insets));
    
    if (arc <= 0) {
      
      g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    } else {
      
      arc = Math.min(UIScale.scale(arc), Math.min(bounds.width, bounds.height));
      g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, arc, arc);
    } 
  }


  
  protected void paintDecreaseHighlight(Graphics g) {}


  
  protected void paintIncreaseHighlight(Graphics g) {}


  
  protected Color getTrackColor(JComponent c, boolean hover, boolean pressed) {
    Color trackColor = FlatUIUtils.deriveColor(this.trackColor, c.getBackground());
    return (pressed && this.pressedTrackColor != null) ? 
      FlatUIUtils.deriveColor(this.pressedTrackColor, trackColor) : ((hover && this.hoverTrackColor != null) ? 
      
      FlatUIUtils.deriveColor(this.hoverTrackColor, trackColor) : trackColor);
  }

  
  protected Color getThumbColor(JComponent c, boolean hover, boolean pressed) {
    Color trackColor = FlatUIUtils.deriveColor(this.trackColor, c.getBackground());
    Color thumbColor = FlatUIUtils.deriveColor(this.thumbColor, trackColor);
    return (pressed && this.pressedThumbColor != null) ? 
      FlatUIUtils.deriveColor(this.pressedThumbColor, thumbColor) : ((hover && this.hoverThumbColor != null) ? 
      
      FlatUIUtils.deriveColor(this.hoverThumbColor, thumbColor) : thumbColor);
  }


  
  protected Dimension getMinimumThumbSize() {
    return UIScale.scale(FlatUIUtils.addInsets(super.getMinimumThumbSize(), this.thumbInsets));
  }

  
  protected Dimension getMaximumThumbSize() {
    return UIScale.scale(FlatUIUtils.addInsets(super.getMaximumThumbSize(), this.thumbInsets));
  }


  
  private class ScrollBarHoverListener
    extends MouseAdapter
  {
    private ScrollBarHoverListener() {}


    
    public void mouseExited(MouseEvent e) {
      if (!FlatScrollBarUI.isPressed) {
        FlatScrollBarUI.this.hoverTrack = FlatScrollBarUI.this.hoverThumb = false;
        repaint();
      } 
    }

    
    public void mouseMoved(MouseEvent e) {
      if (!FlatScrollBarUI.isPressed) {
        update(e.getX(), e.getY());
      }
    }
    
    public void mousePressed(MouseEvent e) {
      FlatScrollBarUI.isPressed = true;
      repaint();
    }

    
    public void mouseReleased(MouseEvent e) {
      FlatScrollBarUI.isPressed = false;
      repaint();
      
      update(e.getX(), e.getY());
    }
    
    private void update(int x, int y) {
      boolean inTrack = FlatScrollBarUI.this.getTrackBounds().contains(x, y);
      boolean inThumb = FlatScrollBarUI.this.getThumbBounds().contains(x, y);
      if (inTrack != FlatScrollBarUI.this.hoverTrack || inThumb != FlatScrollBarUI.this.hoverThumb) {
        FlatScrollBarUI.this.hoverTrack = inTrack;
        FlatScrollBarUI.this.hoverThumb = inThumb;
        repaint();
      } 
    }
    
    private void repaint() {
      if (FlatScrollBarUI.this.scrollbar.isEnabled()) {
        FlatScrollBarUI.this.scrollbar.repaint();
      }
    }
  }

  
  protected class FlatScrollBarButton
    extends FlatArrowButton
  {
    protected FlatScrollBarButton(int direction) {
      this(direction, FlatScrollBarUI.this.arrowType, FlatScrollBarUI.this.buttonArrowColor, FlatScrollBarUI.this.buttonDisabledArrowColor, (Color)null, FlatScrollBarUI.this.hoverButtonBackground, (Color)null, FlatScrollBarUI.this.pressedButtonBackground);
    }



    
    protected FlatScrollBarButton(int direction, String type, Color foreground, Color disabledForeground, Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground) {
      super(direction, type, foreground, disabledForeground, hoverForeground, hoverBackground, pressedForeground, pressedBackground);

      
      setArrowWidth(6);
      setFocusable(false);
      setRequestFocusEnabled(false);
    }

    
    protected Color deriveBackground(Color background) {
      return FlatUIUtils.deriveColor(background, FlatScrollBarUI.this.scrollbar.getBackground());
    }

    
    public Dimension getPreferredSize() {
      if (FlatScrollBarUI.this.isShowButtons()) {
        int w = UIScale.scale(FlatScrollBarUI.this.scrollBarWidth);
        return new Dimension(w, w);
      } 
      return new Dimension();
    }

    
    public Dimension getMinimumSize() {
      return FlatScrollBarUI.this.isShowButtons() ? super.getMinimumSize() : new Dimension();
    }

    
    public Dimension getMaximumSize() {
      return FlatScrollBarUI.this.isShowButtons() ? super.getMaximumSize() : new Dimension();
    }
  }
}
