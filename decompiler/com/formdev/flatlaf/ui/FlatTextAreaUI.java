package com.formdev.flatlaf.ui;

import com.formdev.flatlaf.util.HiDPIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;











































public class FlatTextAreaUI
  extends BasicTextAreaUI
{
  protected int minimumWidth;
  protected boolean isIntelliJTheme;
  protected Color background;
  protected Color disabledBackground;
  protected Color inactiveBackground;
  
  public static ComponentUI createUI(JComponent c) {
    return new FlatTextAreaUI();
  }

  
  public void installUI(JComponent c) {
    super.installUI(c);
    
    updateBackground();
  }

  
  protected void installDefaults() {
    super.installDefaults();
    
    this.minimumWidth = UIManager.getInt("Component.minimumWidth");
    this.isIntelliJTheme = UIManager.getBoolean("Component.isIntelliJTheme");
    this.background = UIManager.getColor("TextArea.background");
    this.disabledBackground = UIManager.getColor("TextArea.disabledBackground");
    this.inactiveBackground = UIManager.getColor("TextArea.inactiveBackground");
  }

  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    
    this.background = null;
    this.disabledBackground = null;
    this.inactiveBackground = null;
  }

  
  protected void propertyChange(PropertyChangeEvent e) {
    super.propertyChange(e);
    FlatEditorPaneUI.propertyChange(getComponent(), e);
    
    switch (e.getPropertyName()) {
      case "editable":
      case "enabled":
        updateBackground();
        break;
    } 
  }
  
  private void updateBackground() {
    JTextComponent c = getComponent();
    
    Color background = c.getBackground();
    if (!(background instanceof javax.swing.plaf.UIResource)) {
      return;
    }
    
    if (background != this.background && background != this.disabledBackground && background != this.inactiveBackground) {
      return;
    }



    
    Color newBackground = !c.isEnabled() ? this.disabledBackground : (!c.isEditable() ? this.inactiveBackground : this.background);


    
    if (newBackground != background) {
      c.setBackground(newBackground);
    }
  }
  
  public Dimension getPreferredSize(JComponent c) {
    return applyMinimumWidth(c, super.getPreferredSize(c));
  }

  
  public Dimension getMinimumSize(JComponent c) {
    return applyMinimumWidth(c, super.getMinimumSize(c));
  }

  
  private Dimension applyMinimumWidth(JComponent c, Dimension size) {
    if (c instanceof JTextArea && ((JTextArea)c).getColumns() > 0) {
      return size;
    }
    return FlatEditorPaneUI.applyMinimumWidth(c, size, this.minimumWidth);
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
