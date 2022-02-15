package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
























public class FlatTreeOpenIcon
  extends FlatAbstractIcon
{
  public FlatTreeOpenIcon() {
    super(16, 16, UIManager.getColor("Tree.icon.openColor"));
  }










  
  protected void paintIcon(Component c, Graphics2D g) {
    g.fill(FlatUIUtils.createPath(new double[] { 1.0D, 2.0D, 6.0D, 2.0D, 8.0D, 4.0D, 14.0D, 4.0D, 14.0D, 6.0D, 3.5D, 6.0D, 1.0D, 11.0D }));
    g.fill(FlatUIUtils.createPath(new double[] { 4.0D, 7.0D, 16.0D, 7.0D, 13.0D, 13.0D, 1.0D, 13.0D }));
  }
}
