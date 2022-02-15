package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;









































public class FlatTitlePane
  extends JComponent
{
  protected final Color activeBackground = UIManager.getColor("TitlePane.background");
  protected final Color inactiveBackground = UIManager.getColor("TitlePane.inactiveBackground");
  protected final Color activeForeground = UIManager.getColor("TitlePane.foreground");
  protected final Color inactiveForeground = UIManager.getColor("TitlePane.inactiveForeground");
  protected final Color embeddedForeground = UIManager.getColor("TitlePane.embeddedForeground");
  protected final Color borderColor = UIManager.getColor("TitlePane.borderColor");
  
  protected final Insets menuBarMargins = UIManager.getInsets("TitlePane.menuBarMargins");
  protected final Dimension iconSize = UIManager.getDimension("TitlePane.iconSize");
  protected final int buttonMaximizedHeight = UIManager.getInt("TitlePane.buttonMaximizedHeight");
  
  protected final JRootPane rootPane;
  
  protected JPanel leftPanel;
  
  protected JLabel iconLabel;
  
  protected JComponent menuBarPlaceholder;
  protected JLabel titleLabel;
  protected JPanel buttonPanel;
  protected JButton iconifyButton;
  protected JButton maximizeButton;
  protected JButton restoreButton;
  protected JButton closeButton;
  protected Window window;
  private final Handler handler;
  
  public FlatTitlePane(JRootPane rootPane) {
    this.rootPane = rootPane;
    
    this.handler = createHandler();
    setBorder(createTitlePaneBorder());
    
    addSubComponents();
    activeChanged(true);
    
    addMouseListener(this.handler);
    addMouseMotionListener(this.handler);

    
    this.iconLabel.addMouseListener(this.handler);
  }
  
  protected FlatTitlePaneBorder createTitlePaneBorder() {
    return new FlatTitlePaneBorder();
  }
  
  protected Handler createHandler() {
    return new Handler();
  }
  
  protected void addSubComponents() {
    this.leftPanel = new JPanel();
    this.iconLabel = new JLabel();
    this.titleLabel = new JLabel();
    this.iconLabel.setBorder(new FlatEmptyBorder(UIManager.getInsets("TitlePane.iconMargins")));
    this.titleLabel.setBorder(new FlatEmptyBorder(UIManager.getInsets("TitlePane.titleMargins")));
    
    this.leftPanel.setLayout(new BoxLayout(this.leftPanel, 2));
    this.leftPanel.setOpaque(false);
    this.leftPanel.add(this.iconLabel);
    
    this.menuBarPlaceholder = new JComponent()
      {
        public Dimension getPreferredSize() {
          JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
          return (menuBar != null && menuBar.isVisible() && FlatTitlePane.this.isMenuBarEmbedded()) ? 
            FlatUIUtils.addInsets(menuBar.getPreferredSize(), UIScale.scale(FlatTitlePane.this.menuBarMargins)) : new Dimension();
        }
      };
    
    this.leftPanel.add(this.menuBarPlaceholder);
    
    createButtons();
    
    setLayout(new BorderLayout()
        {
          public void layoutContainer(Container target) {
            super.layoutContainer(target);


            
            Insets insets = target.getInsets();
            int width = target.getWidth() - insets.left - insets.right;
            if (FlatTitlePane.this.leftPanel.getWidth() + FlatTitlePane.this.buttonPanel.getWidth() > width) {
              int oldWidth = FlatTitlePane.this.leftPanel.getWidth();
              int newWidth = Math.max(width - FlatTitlePane.this.buttonPanel.getWidth(), 0);
              FlatTitlePane.this.leftPanel.setSize(newWidth, FlatTitlePane.this.leftPanel.getHeight());
              if (!FlatTitlePane.this.getComponentOrientation().isLeftToRight()) {
                FlatTitlePane.this.leftPanel.setLocation(FlatTitlePane.this.leftPanel.getX() + oldWidth - newWidth, FlatTitlePane.this.leftPanel.getY());
              }
            } 
          }
        });
    add(this.leftPanel, "Before");
    add(this.titleLabel, "Center");
    add(this.buttonPanel, "After");
  }
  
  protected void createButtons() {
    this.iconifyButton = createButton("TitlePane.iconifyIcon", "Iconify", e -> iconify());
    this.maximizeButton = createButton("TitlePane.maximizeIcon", "Maximize", e -> maximize());
    this.restoreButton = createButton("TitlePane.restoreIcon", "Restore", e -> restore());
    this.closeButton = createButton("TitlePane.closeIcon", "Close", e -> close());
    
    this.buttonPanel = new JPanel()
      {
        public Dimension getPreferredSize() {
          Dimension size = super.getPreferredSize();
          if (FlatTitlePane.this.buttonMaximizedHeight > 0 && FlatTitlePane.this.window instanceof Frame && (((Frame)FlatTitlePane.this.window)
            
            .getExtendedState() & 0x6) != 0)
          {
            
            size = new Dimension(size.width, Math.min(size.height, UIScale.scale(FlatTitlePane.this.buttonMaximizedHeight)));
          }
          return size;
        }
      };
    this.buttonPanel.setOpaque(false);
    this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, 2));
    if (this.rootPane.getWindowDecorationStyle() == 1) {




      
      this.restoreButton.setVisible(false);
      
      this.buttonPanel.add(this.iconifyButton);
      this.buttonPanel.add(this.maximizeButton);
      this.buttonPanel.add(this.restoreButton);
    } 
    this.buttonPanel.add(this.closeButton);
  }
  
  protected JButton createButton(String iconKey, String accessibleName, ActionListener action) {
    JButton button = new JButton(UIManager.getIcon(iconKey));
    button.setFocusable(false);
    button.setContentAreaFilled(false);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.putClientProperty("AccessibleName", accessibleName);
    button.addActionListener(action);
    return button;
  }
  
  protected void activeChanged(boolean active) {
    boolean hasEmbeddedMenuBar = (this.rootPane.getJMenuBar() != null && this.rootPane.getJMenuBar().isVisible() && isMenuBarEmbedded());
    Color background = FlatUIUtils.nonUIResource(active ? this.activeBackground : this.inactiveBackground);
    Color foreground = FlatUIUtils.nonUIResource(active ? this.activeForeground : this.inactiveForeground);
    Color titleForeground = (hasEmbeddedMenuBar && active) ? FlatUIUtils.nonUIResource(this.embeddedForeground) : foreground;
    
    setBackground(background);
    this.titleLabel.setForeground(titleForeground);
    this.iconifyButton.setForeground(foreground);
    this.maximizeButton.setForeground(foreground);
    this.restoreButton.setForeground(foreground);
    this.closeButton.setForeground(foreground);
    
    this.titleLabel.setHorizontalAlignment(hasEmbeddedMenuBar ? 0 : 10);

    
    this.iconifyButton.setBackground(background);
    this.maximizeButton.setBackground(background);
    this.restoreButton.setBackground(background);
    this.closeButton.setBackground(background);
  }
  
  protected void frameStateChanged() {
    if (this.window == null || this.rootPane.getWindowDecorationStyle() != 1) {
      return;
    }
    if (this.window instanceof Frame) {
      Frame frame = (Frame)this.window;
      boolean resizable = frame.isResizable();
      boolean maximized = ((frame.getExtendedState() & 0x6) != 0);
      
      this.iconifyButton.setVisible(true);
      this.maximizeButton.setVisible((resizable && !maximized));
      this.restoreButton.setVisible((resizable && maximized));
      
      if (maximized && this.rootPane
        .getClientProperty("_flatlaf.maximizedBoundsUpToDate") == null) {
        
        this.rootPane.putClientProperty("_flatlaf.maximizedBoundsUpToDate", (Object)null);





        
        Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
        updateMaximizedBounds();
        Rectangle newMaximizedBounds = frame.getMaximizedBounds();
        if (newMaximizedBounds != null && !newMaximizedBounds.equals(oldMaximizedBounds)) {
          int oldExtendedState = frame.getExtendedState();
          frame.setExtendedState(oldExtendedState & 0xFFFFFFF9);
          frame.setExtendedState(oldExtendedState);
        } 
      } 
    } else {
      
      this.iconifyButton.setVisible(false);
      this.maximizeButton.setVisible(false);
      this.restoreButton.setVisible(false);
      
      revalidate();
      repaint();
    } 
  }

  
  protected void updateIcon() {
    List<Image> images = this.window.getIconImages();
    if (images.isEmpty())
    {
      for (Window owner = this.window.getOwner(); owner != null; owner = owner.getOwner()) {
        images = owner.getIconImages();
        if (!images.isEmpty()) {
          break;
        }
      } 
    }
    boolean hasIcon = true;

    
    if (!images.isEmpty()) {
      this.iconLabel.setIcon(FlatTitlePaneIcon.create(images, this.iconSize));
    } else {
      
      Icon defaultIcon = UIManager.getIcon("InternalFrame.icon");
      if (defaultIcon != null && (defaultIcon.getIconWidth() == 0 || defaultIcon.getIconHeight() == 0))
        defaultIcon = null; 
      if (defaultIcon != null) {
        ScaledImageIcon scaledImageIcon; if (defaultIcon instanceof ImageIcon)
          scaledImageIcon = new ScaledImageIcon((ImageIcon)defaultIcon, this.iconSize.width, this.iconSize.height); 
        this.iconLabel.setIcon((Icon)scaledImageIcon);
      } else {
        hasIcon = false;
      } 
    } 
    
    this.iconLabel.setVisible(hasIcon);
    
    updateJBRHitTestSpotsAndTitleBarHeightLater();
  }

  
  public void addNotify() {
    super.addNotify();
    
    uninstallWindowListeners();
    
    this.window = SwingUtilities.getWindowAncestor(this);
    if (this.window != null) {
      frameStateChanged();
      activeChanged(this.window.isActive());
      updateIcon();
      this.titleLabel.setText(getWindowTitle());
      installWindowListeners();
    } 
    
    updateJBRHitTestSpotsAndTitleBarHeightLater();
  }

  
  public void removeNotify() {
    super.removeNotify();
    
    uninstallWindowListeners();
    this.window = null;
  }
  
  protected String getWindowTitle() {
    if (this.window instanceof Frame)
      return ((Frame)this.window).getTitle(); 
    if (this.window instanceof Dialog)
      return ((Dialog)this.window).getTitle(); 
    return null;
  }
  
  protected void installWindowListeners() {
    if (this.window == null) {
      return;
    }
    this.window.addPropertyChangeListener(this.handler);
    this.window.addWindowListener(this.handler);
    this.window.addWindowStateListener(this.handler);
    this.window.addComponentListener(this.handler);
  }
  
  protected void uninstallWindowListeners() {
    if (this.window == null) {
      return;
    }
    this.window.removePropertyChangeListener(this.handler);
    this.window.removeWindowListener(this.handler);
    this.window.removeWindowStateListener(this.handler);
    this.window.removeComponentListener(this.handler);
  }

  
  protected boolean isMenuBarEmbedded() {
    return (UIManager.getBoolean("TitlePane.menuBarEmbedded") && 
      FlatClientProperties.clientPropertyBoolean(this.rootPane, "JRootPane.menuBarEmbedded", true) && 
      FlatSystemProperties.getBoolean("flatlaf.menuBarEmbedded", true));
  }
  
  protected Rectangle getMenuBarBounds() {
    Insets insets = this.rootPane.getInsets();

    
    Rectangle bounds = new Rectangle(SwingUtilities.convertPoint(this.menuBarPlaceholder, -insets.left, -insets.top, this.rootPane), this.menuBarPlaceholder.getSize());


    
    Insets borderInsets = getBorder().getBorderInsets(this);
    bounds.height += borderInsets.bottom;
    
    return FlatUIUtils.subtractInsets(bounds, UIScale.scale(getMenuBarMargins()));
  }
  
  protected Insets getMenuBarMargins() {
    return getComponentOrientation().isLeftToRight() ? this.menuBarMargins : new Insets(this.menuBarMargins.top, this.menuBarMargins.right, this.menuBarMargins.bottom, this.menuBarMargins.left);
  }


  
  protected void menuBarChanged() {
    this.menuBarPlaceholder.invalidate();


    
    repaint();

    
    EventQueue.invokeLater(() -> activeChanged(
          (this.window == null || this.window.isActive())));
  }

  
  protected void menuBarLayouted() {
    updateJBRHitTestSpotsAndTitleBarHeightLater();
  }


















  
  protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
  }
  
  protected void repaintWindowBorder() {
    int width = this.rootPane.getWidth();
    int height = this.rootPane.getHeight();
    Insets insets = this.rootPane.getInsets();
    this.rootPane.repaint(0, 0, width, insets.top);
    this.rootPane.repaint(0, 0, insets.left, height);
    this.rootPane.repaint(0, height - insets.bottom, width, insets.bottom);
    this.rootPane.repaint(width - insets.right, 0, insets.right, height);
  }



  
  protected void iconify() {
    if (this.window instanceof Frame) {
      Frame frame = (Frame)this.window;
      frame.setExtendedState(frame.getExtendedState() | 0x1);
    } 
  }



  
  protected void maximize() {
    if (!(this.window instanceof Frame)) {
      return;
    }
    Frame frame = (Frame)this.window;
    
    updateMaximizedBounds();

    
    this.rootPane.putClientProperty("_flatlaf.maximizedBoundsUpToDate", Boolean.valueOf(true));

    
    frame.setExtendedState(frame.getExtendedState() | 0x6);
  }
  
  protected void updateMaximizedBounds() {
    Frame frame = (Frame)this.window;


    
    Rectangle oldMaximizedBounds = frame.getMaximizedBounds();
    if (!hasJBRCustomDecoration() && (oldMaximizedBounds == null || 
      
      Objects.equals(oldMaximizedBounds, this.rootPane.getClientProperty("_flatlaf.maximizedBounds")))) {
      
      GraphicsConfiguration gc = this.window.getGraphicsConfiguration();










      
      Rectangle screenBounds = gc.getBounds();
      
      int maximizedX = screenBounds.x;
      int maximizedY = screenBounds.y;
      int maximizedWidth = screenBounds.width;
      int maximizedHeight = screenBounds.height;
      
      if (!isMaximizedBoundsFixed()) {
        
        maximizedX = 0;
        maximizedY = 0;

        
        AffineTransform defaultTransform = gc.getDefaultTransform();
        maximizedWidth = (int)(maximizedWidth * defaultTransform.getScaleX());
        maximizedHeight = (int)(maximizedHeight * defaultTransform.getScaleY());
      } 



      
      Insets screenInsets = this.window.getToolkit().getScreenInsets(gc);




      
      Rectangle newMaximizedBounds = new Rectangle(maximizedX + screenInsets.left, maximizedY + screenInsets.top, maximizedWidth - screenInsets.left - screenInsets.right, maximizedHeight - screenInsets.top - screenInsets.bottom);




      
      if (!Objects.equals(oldMaximizedBounds, newMaximizedBounds)) {
        
        frame.setMaximizedBounds(newMaximizedBounds);


        
        this.rootPane.putClientProperty("_flatlaf.maximizedBounds", newMaximizedBounds);
      } 
    } 
  }







  
  private boolean isMaximizedBoundsFixed() {
    return (SystemInfo.isJava_15_orLater || (SystemInfo.javaVersion >= 
      SystemInfo.toVersion(11, 0, 8, 0) && SystemInfo.javaVersion < 
      SystemInfo.toVersion(12, 0, 0, 0)) || (SystemInfo.javaVersion >= 
      SystemInfo.toVersion(13, 0, 4, 0) && SystemInfo.javaVersion < 
      SystemInfo.toVersion(14, 0, 0, 0)));
  }



  
  protected void restore() {
    if (this.window instanceof Frame) {
      Frame frame = (Frame)this.window;
      int state = frame.getExtendedState();
      frame.setExtendedState(((state & 0x1) != 0) ? (state & 0xFFFFFFFE) : (state & 0xFFFFFFF9));
    } 
  }





  
  protected void close() {
    if (this.window != null)
      this.window.dispatchEvent(new WindowEvent(this.window, 201)); 
  }
  
  protected boolean hasJBRCustomDecoration() {
    return (FlatRootPaneUI.canUseJBRCustomDecorations && this.window != null && 
      
      JBRCustomDecorations.hasCustomDecoration(this.window));
  }
  
  protected void updateJBRHitTestSpotsAndTitleBarHeightLater() {
    EventQueue.invokeLater(() -> updateJBRHitTestSpotsAndTitleBarHeight());
  }


  
  protected void updateJBRHitTestSpotsAndTitleBarHeight() {
    if (!isDisplayable()) {
      return;
    }
    if (!hasJBRCustomDecoration()) {
      return;
    }
    List<Rectangle> hitTestSpots = new ArrayList<>();
    if (this.iconLabel.isVisible())
      addJBRHitTestSpot(this.iconLabel, false, hitTestSpots); 
    addJBRHitTestSpot(this.buttonPanel, false, hitTestSpots);
    addJBRHitTestSpot(this.menuBarPlaceholder, true, hitTestSpots);
    
    int titleBarHeight = getHeight();
    
    if (titleBarHeight > 0) {
      titleBarHeight--;
    }
    JBRCustomDecorations.setHitTestSpotsAndTitleBarHeight(this.window, hitTestSpots, titleBarHeight);
  }






  
  protected void addJBRHitTestSpot(JComponent c, boolean subtractMenuBarMargins, List<Rectangle> hitTestSpots) {
    Dimension size = c.getSize();
    if (size.width <= 0 || size.height <= 0) {
      return;
    }
    Point location = SwingUtilities.convertPoint(c, 0, 0, this.window);
    Rectangle r = new Rectangle(location, size);
    if (subtractMenuBarMargins) {
      r = FlatUIUtils.subtractInsets(r, UIScale.scale(getMenuBarMargins()));
    }
    r.grow(2, 2);
    hitTestSpots.add(r);
  }








  
  protected class FlatTitlePaneBorder
    extends AbstractBorder
  {
    public Insets getBorderInsets(Component c, Insets insets) {
      super.getBorderInsets(c, insets);
      
      Border menuBarBorder = getMenuBarBorder();
      if (menuBarBorder != null) {
        
        Insets menuBarInsets = menuBarBorder.getBorderInsets(c);
        insets.bottom += menuBarInsets.bottom;
      } else if (FlatTitlePane.this.borderColor != null && (FlatTitlePane.this.rootPane.getJMenuBar() == null || !FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
        insets.bottom += UIScale.scale(1);
      } 
      if (FlatTitlePane.this.hasJBRCustomDecoration()) {
        insets = FlatUIUtils.addInsets(insets, JBRCustomDecorations.JBRWindowTopBorder.getInstance().getBorderInsets());
      }
      return insets;
    }


    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Border menuBarBorder = getMenuBarBorder();
      if (menuBarBorder != null) {
        
        menuBarBorder.paintBorder(c, g, x, y, width, height);
      } else if (FlatTitlePane.this.borderColor != null && (FlatTitlePane.this.rootPane.getJMenuBar() == null || !FlatTitlePane.this.rootPane.getJMenuBar().isVisible())) {
        
        float lineHeight = UIScale.scale(1.0F);
        FlatUIUtils.paintFilledRectangle(g, FlatTitlePane.this.borderColor, x, (y + height) - lineHeight, width, lineHeight);
      } 
      
      if (FlatTitlePane.this.hasJBRCustomDecoration())
        JBRCustomDecorations.JBRWindowTopBorder.getInstance().paintBorder(c, g, x, y, width, height); 
    }
    
    protected Border getMenuBarBorder() {
      JMenuBar menuBar = FlatTitlePane.this.rootPane.getJMenuBar();
      return (menuBar != null && menuBar.isVisible() && FlatTitlePane.this.isMenuBarEmbedded()) ? menuBar.getBorder() : null;
    }
  }


  
  protected class Handler
    extends WindowAdapter
    implements PropertyChangeListener, MouseListener, MouseMotionListener, ComponentListener
  {
    private Point dragOffset;

    
    public void propertyChange(PropertyChangeEvent e) {
      switch (e.getPropertyName()) {
        case "title":
          FlatTitlePane.this.titleLabel.setText(FlatTitlePane.this.getWindowTitle());
          break;
        
        case "resizable":
          if (FlatTitlePane.this.window instanceof Frame) {
            FlatTitlePane.this.frameStateChanged();
          }
          break;
        case "iconImage":
          FlatTitlePane.this.updateIcon();
          break;
        
        case "componentOrientation":
          FlatTitlePane.this.updateJBRHitTestSpotsAndTitleBarHeightLater();
          break;
      } 
    }



    
    public void windowActivated(WindowEvent e) {
      FlatTitlePane.this.activeChanged(true);
      FlatTitlePane.this.updateJBRHitTestSpotsAndTitleBarHeight();
      
      if (FlatTitlePane.this.hasJBRCustomDecoration()) {
        JBRCustomDecorations.JBRWindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
      }
      FlatTitlePane.this.repaintWindowBorder();
    }

    
    public void windowDeactivated(WindowEvent e) {
      FlatTitlePane.this.activeChanged(false);
      FlatTitlePane.this.updateJBRHitTestSpotsAndTitleBarHeight();
      
      if (FlatTitlePane.this.hasJBRCustomDecoration()) {
        JBRCustomDecorations.JBRWindowTopBorder.getInstance().repaintBorder(FlatTitlePane.this);
      }
      FlatTitlePane.this.repaintWindowBorder();
    }

    
    public void windowStateChanged(WindowEvent e) {
      FlatTitlePane.this.frameStateChanged();
      FlatTitlePane.this.updateJBRHitTestSpotsAndTitleBarHeight();
    }





    
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
        if (e.getSource() == FlatTitlePane.this.iconLabel) {
          
          FlatTitlePane.this.close();
        } else if (!FlatTitlePane.this.hasJBRCustomDecoration() && FlatTitlePane.this.window instanceof Frame && ((Frame)FlatTitlePane.this.window)
          
          .isResizable()) {

          
          Frame frame = (Frame)FlatTitlePane.this.window;
          if ((frame.getExtendedState() & 0x6) != 0) {
            FlatTitlePane.this.restore();
          } else {
            FlatTitlePane.this.maximize();
          } 
        } 
      }
    }
    
    public void mousePressed(MouseEvent e) {
      if (FlatTitlePane.this.window == null) {
        return;
      }
      this.dragOffset = SwingUtilities.convertPoint(FlatTitlePane.this, e.getPoint(), FlatTitlePane.this.window);
    }

    
    public void mouseReleased(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseDragged(MouseEvent e) {
      if (FlatTitlePane.this.window == null) {
        return;
      }
      if (FlatTitlePane.this.hasJBRCustomDecoration()) {
        return;
      }
      
      if (FlatTitlePane.this.window instanceof Frame) {
        Frame frame = (Frame)FlatTitlePane.this.window;
        int state = frame.getExtendedState();
        if ((state & 0x6) != 0) {
          int maximizedWidth = FlatTitlePane.this.window.getWidth();

          
          frame.setExtendedState(state & 0xFFFFFFF9);


          
          int restoredWidth = FlatTitlePane.this.window.getWidth();
          int center = restoredWidth / 2;
          if (this.dragOffset.x > center)
          {
            if (this.dragOffset.x > maximizedWidth - center) {
              this.dragOffset.x = restoredWidth - maximizedWidth - this.dragOffset.x;
            } else {
              this.dragOffset.x = center;
            } 
          }
        } 
      } 
      
      int newX = e.getXOnScreen() - this.dragOffset.x;
      int newY = e.getYOnScreen() - this.dragOffset.y;
      
      if (newX == FlatTitlePane.this.window.getX() && newY == FlatTitlePane.this.window.getY()) {
        return;
      }
      
      FlatTitlePane.this.window.setLocation(newX, newY);
    }


    
    public void mouseMoved(MouseEvent e) {}

    
    public void componentResized(ComponentEvent e) {
      FlatTitlePane.this.updateJBRHitTestSpotsAndTitleBarHeightLater();
    }


    
    public void componentShown(ComponentEvent e) {
      FlatTitlePane.this.frameStateChanged();
    }
    
    public void componentMoved(ComponentEvent e) {}
    
    public void componentHidden(ComponentEvent e) {}
  }
}
