package org.fife.rsta.ac.java;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;











































public class DecoratableIcon
  implements Icon
{
  private int width;
  private Icon mainIcon;
  private List<Icon> decorations;
  private boolean deprecated;
  private static final int DEFAULT_WIDTH = 24;
  
  public DecoratableIcon(Icon mainIcon) {
    this(24, mainIcon);
  }







  
  public DecoratableIcon(int width, Icon mainIcon) {
    setMainIcon(mainIcon);
    this.width = width;
  }








  
  public void addDecorationIcon(Icon decoration) {
    if (decoration == null) {
      throw new IllegalArgumentException("decoration cannot be null");
    }
    if (this.decorations == null) {
      this.decorations = new ArrayList<>(1);
    }
    this.decorations.add(decoration);
  }





  
  public int getIconHeight() {
    return this.mainIcon.getIconHeight();
  }





  
  public int getIconWidth() {
    return this.width;
  }





  
  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (this.deprecated) {
      IconFactory.get().getIcon("deprecatedIcon")
        .paintIcon(c, g, x, y);
    }
    this.mainIcon.paintIcon(c, g, x, y);
    if (this.decorations != null) {
      x = x + getIconWidth() - 8;
      for (int i = this.decorations.size() - 1; i >= 0; i--) {
        Icon icon = this.decorations.get(i);
        icon.paintIcon(c, g, x, y);
        x -= 8;
      } 
    } 
  }






  
  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }







  
  public void setMainIcon(Icon icon) {
    if (icon == null) {
      throw new IllegalArgumentException("icon cannot be null");
    }
    this.mainIcon = icon;
  }
}
