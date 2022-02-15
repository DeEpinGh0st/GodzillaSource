package com.formdev.flatlaf.extras.components;

import javax.swing.JScrollPane;



























public class FlatScrollPane
  extends JScrollPane
  implements FlatComponentExtension
{
  public boolean isShowButtons() {
    return getClientPropertyBoolean("JScrollBar.showButtons", "ScrollBar.showButtons");
  }



  
  public void setShowButtons(boolean showButtons) {
    putClientProperty("JScrollBar.showButtons", Boolean.valueOf(showButtons));
  }




  
  public boolean isSmoothScrolling() {
    return getClientPropertyBoolean("JScrollPane.smoothScrolling", "ScrollPane.smoothScrolling");
  }



  
  public void setSmoothScrolling(boolean smoothScrolling) {
    putClientProperty("JScrollPane.smoothScrolling", Boolean.valueOf(smoothScrolling));
  }




  
  public Object getOutline() {
    return getClientProperty("JComponent.outline");
  }













  
  public void setOutline(Object outline) {
    putClientProperty("JComponent.outline", outline);
  }
}
