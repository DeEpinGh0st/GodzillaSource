package com.formdev.flatlaf.extras.components;

import javax.swing.JButton;























public class FlatButton
  extends JButton
  implements FlatComponentExtension
{
  public enum ButtonType
  {
    none, square, roundRect, tab, help, toolBarButton;
  }


  
  public ButtonType getButtonType() {
    return getClientPropertyEnumString("JButton.buttonType", ButtonType.class, null, ButtonType.none);
  }



  
  public void setButtonType(ButtonType buttonType) {
    if (buttonType == ButtonType.none)
      buttonType = null; 
    putClientPropertyEnumString("JButton.buttonType", buttonType);
  }




  
  public boolean isSquareSize() {
    return getClientPropertyBoolean("JButton.squareSize", false);
  }



  
  public void setSquareSize(boolean squareSize) {
    putClientPropertyBoolean("JButton.squareSize", squareSize, false);
  }




  
  public int getMinimumWidth() {
    return getClientPropertyInt("JComponent.minimumWidth", "Button.minimumWidth");
  }



  
  public void setMinimumWidth(int minimumWidth) {
    putClientProperty("JComponent.minimumWidth", (minimumWidth >= 0) ? Integer.valueOf(minimumWidth) : null);
  }




  
  public int getMinimumHeight() {
    return getClientPropertyInt("JComponent.minimumHeight", 0);
  }



  
  public void setMinimumHeight(int minimumHeight) {
    putClientProperty("JComponent.minimumHeight", (minimumHeight >= 0) ? Integer.valueOf(minimumHeight) : null);
  }




  
  public Object getOutline() {
    return getClientProperty("JComponent.outline");
  }













  
  public void setOutline(Object outline) {
    putClientProperty("JComponent.outline", outline);
  }
}
