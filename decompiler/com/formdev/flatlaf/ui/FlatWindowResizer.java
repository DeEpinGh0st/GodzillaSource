package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Supplier;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.UIManager;


























public abstract class FlatWindowResizer
  implements PropertyChangeListener, ComponentListener
{
  protected static final Integer WINDOW_RESIZER_LAYER = Integer.valueOf(JLayeredPane.DRAG_LAYER.intValue() + 1);
  
  protected final JComponent resizeComp;
  
  protected final int borderDragThickness = FlatUIUtils.getUIInt("RootPane.borderDragThickness", 5);
  protected final int cornerDragWidth = FlatUIUtils.getUIInt("RootPane.cornerDragWidth", 16);
  protected final boolean honorFrameMinimumSizeOnResize = UIManager.getBoolean("RootPane.honorFrameMinimumSizeOnResize");
  protected final boolean honorDialogMinimumSizeOnResize = UIManager.getBoolean("RootPane.honorDialogMinimumSizeOnResize");
  
  protected final DragBorderComponent topDragComp;
  protected final DragBorderComponent bottomDragComp;
  protected final DragBorderComponent leftDragComp;
  protected final DragBorderComponent rightDragComp;
  
  protected FlatWindowResizer(JComponent resizeComp) {
    this.resizeComp = resizeComp;
    
    this.topDragComp = createDragBorderComponent(6, 8, 7);
    this.bottomDragComp = createDragBorderComponent(4, 9, 5);
    this.leftDragComp = createDragBorderComponent(6, 10, 4);
    this.rightDragComp = createDragBorderComponent(7, 11, 5);
    
    Container cont = (resizeComp instanceof JRootPane) ? ((JRootPane)resizeComp).getLayeredPane() : resizeComp;
    Object cons = (cont instanceof JLayeredPane) ? WINDOW_RESIZER_LAYER : null;
    cont.add(this.topDragComp, cons, 0);
    cont.add(this.bottomDragComp, cons, 1);
    cont.add(this.leftDragComp, cons, 2);
    cont.add(this.rightDragComp, cons, 3);
    
    resizeComp.addComponentListener(this);
    resizeComp.addPropertyChangeListener("ancestor", this);
    
    if (resizeComp.isDisplayable())
      addNotify(); 
  }
  
  protected DragBorderComponent createDragBorderComponent(int leadingResizeDir, int centerResizeDir, int trailingResizeDir) {
    return new DragBorderComponent(leadingResizeDir, centerResizeDir, trailingResizeDir);
  }
  
  public void uninstall() {
    removeNotify();
    
    this.resizeComp.removeComponentListener(this);
    this.resizeComp.removePropertyChangeListener("ancestor", this);
    
    Container cont = this.topDragComp.getParent();
    cont.remove(this.topDragComp);
    cont.remove(this.bottomDragComp);
    cont.remove(this.leftDragComp);
    cont.remove(this.rightDragComp);
  }
  
  public void doLayout() {
    if (!this.topDragComp.isVisible()) {
      return;
    }
    int x = 0;
    int y = 0;
    int width = this.resizeComp.getWidth();
    int height = this.resizeComp.getHeight();
    if (width == 0 || height == 0) {
      return;
    }
    Insets resizeInsets = getResizeInsets();
    int thickness = UIScale.scale(this.borderDragThickness);
    int topThickness = Math.max(resizeInsets.top, thickness);
    int bottomThickness = Math.max(resizeInsets.bottom, thickness);
    int leftThickness = Math.max(resizeInsets.left, thickness);
    int rightThickness = Math.max(resizeInsets.right, thickness);
    int y2 = y + topThickness;
    int height2 = height - topThickness - bottomThickness;

    
    this.topDragComp.setBounds(x, y, width, topThickness);
    this.bottomDragComp.setBounds(x, y + height - bottomThickness, width, bottomThickness);
    this.leftDragComp.setBounds(x, y2, leftThickness, height2);
    this.rightDragComp.setBounds(x + width - rightThickness, y2, rightThickness, height2);

    
    int cornerDelta = UIScale.scale(this.cornerDragWidth - this.borderDragThickness);
    this.topDragComp.setCornerDragWidths(leftThickness + cornerDelta, rightThickness + cornerDelta);
    this.bottomDragComp.setCornerDragWidths(leftThickness + cornerDelta, rightThickness + cornerDelta);
    this.leftDragComp.setCornerDragWidths(cornerDelta, cornerDelta);
    this.rightDragComp.setCornerDragWidths(cornerDelta, cornerDelta);
  }
  
  protected Insets getResizeInsets() {
    return new Insets(0, 0, 0, 0);
  }
  
  protected void addNotify() {
    updateVisibility();
  }
  
  protected void removeNotify() {
    updateVisibility();
  }
  
  protected void updateVisibility() {
    boolean visible = isWindowResizable();
    if (visible == this.topDragComp.isVisible()) {
      return;
    }
    this.topDragComp.setVisible(visible);
    this.bottomDragComp.setVisible(visible);
    this.leftDragComp.setVisible(visible);


    
    this.rightDragComp.setEnabled(visible);
    if (visible) {
      this.rightDragComp.setVisible(true);
      doLayout();
    } else {
      this.rightDragComp.setBounds(0, 0, 1, 1);
    } 
  }
  boolean isDialog() {
    return false;
  }


  
  protected abstract boolean isWindowResizable();


  
  protected abstract Rectangle getWindowBounds();

  
  protected abstract void setWindowBounds(Rectangle paramRectangle);

  
  public void propertyChange(PropertyChangeEvent e) {
    switch (e.getPropertyName()) {
      case "ancestor":
        if (e.getNewValue() != null) {
          addNotify(); break;
        } 
        removeNotify();
        break;
      
      case "resizable":
        updateVisibility();
        break;
    } 
  }
  protected abstract boolean honorMinimumSizeOnResize();
  protected abstract Dimension getWindowMinimumSize();
  protected void beginResizing(int direction) {}
  protected void endResizing() {}
  public void componentResized(ComponentEvent e) {
    doLayout();
  }

  
  public void componentMoved(ComponentEvent e) {}

  
  public void componentShown(ComponentEvent e) {}

  
  public void componentHidden(ComponentEvent e) {}
  
  public static class WindowResizer
    extends FlatWindowResizer
    implements WindowStateListener
  {
    protected Window window;
    
    public WindowResizer(JRootPane rootPane) {
      super(rootPane);
    }

    
    protected void addNotify() {
      Container parent = this.resizeComp.getParent();
      this.window = (parent instanceof Window) ? (Window)parent : null;
      if (this.window instanceof Frame) {
        this.window.addPropertyChangeListener("resizable", this);
        this.window.addWindowStateListener(this);
      } 
      
      super.addNotify();
    }

    
    protected void removeNotify() {
      if (this.window instanceof Frame) {
        this.window.removePropertyChangeListener("resizable", this);
        this.window.removeWindowStateListener(this);
      } 
      this.window = null;
      
      super.removeNotify();
    }

    
    protected boolean isWindowResizable() {
      if (FlatUIUtils.isFullScreen(this.resizeComp))
        return false; 
      if (this.window instanceof Frame)
        return (((Frame)this.window).isResizable() && (((Frame)this.window).getExtendedState() & 0x6) == 0); 
      if (this.window instanceof Dialog)
        return ((Dialog)this.window).isResizable(); 
      return false;
    }

    
    protected Rectangle getWindowBounds() {
      return this.window.getBounds();
    }

    
    protected void setWindowBounds(Rectangle r) {
      this.window.setBounds(r);

      
      doLayout();
      
      if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
        this.window.validate();
        this.resizeComp.repaint();
      } 
    }

    
    protected boolean honorMinimumSizeOnResize() {
      return ((this.honorFrameMinimumSizeOnResize && this.window instanceof Frame) || (this.honorDialogMinimumSizeOnResize && this.window instanceof Dialog));
    }



    
    protected Dimension getWindowMinimumSize() {
      return this.window.getMinimumSize();
    }

    
    boolean isDialog() {
      return this.window instanceof Dialog;
    }

    
    public void windowStateChanged(WindowEvent e) {
      updateVisibility();
    }
  }



  
  public static class InternalFrameResizer
    extends FlatWindowResizer
  {
    protected final Supplier<DesktopManager> desktopManager;


    
    public InternalFrameResizer(JInternalFrame frame, Supplier<DesktopManager> desktopManager) {
      super(frame);
      this.desktopManager = desktopManager;
      
      frame.addPropertyChangeListener("resizable", this);
    }

    
    public void uninstall() {
      getFrame().removePropertyChangeListener("resizable", this);
      
      super.uninstall();
    }
    
    private JInternalFrame getFrame() {
      return (JInternalFrame)this.resizeComp;
    }

    
    protected Insets getResizeInsets() {
      return getFrame().getInsets();
    }

    
    protected boolean isWindowResizable() {
      return getFrame().isResizable();
    }

    
    protected Rectangle getWindowBounds() {
      return getFrame().getBounds();
    }

    
    protected void setWindowBounds(Rectangle r) {
      ((DesktopManager)this.desktopManager.get()).resizeFrame(getFrame(), r.x, r.y, r.width, r.height);
    }

    
    protected boolean honorMinimumSizeOnResize() {
      return true;
    }

    
    protected Dimension getWindowMinimumSize() {
      return getFrame().getMinimumSize();
    }

    
    protected void beginResizing(int direction) {
      ((DesktopManager)this.desktopManager.get()).beginResizingFrame(getFrame(), direction);
    }

    
    protected void endResizing() {
      ((DesktopManager)this.desktopManager.get()).endResizingFrame(getFrame());
    }
  }

  
  protected class DragBorderComponent
    extends JComponent
    implements MouseListener, MouseMotionListener
  {
    private final int leadingResizeDir;
    
    private final int centerResizeDir;
    
    private final int trailingResizeDir;
    private int resizeDir = -1;
    
    private int leadingCornerDragWidth;
    
    private int trailingCornerDragWidth;
    
    private int dragLeftOffset;
    private int dragRightOffset;
    private int dragTopOffset;
    private int dragBottomOffset;
    
    protected DragBorderComponent(int leadingResizeDir, int centerResizeDir, int trailingResizeDir) {
      this.leadingResizeDir = leadingResizeDir;
      this.centerResizeDir = centerResizeDir;
      this.trailingResizeDir = trailingResizeDir;
      
      setResizeDir(centerResizeDir);
      setVisible(false);
      
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    void setCornerDragWidths(int leading, int trailing) {
      this.leadingCornerDragWidth = leading;
      this.trailingCornerDragWidth = trailing;
    }
    
    protected void setResizeDir(int resizeDir) {
      if (this.resizeDir == resizeDir)
        return; 
      this.resizeDir = resizeDir;
      
      setCursor(Cursor.getPredefinedCursor(resizeDir));
    }

    
    public Dimension getPreferredSize() {
      int thickness = UIScale.scale(FlatWindowResizer.this.borderDragThickness);
      return new Dimension(thickness, thickness);
    }

    
    protected void paintComponent(Graphics g) {
      paintChildren(g);


      
      FlatWindowResizer.this.updateVisibility();
    }











    
    public void mouseClicked(MouseEvent e) {}










    
    public void mousePressed(MouseEvent e) {
      if (!FlatWindowResizer.this.isWindowResizable()) {
        return;
      }
      int xOnScreen = e.getXOnScreen();
      int yOnScreen = e.getYOnScreen();
      Rectangle windowBounds = FlatWindowResizer.this.getWindowBounds();

      
      this.dragLeftOffset = xOnScreen - windowBounds.x;
      this.dragTopOffset = yOnScreen - windowBounds.y;
      this.dragRightOffset = windowBounds.x + windowBounds.width - xOnScreen;
      this.dragBottomOffset = windowBounds.y + windowBounds.height - yOnScreen;
      
      int direction = 0;
      switch (this.resizeDir) { case 8:
          direction = 1; break;
        case 9: direction = 5; break;
        case 10: direction = 7; break;
        case 11: direction = 3; break;
        case 6: direction = 8; break;
        case 7: direction = 2; break;
        case 4: direction = 6; break;
        case 5: direction = 4; break; }
      
      FlatWindowResizer.this.beginResizing(direction);
    }

    
    public void mouseReleased(MouseEvent e) {
      if (!FlatWindowResizer.this.isWindowResizable()) {
        return;
      }
      this.dragLeftOffset = this.dragRightOffset = this.dragTopOffset = this.dragBottomOffset = 0;
      
      FlatWindowResizer.this.endResizing();
    }
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseMoved(MouseEvent e) {
      boolean topOrBottom = (this.centerResizeDir == 8 || this.centerResizeDir == 9);
      int xy = topOrBottom ? e.getX() : e.getY();
      int wh = topOrBottom ? getWidth() : getHeight();
      
      setResizeDir((xy <= this.leadingCornerDragWidth) ? this.leadingResizeDir : ((xy >= wh - this.trailingCornerDragWidth) ? this.trailingResizeDir : this.centerResizeDir));
    }





    
    public void mouseDragged(MouseEvent e) {
      if (!FlatWindowResizer.this.isWindowResizable()) {
        return;
      }
      int xOnScreen = e.getXOnScreen();
      int yOnScreen = e.getYOnScreen();






      
      Rectangle oldBounds = FlatWindowResizer.this.getWindowBounds();
      Rectangle newBounds = new Rectangle(oldBounds);



      
      if (this.resizeDir == 8 || this.resizeDir == 6 || this.resizeDir == 7) {
        newBounds.y = yOnScreen - this.dragTopOffset;
        newBounds.height += oldBounds.y - newBounds.y;
      } 

      
      if (this.resizeDir == 9 || this.resizeDir == 4 || this.resizeDir == 5) {
        newBounds.height = yOnScreen + this.dragBottomOffset - newBounds.y;
      }
      
      if (this.resizeDir == 10 || this.resizeDir == 6 || this.resizeDir == 4) {
        newBounds.x = xOnScreen - this.dragLeftOffset;
        newBounds.width += oldBounds.x - newBounds.x;
      } 

      
      if (this.resizeDir == 11 || this.resizeDir == 7 || this.resizeDir == 5) {
        newBounds.width = xOnScreen + this.dragRightOffset - newBounds.x;
      }
      
      Dimension minimumSize = FlatWindowResizer.this.honorMinimumSizeOnResize() ? FlatWindowResizer.this.getWindowMinimumSize() : null;
      if (minimumSize == null)
        minimumSize = UIScale.scale(new Dimension(150, 50)); 
      if (newBounds.width < minimumSize.width) {
        if (newBounds.x != oldBounds.x)
          newBounds.x -= minimumSize.width - newBounds.width; 
        newBounds.width = minimumSize.width;
      } 
      if (newBounds.height < minimumSize.height) {
        if (newBounds.y != oldBounds.y)
          newBounds.y -= minimumSize.height - newBounds.height; 
        newBounds.height = minimumSize.height;
      } 

      
      if (!newBounds.equals(oldBounds))
        FlatWindowResizer.this.setWindowBounds(newBounds); 
    }
  }
}
