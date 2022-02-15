package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;














































public class FlatEditorPaneUI
  extends BasicEditorPaneUI
{
  protected int minimumWidth;
  protected boolean isIntelliJTheme;
  private Object oldHonorDisplayProperties;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatEditorPaneUI();
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
    propertyChange(getComponent(), e);
  }
  
  static void propertyChange(JTextComponent c, PropertyChangeEvent e) {
    switch (e.getPropertyName()) {
      case "JComponent.minimumWidth":
        c.revalidate();
        break;
    } 
  }

  
  public Dimension getPreferredSize(JComponent c) {
    return applyMinimumWidth(c, super.getPreferredSize(c), this.minimumWidth);
  }

  
  public Dimension getMinimumSize(JComponent c) {
    return applyMinimumWidth(c, super.getMinimumSize(c), this.minimumWidth);
  }




  
  static Dimension applyMinimumWidth(JComponent c, Dimension size, int minimumWidth) {
    minimumWidth = FlatUIUtils.minimumWidth(c, minimumWidth);
    size.width = Math.max(size.width, UIScale.scale(minimumWidth) - UIScale.scale(1) * 2);
    return size;
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
