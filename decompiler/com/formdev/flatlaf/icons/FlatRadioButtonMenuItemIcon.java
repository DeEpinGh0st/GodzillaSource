package com.formdev.flatlaf.icons;

import java.awt.Graphics2D;























public class FlatRadioButtonMenuItemIcon
  extends FlatCheckBoxMenuItemIcon
{
  protected void paintCheckmark(Graphics2D g2) {
    g2.fillOval(4, 4, 7, 7);
  }
}
