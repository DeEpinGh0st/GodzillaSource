package com.formdev.flatlaf.extras.components;

import javax.swing.JTree;

























public class FlatTree
  extends JTree
  implements FlatComponentExtension
{
  public boolean isWideSelection() {
    return getClientPropertyBoolean("JTree.wideSelection", "Tree.wideSelection");
  }



  
  public void setWideSelection(boolean wideSelection) {
    putClientProperty("JTree.wideSelection", Boolean.valueOf(wideSelection));
  }




  
  public boolean isPaintSelection() {
    return getClientPropertyBoolean("JTree.paintSelection", true);
  }




  
  public void setPaintSelection(boolean paintSelection) {
    putClientProperty("JTree.paintSelection", Boolean.valueOf(paintSelection));
  }
}
