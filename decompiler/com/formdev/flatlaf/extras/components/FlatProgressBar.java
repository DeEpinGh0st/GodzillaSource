package com.formdev.flatlaf.extras.components;

import javax.swing.JProgressBar;


























public class FlatProgressBar
  extends JProgressBar
  implements FlatComponentExtension
{
  public boolean isLargeHeight() {
    return getClientPropertyBoolean("JProgressBar.largeHeight", false);
  }



  
  public void setLargeHeight(boolean largeHeight) {
    putClientPropertyBoolean("JProgressBar.largeHeight", largeHeight, false);
  }




  
  public boolean isSquare() {
    return getClientPropertyBoolean("JProgressBar.square", false);
  }



  
  public void setSquare(boolean square) {
    putClientPropertyBoolean("JProgressBar.square", square, false);
  }
}
