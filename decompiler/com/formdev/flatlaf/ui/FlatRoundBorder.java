package com.formdev.flatlaf.ui;

import java.awt.Component;
import javax.swing.UIManager;
























public class FlatRoundBorder
  extends FlatBorder
{
  protected final int arc = UIManager.getInt("Component.arc");

  
  protected int getArc(Component c) {
    if (isCellEditor(c)) {
      return 0;
    }
    Boolean roundRect = FlatUIUtils.isRoundRect(c);
    return (roundRect != null) ? (roundRect.booleanValue() ? 32767 : 0) : this.arc;
  }
}
