package com.formdev.flatlaf.extras.components;

import javax.swing.JPasswordField;




























public class FlatPasswordField
  extends JPasswordField
  implements FlatComponentExtension
{
  public String getPlaceholderText() {
    return (String)getClientProperty("JTextField.placeholderText");
  }



  
  public void setPlaceholderText(String placeholderText) {
    putClientProperty("JTextField.placeholderText", placeholderText);
  }




  
  public FlatTextField.SelectAllOnFocusPolicy getSelectAllOnFocusPolicy() {
    return getClientPropertyEnumString("JTextField.selectAllOnFocusPolicy", FlatTextField.SelectAllOnFocusPolicy.class, "TextComponent.selectAllOnFocusPolicy", FlatTextField.SelectAllOnFocusPolicy.once);
  }




  
  public void setSelectAllOnFocusPolicy(FlatTextField.SelectAllOnFocusPolicy selectAllOnFocusPolicy) {
    putClientPropertyEnumString("JTextField.selectAllOnFocusPolicy", selectAllOnFocusPolicy);
  }




  
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
