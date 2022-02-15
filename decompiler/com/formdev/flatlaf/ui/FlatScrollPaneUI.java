package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;






































public class FlatScrollPaneUI
  extends BasicScrollPaneUI
{
  private Handler handler;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatScrollPaneUI();
  }

  
  public void installUI(JComponent c) {
    super.installUI(c);
    
    int focusWidth = UIManager.getInt("Component.focusWidth");
    LookAndFeel.installProperty(c, "opaque", Boolean.valueOf((focusWidth == 0)));
    
    MigLayoutVisualPadding.install(this.scrollpane);
  }

  
  public void uninstallUI(JComponent c) {
    MigLayoutVisualPadding.uninstall(this.scrollpane);
    
    super.uninstallUI(c);
  }

  
  protected void installListeners(JScrollPane c) {
    super.installListeners(c);
    
    addViewportListeners(this.scrollpane.getViewport());
  }

  
  protected void uninstallListeners(JComponent c) {
    super.uninstallListeners(c);
    
    removeViewportListeners(this.scrollpane.getViewport());
    
    this.handler = null;
  }

  
  protected MouseWheelListener createMouseWheelListener() {
    return new BasicScrollPaneUI.MouseWheelHandler()
      {
        public void mouseWheelMoved(MouseWheelEvent e) {
          if (FlatScrollPaneUI.this.isSmoothScrollingEnabled() && FlatScrollPaneUI.this
            .scrollpane.isWheelScrollingEnabled() && e
            .getScrollType() == 0 && e
            .getPreciseWheelRotation() != 0.0D && e
            .getPreciseWheelRotation() != e.getWheelRotation()) {
            
            FlatScrollPaneUI.this.mouseWheelMovedSmooth(e);
          } else {
            super.mouseWheelMoved(e);
          } 
        }
      };
  }
  protected boolean isSmoothScrollingEnabled() {
    Object smoothScrolling = this.scrollpane.getClientProperty("JScrollPane.smoothScrolling");
    if (smoothScrolling instanceof Boolean) {
      return ((Boolean)smoothScrolling).booleanValue();
    }


    
    return UIManager.getBoolean("ScrollPane.smoothScrolling");
  }
  
  private void mouseWheelMovedSmooth(MouseWheelEvent e) {
    int unitIncrement;
    JViewport viewport = this.scrollpane.getViewport();
    if (viewport == null) {
      return;
    }
    
    JScrollBar scrollbar = this.scrollpane.getVerticalScrollBar();
    if (scrollbar == null || !scrollbar.isVisible() || e.isShiftDown()) {
      scrollbar = this.scrollpane.getHorizontalScrollBar();
      if (scrollbar == null || !scrollbar.isVisible()) {
        return;
      }
    } 
    
    e.consume();

    
    double rotation = e.getPreciseWheelRotation();


    
    int orientation = scrollbar.getOrientation();
    Component view = viewport.getView();
    if (view instanceof Scrollable) {
      Scrollable scrollable = (Scrollable)view;


      
      Rectangle visibleRect = new Rectangle(viewport.getViewSize());
      unitIncrement = scrollable.getScrollableUnitIncrement(visibleRect, orientation, 1);
      
      if (unitIncrement > 0) {


        
        if (orientation == 1) {
          visibleRect.y += unitIncrement;
          visibleRect.height -= unitIncrement;
        } else {
          visibleRect.x += unitIncrement;
          visibleRect.width -= unitIncrement;
        } 
        int unitIncrement2 = scrollable.getScrollableUnitIncrement(visibleRect, orientation, 1);
        if (unitIncrement2 > 0)
          unitIncrement = Math.min(unitIncrement, unitIncrement2); 
      } 
    } else {
      int direction = (rotation < 0.0D) ? -1 : 1;
      unitIncrement = scrollbar.getUnitIncrement(direction);
    } 



    
    int viewportWH = (orientation == 1) ? viewport.getHeight() : viewport.getWidth();



    
    int scrollIncrement = Math.min(unitIncrement * e.getScrollAmount(), viewportWH);

    
    double delta = rotation * scrollIncrement;
    int idelta = (int)Math.round(delta);




    
    if (idelta == 0) {
      if (rotation > 0.0D) {
        idelta = 1;
      } else if (rotation < 0.0D) {
        idelta = -1;
      } 
    }
    
    int value = scrollbar.getValue();
    int minValue = scrollbar.getMinimum();
    int maxValue = scrollbar.getMaximum() - scrollbar.getModel().getExtent();
    int newValue = Math.max(minValue, Math.min(value + idelta, maxValue));

    
    if (newValue != value) {
      scrollbar.setValue(newValue);
    }
  }

















  
  protected PropertyChangeListener createPropertyChangeListener() {
    return new BasicScrollPaneUI.PropertyChangeHandler() { public void propertyChange(PropertyChangeEvent e) {
          JScrollBar vsb, hsb;
          Object corner;
          super.propertyChange(e);
          
          switch (e.getPropertyName()) {
            case "JScrollBar.showButtons":
              vsb = FlatScrollPaneUI.this.scrollpane.getVerticalScrollBar();
              hsb = FlatScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
              if (vsb != null) {
                vsb.revalidate();
                vsb.repaint();
              } 
              if (hsb != null) {
                hsb.revalidate();
                hsb.repaint();
              } 
              break;

            
            case "LOWER_LEFT_CORNER":
            case "LOWER_RIGHT_CORNER":
            case "UPPER_LEFT_CORNER":
            case "UPPER_RIGHT_CORNER":
              corner = e.getNewValue();
              if (corner instanceof JButton && ((JButton)corner)
                .getBorder() instanceof FlatButtonBorder && FlatScrollPaneUI.this
                .scrollpane.getViewport() != null && FlatScrollPaneUI.this
                .scrollpane.getViewport().getView() instanceof javax.swing.JTable) {
                
                ((JButton)corner).setBorder(BorderFactory.createEmptyBorder());
                ((JButton)corner).setFocusable(false);
              } 
              break;
          } 
        } }
      ;
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }

  
  protected void updateViewport(PropertyChangeEvent e) {
    super.updateViewport(e);
    
    JViewport oldViewport = (JViewport)e.getOldValue();
    JViewport newViewport = (JViewport)e.getNewValue();
    
    removeViewportListeners(oldViewport);
    addViewportListeners(newViewport);
  }
  
  private void addViewportListeners(JViewport viewport) {
    if (viewport == null) {
      return;
    }
    viewport.addContainerListener(getHandler());
    
    Component view = viewport.getView();
    if (view != null)
      view.addFocusListener(getHandler()); 
  }
  
  private void removeViewportListeners(JViewport viewport) {
    if (viewport == null) {
      return;
    }
    viewport.removeContainerListener(getHandler());
    
    Component view = viewport.getView();
    if (view != null) {
      view.removeFocusListener(getHandler());
    }
  }
  
  public void update(Graphics g, JComponent c) {
    if (c.isOpaque()) {
      FlatUIUtils.paintParentBackground(g, c);

      
      Insets insets = c.getInsets();
      g.setColor(c.getBackground());
      g.fillRect(insets.left, insets.top, c
          .getWidth() - insets.left - insets.right, c
          .getHeight() - insets.top - insets.bottom);
    } 
    
    paint(g, c);
  }



  
  private class Handler
    implements ContainerListener, FocusListener
  {
    private Handler() {}


    
    public void componentAdded(ContainerEvent e) {
      e.getChild().addFocusListener(this);
    }

    
    public void componentRemoved(ContainerEvent e) {
      e.getChild().removeFocusListener(this);
    }

    
    public void focusGained(FocusEvent e) {
      FlatScrollPaneUI.this.scrollpane.repaint();
    }

    
    public void focusLost(FocusEvent e) {
      FlatScrollPaneUI.this.scrollpane.repaint();
    }
  }
}
