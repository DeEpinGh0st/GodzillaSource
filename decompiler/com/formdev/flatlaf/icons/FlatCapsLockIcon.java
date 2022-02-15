package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;
























public class FlatCapsLockIcon
  extends FlatAbstractIcon
{
  public FlatCapsLockIcon() {
    super(16, 16, UIManager.getColor("PasswordField.capsLockIconColor"));
  }











  
  protected void paintIcon(Component c, Graphics2D g) {
    Path2D path = new Path2D.Float(0);
    path.append(new RoundRectangle2D.Float(0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 6.0F), false);
    path.append(new Rectangle2D.Float(5.0F, 12.0F, 6.0F, 2.0F), false);
    path.append(FlatUIUtils.createPath(new double[] { 2.0D, 8.0D, 8.0D, 2.0D, 14.0D, 8.0D, 11.0D, 8.0D, 11.0D, 10.0D, 5.0D, 10.0D, 5.0D, 8.0D }, ), false);
    g.fill(path);
  }
}
