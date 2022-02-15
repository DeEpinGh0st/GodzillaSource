package com.formdev.flatlaf.ui;

import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
































public class FlatPopupMenuSeparatorUI
  extends FlatSeparatorUI
{
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatPopupMenuSeparatorUI.class, FlatPopupMenuSeparatorUI::new);
  }

  
  protected String getPropertyPrefix() {
    return "PopupMenuSeparator";
  }
}
