package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Graphics2D;


























public class FlatWindowMaximizeIcon
  extends FlatWindowAbstractIcon
{
  protected void paintIconAt1x(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
    int iwh = (int)(10.0D * scaleFactor);
    int ix = x + (width - iwh) / 2;
    int iy = y + (height - iwh) / 2;
    int thickness = (int)scaleFactor;
    
    g.fill(FlatUIUtils.createRectangle(ix, iy, iwh, iwh, thickness));
  }
}
