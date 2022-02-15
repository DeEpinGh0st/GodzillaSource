package com.formdev.flatlaf.ui;

import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;




























public class FlatPanelUI
  extends BasicPanelUI
{
  public static ComponentUI createUI(JComponent c) {
    return FlatUIUtils.createSharedUI(FlatPanelUI.class, FlatPanelUI::new);
  }
}
