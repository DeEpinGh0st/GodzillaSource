package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatButtonUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;




























public class FlatInternalFrameCloseIcon
  extends FlatInternalFrameAbstractIcon
{
  private final Color hoverForeground = UIManager.getColor("InternalFrame.closeHoverForeground");
  private final Color pressedForeground = UIManager.getColor("InternalFrame.closePressedForeground");
  
  public FlatInternalFrameCloseIcon() {
    super(UIManager.getDimension("InternalFrame.buttonSize"), 
        UIManager.getColor("InternalFrame.closeHoverBackground"), 
        UIManager.getColor("InternalFrame.closePressedBackground"));
  }

  
  protected void paintIcon(Component c, Graphics2D g) {
    paintBackground(c, g);
    
    g.setColor(FlatButtonUI.buttonStateColor(c, c.getForeground(), null, null, this.hoverForeground, this.pressedForeground));
    
    float mx = (this.width / 2);
    float my = (this.height / 2);
    float r = 3.25F;
    
    Path2D path = new Path2D.Float(0);
    path.append(new Line2D.Float(mx - r, my - r, mx + r, my + r), false);
    path.append(new Line2D.Float(mx - r, my + r, mx + r, my - r), false);
    g.setStroke(new BasicStroke(1.0F));
    g.draw(path);
  }
}
