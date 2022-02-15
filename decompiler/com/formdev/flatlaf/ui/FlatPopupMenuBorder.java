package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.UIManager;


























public class FlatPopupMenuBorder
  extends FlatLineBorder
{
  public FlatPopupMenuBorder() {
    super(UIManager.getInsets("PopupMenu.borderInsets"), 
        UIManager.getColor("PopupMenu.borderColor"));
  }

  
  public Insets getBorderInsets(Component c, Insets insets) {
    if (c instanceof Container && ((Container)c)
      .getComponentCount() > 0 && ((Container)c)
      .getComponent(0) instanceof javax.swing.JScrollPane) {

      
      insets.left = insets.top = insets.right = insets.bottom = UIScale.scale(1);
      return insets;
    } 
    
    return super.getBorderInsets(c, insets);
  }
}
