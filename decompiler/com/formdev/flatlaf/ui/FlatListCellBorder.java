package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

























public class FlatListCellBorder
  extends FlatLineBorder
{
  final boolean showCellFocusIndicator = UIManager.getBoolean("List.showCellFocusIndicator");
  
  protected FlatListCellBorder() {
    super(UIManager.getInsets("List.cellMargins"), UIManager.getColor("List.cellFocusColor"));
  }








  
  public static class Default
    extends FlatListCellBorder
  {
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {}
  }







  
  public static class Focused
    extends FlatListCellBorder {}







  
  public static class Selected
    extends FlatListCellBorder
  {
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if (!this.showCellFocusIndicator) {
        return;
      }
      
      JList<?> list = (JList)SwingUtilities.getAncestorOfClass(JList.class, c);
      if (list != null && list.getMinSelectionIndex() == list.getMaxSelectionIndex()) {
        return;
      }
      super.paintBorder(c, g, x, y, width, height);
    }
  }
}
