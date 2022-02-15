package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

































public class FlatSeparatorUI
  extends BasicSeparatorUI
{
  protected int height;
  protected int stripeWidth;
  protected int stripeIndent;
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatSeparatorUI.class, FlatSeparatorUI::new);
  }

  
  protected void installDefaults(JSeparator s) {
    super.installDefaults(s);
    
    if (!this.defaults_initialized) {
      String prefix = getPropertyPrefix();
      this.height = UIManager.getInt(prefix + ".height");
      this.stripeWidth = UIManager.getInt(prefix + ".stripeWidth");
      this.stripeIndent = UIManager.getInt(prefix + ".stripeIndent");
      
      this.defaults_initialized = true;
    } 
  }

  
  protected void uninstallDefaults(JSeparator s) {
    super.uninstallDefaults(s);
    this.defaults_initialized = false;
  }
  
  protected String getPropertyPrefix() {
    return "Separator";
  }

  
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2 = (Graphics2D)g.create();
    
    try { FlatUIUtils.setRenderingHints(g2);
      g2.setColor(c.getForeground());
      
      float width = UIScale.scale(this.stripeWidth);
      float indent = UIScale.scale(this.stripeIndent);
      
      if (((JSeparator)c).getOrientation() == 1) {
        g2.fill(new Rectangle2D.Float(indent, 0.0F, width, c.getHeight()));
      } else {
        g2.fill(new Rectangle2D.Float(0.0F, indent, c.getWidth(), width));
      }  }
    finally { g2.dispose(); }
  
  }

  
  public Dimension getPreferredSize(JComponent c) {
    if (((JSeparator)c).getOrientation() == 1) {
      return new Dimension(UIScale.scale(this.height), 0);
    }
    return new Dimension(0, UIScale.scale(this.height));
  }
}
