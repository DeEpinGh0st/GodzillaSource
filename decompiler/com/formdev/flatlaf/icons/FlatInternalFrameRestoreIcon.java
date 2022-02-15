package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;


























public class FlatInternalFrameRestoreIcon
  extends FlatInternalFrameAbstractIcon
{
  protected void paintIcon(Component c, Graphics2D g) {
    paintBackground(c, g);
    
    g.setColor(c.getForeground());
    
    int x = this.width / 2 - 4;
    int y = this.height / 2 - 4;
    Path2D r1 = FlatUIUtils.createRectangle((x + 1), (y - 1), 8.0F, 8.0F, 1.0F);
    Path2D r2 = FlatUIUtils.createRectangle((x - 1), (y + 1), 8.0F, 8.0F, 1.0F);
    
    Area area = new Area(r1);
    area.subtract(new Area(new Rectangle2D.Float((x - 1), (y + 1), 8.0F, 8.0F)));
    g.fill(area);
    
    g.fill(r2);
  }
}
