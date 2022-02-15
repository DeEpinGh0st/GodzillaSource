package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
























public class FlatFileViewHardDriveIcon
  extends FlatAbstractIcon
{
  public FlatFileViewHardDriveIcon() {
    super(16, 16, UIManager.getColor("Objects.Grey"));
  }







  
  protected void paintIcon(Component c, Graphics2D g) {
    Path2D path = new Path2D.Float(0);
    path.append(new Rectangle2D.Float(2.0F, 6.0F, 12.0F, 4.0F), false);
    path.append(new Rectangle2D.Float(12.0F, 8.0F, 1.0F, 1.0F), false);
    path.append(new Rectangle2D.Float(10.0F, 8.0F, 1.0F, 1.0F), false);
    g.fill(path);
  }
}
