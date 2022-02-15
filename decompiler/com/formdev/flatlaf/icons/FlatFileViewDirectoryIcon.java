package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
























public class FlatFileViewDirectoryIcon
  extends FlatAbstractIcon
{
  public FlatFileViewDirectoryIcon() {
    super(16, 16, UIManager.getColor("Objects.Grey"));
  }







  
  protected void paintIcon(Component c, Graphics2D g) {
    g.fill(FlatUIUtils.createPath(new double[] { 1.0D, 2.0D, 6.0D, 2.0D, 8.0D, 4.0D, 15.0D, 4.0D, 15.0D, 13.0D, 1.0D, 13.0D }));
  }
}
