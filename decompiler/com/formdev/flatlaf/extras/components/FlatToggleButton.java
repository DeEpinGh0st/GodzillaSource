package com.formdev.flatlaf.extras.components;

import java.awt.Color;
import javax.swing.JToggleButton;



























public class FlatToggleButton
  extends JToggleButton
  implements FlatComponentExtension
{
  public FlatButton.ButtonType getButtonType() {
    return getClientPropertyEnumString("JButton.buttonType", FlatButton.ButtonType.class, null, FlatButton.ButtonType.none);
  }



  
  public void setButtonType(FlatButton.ButtonType buttonType) {
    if (buttonType == FlatButton.ButtonType.none)
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
    return getClientPropertyInt("JComponent.minimumWidth", "ToggleButton.minimumWidth");
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




  
  public int getTabUnderlineHeight() {
    return getClientPropertyInt("JToggleButton.tab.underlineHeight", "ToggleButton.tab.underlineHeight");
  }



  
  public void setTabUnderlineHeight(int tabUnderlineHeight) {
    putClientProperty("JToggleButton.tab.underlineHeight", (tabUnderlineHeight >= 0) ? Integer.valueOf(tabUnderlineHeight) : null);
  }




  
  public Color getTabUnderlineColor() {
    return getClientPropertyColor("JToggleButton.tab.underlineColor", "ToggleButton.tab.underlineColor");
  }



  
  public void setTabUnderlineColor(Color tabUnderlineColor) {
    putClientProperty("JToggleButton.tab.underlineColor", tabUnderlineColor);
  }




  
  public Color getTabSelectedBackground() {
    return getClientPropertyColor("JToggleButton.tab.selectedBackground", "ToggleButton.tab.selectedBackground");
  }



  
  public void setTabSelectedBackground(Color tabSelectedBackground) {
    putClientProperty("JToggleButton.tab.selectedBackground", tabSelectedBackground);
  }
}
