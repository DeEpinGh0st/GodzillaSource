package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
























public class FlatTreeExpandedIcon
  extends FlatTreeCollapsedIcon
{
  public FlatTreeExpandedIcon() {
    super(UIManager.getColor("Tree.icon.expandedColor"));
  }

  
  void rotate(Component c, Graphics2D g) {
    g.rotate(Math.toRadians(90.0D), this.width / 2.0D, this.height / 2.0D);
  }
}
