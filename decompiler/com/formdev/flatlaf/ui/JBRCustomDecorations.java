package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;


























public class JBRCustomDecorations
{
  private static boolean initialized;
  private static Method Window_hasCustomDecoration;
  private static Method Window_setHasCustomDecoration;
  private static Method WWindowPeer_setCustomDecorationHitTestSpots;
  private static Method WWindowPeer_setCustomDecorationTitleBarHeight;
  private static Method AWTAccessor_getComponentAccessor;
  private static Method AWTAccessor_ComponentAccessor_getPeer;
  
  public static boolean isSupported() {
    initialize();
    return (Window_setHasCustomDecoration != null);
  }
  
  static void install(final JRootPane rootPane) {
    if (!isSupported()) {
      return;
    }
    
    if (rootPane.getParent() != null) {
      return;
    }



    
    HierarchyListener addListener = new HierarchyListener()
      {
        public void hierarchyChanged(HierarchyEvent e) {
          if (e.getChanged() != rootPane || (e.getChangeFlags() & 0x1L) == 0L) {
            return;
          }
          Container parent = e.getChangedParent();
          if (parent instanceof Window) {
            JBRCustomDecorations.install((Window)parent);
          }

          
          EventQueue.invokeLater(() -> rootPane.removeHierarchyListener(this));
        }
      };

    
    rootPane.addHierarchyListener(addListener);
  }
  
  static void install(Window window) {
    if (!isSupported()) {
      return;
    }
    
    if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
      return;
    }
    if (window instanceof JFrame) {
      JFrame frame = (JFrame)window;


      
      if (!JFrame.isDefaultLookAndFeelDecorated() && 
        !FlatSystemProperties.getBoolean("flatlaf.useJetBrainsCustomDecorations", false)) {
        return;
      }
      
      if (frame.isUndecorated()) {
        return;
      }
      
      setHasCustomDecoration(frame);

      
      frame.getRootPane().setWindowDecorationStyle(1);
    }
    else if (window instanceof JDialog) {
      JDialog dialog = (JDialog)window;


      
      if (!JDialog.isDefaultLookAndFeelDecorated() && 
        !FlatSystemProperties.getBoolean("flatlaf.useJetBrainsCustomDecorations", false)) {
        return;
      }
      
      if (dialog.isUndecorated()) {
        return;
      }
      
      setHasCustomDecoration(dialog);

      
      dialog.getRootPane().setWindowDecorationStyle(2);
    } 
  }
  
  static boolean hasCustomDecoration(Window window) {
    if (!isSupported()) {
      return false;
    }
    try {
      return ((Boolean)Window_hasCustomDecoration.invoke(window, new Object[0])).booleanValue();
    } catch (Exception ex) {
      Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
      return false;
    } 
  }
  
  static void setHasCustomDecoration(Window window) {
    if (!isSupported()) {
      return;
    }
    try {
      Window_setHasCustomDecoration.invoke(window, new Object[0]);
    } catch (Exception ex) {
      Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
  }
  
  static void setHitTestSpotsAndTitleBarHeight(Window window, List<Rectangle> hitTestSpots, int titleBarHeight) {
    if (!isSupported()) {
      return;
    }
    try {
      Object compAccessor = AWTAccessor_getComponentAccessor.invoke((Object)null, new Object[0]);
      Object peer = AWTAccessor_ComponentAccessor_getPeer.invoke(compAccessor, new Object[] { window });
      WWindowPeer_setCustomDecorationHitTestSpots.invoke(peer, new Object[] { hitTestSpots });
      WWindowPeer_setCustomDecorationTitleBarHeight.invoke(peer, new Object[] { Integer.valueOf(titleBarHeight) });
    } catch (Exception ex) {
      Logger.getLogger(FlatLaf.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
  }
  
  private static void initialize() {
    if (initialized)
      return; 
    initialized = true;

    
    if (!SystemInfo.isJetBrainsJVM_11_orLater || !SystemInfo.isWindows_10_orLater) {
      return;
    }
    if (!FlatSystemProperties.getBoolean("flatlaf.useJetBrainsCustomDecorations", true)) {
      return;
    }
    try {
      Class<?> awtAcessorClass = Class.forName("sun.awt.AWTAccessor");
      Class<?> compAccessorClass = Class.forName("sun.awt.AWTAccessor$ComponentAccessor");
      AWTAccessor_getComponentAccessor = awtAcessorClass.getDeclaredMethod("getComponentAccessor", new Class[0]);
      AWTAccessor_ComponentAccessor_getPeer = compAccessorClass.getDeclaredMethod("getPeer", new Class[] { Component.class });
      
      Class<?> peerClass = Class.forName("sun.awt.windows.WWindowPeer");
      WWindowPeer_setCustomDecorationHitTestSpots = peerClass.getDeclaredMethod("setCustomDecorationHitTestSpots", new Class[] { List.class });
      WWindowPeer_setCustomDecorationTitleBarHeight = peerClass.getDeclaredMethod("setCustomDecorationTitleBarHeight", new Class[] { int.class });
      WWindowPeer_setCustomDecorationHitTestSpots.setAccessible(true);
      WWindowPeer_setCustomDecorationTitleBarHeight.setAccessible(true);
      
      Window_hasCustomDecoration = Window.class.getDeclaredMethod("hasCustomDecoration", new Class[0]);
      Window_setHasCustomDecoration = Window.class.getDeclaredMethod("setHasCustomDecoration", new Class[0]);
      Window_hasCustomDecoration.setAccessible(true);
      Window_setHasCustomDecoration.setAccessible(true);
    } catch (Exception exception) {}
  }



  
  static class JBRWindowTopBorder
    extends BorderUIResource.EmptyBorderUIResource
  {
    private static JBRWindowTopBorder instance;

    
    private final Color defaultActiveBorder = new Color(7368816);
    private final Color inactiveLightColor = new Color(11184810);
    
    private boolean colorizationAffectsBorders;
    private Color activeColor = this.defaultActiveBorder;
    
    static JBRWindowTopBorder getInstance() {
      if (instance == null)
        instance = new JBRWindowTopBorder(); 
      return instance;
    }
    
    private JBRWindowTopBorder() {
      super(1, 0, 0, 0);
      
      this.colorizationAffectsBorders = calculateAffectsBorders();
      this.activeColor = calculateActiveBorderColor();
      
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      toolkit.addPropertyChangeListener("win.dwm.colorizationColor.affects.borders", e -> {
            this.colorizationAffectsBorders = calculateAffectsBorders();
            
            this.activeColor = calculateActiveBorderColor();
          });
      PropertyChangeListener l = e -> this.activeColor = calculateActiveBorderColor();

      
      toolkit.addPropertyChangeListener("win.dwm.colorizationColor", l);
      toolkit.addPropertyChangeListener("win.dwm.colorizationColorBalance", l);
      toolkit.addPropertyChangeListener("win.frame.activeBorderColor", l);
    }
    
    private boolean calculateAffectsBorders() {
      Object value = Toolkit.getDefaultToolkit().getDesktopProperty("win.dwm.colorizationColor.affects.borders");
      return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : true;
    }
    
    private Color calculateActiveBorderColor() {
      if (!this.colorizationAffectsBorders) {
        return this.defaultActiveBorder;
      }
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Color colorizationColor = (Color)toolkit.getDesktopProperty("win.dwm.colorizationColor");
      if (colorizationColor != null) {
        Object colorizationColorBalanceObj = toolkit.getDesktopProperty("win.dwm.colorizationColorBalance");
        if (colorizationColorBalanceObj instanceof Integer) {
          int colorizationColorBalance = ((Integer)colorizationColorBalanceObj).intValue();
          if (colorizationColorBalance < 0 || colorizationColorBalance > 100) {
            colorizationColorBalance = 100;
          }
          if (colorizationColorBalance == 0)
            return new Color(14277081); 
          if (colorizationColorBalance == 100) {
            return colorizationColor;
          }
          float alpha = colorizationColorBalance / 100.0F;
          float remainder = 1.0F - alpha;
          int r = Math.round(colorizationColor.getRed() * alpha + 217.0F * remainder);
          int g = Math.round(colorizationColor.getGreen() * alpha + 217.0F * remainder);
          int b = Math.round(colorizationColor.getBlue() * alpha + 217.0F * remainder);

          
          r = Math.min(Math.max(r, 0), 255);
          g = Math.min(Math.max(g, 0), 255);
          b = Math.min(Math.max(b, 0), 255);
          
          return new Color(r, g, b);
        } 
        return colorizationColor;
      } 
      
      Color activeBorderColor = (Color)toolkit.getDesktopProperty("win.frame.activeBorderColor");
      return (activeBorderColor != null) ? activeBorderColor : UIManager.getColor("MenuBar.borderColor");
    }

    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Window window = SwingUtilities.windowForComponent(c);
      boolean active = (window != null) ? window.isActive() : false;



      
      boolean paintTopBorder = (!FlatLaf.isLafDark() || (active && this.colorizationAffectsBorders));
      if (!paintTopBorder) {
        return;
      }
      g.setColor(active ? this.activeColor : this.inactiveLightColor);
      HiDPIUtils.paintAtScale1x((Graphics2D)g, x, y, width, height, this::paintImpl);
    }
    
    private void paintImpl(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
      g.drawRect(x, y, width - 1, 0);
    }
    
    void repaintBorder(Component c) {
      c.repaint(0, 0, c.getWidth(), 1);
    }
  }
}
