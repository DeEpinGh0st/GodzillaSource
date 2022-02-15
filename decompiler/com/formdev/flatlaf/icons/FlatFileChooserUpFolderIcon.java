package com.formdev.flatlaf.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;

























public class FlatFileChooserUpFolderIcon
  extends FlatAbstractIcon
{
  private final Color blueColor = UIManager.getColor("Actions.Blue");
  
  public FlatFileChooserUpFolderIcon() {
    super(16, 16, UIManager.getColor("Actions.Grey"));
  }










  
  protected void paintIcon(Component c, Graphics2D g) {
    g.fill(FlatUIUtils.createPath(new double[] { 2.0D, 3.0D, 5.5D, 3.0D, 7.0D, 5.0D, 9.0D, 5.0D, 9.0D, 9.0D, 13.0D, 9.0D, 13.0D, 5.0D, 14.0D, 5.0D, 14.0D, 13.0D, 2.0D, 13.0D }));
    
    g.setColor(this.blueColor);
    g.fill(FlatUIUtils.createPath(new double[] { 12.0D, 4.0D, 12.0D, 8.0D, 10.0D, 8.0D, 10.0D, 4.0D, 8.0D, 4.0D, 11.0D, 1.0D, 14.0D, 4.0D, 12.0D, 4.0D }));
  }
}
