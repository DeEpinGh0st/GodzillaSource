package org.fife.rsta.ui;

import java.awt.Dimension;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;































public class MaxWidthComboBox<E>
  extends JComboBox<E>
{
  private static final long serialVersionUID = 1L;
  private int maxWidth;
  
  public MaxWidthComboBox(int maxWidth) {
    this.maxWidth = maxWidth;
  }







  
  public MaxWidthComboBox(ComboBoxModel<E> model, int maxWidth) {
    super(model);
    this.maxWidth = maxWidth;
  }








  
  public Dimension getMaximumSize() {
    Dimension size = super.getMaximumSize();
    size.width = Math.min(size.width, this.maxWidth);
    return size;
  }








  
  public Dimension getMinimumSize() {
    Dimension size = super.getMinimumSize();
    size.width = Math.min(size.width, this.maxWidth);
    return size;
  }








  
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    size.width = Math.min(size.width, this.maxWidth);
    return size;
  }
}
