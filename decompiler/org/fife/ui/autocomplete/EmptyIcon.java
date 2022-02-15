package org.fife.ui.autocomplete;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;


















public class EmptyIcon
  implements Icon, Serializable
{
  private int size;
  
  public EmptyIcon(int size) {
    this.size = size;
  }


  
  public int getIconHeight() {
    return this.size;
  }


  
  public int getIconWidth() {
    return this.size;
  }
  
  public void paintIcon(Component c, Graphics g, int x, int y) {}
}
