package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
























public class FlatFileViewComputerIcon
  extends FlatAbstractIcon
{
  public FlatFileViewComputerIcon() {
    super(16, 16, UIManager.getColor("Objects.Grey"));
  }










  
  protected void paintIcon(Component c, Graphics2D g) {
    Path2D path = new Path2D.Float(0);
    path.append(new Rectangle2D.Float(2.0F, 3.0F, 12.0F, 8.0F), false);
    path.append(new Rectangle2D.Float(4.0F, 5.0F, 8.0F, 4.0F), false);
    g.fill(path);
    
    g.fillRect(2, 12, 12, 2);
  }
}
