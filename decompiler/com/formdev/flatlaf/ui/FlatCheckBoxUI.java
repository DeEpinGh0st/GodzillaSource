package com.formdev.flatlaf.ui;

import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;




































public class FlatCheckBoxUI
  extends FlatRadioButtonUI
{
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatCheckBoxUI.class, FlatCheckBoxUI::new);
  }

  
  public String getPropertyPrefix() {
    return "CheckBox.";
  }
}
