package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;




























public class FlatRadioButtonIcon
  extends FlatCheckBoxIcon
{
  protected final int centerDiameter = getUIInt("RadioButton.icon.centerDiameter", 8, this.style);


  
  protected void paintFocusBorder(Component c, Graphics2D g) {
    int wh = 15 + this.focusWidth * 2;
    g.fillOval(-this.focusWidth, -this.focusWidth, wh, wh);
  }

  
  protected void paintBorder(Component c, Graphics2D g) {
    g.fillOval(0, 0, 15, 15);
  }

  
  protected void paintBackground(Component c, Graphics2D g) {
    g.fillOval(1, 1, 13, 13);
  }

  
  protected void paintCheckmark(Component c, Graphics2D g) {
    float xy = (15 - this.centerDiameter) / 2.0F;
    g.fill(new Ellipse2D.Float(xy, xy, this.centerDiameter, this.centerDiameter));
  }
}
