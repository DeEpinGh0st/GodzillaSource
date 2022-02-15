package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;





























public class FlatPopupFactory
  extends PopupFactory
{
  private Method java8getPopupMethod;
  private Method java9getPopupMethod;
  
  public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
    Point pt = fixToolTipLocation(owner, contents, x, y);
    if (pt != null) {
      x = pt.x;
      y = pt.y;
    } 
    
    boolean forceHeavyWeight = isOptionEnabled(owner, contents, "Popup.forceHeavyWeight", "Popup.forceHeavyWeight");
    
    if (!isOptionEnabled(owner, contents, "Popup.dropShadowPainted", "Popup.dropShadowPainted")) {
      return new NonFlashingPopup(getPopupForScreenOfOwner(owner, contents, x, y, forceHeavyWeight), contents);
    }
    
    if (SystemInfo.isMacOS || SystemInfo.isLinux) {
      return new NonFlashingPopup(getPopupForScreenOfOwner(owner, contents, x, y, true), contents);
    }
    
    return new DropShadowPopup(getPopupForScreenOfOwner(owner, contents, x, y, forceHeavyWeight), owner, contents);
  }
















  
  private Popup getPopupForScreenOfOwner(Component owner, Component contents, int x, int y, boolean forceHeavyWeight) throws IllegalArgumentException {
    int count = 0;



    
    while (true) {
      Popup popup = forceHeavyWeight ? getHeavyWeightPopup(owner, contents, x, y) : super.getPopup(owner, contents, x, y);

      
      Window popupWindow = SwingUtilities.windowForComponent(contents);

      
      if (popupWindow == null || popupWindow
        .getGraphicsConfiguration() == owner.getGraphicsConfiguration()) {
        return popup;
      }
      
      if (popupWindow instanceof JWindow) {
        ((JWindow)popupWindow).getContentPane().removeAll();
      }

      
      popupWindow.dispose();

      
      if (++count > 10) {
        return popup;
      }
    } 
  }








  
  private static void showPopupAndFixLocation(Popup popup, Window popupWindow) {
    if (popupWindow != null) {
      
      int x = popupWindow.getX();
      int y = popupWindow.getY();
      
      popup.show();


      
      if (popupWindow.getX() != x || popupWindow.getY() != y)
        popupWindow.setLocation(x, y); 
    } else {
      popup.show();
    } 
  }
  private boolean isOptionEnabled(Component owner, Component contents, String clientKey, String uiKey) {
    if (owner instanceof JComponent) {
      Boolean b = FlatClientProperties.clientPropertyBooleanStrict((JComponent)owner, clientKey, null);
      if (b != null) {
        return b.booleanValue();
      }
    } 
    if (contents instanceof JComponent) {
      Boolean b = FlatClientProperties.clientPropertyBooleanStrict((JComponent)contents, clientKey, null);
      if (b != null) {
        return b.booleanValue();
      }
    } 
    return UIManager.getBoolean(uiKey);
  }










  
  private Popup getHeavyWeightPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
    try {
      if (SystemInfo.isJava_9_orLater) {
        if (this.java9getPopupMethod == null) {
          this.java9getPopupMethod = PopupFactory.class.getDeclaredMethod("getPopup", new Class[] { Component.class, Component.class, int.class, int.class, boolean.class });
        }
        
        return (Popup)this.java9getPopupMethod.invoke(this, new Object[] { owner, contents, Integer.valueOf(x), Integer.valueOf(y), Boolean.valueOf(true) });
      } 
      
      if (this.java8getPopupMethod == null) {
        this.java8getPopupMethod = PopupFactory.class.getDeclaredMethod("getPopup", new Class[] { Component.class, Component.class, int.class, int.class, int.class });
        
        this.java8getPopupMethod.setAccessible(true);
      } 
      return (Popup)this.java8getPopupMethod.invoke(this, new Object[] { owner, contents, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(2) });
    }
    catch (NoSuchMethodException|SecurityException|IllegalAccessException|java.lang.reflect.InvocationTargetException ex) {
      
      return null;
    } 
  }









  
  private Point fixToolTipLocation(Component owner, Component contents, int x, int y) {
    if (!(contents instanceof javax.swing.JToolTip) || !wasInvokedFromToolTipManager()) {
      return null;
    }
    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    Dimension tipSize = contents.getPreferredSize();

    
    Rectangle tipBounds = new Rectangle(x, y, tipSize.width, tipSize.height);
    if (!tipBounds.contains(mouseLocation)) {
      return null;
    }
    
    return new Point(x, mouseLocation.y - tipSize.height - UIScale.scale(20));
  }
  
  private boolean wasInvokedFromToolTipManager() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement stackTraceElement : stackTrace) {
      if ("javax.swing.ToolTipManager".equals(stackTraceElement.getClassName()) && "showTipWindow"
        .equals(stackTraceElement.getMethodName()))
        return true; 
    } 
    return false;
  }

  
  private class NonFlashingPopup
    extends Popup
  {
    private Popup delegate;
    
    private Component contents;
    
    protected Window popupWindow;
    
    private Color oldPopupWindowBackground;
    
    NonFlashingPopup(Popup delegate, Component contents) {
      this.delegate = delegate;
      this.contents = contents;
      
      this.popupWindow = SwingUtilities.windowForComponent(contents);
      if (this.popupWindow != null) {



        
        this.oldPopupWindowBackground = this.popupWindow.getBackground();
        this.popupWindow.setBackground(contents.getBackground());
      } 
    }

    
    public void show() {
      if (this.delegate != null) {
        FlatPopupFactory.showPopupAndFixLocation(this.delegate, this.popupWindow);


        
        if (this.contents instanceof javax.swing.JToolTip && this.popupWindow == null) {
          Container parent = this.contents.getParent();
          if (parent instanceof JPanel) {
            Dimension prefSize = parent.getPreferredSize();
            if (!prefSize.equals(parent.getSize())) {
              Container mediumWeightPanel = SwingUtilities.getAncestorOfClass(Panel.class, parent);
              Container c = (mediumWeightPanel != null) ? mediumWeightPanel : parent;

              
              c.setSize(prefSize);
              c.validate();
            } 
          } 
        } 
      } 
    }

    
    public void hide() {
      if (this.delegate != null) {
        this.delegate.hide();
        this.delegate = null;
        this.contents = null;
      } 
      
      if (this.popupWindow != null) {

        
        this.popupWindow.setBackground(this.oldPopupWindowBackground);
        this.popupWindow = null;
      } 
    }
  }

  
  private class DropShadowPopup
    extends NonFlashingPopup
  {
    private final Component owner;
    
    private JComponent lightComp;
    
    private Border oldBorder;
    
    private boolean oldOpaque;
    
    private boolean mediumWeightShown;
    
    private Panel mediumWeightPanel;
    
    private JPanel dropShadowPanel;
    
    private ComponentListener mediumPanelListener;
    private Popup dropShadowDelegate;
    private Window dropShadowWindow;
    private Color oldDropShadowWindowBackground;
    
    DropShadowPopup(Popup delegate, Component owner, Component contents) {
      super(delegate, contents);
      this.owner = owner;
      
      Dimension size = contents.getPreferredSize();
      if (size.width <= 0 || size.height <= 0) {
        return;
      }
      if (this.popupWindow != null) {








        
        JPanel dropShadowPanel = new JPanel();
        dropShadowPanel.setBorder(createDropShadowBorder());
        dropShadowPanel.setOpaque(false);

        
        Dimension prefSize = this.popupWindow.getPreferredSize();
        Insets insets = dropShadowPanel.getInsets();
        dropShadowPanel.setPreferredSize(new Dimension(prefSize.width + insets.left + insets.right, prefSize.height + insets.top + insets.bottom));



        
        int x = this.popupWindow.getX() - insets.left;
        int y = this.popupWindow.getY() - insets.top;
        this.dropShadowDelegate = FlatPopupFactory.this.getPopupForScreenOfOwner(owner, dropShadowPanel, x, y, true);

        
        this.dropShadowWindow = SwingUtilities.windowForComponent(dropShadowPanel);
        if (this.dropShadowWindow != null) {
          this.oldDropShadowWindowBackground = this.dropShadowWindow.getBackground();
          this.dropShadowWindow.setBackground(new Color(0, true));
        } 
      } else {
        this.mediumWeightPanel = (Panel)SwingUtilities.getAncestorOfClass(Panel.class, contents);
        if (this.mediumWeightPanel != null) {
          
          this.dropShadowPanel = new JPanel();
          this.dropShadowPanel.setBorder(createDropShadowBorder());
          this.dropShadowPanel.setOpaque(false);
          this.dropShadowPanel.setSize(FlatUIUtils.addInsets(this.mediumWeightPanel.getSize(), this.dropShadowPanel.getInsets()));
        } else {
          
          Container p = contents.getParent();
          if (!(p instanceof JComponent)) {
            return;
          }
          this.lightComp = (JComponent)p;
          this.oldBorder = this.lightComp.getBorder();
          this.oldOpaque = this.lightComp.isOpaque();
          this.lightComp.setBorder(createDropShadowBorder());
          this.lightComp.setOpaque(false);
          this.lightComp.setSize(this.lightComp.getPreferredSize());
        } 
      } 
    }
    
    private Border createDropShadowBorder() {
      return new FlatDropShadowBorder(
          UIManager.getColor("Popup.dropShadowColor"), 
          UIManager.getInsets("Popup.dropShadowInsets"), 
          FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5F));
    }

    
    public void show() {
      if (this.dropShadowDelegate != null) {
        FlatPopupFactory.showPopupAndFixLocation(this.dropShadowDelegate, this.dropShadowWindow);
      }
      if (this.mediumWeightPanel != null) {
        showMediumWeightDropShadow();
      }
      super.show();

      
      if (this.lightComp != null) {
        Insets insets = this.lightComp.getInsets();
        if (insets.left != 0 || insets.top != 0) {
          this.lightComp.setLocation(this.lightComp.getX() - insets.left, this.lightComp.getY() - insets.top);
        }
      } 
    }
    
    public void hide() {
      if (this.dropShadowDelegate != null) {
        this.dropShadowDelegate.hide();
        this.dropShadowDelegate = null;
      } 
      
      if (this.mediumWeightPanel != null) {
        hideMediumWeightDropShadow();
        this.dropShadowPanel = null;
        this.mediumWeightPanel = null;
      } 
      
      super.hide();
      
      if (this.dropShadowWindow != null) {
        this.dropShadowWindow.setBackground(this.oldDropShadowWindowBackground);
        this.dropShadowWindow = null;
      } 
      
      if (this.lightComp != null) {
        this.lightComp.setBorder(this.oldBorder);
        this.lightComp.setOpaque(this.oldOpaque);
        this.lightComp = null;
      } 
    }
    
    private void showMediumWeightDropShadow() {
      if (this.mediumWeightShown) {
        return;
      }
      this.mediumWeightShown = true;
      
      Window window = SwingUtilities.windowForComponent(this.owner);
      if (window == null) {
        return;
      }
      if (!(window instanceof RootPaneContainer)) {
        return;
      }
      this.dropShadowPanel.setVisible(false);
      
      JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();
      layeredPane.add(this.dropShadowPanel, JLayeredPane.POPUP_LAYER, 0);
      
      this.mediumPanelListener = new ComponentListener()
        {
          public void componentShown(ComponentEvent e) {
            if (FlatPopupFactory.DropShadowPopup.this.dropShadowPanel != null) {
              FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.setVisible(true);
            }
          }
          
          public void componentHidden(ComponentEvent e) {
            if (FlatPopupFactory.DropShadowPopup.this.dropShadowPanel != null) {
              FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.setVisible(false);
            }
          }
          
          public void componentMoved(ComponentEvent e) {
            if (FlatPopupFactory.DropShadowPopup.this.dropShadowPanel != null && FlatPopupFactory.DropShadowPopup.this.mediumWeightPanel != null) {
              Point location = FlatPopupFactory.DropShadowPopup.this.mediumWeightPanel.getLocation();
              Insets insets = FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.getInsets();
              FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.setLocation(location.x - insets.left, location.y - insets.top);
            } 
          }

          
          public void componentResized(ComponentEvent e) {
            if (FlatPopupFactory.DropShadowPopup.this.dropShadowPanel != null)
              FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.setSize(FlatUIUtils.addInsets(FlatPopupFactory.DropShadowPopup.this.mediumWeightPanel.getSize(), FlatPopupFactory.DropShadowPopup.this.dropShadowPanel.getInsets())); 
          }
        };
      this.mediumWeightPanel.addComponentListener(this.mediumPanelListener);
    }
    
    private void hideMediumWeightDropShadow() {
      this.mediumWeightPanel.removeComponentListener(this.mediumPanelListener);
      
      Container parent = this.dropShadowPanel.getParent();
      if (parent != null) {
        Rectangle bounds = this.dropShadowPanel.getBounds();
        parent.remove(this.dropShadowPanel);
        parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
      } 
    }
  }
}
