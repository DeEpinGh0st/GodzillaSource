package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;


























public abstract class FlatAbstractIcon
  implements Icon, UIResource
{
  protected final int width;
  protected final int height;
  protected final Color color;
  
  public FlatAbstractIcon(int width, int height, Color color) {
    this.width = width;
    this.height = height;
    this.color = color;
  }

  
  public void paintIcon(Component c, Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D)g.create();
    try {
      FlatUIUtils.setRenderingHints(g2);




      
      g2.translate(x, y);
      UIScale.scaleGraphics(g2);
      
      if (this.color != null) {
        g2.setColor(this.color);
      }
      paintIcon(c, g2);
    } finally {
      g2.dispose();
    } 
  }

  
  protected abstract void paintIcon(Component paramComponent, Graphics2D paramGraphics2D);
  
  public int getIconWidth() {
    return UIScale.scale(this.width);
  }

  
  public int getIconHeight() {
    return UIScale.scale(this.height);
  }
}
