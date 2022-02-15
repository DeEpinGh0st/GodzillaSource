package com.formdev.flatlaf.extras.components;

import javax.swing.JSpinner;



























public class FlatSpinner
  extends JSpinner
  implements FlatComponentExtension
{
  public int getMinimumWidth() {
    return getClientPropertyInt("JComponent.minimumWidth", "Component.minimumWidth");
  }



  
  public void setMinimumWidth(int minimumWidth) {
    putClientProperty("JComponent.minimumWidth", (minimumWidth >= 0) ? Integer.valueOf(minimumWidth) : null);
  }




  
  public boolean isRoundRect() {
    return getClientPropertyBoolean("JComponent.roundRect", false);
  }



  
  public void setRoundRect(boolean roundRect) {
    putClientPropertyBoolean("JComponent.roundRect", roundRect, false);
  }




  
  public Object getOutline() {
    return getClientProperty("JComponent.outline");
  }













  
  public void setOutline(Object outline) {
    putClientProperty("JComponent.outline", outline);
  }
}
