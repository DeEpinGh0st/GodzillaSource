package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.UIManager;


























public class FlatMenuItemBorder
  extends FlatMarginBorder
{
  private final Insets menuBarItemMargins = UIManager.getInsets("MenuBar.itemMargins");

  
  public Insets getBorderInsets(Component c, Insets insets) {
    if (c.getParent() instanceof javax.swing.JMenuBar) {
      insets.top = UIScale.scale(this.menuBarItemMargins.top);
      insets.left = UIScale.scale(this.menuBarItemMargins.left);
      insets.bottom = UIScale.scale(this.menuBarItemMargins.bottom);
      insets.right = UIScale.scale(this.menuBarItemMargins.right);
      return insets;
    } 
    return super.getBorderInsets(c, insets);
  }
}
