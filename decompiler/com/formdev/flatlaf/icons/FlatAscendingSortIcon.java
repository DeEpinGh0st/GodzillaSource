package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;

























public class FlatAscendingSortIcon
  extends FlatAbstractIcon
{
  protected final boolean chevron = FlatUIUtils.isChevron(UIManager.getString("Component.arrowType"));
  protected final Color sortIconColor = UIManager.getColor("Table.sortIconColor");
  
  public FlatAscendingSortIcon() {
    super(10, 5, null);
  }

  
  protected void paintIcon(Component c, Graphics2D g) {
    g.setColor(this.sortIconColor);
    if (this.chevron) {
      
      Path2D path = FlatUIUtils.createPath(false, new double[] { 1.0D, 4.0D, 5.0D, 0.0D, 9.0D, 4.0D });
      g.setStroke(new BasicStroke(1.0F));
      g.draw(path);
    } else {
      
      g.fill(FlatUIUtils.createPath(new double[] { 0.5D, 5.0D, 5.0D, 0.0D, 9.5D, 5.0D }));
    } 
  }
}
