package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;













































public class FlatSplitPaneUI
  extends BasicSplitPaneUI
{
  protected String arrowType;
  private Boolean continuousLayout;
  protected Color oneTouchArrowColor;
  protected Color oneTouchHoverArrowColor;
  protected Color oneTouchPressedArrowColor;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatSplitPaneUI();
  }

  
  protected void installDefaults() {
    this.arrowType = UIManager.getString("Component.arrowType");


    
    this.oneTouchArrowColor = UIManager.getColor("SplitPaneDivider.oneTouchArrowColor");
    this.oneTouchHoverArrowColor = UIManager.getColor("SplitPaneDivider.oneTouchHoverArrowColor");
    this.oneTouchPressedArrowColor = UIManager.getColor("SplitPaneDivider.oneTouchPressedArrowColor");
    
    super.installDefaults();
    
    this.continuousLayout = (Boolean)UIManager.get("SplitPane.continuousLayout");
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.oneTouchArrowColor = null;
    this.oneTouchHoverArrowColor = null;
    this.oneTouchPressedArrowColor = null;
  }

  
  public boolean isContinuousLayout() {
    return (super.isContinuousLayout() || (this.continuousLayout != null && Boolean.TRUE.equals(this.continuousLayout)));
  }

  
  public BasicSplitPaneDivider createDefaultDivider() {
    return new FlatSplitPaneDivider(this);
  }


  
  protected class FlatSplitPaneDivider
    extends BasicSplitPaneDivider
  {
    protected final String style = UIManager.getString("SplitPaneDivider.style");
    protected final Color gripColor = UIManager.getColor("SplitPaneDivider.gripColor");
    protected final int gripDotCount = FlatUIUtils.getUIInt("SplitPaneDivider.gripDotCount", 3);
    protected final int gripDotSize = FlatUIUtils.getUIInt("SplitPaneDivider.gripDotSize", 3);
    protected final int gripGap = FlatUIUtils.getUIInt("SplitPaneDivider.gripGap", 2);
    
    protected FlatSplitPaneDivider(BasicSplitPaneUI ui) {
      super(ui);
      
      setLayout(new FlatDividerLayout());
    }

    
    public void setDividerSize(int newSize) {
      super.setDividerSize(UIScale.scale(newSize));
    }

    
    protected JButton createLeftOneTouchButton() {
      return new FlatOneTouchButton(true);
    }

    
    protected JButton createRightOneTouchButton() {
      return new FlatOneTouchButton(false);
    }

    
    public void propertyChange(PropertyChangeEvent e) {
      super.propertyChange(e);
      
      switch (e.getPropertyName()) {
        
        case "dividerLocation":
          revalidate();
          break;
      } 
    }

    
    public void paint(Graphics g) {
      super.paint(g);
      
      if ("plain".equals(this.style)) {
        return;
      }
      Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g);
      
      g.setColor(this.gripColor);
      paintGrip(g, 0, 0, getWidth(), getHeight());
      
      FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
    }
    
    protected void paintGrip(Graphics g, int x, int y, int width, int height) {
      FlatUIUtils.paintGrip(g, x, y, width, height, 
          (this.splitPane.getOrientation() == 0), this.gripDotCount, this.gripDotSize, this.gripGap, true);
    }

    
    protected boolean isLeftCollapsed() {
      int location = this.splitPane.getDividerLocation();
      Insets insets = this.splitPane.getInsets();
      return (this.orientation == 0) ? ((location == insets.top)) : ((location == insets.left));
    }


    
    protected boolean isRightCollapsed() {
      int location = this.splitPane.getDividerLocation();
      Insets insets = this.splitPane.getInsets();
      return (this.orientation == 0) ? (
        (location == this.splitPane.getHeight() - getHeight() - insets.bottom)) : (
        (location == this.splitPane.getWidth() - getWidth() - insets.right));
    }

    
    protected class FlatOneTouchButton
      extends FlatArrowButton
    {
      protected final boolean left;

      
      protected FlatOneTouchButton(boolean left) {
        super(1, FlatSplitPaneUI.this.arrowType, FlatSplitPaneUI.this.oneTouchArrowColor, (Color)null, FlatSplitPaneUI.this.oneTouchHoverArrowColor, (Color)null, FlatSplitPaneUI.this.oneTouchPressedArrowColor, (Color)null);
        
        setCursor(Cursor.getPredefinedCursor(0));
        ToolTipManager.sharedInstance().registerComponent(this);
        
        this.left = left;
      }

      
      public int getDirection() {
        return (FlatSplitPaneUI.FlatSplitPaneDivider.this.orientation == 0) ? (this.left ? 1 : 5) : (this.left ? 7 : 3);
      }















      
      public String getToolTipText(MouseEvent e) {
        String key = (FlatSplitPaneUI.FlatSplitPaneDivider.this.orientation == 0) ? (this.left ? (FlatSplitPaneUI.FlatSplitPaneDivider.this.isRightCollapsed() ? "SplitPaneDivider.expandBottomToolTipText" : "SplitPaneDivider.collapseTopToolTipText") : (FlatSplitPaneUI.FlatSplitPaneDivider.this.isLeftCollapsed() ? "SplitPaneDivider.expandTopToolTipText" : "SplitPaneDivider.collapseBottomToolTipText")) : (this.left ? (FlatSplitPaneUI.FlatSplitPaneDivider.this.isRightCollapsed() ? "SplitPaneDivider.expandRightToolTipText" : "SplitPaneDivider.collapseLeftToolTipText") : (FlatSplitPaneUI.FlatSplitPaneDivider.this.isLeftCollapsed() ? "SplitPaneDivider.expandLeftToolTipText" : "SplitPaneDivider.collapseRightToolTipText"));



        
        Object value = FlatSplitPaneUI.FlatSplitPaneDivider.this.splitPane.getClientProperty(key);
        if (value instanceof String) {
          return (String)value;
        }
        
        return UIManager.getString(key, getLocale());
      }
    }



    
    protected class FlatDividerLayout
      extends BasicSplitPaneDivider.DividerLayout
    {
      public void layoutContainer(Container c) {
        super.layoutContainer(c);
        
        if (FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton == null || FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton == null || !FlatSplitPaneUI.FlatSplitPaneDivider.this.splitPane.isOneTouchExpandable()) {
          return;
        }

        
        int extraSize = UIScale.scale(4);
        if (FlatSplitPaneUI.FlatSplitPaneDivider.this.orientation == 0) {
          FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.setSize(FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getWidth() + extraSize, FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getHeight());
          FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.setBounds(FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getX() + FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getWidth(), FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.getY(), FlatSplitPaneUI.FlatSplitPaneDivider.this
              .rightButton.getWidth() + extraSize, FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.getHeight());
        } else {
          FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.setSize(FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getWidth(), FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getHeight() + extraSize);
          FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.setBounds(FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.getX(), FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getY() + FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getHeight(), FlatSplitPaneUI.FlatSplitPaneDivider.this
              .rightButton.getWidth(), FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.getHeight() + extraSize);
        } 

        
        boolean leftCollapsed = FlatSplitPaneUI.FlatSplitPaneDivider.this.isLeftCollapsed();
        if (leftCollapsed)
          FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.setLocation(FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.getLocation()); 
        FlatSplitPaneUI.FlatSplitPaneDivider.this.leftButton.setVisible(!leftCollapsed);
        FlatSplitPaneUI.FlatSplitPaneDivider.this.rightButton.setVisible(!FlatSplitPaneUI.FlatSplitPaneDivider.this.isRightCollapsed());
      }
    }
  }
}
