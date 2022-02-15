package com.formdev.flatlaf.extras.components;

import javax.swing.JScrollBar;


























public class FlatScrollBar
  extends JScrollBar
  implements FlatComponentExtension
{
  public boolean isShowButtons() {
    return getClientPropertyBoolean("JScrollBar.showButtons", "ScrollBar.showButtons");
  }



  
  public void setShowButtons(boolean showButtons) {
    putClientProperty("JScrollBar.showButtons", Boolean.valueOf(showButtons));
  }
}
