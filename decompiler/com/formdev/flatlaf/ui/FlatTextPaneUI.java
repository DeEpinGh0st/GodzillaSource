package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.JTextComponent;













































public class FlatTextPaneUI
  extends BasicTextPaneUI
{
  protected int minimumWidth;
  protected boolean isIntelliJTheme;
  private Object oldHonorDisplayProperties;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatTextPaneUI();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    this.minimumWidth = UIManager.getInt("Component.minimumWidth");
    this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");

    
    this.oldHonorDisplayProperties = getComponent().getClientProperty("JEditorPane.honorDisplayProperties");
    getComponent().putClientProperty("JEditorPane.honorDisplayProperties", Boolean.valueOf(true));
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    getComponent().putClientProperty("JEditorPane.honorDisplayProperties", this.oldHonorDisplayProperties);
  }

  
  protected void propertyChange(PropertyChangeEvent e) {
    super.propertyChange(e);
    FlatEditorPaneUI.propertyChange(getComponent(), e);
  }

  
  public Dimension getPreferredSize(JComponent c) {
    return FlatEditorPaneUI.applyMinimumWidth(c, super.getPreferredSize(c), this.minimumWidth);
  }

  
  public Dimension getMinimumSize(JComponent c) {
    return FlatEditorPaneUI.applyMinimumWidth(c, super.getMinimumSize(c), this.minimumWidth);
  }

  
  protected void paintSafely(Graphics g) {
    super.paintSafely(HiDPIUtils.createGraphicsTextYCorrection((Graphics2D)g));
  }

  
  protected void paintBackground(Graphics g) {
    JTextComponent c = getComponent();

    
    if (this.isIntelliJTheme && (!c.isEnabled() || !c.isEditable()) && c.getBackground() instanceof javax.swing.plaf.UIResource) {
      FlatUIUtils.paintParentBackground(g, c);
      
      return;
    } 
    super.paintBackground(g);
  }
}
