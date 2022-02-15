package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;








































public class FlatRootPaneUI
  extends BasicRootPaneUI
{
  static final boolean canUseJBRCustomDecorations = (SystemInfo.isJetBrainsJVM_11_orLater && SystemInfo.isWindows_10_orLater);

  
  protected final Color borderColor = UIManager.getColor("TitlePane.borderColor");
  
  protected JRootPane rootPane;
  
  protected FlatTitlePane titlePane;
  protected FlatWindowResizer windowResizer;
  private LayoutManager oldLayout;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatRootPaneUI();
  }

  
  public void installUI(JComponent c) {
    super.installUI(c);
    
    this.rootPane = (JRootPane)c;
    
    if (this.rootPane.getWindowDecorationStyle() != 0) {
      installClientDecorations();
    } else {
      installBorder();
    } 
    if (canUseJBRCustomDecorations)
      JBRCustomDecorations.install(this.rootPane); 
  }
  
  protected void installBorder() {
    if (this.borderColor != null) {
      Border b = this.rootPane.getBorder();
      if (b == null || b instanceof javax.swing.plaf.UIResource) {
        this.rootPane.setBorder(new FlatWindowTitleBorder(this.borderColor));
      }
    } 
  }
  
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    
    uninstallClientDecorations();
    this.rootPane = null;
  }

  
  protected void installDefaults(JRootPane c) {
    super.installDefaults(c);





    
    Container parent = c.getParent();
    if (parent instanceof JFrame || parent instanceof javax.swing.JDialog) {
      Color background = parent.getBackground();
      if (background == null || background instanceof javax.swing.plaf.UIResource) {
        parent.setBackground(UIManager.getColor("control"));
      }
    } 
    
    if (SystemInfo.isJetBrainsJVM && SystemInfo.isMacOS_10_14_Mojave_orLater)
      c.putClientProperty("jetbrains.awt.windowDarkAppearance", Boolean.valueOf(FlatLaf.isLafDark())); 
  }
  
  protected void installClientDecorations() {
    boolean isJBRSupported = (canUseJBRCustomDecorations && JBRCustomDecorations.isSupported());

    
    if (this.rootPane.getWindowDecorationStyle() != 0 && !isJBRSupported) {
      LookAndFeel.installBorder(this.rootPane, "RootPane.border");
    } else {
      LookAndFeel.uninstallBorder(this.rootPane);
    } 
    
    setTitlePane(createTitlePane());

    
    this.oldLayout = this.rootPane.getLayout();
    this.rootPane.setLayout(createRootLayout());

    
    if (!isJBRSupported)
      this.windowResizer = createWindowResizer(); 
  }
  
  protected void uninstallClientDecorations() {
    LookAndFeel.uninstallBorder(this.rootPane);
    setTitlePane(null);
    
    if (this.windowResizer != null) {
      this.windowResizer.uninstall();
      this.windowResizer = null;
    } 
    
    if (this.oldLayout != null) {
      this.rootPane.setLayout(this.oldLayout);
      this.oldLayout = null;
    } 
    
    if (this.rootPane.getWindowDecorationStyle() == 0) {
      this.rootPane.revalidate();
      this.rootPane.repaint();
    } 
  }
  
  protected FlatRootLayout createRootLayout() {
    return new FlatRootLayout();
  }
  
  protected FlatWindowResizer createWindowResizer() {
    return new FlatWindowResizer.WindowResizer(this.rootPane);
  }
  
  protected FlatTitlePane createTitlePane() {
    return new FlatTitlePane(this.rootPane);
  }

  
  protected static final Integer TITLE_PANE_LAYER = Integer.valueOf(JLayeredPane.FRAME_CONTENT_LAYER.intValue() - 1);
  
  protected void setTitlePane(FlatTitlePane newTitlePane) {
    JLayeredPane layeredPane = this.rootPane.getLayeredPane();
    
    if (this.titlePane != null) {
      layeredPane.remove(this.titlePane);
    }
    if (newTitlePane != null) {
      layeredPane.add(newTitlePane, TITLE_PANE_LAYER);
    }
    this.titlePane = newTitlePane;
  }

  
  public void propertyChange(PropertyChangeEvent e) {
    super.propertyChange(e);
    
    switch (e.getPropertyName()) {
      case "windowDecorationStyle":
        uninstallClientDecorations();
        if (this.rootPane.getWindowDecorationStyle() != 0) {
          installClientDecorations(); break;
        } 
        installBorder();
        break;
      
      case "JRootPane.menuBarEmbedded":
        if (this.titlePane != null) {
          this.titlePane.menuBarChanged();
          this.rootPane.revalidate();
          this.rootPane.repaint();
        } 
        break;
    } 
  }

  
  protected class FlatRootLayout
    implements LayoutManager2
  {
    public void addLayoutComponent(String name, Component comp) {}
    
    public void addLayoutComponent(Component comp, Object constraints) {}
    
    public void removeLayoutComponent(Component comp) {}
    
    public Dimension preferredLayoutSize(Container parent) {
      return computeLayoutSize(parent, c -> c.getPreferredSize());
    }

    
    public Dimension minimumLayoutSize(Container parent) {
      return computeLayoutSize(parent, c -> c.getMinimumSize());
    }

    
    public Dimension maximumLayoutSize(Container parent) {
      return new Dimension(2147483647, 2147483647);
    }
    
    private Dimension computeLayoutSize(Container parent, Function<Component, Dimension> getSizeFunc) {
      JRootPane rootPane = (JRootPane)parent;

      
      Dimension titlePaneSize = (FlatRootPaneUI.this.titlePane != null) ? getSizeFunc.apply(FlatRootPaneUI.this.titlePane) : new Dimension();


      
      Dimension contentSize = (rootPane.getContentPane() != null) ? getSizeFunc.apply(rootPane.getContentPane()) : rootPane.getSize();
      
      int width = Math.max(titlePaneSize.width, contentSize.width);
      int height = titlePaneSize.height + contentSize.height;
      if (FlatRootPaneUI.this.titlePane == null || !FlatRootPaneUI.this.titlePane.isMenuBarEmbedded()) {
        JMenuBar menuBar = rootPane.getJMenuBar();
        
        Dimension menuBarSize = (menuBar != null && menuBar.isVisible()) ? getSizeFunc.apply(menuBar) : new Dimension();

        
        width = Math.max(width, menuBarSize.width);
        height += menuBarSize.height;
      } 
      
      Insets insets = rootPane.getInsets();
      
      return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }



    
    public void layoutContainer(Container parent) {
      JRootPane rootPane = (JRootPane)parent;
      boolean isFullScreen = FlatUIUtils.isFullScreen(rootPane);
      
      Insets insets = rootPane.getInsets();
      int x = insets.left;
      int y = insets.top;
      int width = rootPane.getWidth() - insets.left - insets.right;
      int height = rootPane.getHeight() - insets.top - insets.bottom;
      
      if (rootPane.getLayeredPane() != null)
        rootPane.getLayeredPane().setBounds(x, y, width, height); 
      if (rootPane.getGlassPane() != null) {
        rootPane.getGlassPane().setBounds(x, y, width, height);
      }
      int nextY = 0;
      if (!isFullScreen && FlatRootPaneUI.this.titlePane != null) {
        Dimension prefSize = FlatRootPaneUI.this.titlePane.getPreferredSize();
        FlatRootPaneUI.this.titlePane.setBounds(0, 0, width, prefSize.height);
        nextY += prefSize.height;
      } 
      
      JMenuBar menuBar = rootPane.getJMenuBar();
      if (menuBar != null && menuBar.isVisible()) {
        if (!isFullScreen && FlatRootPaneUI.this.titlePane != null && FlatRootPaneUI.this.titlePane.isMenuBarEmbedded()) {
          FlatRootPaneUI.this.titlePane.validate();
          menuBar.setBounds(FlatRootPaneUI.this.titlePane.getMenuBarBounds());
        } else {
          Dimension prefSize = menuBar.getPreferredSize();
          menuBar.setBounds(0, nextY, width, prefSize.height);
          nextY += prefSize.height;
        } 
      }
      
      Container contentPane = rootPane.getContentPane();
      if (contentPane != null) {
        contentPane.setBounds(0, nextY, width, Math.max(height - nextY, 0));
      }
      if (FlatRootPaneUI.this.titlePane != null) {
        FlatRootPaneUI.this.titlePane.menuBarLayouted();
      }
    }
    
    public void invalidateLayout(Container parent) {
      if (FlatRootPaneUI.this.titlePane != null) {
        FlatRootPaneUI.this.titlePane.menuBarChanged();
      }
    }
    
    public float getLayoutAlignmentX(Container target) {
      return 0.0F;
    }

    
    public float getLayoutAlignmentY(Container target) {
      return 0.0F;
    }
  }


  
  public static class FlatWindowBorder
    extends BorderUIResource.EmptyBorderUIResource
  {
    protected final Color activeBorderColor = UIManager.getColor("RootPane.activeBorderColor");
    protected final Color inactiveBorderColor = UIManager.getColor("RootPane.inactiveBorderColor");
    protected final Color baseBorderColor = UIManager.getColor("Panel.background");
    
    public FlatWindowBorder() {
      super(1, 1, 1, 1);
    }

    
    public Insets getBorderInsets(Component c, Insets insets) {
      if (isWindowMaximized(c) || FlatUIUtils.isFullScreen(c)) {
        
        insets.top = insets.left = insets.bottom = insets.right = 0;
        return insets;
      } 
      return super.getBorderInsets(c, insets);
    }

    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if (isWindowMaximized(c) || FlatUIUtils.isFullScreen(c)) {
        return;
      }
      Container parent = c.getParent();
      boolean active = (parent instanceof Window) ? ((Window)parent).isActive() : false;
      
      g.setColor(FlatUIUtils.deriveColor(active ? this.activeBorderColor : this.inactiveBorderColor, this.baseBorderColor));
      HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, width, height, this::paintImpl);
    }
    
    private void paintImpl(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
      g.drawRect(x, y, width - 1, height - 1);
    }
    
    protected boolean isWindowMaximized(Component c) {
      Container parent = c.getParent();
      return (parent instanceof Frame) ? (
        ((((Frame)parent).getExtendedState() & 0x6) != 0)) : false;
    }
  }


  
  private static class FlatWindowTitleBorder
    extends BorderUIResource.EmptyBorderUIResource
  {
    private final Color borderColor;

    
    FlatWindowTitleBorder(Color borderColor) {
      super(0, 0, 0, 0);
      this.borderColor = borderColor;
    }

    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if (showBorder(c)) {
        float lineHeight = UIScale.scale(1.0F);
        FlatUIUtils.paintFilledRectangle(g, this.borderColor, x, y, width, lineHeight);
      } 
    }

    
    public Insets getBorderInsets(Component c, Insets insets) {
      insets.set(showBorder(c) ? 1 : 0, 0, 0, 0);
      return insets;
    }
    
    private boolean showBorder(Component c) {
      Container parent = c.getParent();
      return ((parent instanceof JFrame && (((JFrame)parent)
        
        .getJMenuBar() == null || 
        !((JFrame)parent).getJMenuBar().isVisible())) || parent instanceof javax.swing.JDialog);
    }
  }
}
