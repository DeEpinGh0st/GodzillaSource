package com.formdev.flatlaf.extras.components;

import javax.swing.JTextArea;


























public class FlatTextArea
  extends JTextArea
  implements FlatComponentExtension
{
  public int getMinimumWidth() {
    return getClientPropertyInt("JComponent.minimumWidth", "Component.minimumWidth");
  }



  
  public void setMinimumWidth(int minimumWidth) {
    putClientProperty("JComponent.minimumWidth", (minimumWidth >= 0) ? Integer.valueOf(minimumWidth) : null);
  }
}
