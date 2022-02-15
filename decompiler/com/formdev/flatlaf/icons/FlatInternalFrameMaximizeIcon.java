package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;


























public class FlatInternalFrameMaximizeIcon
  extends FlatInternalFrameAbstractIcon
{
  protected void paintIcon(Component c, Graphics2D g) {
    paintBackground(c, g);
    
    g.setColor(c.getForeground());
    g.fill(FlatUIUtils.createRectangle((this.width / 2 - 4), (this.height / 2 - 4), 8.0F, 8.0F, 1.0F));
  }
}
