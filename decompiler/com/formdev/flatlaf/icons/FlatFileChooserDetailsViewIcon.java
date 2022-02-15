package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;
























public class FlatFileChooserDetailsViewIcon
  extends FlatAbstractIcon
{
  public FlatFileChooserDetailsViewIcon() {
    super(16, 16, UIManager.getColor("Actions.Grey"));
  }














  
  protected void paintIcon(Component c, Graphics2D g) {
    g.fillRect(2, 3, 2, 2);
    g.fillRect(2, 7, 2, 2);
    g.fillRect(2, 11, 2, 2);
    g.fillRect(6, 3, 8, 2);
    g.fillRect(6, 7, 8, 2);
    g.fillRect(6, 11, 8, 2);
  }
}
