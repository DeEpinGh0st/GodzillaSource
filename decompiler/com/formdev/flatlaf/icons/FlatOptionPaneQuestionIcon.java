package com.formdev.flatlaf.icons;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

























public class FlatOptionPaneQuestionIcon
  extends FlatOptionPaneAbstractIcon
{
  public FlatOptionPaneQuestionIcon() {
    super("OptionPane.icon.questionColor", "Actions.Blue");
  }











  
  protected Shape createOutside() {
    return new Ellipse2D.Float(2.0F, 2.0F, 28.0F, 28.0F);
  }

  
  protected Shape createInside() {
    Path2D q = new Path2D.Float();
    q.moveTo(14.0D, 20.0D);
    q.lineTo(18.0D, 20.0D);
    q.curveTo(18.0D, 16.0D, 23.0D, 16.0D, 23.0D, 12.0D);
    q.curveTo(23.0D, 8.0D, 20.0D, 6.0D, 16.0D, 6.0D);
    q.curveTo(12.0D, 6.0D, 9.0D, 8.0D, 9.0D, 12.0D);
    q.curveTo(9.0D, 12.0D, 13.0D, 12.0D, 13.0D, 12.0D);
    q.curveTo(13.0D, 10.0D, 14.0D, 9.0D, 16.0D, 9.0D);
    q.curveTo(18.0D, 9.0D, 19.0D, 10.0D, 19.0D, 12.0D);
    q.curveTo(19.0D, 15.0D, 14.0D, 15.0D, 14.0D, 20.0D);
    q.closePath();
    
    Path2D inside = new Path2D.Float(0);
    inside.append(new Rectangle2D.Float(14.0F, 22.0F, 4.0F, 4.0F), false);
    inside.append(q, false);
    return inside;
  }
}
