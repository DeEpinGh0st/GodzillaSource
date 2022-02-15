package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.JMenu;
import javax.swing.UIManager;




























public class FlatMenuArrowIcon
  extends FlatAbstractIcon
{
  protected final boolean chevron = FlatUIUtils.isChevron(UIManager.getString("Component.arrowType"));
  protected final Color arrowColor = UIManager.getColor("Menu.icon.arrowColor");
  protected final Color disabledArrowColor = UIManager.getColor("Menu.icon.disabledArrowColor");
  protected final Color selectionForeground = UIManager.getColor("Menu.selectionForeground");
  
  public FlatMenuArrowIcon() {
    super(6, 10, null);
  }

  
  protected void paintIcon(Component c, Graphics2D g) {
    if (!c.getComponentOrientation().isLeftToRight()) {
      g.rotate(Math.toRadians(180.0D), this.width / 2.0D, this.height / 2.0D);
    }
    g.setColor(getArrowColor(c));
    if (this.chevron) {
      
      Path2D path = FlatUIUtils.createPath(false, new double[] { 1.0D, 1.0D, 5.0D, 5.0D, 1.0D, 9.0D });
      g.setStroke(new BasicStroke(1.0F));
      g.draw(path);
    } else {
      
      g.fill(FlatUIUtils.createPath(new double[] { 0.0D, 0.5D, 5.0D, 5.0D, 0.0D, 9.5D }));
    } 
  }
  
  protected Color getArrowColor(Component c) {
    if (c instanceof JMenu && ((JMenu)c).isSelected() && !isUnderlineSelection()) {
      return this.selectionForeground;
    }
    return c.isEnabled() ? this.arrowColor : this.disabledArrowColor;
  }

  
  protected boolean isUnderlineSelection() {
    return "underline".equals(UIManager.getString("MenuItem.selectionType"));
  }
}
