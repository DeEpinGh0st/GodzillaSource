package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;





























public class FlatToolBarSeparatorUI
  extends BasicToolBarSeparatorUI
{
  private static final int LINE_WIDTH = 1;
  protected int separatorWidth;
  protected Color separatorColor;
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatToolBarSeparatorUI.class, FlatToolBarSeparatorUI::new);
  }

  
  protected void installDefaults(JSeparator c) {
    super.installDefaults(c);
    
    if (!this.defaults_initialized) {
      this.separatorWidth = UIManager.getInt("ToolBar.separatorWidth");
      this.separatorColor = UIManager.getColor("ToolBar.separatorColor");
      
      this.defaults_initialized = true;
    } 


    
    c.setAlignmentX(0.0F);
  }

  
  protected void uninstallDefaults(JSeparator s) {
    super.uninstallDefaults(s);
    this.defaults_initialized = false;
  }

  
  public Dimension getPreferredSize(JComponent c) {
    Dimension size = ((JToolBar.Separator)c).getSeparatorSize();
    
    if (size != null) {
      return UIScale.scale(size);
    }
    
    int sepWidth = UIScale.scale((this.separatorWidth - 1) / 2) * 2 + UIScale.scale(1);
    
    boolean vertical = isVertical(c);
    return new Dimension(vertical ? sepWidth : 0, vertical ? 0 : sepWidth);
  }

  
  public Dimension getMaximumSize(JComponent c) {
    Dimension size = getPreferredSize(c);
    if (isVertical(c)) {
      return new Dimension(size.width, 32767);
    }
    return new Dimension(32767, size.height);
  }

  
  public void paint(Graphics g, JComponent c) {
    int width = c.getWidth();
    int height = c.getHeight();
    float lineWidth = UIScale.scale(1.0F);
    float offset = UIScale.scale(2.0F);
    
    Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g);
    g.setColor(this.separatorColor);
    
    if (isVertical(c)) {
      ((Graphics2D)g).fill(new Rectangle2D.Float(Math.round((width - lineWidth) / 2.0F), offset, lineWidth, height - offset * 2.0F));
    } else {
      ((Graphics2D)g).fill(new Rectangle2D.Float(offset, Math.round((height - lineWidth) / 2.0F), width - offset * 2.0F, lineWidth));
    } 
    FlatUIUtils.resetRenderingHints(g, oldRenderingHints);
  }
  
  private boolean isVertical(JComponent c) {
    return (((JToolBar.Separator)c).getOrientation() == 1);
  }
}
