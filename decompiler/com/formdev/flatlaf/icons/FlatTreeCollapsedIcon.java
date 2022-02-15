package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;

























public class FlatTreeCollapsedIcon
  extends FlatAbstractIcon
{
  private final boolean chevron;
  
  public FlatTreeCollapsedIcon() {
    this(UIManager.getColor("Tree.icon.collapsedColor"));
  }
  
  FlatTreeCollapsedIcon(Color color) {
    super(11, 11, color);
    this.chevron = FlatUIUtils.isChevron(UIManager.getString("Component.arrowType"));
  }

  
  protected void paintIcon(Component c, Graphics2D g) {
    rotate(c, g);
    
    if (this.chevron) {
      
      g.fill(FlatUIUtils.createPath(new double[] { 3.0D, 1.0D, 3.0D, 2.5D, 6.0D, 5.5D, 3.0D, 8.5D, 3.0D, 10.0D, 4.5D, 10.0D, 9.0D, 5.5D, 4.5D, 1.0D }));
    } else {
      
      g.fill(FlatUIUtils.createPath(new double[] { 2.0D, 1.0D, 2.0D, 10.0D, 10.0D, 5.5D }));
    } 
  }
  
  void rotate(Component c, Graphics2D g) {
    if (!c.getComponentOrientation().isLeftToRight())
      g.rotate(Math.toRadians(180.0D), this.width / 2.0D, this.height / 2.0D); 
  }
}
