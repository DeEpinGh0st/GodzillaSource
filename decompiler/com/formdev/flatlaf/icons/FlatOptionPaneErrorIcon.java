package com.formdev.flatlaf.icons;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

























public class FlatOptionPaneErrorIcon
  extends FlatOptionPaneAbstractIcon
{
  public FlatOptionPaneErrorIcon() {
    super("OptionPane.icon.errorColor", "Actions.Red");
  }











  
  protected Shape createOutside() {
    return new Ellipse2D.Float(2.0F, 2.0F, 28.0F, 28.0F);
  }

  
  protected Shape createInside() {
    Path2D inside = new Path2D.Float(0);
    inside.append(new Rectangle2D.Float(14.0F, 7.0F, 4.0F, 11.0F), false);
    inside.append(new Rectangle2D.Float(14.0F, 21.0F, 4.0F, 4.0F), false);
    return inside;
  }
}
